package r01f.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import r01f.httpclient.HttpClientProxySettings;
import r01f.mail.GoogleAPI.GoogleAPIServiceAccountClientData;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

/**
 * A Spring's {@link JavaMailSender} implementation using Google's GMAIL
 * It uses a google API [Service Account] in order to avoid end-user interaction (server-to-server)
 * In order to set-up a [Service Account] client ID: (see http://stackoverflow.com/questions/29327846/gmail-rest-api-400-bad-request-failed-precondition/29328258#29328258)
 *	 1.- Using a google apps user open the developer console
 *	 2.- Create a new project (ie MyProject)
 * 	 3.- Go to [Apis & auth] > [Credentials] and create a new [Service Account] client ID
 * 	 4.- Copy the [service account]'s [Client ID] (the one like xxx.apps.googleusercontent.com) for later use
 * 	 5.- Now you have to Delegate domain-wide authority to the service account in order to authorize your appl to access user data on behalf of users in the Google Apps domain 
 * 		 ... so go to your google apps domain admin console
 * 	 6.- Go to the [Security] section and find the [Advanced Settings] (it might be hidden so you'd have to click [Show more..])
 * 	 7.- Click con [Manage API Client Access]
 *	 8.- Paste the [Client ID] you previously copied at [4] into the [Client Name] text box.
 *	 9.- To grant your app full access to gmail, at the [API Scopes] text box enter: https://mail.google.com, https://www.googleapis.com/auth/gmail.compose, https://www.googleapis.com/auth/gmail.modify, https://www.googleapis.com/auth/gmail.readonly
 *		 (it's very important that you enter ALL the scopes)
 * Usage:
 * <pre class='brush:java'>
 * 		GoogleAPIServiceAccountClientData serviceAccountClientID = new GoogleAPIServiceAccountClientData(AppCode.forId(...the app code registered at the google developer console...),
 * 																										 GoogleAPIClientID.of("xxx.apps.googleusercontent.com"),
 * 																									 	 GoogleAPIClientEMailAddress.of("xxx@developer.gserviceaccount.com"),
 * 																									 	 GoogleAPIClientIDP12KeyPath.loadedFromFileSystem(...path_to a P12 file downloaded from google developer console...),
 * 																									 	 EMail.of("user@example.com"),	// a google apps domain user
 * 																									 	 GmailScopes.all());
 * 		 JavaMailSender mailSender = GMailMailSender.create(serviceAccountClientID);
 * 		
 * 		// [1] - Create a MimeMessagePreparator
 * 		MimeMessagePreparator msgPreparator = _createMimeMessagePreparator(EMail.of("dest_user@test.com"),
 * 																	       EMail.of("src_user@test.com"),
 * 																	       "A TEST mail message sent using GMail API",
 * 																	       "Just testing GMail API");
 * 		// [2] - Send the message
 *         mailSender.send(msgPreparator);
 *         
 * </pre>
 * Where:
 * <pre class='brush:java'>
 *      private static MimeMessagePreparator _createMimeMessagePreparator(final EMail to,
 *      												   				  final EMail from,
 *      												   				  final String subject,
 *      												   				  final String text) {
 *      	return new MimeMessagePreparator() {
 *      						@Override
 *      			            public void prepare(final MimeMessage mimeMessage) throws Exception {	
 *      							_createMimeMessageHelper(mimeMessage,
 *      													 to,from,
 *      													 subject,text);
 *      			            }
 *          };
 *      }
 *      private static MimeMessageHelper _createMimeMessageHelper(final MimeMessage mimeMessage,
 *      												   		  final EMail to,final EMail from,
 *      												   		  final String subject,final String text) throws MessagingException {
 *          MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
 *          												  true);	// multi-part!!
 *          // To & From
 *          message.setTo(to.asString());
 *          message.setFrom(from.asString());
 *          
 *          // Subject
 *          message.setSubject(subject);
 *          
 *          // Text
 *          message.setText(text,
 *          				true);	// html message	
 *          return message;
 *      }
 * </pre>
 */
@Slf4j
@Accessors(prefix="_")
public class JavaMailSenderGmailImpl 
  	 extends JavaMailSenderImplBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final GoogleAPIServiceAccountClientData _googleServiceAccountClientData;
	@Getter private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderGmailImpl(final GoogleAPIServiceAccountClientData googleServiceAccountClientData) {
		this(googleServiceAccountClientData,
			 null);	// no proxy
	}
	public JavaMailSenderGmailImpl(final GoogleAPIServiceAccountClientData googleServiceAccountClientData,
								   final HttpClientProxySettings proxySettings) {
		super();
		_googleServiceAccountClientData = googleServiceAccountClientData;		
		_proxySettings = proxySettings;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JavaMailSender API
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _doSend(final MimeMessage[] mimeMessages,
						   final Object[] originalMessages) throws MailException {
		// [1] - Create the transport & json factory
		HttpTransport httpTransport;
		try {
			if (_proxySettings == null) {
				httpTransport = GoogleAPI.createHttpTransport().noProxy();
			} else {
				httpTransport = GoogleAPI.createHttpTransport().usingProxy(_proxySettings);
			}
		} catch (IOException ioEx) {
			throw new MailAuthenticationException("Cannot access Google certificates trust store",ioEx);
		} catch (GeneralSecurityException secEx) {
			throw new MailAuthenticationException("Cannot access Google certificates trust store",secEx);
		}
		JsonFactory jsonFactory = GoogleAPI.createJsonFactory();
		
		// [2] - Create the google credential
		GoogleCredential googleCredential;
		try {
			googleCredential = GoogleAPI.createCredentialForServiceAccount(httpTransport,
														   			 	   jsonFactory,
														   			 	   _googleServiceAccountClientData);
		} catch (IOException ioEx) {
			throw new MailAuthenticationException("Cannot access google client secret at " + _googleServiceAccountClientData.getP12KeyPath(),ioEx);
		} catch (GeneralSecurityException secEx) {
			throw new MailAuthenticationException(secEx);
		}
		// [3] - Create the gmail service
		Gmail gmailService = GoogleAPI.createGmailService(httpTransport,
												 		  jsonFactory,
												 		  _googleServiceAccountClientData.getAppCode(),
												 		  googleCredential);
		// [4] - Send the messages
		Map<Object,Exception> failedMessages = new LinkedHashMap<Object,Exception>();
		for (int i = 0; i < mimeMessages.length; i++) {
			MimeMessage mimeMessage = mimeMessages[i];
			try {
				log.info("... starting mail sending");
				Message gmailMessage = _createGmailMessageWithMimeMessage(mimeMessage);
				gmailMessage = gmailService.users()
										   .messages()
										   .send("me",
												 gmailMessage)
										   .execute();
				log.info("... end mail sending");
			} catch (IOException ioEx) {
				Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
				failedMessages.put(original,ioEx);
			} catch(MessagingException gmailMsgEx) {
				Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
				failedMessages.put(original,gmailMsgEx);
			}
		}
		// [5] - Throw exception if any
		if (!failedMessages.isEmpty()) {
			throw new MailSendException(failedMessages);
		}
	}
	private static Message _createGmailMessageWithMimeMessage(final MimeMessage mimeMessage) throws MessagingException,
																		   		  			 		IOException {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    mimeMessage.writeTo(bytes);
	    String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
	    Message gmailMessage = new Message();
	    gmailMessage.setRaw(encodedEmail);
	    return gmailMessage;
	}
}
