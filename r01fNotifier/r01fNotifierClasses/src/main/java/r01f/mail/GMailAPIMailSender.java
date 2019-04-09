package r01f.mail;


import r01f.httpclient.HttpClientProxySettings;
import r01f.mail.GoogleAPI.GoogleAPIServiceAccountClientData;


/**
 * Uses GMail API service to send mail
 */
public class GMailAPIMailSender {
	/**
	 * Creates a {@link JavaMailSender} to send an email using gmail
	 * @param googleServiceAccountClientData google service account data
	 * @return
	 */
	public static JavaMailSenderGmailImpl create(final GoogleAPIServiceAccountClientData googleServiceAccountClientData,
												 final HttpClientProxySettings proxySettings) {
		JavaMailSenderGmailImpl mailSender = new JavaMailSenderGmailImpl(googleServiceAccountClientData,
																		 proxySettings);
		return mailSender;
	}
	/**
	 * Creates a {@link JavaMailSender} to send an email using gmail
	 * @param googleServiceAccountClientData google service account data
	 * @return
	 */
	public static JavaMailSenderGmailImpl create(final GoogleAPIServiceAccountClientData googleServiceAccountClientData) {
		return GMailAPIMailSender.create(googleServiceAccountClientData,
										 null);	// no proxy
	}

}
