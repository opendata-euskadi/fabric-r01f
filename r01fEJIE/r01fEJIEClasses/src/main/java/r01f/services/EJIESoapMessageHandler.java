package r01f.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EJIESoapMessageHandler
  implements Handler {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    private HandlerInfo _handlerInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//  INIT / DESTROY
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public void init(final HandlerInfo info) {
        _handlerInfo = info;
    }
	@Override
	public void destroy() {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public QName[] getHeaders() {
        return _handlerInfo.getHeaders();
    }
    @Override
	public boolean handleRequest(final MessageContext msgCtxt)  {
         try {
            SOAPMessageContext soapMsgCtx = (SOAPMessageContext)msgCtxt;
            SOAPMessage message = soapMsgCtx.getMessage();

            // Remove XML header from session token
            String sessionToken = (String)_handlerInfo.getHandlerConfig().get("sessionToken");
            if (sessionToken.startsWith("<?xml version=\"1.0\"")) {
                int pos = sessionToken.indexOf('>') + 1;
                sessionToken = sessionToken.substring(pos).trim();
            }

            // add the session token into the soap header
            final SOAPHeader soapHeader = message.getSOAPPart().getEnvelope().getHeader();
            soapHeader.addTextNode(sessionToken);

            // compose the message
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            String soapMessage = new String(baos.toByteArray(),
            								Charset.forName("UTF-8"));
            soapMessage = _processSOAPMessage(soapMessage);

            log.debug("R01FB >>> handleRequest(m) - soapMessage:\n" + soapMessage);

            ByteArrayInputStream in = new ByteArrayInputStream(soapMessage.getBytes());
            MessageFactory factory = MessageFactory.newInstance();
            message =  factory.createMessage(null,in);
            soapMsgCtx.setMessage(message);
        } catch (Exception e) {
            log.error("R01FB >>> JAX-RPC error:" +  e.getMessage());
            throw new JAXRPCException(e);
        }
        return true;
    }
    @Override
    public boolean handleResponse(final MessageContext context) {
        return true;
    }
    @SuppressWarnings({"static-method" })
	public boolean handleResponse(final MessageContext m,
    							  final String error) {
        return true;
    }
	@Override
	public boolean handleFault(final MessageContext context) {
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    private static String _processSOAPMessage(final String soapMessage) {
    	String theSoapMessage = null;
        // Remove XML header from SOAP message
        if (soapMessage.startsWith("<?xml version=\"1.0\"")) {
            int pos = soapMessage.indexOf('>') + 1;
            theSoapMessage = soapMessage.substring(pos).trim();
        } else {
        	theSoapMessage = soapMessage;
        }
    	
        // Remove the < and > characters from the text within <Header>...</Header>
        //		                     <Header>...........</Header>
        //									 ^         ^
        //              headerTextInitPos----|         |-----headerTextEndPos
        int headerTextInitPos = theSoapMessage.indexOf("Header>") + "Header>".length();
        int headerTextEndPos = theSoapMessage.lastIndexOf('<',theSoapMessage.indexOf("Header>",headerTextInitPos) + "Header>".length() - 1);

        String headerText = headerTextInitPos <= headerTextEndPos ? theSoapMessage.substring(headerTextInitPos,headerTextEndPos) 
        												   		  : "";
        headerText = headerText.replaceAll("&lt;","<").replaceAll("&gt;",">");

        // Build the message
        StringBuilder newHeader = new StringBuilder();
        newHeader.append(theSoapMessage.substring(0,headerTextInitPos)); //	<header>
        newHeader.append(headerText);					   			  //	... the header 
        newHeader.append(theSoapMessage.substring(headerTextEndPos));	  // 	</header>

        return newHeader.toString();
    }
}
