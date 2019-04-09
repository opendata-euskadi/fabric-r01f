/*
 * Created on Feb 22, 2007
 *
 * @author ie00165h - Alex Lara
 * (c) 2007 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.mail.simple;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpRequestFormParameter;
import r01f.httpclient.HttpRequestFormParameterForMultiPartBinaryData;
import r01f.httpclient.HttpRequestFormParameterForText;
import r01f.httpclient.HttpRequestPayloadForFileParameter;
import r01f.mime.MimeType;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.types.url.Host;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * A simple helper type to send emails
 * Usage:
 * <pre class="brush:java">
 *      SimpleJavaMailSender mailSender = new SimpleJavaMailSender(smtpHost);
 *      mailSender.sendMessage(from,to,
 *      					   subject,
 *      					   SimpleJavaMailSender.MIME_HTML,"Hello World",
 *      					   null);		// no attachments
 * </pre>
 */
public class SimpleHttpMailSender
	 extends SimpleMailSenderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * SMTP Host
     */
    private final Host _smtpHost;
    /**
     * Time to wait for a session
     */
    private final int _timeout;
    /**
     * Debug enabled?
     */
    private final boolean _debug;
    /**
     * Htpp proxy settings
     */
    private final HttpClientProxySettings _proxySettings; 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public SimpleHttpMailSender(final Host smtpHost,
    							final boolean debug) {
        _smtpHost = smtpHost;
        _timeout = -1;
        _debug = debug;
        _proxySettings = null;
    }
    public SimpleHttpMailSender(final Host smtpHost,final int timeout,
    							final boolean debug) {
        _smtpHost = smtpHost;
        _timeout = timeout;
        _debug = debug;
        _proxySettings = null;
    }
    public SimpleHttpMailSender(final Host smtpHost,final int timeout,
    							final boolean debug,
    							final HttpClientProxySettings proxySettings) {
        _smtpHost = smtpHost;
        _timeout = timeout;
        _debug = debug;
        _proxySettings = proxySettings;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    public void sendMessage(final EMail from,final Collection<EMail> to, 
    						final String subject,
                            final MimeType messageContentType,final String messageText) throws AddressException,
                                                                 	MessagingException {
    	this.sendMessage(from,to,
    					 subject,
    					 messageContentType,messageText,
    					 null);		// no attachments
    }
    public void sendMessage(final EMail from,final Collection<EMail> to, 
    						final String subject,
                            final MimeType messageContentType,final String messageText,
                            final Path[] attachedFilesPaths) throws AddressException,
                                                                 	MessagingException {

		this.sendMessage(from,to,null,null,
						 subject, 
						 messageContentType,messageText,
						 attachedFilesPaths);
    }
    public void sendMessage(final EMail from,final Collection<EMail> to,final Collection<EMail> toCC,final Collection<EMail> toCCO,
    						final String subject,
                            final MimeType messageContentType,final String messageText) throws AddressException,
                                                                 	MessagingException {
    	this.sendMessage(from,to,toCC,toCCO,
    					 subject,
    					 messageContentType,messageText,
    					 null);		// no attachments
    }
    public void sendMessage(final EMail from,final Collection<EMail> to,final Collection<EMail> toCC,final Collection<EMail> toCCO,
    						final String subject,
                            final MimeType mimeType,final String messageText,
                            final Path[] attachedFilesPaths) throws AddressException,
                                                                 	MessagingException {
        // Checks
        if (_smtpHost == null) throw new MessagingException("The SMTP host cannot be null");
        if (CollectionUtils.isNullOrEmpty(to)) throw new MessagingException("Cannot send a mail message to an unknown destination email address");

        // Compose the post-ed parameters
        HttpRequestFormParameterForText fromParam = HttpRequestFormParameterForText.of(from.asString())
        																		   .withName("from");
        HttpRequestFormParameterForText toParam = HttpRequestFormParameterForText.of(CollectionUtils.toStringSeparatedWith(to,';'))
        																		 .withName("to");
        HttpRequestFormParameterForText toCCParam = CollectionUtils.hasData(toCC) 
        													? HttpRequestFormParameterForText.of(CollectionUtils.toStringSeparatedWith(toCC,';')) 
        																					 .withName("toCC")
        													: null;
        HttpRequestFormParameterForText toCCOParam = CollectionUtils.hasData(toCCO) 
        													? HttpRequestFormParameterForText.of(CollectionUtils.toStringSeparatedWith(toCCO,';')) 
        																					 .withName("toCCO")
        													: null;
        HttpRequestFormParameterForText subjParam = HttpRequestFormParameterForText.of(subject)
        																		   .withName("subject");
        HttpRequestFormParameterForText mimeParam = HttpRequestFormParameterForText.of(mimeType.asString())
        																		   .withName("messageContentType");
        HttpRequestFormParameterForText textParam = HttpRequestFormParameterForText.of(messageText)
        																		   .withName("messageText");
        
        List<HttpRequestFormParameter> params = Lists.newArrayList();
        params.add(fromParam);
        params.add(toParam);
        if (toCCParam != null) params.add(toCCParam);
        if (toCCOParam != null) params.add(toCCOParam);
        params.add(subjParam);
        params.add(mimeParam);
        params.add(textParam);

        // Add Attachments
        if (CollectionUtils.hasData(attachedFilesPaths)) {
        	List<HttpRequestPayloadForFileParameter> fileParams = FluentIterable.from(attachedFilesPaths)
        																.transform(new Function<Path,HttpRequestPayloadForFileParameter>() {
																							@Override
																							public HttpRequestPayloadForFileParameter apply(final Path attachmentFilePath) {
																								return HttpRequestPayloadForFileParameter.wrap(attachmentFilePath.asAbsoluteString())
																																		 .withFileName(attachmentFilePath.getFileName());
																							}
        																	
        																		   })
        																.toList();
        	HttpRequestFormParameterForMultiPartBinaryData multiPartBinaryData = HttpRequestFormParameterForMultiPartBinaryData.of(fileParams)
        																													   .withName("files");
        	params.add(multiPartBinaryData);
        	params.add(HttpRequestFormParameterForText.of(fileParams.size())
        											  .withName("numFiles"));
        	
        	// http post multipart
        	try {
				String postResponse = HttpClient.forUrl(_smtpHost.asUrl())										 
										  .POSTMultiPart()
										  		.withPOSTFormParameters(params)
										  		.loadAsString()
										  			.usingProxy(_proxySettings).withoutTimeOut().noAuth();
			} catch (MalformedURLException urlEx) {
				urlEx.printStackTrace();
				throw new MessagingException("http client error: " + urlEx.getMessage(),
											 urlEx);
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
				throw new MessagingException("http client error: " + ioEx.getMessage(),
											 ioEx);
			}
        }
        // no attachments
        else {
        	params.add(HttpRequestFormParameterForText.of(0)
        											  .withName("numFiles"));
        	try {
				String postResponse = HttpClient.forUrl(_smtpHost.asUrl())
										  .POSTForm()
										  		.withPOSTFormParameters(params)
										  		.loadAsString()
										  			.usingProxy(_proxySettings).withoutTimeOut().noAuth();
			} catch (MalformedURLException urlEx) {
				urlEx.printStackTrace();
				throw new MessagingException("http client error: " + urlEx.getMessage(),
											 urlEx);
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
				throw new MessagingException("http client error: " + ioEx.getMessage(),
											 ioEx);
			}
        }
	 }
}
