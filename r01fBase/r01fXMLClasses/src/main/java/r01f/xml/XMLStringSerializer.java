/*
 * Created on 20-feb-2005
 * 
 * @author IE00165H
 * (c) 2005 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Predicate;

import lombok.NoArgsConstructor;

/**
 * Formats an xml string
 */
@NoArgsConstructor
public class XMLStringSerializer {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Checks if a xml's char is a valid char acording to the xml spec
     * @see http://seattlesoftware.wordpress.com/2008/09/11/hexadecimal-value-0-is-an-invalid-character/
     * If invalid chars are found while parsing an xml stream, an error is raised:
     * 		"Hexadecimal value 0x[---] is an invalid character"
     * These type of chars are control chars like null, bell, backspace, etc
     * @param theChar 
     * @return true 
     */
    public static boolean isLegalChar(final char theChar) {
    	return  (theChar == 0x9) ||/* == '\t' == 9   */
                (theChar == 0xA) ||/* == '\n' == 10  */
                (theChar == 0xD) ||/* == '\r' == 13  */
                ((theChar >= 0x20) && (theChar <= 0xD7FF)) ||
                ((theChar >= 0xE000) && (theChar <= 0xFFFD)) ||
                ((theChar >= 0x10000) && (theChar <= 0x10FFFF));
    }
    public static Predicate<Character> isLegalCharPredicate() {
    	return new Predicate<Character>() {
						@Override
						public boolean apply(final Character code) {
							return XMLStringSerializer.isLegalChar(code);
						}
    		   };
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Prints an xml structure
     * @param doc 
     * @param outEncoding generated xml encoding (use null for default encoding)
     * @return
     */
    public static String writeDocument(final Document doc,final Charset outEncoding) {
        if (doc == null) return null;
        return XMLStringSerializer.writeNode(doc.getDocumentElement(),outEncoding);
    }
    
    /**
     * Prints an xml structure
     * @param beginNode 
     * @param outEncoding generated xml encoding (use null for default encoding)
     * @return 
     */
    public static String writeNode(final Node beginNode,final Charset outEncoding) {
        if (beginNode == null) return null;
        try {
            return XMLStringSerializer.writeOuterXML(beginNode,outEncoding);
        } catch (TransformerException tEx) {
            return ("Error while parsing a DOM node to an XML string: " + tEx.toString() );
        }        
    }    
    /**
     * Serializes an xml node
     * @param node 
     * @param outEncoding egenerated xml encoding (use null for default encoding
     * @return 
     * @throws TransformerException
     */
    public static String writeInnerXML(final Node node,final Charset outEncoding) throws TransformerException {
        StringBuilder innerXml = new StringBuilder();
        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();
            int i = childNodes.getLength();
            for (int c = 0; c < i; c++) {
                innerXml.append(XMLStringSerializer.writeOuterXML(childNodes.item(c),outEncoding));
            }
            return innerXml.toString();
        }
        return "";
    }    
    /**
     * Serializes a given xml node 
     * @param node 
     * @param outEncoding generated xml encoding (use null for default encoding
     * @return 
     * @throws TransformerException 
     */
    public static String writeOuterXML(final Node node,final Charset outEncoding) throws TransformerException {
        if (node == null) return null;
        try {
            TransformerFactory fac = TransformerFactory.newInstance();
            Transformer tf = fac.newTransformer();
            Properties tfProps = new Properties();
            if (outEncoding != null) tfProps.setProperty(OutputKeys.ENCODING,outEncoding.name());
            tfProps.setProperty(OutputKeys.INDENT,"yes");  // indent
            tf.setOutputProperties(tfProps);
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");  // 2 spaces as tab
            DOMSource src = new DOMSource(node);  // transform source
            StringWriter w = new StringWriter();  // serialized destination
            StreamResult rslt = new StreamResult(w);
            tf.transform(src,rslt);
            return w.toString();
        } catch (TransformerFactoryConfigurationError tfCfgEx) {
            throw new TransformerException(tfCfgEx);
        } catch (TransformerConfigurationException tfCfgEx) {
            throw new TransformerException(tfCfgEx);
        } catch (IllegalArgumentException illArgEx) {
            throw new TransformerException(illArgEx);
        }      
    }
    /**
     * Beautifies an xml string 
     * IMPORTANT!!	It uses the default encoding
     * @param notFormatedXMLString
     * @return 
     * @throws TransformerException
     */    
    public static String beautifyXMLString(final String notFormatedXMLString) throws TransformerException {
    	return XMLStringSerializer.beautifyXMLString(notFormatedXMLString,Charset.defaultCharset(),Charset.defaultCharset());
    }
    /**
     * Beautifies an xml string 
     * @param notFormatedXMLString
     * @param inputEncoding input string encoding
     * @param outEncoding generated xml encoding (use null for default encoding
     * @return 
     * @throws TransformerException
     */    
    public static String beautifyXMLString(final String notFormatedXMLString,final Charset inputEncoding,
    									   final Charset outEncoding) throws TransformerException {
        Document doc = null;
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setIgnoringComments(true);  // Pasar de los comentarios
        dfactory.setNamespaceAware(true);
        try {
            InputSource is = new InputSource( new ByteArrayInputStream(notFormatedXMLString.getBytes(inputEncoding)));
            is.setEncoding(inputEncoding.name());
            doc = dfactory.newDocumentBuilder().parse(is);
        } catch (ParserConfigurationException pcEx) {
            throw new TransformerException("XML parser error: " + pcEx.toString());
        } catch (SAXException saxEx) {
            throw new TransformerException("SAX parser error: " + saxEx.toString());
        } catch (IOException ioEx) {
            throw new TransformerException("SAX parser error: " + ioEx.toString());
        }
        
        String salida = XMLStringSerializer.writeDocument(doc,outEncoding);
        return salida;
    }
}
