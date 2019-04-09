/*
 * Created on Feb 22, 2007
 *
 * @author ie00165h - Alex Lara
 * (c) 2007 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.mail.simple;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import r01f.exceptions.Throwables;
import r01f.mime.MimeType;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.types.url.Host;
import r01f.util.types.collections.CollectionUtils;

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
public class SimpleJavaMailSender 
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
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public SimpleJavaMailSender(final Host smtpHost) {
    	this(smtpHost,
    		 false);	// debug
    }
    public SimpleJavaMailSender(final Host smtpHost,
    							final boolean debug) {
        _smtpHost = smtpHost;
        _timeout = -1;
        _debug = debug;
    }
    public SimpleJavaMailSender(final Host smtpHost,final int timeout) {
    	this(smtpHost,timeout,
    		 false);	// debug
    }
    public SimpleJavaMailSender(final Host smtpHost,final int timeout,
    							final boolean debug) {
        _smtpHost = smtpHost;
        _timeout = timeout;
        _debug = debug;
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
                            final MimeType messageContentType,final String messageText,
                            final Path[] attachedFilesPaths) throws AddressException,
                                                                 	MessagingException {
        // Checks
        if (_smtpHost == null) throw new MessagingException("The SMTP host cannot be null");
        if (CollectionUtils.isNullOrEmpty(to)) throw new MessagingException("Cannot send a mail message to an unknown destination email address");
        
        // ----> Open SMTP session
        Properties props = new Properties();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Cuando asignamos propiedades al objeto Properties, al usar el metodo put hay que pasarle 2 String,
        //    ya que si le pasas 2 Objetos como tal a la hora de leer la propiedadd con getProperty, devuelve NULL.
        //
        //    https://stackoverflow.com/questions/30381563/putting-objects-into-java-util-properties
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        props.put("mail.smtp.host",_smtpHost.toString());
        if ( _timeout > -1 ) props.put("mail.smtp.connectiontimeout", "" + _timeout);
        props.put("mail.debug",String.valueOf(_debug));
        
        Session sesion = Session.getInstance(props,null);
        sesion.setDebug(false);

        // ----> Message header
        Message message = new MimeMessage(sesion);
        
        // FROM
        InternetAddress from_addr = new InternetAddress(from.asString());
        message.setFrom(from_addr);
        
        // TO 
        InternetAddress[] addrsTo = SimpleJavaMailSender.eMailCollectionToInternetAddress(to);
        if (CollectionUtils.hasData(addrsTo)) message.addRecipients(Message.RecipientType.TO,addrsTo);

        // TOCC
	    InternetAddress[] addrsToCC = SimpleJavaMailSender.eMailCollectionToInternetAddress(toCC);
	    if (CollectionUtils.hasData(addrsToCC)) message.addRecipients(Message.RecipientType.CC,addrsToCC);

        // TOCCO
        InternetAddress[] addrsToCCO = SimpleJavaMailSender.eMailCollectionToInternetAddress(toCCO);
        if (CollectionUtils.hasData(addrsToCCO)) message.addRecipients(Message.RecipientType.BCC,addrsToCCO);

        // subject
        message.setSubject(subject);

        // ----> Message body
        //String theMessageContentType = CONTENT_TYPE_TEXT;
        //if (messageContentType != null) theMessageContentType = messageContentType;
        MimeMultipart mp = new MimeMultipart("related");
        BodyPart textMP = new MimeBodyPart();
        textMP.setDisposition(Part.INLINE);
        textMP.setContent(messageText,messageContentType.asString());
        mp.addBodyPart(textMP);

        // ----> Attached files (if they exist)
        if (CollectionUtils.hasData(attachedFilesPaths)) {
        	Collection<MimeBodyPart> mimeBodyParts = FluentIterable.from(attachedFilesPaths)
        														   .transform(new Function<Path,MimeBodyPart>() {
																						@Override
																						public MimeBodyPart apply(final Path attachedFilePath) {
																							try {
																				                MimeBodyPart file_part = new MimeBodyPart();
																				                File file = new File(attachedFilePath.asAbsoluteString());
																				                FileDataSource fds = new FileDataSource(file);
																				                DataHandler dh = new DataHandler(fds);
																				                file_part.setFileName(file.getName());
																				                file_part.setDisposition(Part.ATTACHMENT);
																				                file_part.setDataHandler(dh);
																				                return file_part;
																							} catch(MessagingException msgEx) {
																								throw Throwables.throwUnchecked(msgEx);
																							}
																						}
        														   			  })
        														   .toList();
        	
            for (MimeBodyPart mimeBodyPart : mimeBodyParts) {
                mp.addBodyPart(mimeBodyPart);
            }
        }
        message.setContent(mp);

        // ----> Send the message
        Transport.send(message);

        // ----> Delete attached files temp files
        if (attachedFilesPaths != null && attachedFilesPaths.length > 0) _deleteTempFiles(attachedFilesPaths);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    private static InternetAddress[] eMailCollectionToInternetAddress(final Collection<EMail> emails) {
    	return CollectionUtils.hasData(emails)
        			? FluentIterable.from(emails)
						  .transform(new Function<EMail,InternetAddress>() {
											@Override
											public InternetAddress apply(final EMail email) {
												try {
													return new InternetAddress(email.toString());
												} catch (AddressException addrEx) {
													throw Throwables.throwUnchecked(addrEx);
												}
											}
						  			 })
						  .toArray(InternetAddress.class)
					: null;
    }
}
