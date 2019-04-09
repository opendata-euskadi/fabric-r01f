package r01f.mail;

import org.springframework.mail.javamail.JavaMailSender;

import r01f.httpclient.HttpClientProxySettings;
import r01f.types.url.Url;
import r01f.util.types.Strings;

/**
 * Uses a third party http enable mail sevice
 */
public class ThridPartyHTTPMailSender {

	/**
	 * Creates a {@link JavaMailSender} to send an email using a http enabled Third Party Mail Sender
	 * @param host
	 * @param port
	 * @return
	 */
	public static JavaMailSender create(final Url thirdPartyProviderUri,
										final HttpClientProxySettings proxySettings,
										final boolean supportsMimeMessage) {
		if (thirdPartyProviderUri == null || Strings.isNullOrEmpty(thirdPartyProviderUri.asString())) throw new IllegalArgumentException("Invalid URL for Third Party Mail Sender");
		JavaMailSender outJavaMailSender =  new JavaMailSenderThridPartyHTTPImpl(thirdPartyProviderUri,
																				 proxySettings,
																				 supportsMimeMessage);
		return outJavaMailSender;
	}
}
