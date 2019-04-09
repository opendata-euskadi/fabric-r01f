package r01f.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.FileTypeMap;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.service.ServiceCanBeDisabled;

import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * A Java {@link JavaMailSender} implementation base
 */
@Accessors(prefix="_")
abstract class JavaMailSenderImplBase 
    implements JavaMailSender,
    		   ServiceCanBeDisabled {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private Properties _javaMailProperties = new Properties();
	@Getter @Setter private Session _session;
	@Getter @Setter private String _defaultEncoding;
	@Getter @Setter private FileTypeMap _defaultFileTypeMap;
					private boolean _disabled;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderImplBase() {
		ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
		fileTypeMap.afterPropertiesSet();
		_defaultFileTypeMap = fileTypeMap;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ServiceCanBeDisabled
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isEnabled() {
		return !_disabled;
	}
	@Override
	public boolean isDisabled() {
		return _disabled;
	}
	@Override
	public void setEnabled() {
		_disabled = false;
	}
	@Override
	public void setDisabled() {
		_disabled = true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JavaMailSender API
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void send(final SimpleMailMessage simpleMessage) throws MailException {
		this.send(new SimpleMailMessage[] { simpleMessage });				
	}
	@Override
	public void send(final SimpleMailMessage[] simpleMessages) throws MailException {
		List<MimeMessage> mimeMessages = new ArrayList<MimeMessage>(simpleMessages.length);
		for (SimpleMailMessage simpleMessage : simpleMessages) {
			MimeMailMessage message = new MimeMailMessage(createMimeMessage());
			simpleMessage.copyTo(message);
			mimeMessages.add(message.getMimeMessage());
		}
		_doSend(mimeMessages.toArray(new MimeMessage[mimeMessages.size()]),
				simpleMessages);
	}
	@Override
	public void send(final MimeMessage mimeMessage) throws MailException {
		this.send(new MimeMessage[] {mimeMessage});
	}
	@Override
	public void send(final MimeMessage[] mimeMessages) throws MailException {
		_doSend(mimeMessages,
				null);
	}
	@Override
	public void send(final MimeMessagePreparator mimeMessagePreparator) throws MailException {
		this.send(new MimeMessagePreparator[] { mimeMessagePreparator });
	}
	@Override
	public void send(final MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
		try {
			List<MimeMessage> mimeMessages = new ArrayList<MimeMessage>(mimeMessagePreparators.length);
			for (MimeMessagePreparator preparator : mimeMessagePreparators) {
				MimeMessage mimeMessage = createMimeMessage();
				preparator.prepare(mimeMessage);
				mimeMessages.add(mimeMessage);
			}
			this.send(mimeMessages.toArray(new MimeMessage[mimeMessages.size()]));
		} catch (MailException ex) {
			throw ex;
		} catch (MessagingException ex) {
			throw new MailParseException(ex);
		} catch (IOException ex) {
			throw new MailPreparationException(ex);
		} catch (Exception ex) {
			throw new MailPreparationException(ex);
		}
	}
	@Override
	public MimeMessage createMimeMessage() {
		return new SmartMimeMessage(_session,
									_defaultEncoding,
									_defaultFileTypeMap);
	}

	@Override
	public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
		try {
			return new MimeMessage(_session,
								   contentStream);
		} catch (MessagingException ex) {
			throw new MailParseException("Could not parse raw MIME content", ex);
		}
	}
	
	protected abstract void _doSend(final MimeMessage[] mimeMessages,
						   			final Object[] originalMessages) throws MailException;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Special subclass of the standard JavaMail {@link MimeMessage}, carrying a
	 * default encoding to be used when populating the message and a default Java
	 * Activation {@link FileTypeMap} to be used for resolving attachment types.
	 */
	@SuppressWarnings("hiding")
	@Accessors(prefix="_")
	protected class SmartMimeMessage 
	  extends MimeMessage {
		@Getter private final String _defaultEncoding;
		@Getter private final FileTypeMap _defaultFileTypeMap;
		/**
		 * Create a new SmartMimeMessage.
		 * @param session the JavaMail Session to create the message for
		 * @param defaultEncoding the default encoding, or {@code null} if none
		 * @param defaultFileTypeMap the default FileTypeMap, or {@code null} if none
		 */
		public SmartMimeMessage(final Session session,
								final String defaultEncoding,
								final FileTypeMap defaultFileTypeMap) {
			super(session);
			_defaultEncoding = defaultEncoding;
			_defaultFileTypeMap = defaultFileTypeMap;
		}
	}
}
