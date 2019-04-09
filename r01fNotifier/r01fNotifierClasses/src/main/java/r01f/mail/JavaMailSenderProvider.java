package r01f.mail;

import javax.inject.Provider;

import org.springframework.mail.javamail.JavaMailSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.mail.config.JavaMailSenderConfig;
import r01f.mail.config.JavaMailSenderConfigForGoogleAPI;
import r01f.mail.config.JavaMailSenderConfigForGoogleSMTP;
import r01f.mail.config.JavaMailSenderConfigForMSExchange;
import r01f.mail.config.JavaMailSenderConfigForThirdPartyHttpService;
import r01f.mail.config.JavaMailSenderImpl;

/**
 * Provides a {@link JavaMailSender} using a properties file inf
 * The properties file MUST contain a config section like:
 * <pre class='xml'>
 *          <javaMailSenderImpls active="google_API">
 *          	<javaMailSenderImpl id='microsoft_exchange'>
 *          		<host>exchangeHost</host>
 *          	</javaMailSenderImpl>
 *          	<javaMailSenderImpl id='google_API'>
 *          		<!--
 *          		 A google API [Service Account] is used in order to avoid end-user interaction (server-to-server)
 *          		 To set-up a [Service Account] client ID: (see http://stackoverflow.com/questions/29327846/gmail-rest-api-400-bad-request-failed-precondition/29328258#29328258)
 *          			1.- Using a google apps user open the developer console
 *          			2.- Create a new project (ie MyProject)
 *          			3.- Go to [Apis & auth] > [Credentials] and create a new [Service Account] client ID
 *          			4.- Copy the [service account]'s [Client ID] (the one like xxx.apps.googleusercontent.com) for later use
 *          			5.- Now you have to Delegate domain-wide authority to the service account in order to authorize your appl to access user data on behalf of users in the Google Apps domain
 *          			    ... so go to your google apps domain admin console
 *          			6.- Go to the [Security] section and find the [Advanced Settings] (it might be hidden so you'd have to click [Show more..])
 *          			7.- Click con [Manage API Client Access]
 *          			8.- Paste the [Client ID] you previously copied at [4] into the [Client Name] text box.
 *          			9.- To grant your app full access to gmail, at the [API Scopes] text box enter: https://mail.google.com, https://www.googleapis.com/auth/gmail.compose, https://www.googleapis.com/auth/gmail.modify, https://www.googleapis.com/auth/gmail.readonly
 *          				(it's very important that you enter ALL the scopes)
 *          		-->
 *          		<serviceAccountClientID>xxx.apps.googleusercontent.com</serviceAccountClientID>
 *          		<serviceAccountEmailAddress>xxx@developer.gserviceaccount.com</serviceAccountEmailAddress>
 *          		<p12Key loadedUsing='classPath'>my_serviceAccount.p12</p12Key>	<!-- change to fileSystem if the p12 file is found at the fileSystem -->
 *          		<googleAppsUser>admin@example.com</googleAppsUser>
 *          	</javaMailSenderImpl>
 *          	<!-- DEPRECATED!! -->
 *          	<javaMailSenderImpl id='google_SMTP'>
 *          		<!--
 *          		How to create an app password:
 *          			1.- Login to the account settings: https://myaccount.google.com/
 *          			2.- Find the [Signing in] section and click on [App passwords]
 *          			3.- Select [Other(custom name)] and give it a name (ie X47B)
 *          			4.- Copy the generated password and put it here
 *          		-->
 *          		<user>a_user</user>
 *          		<password>a password</password>
 *          	</javaMailSenderImpl>
 *          </javaMailSenderImpls>
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class JavaMailSenderProvider
  implements Provider<JavaMailSender> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final JavaMailSenderConfig _config;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("deprecation")
	public JavaMailSender get() {
		JavaMailSender outJavaMailSender = null;
		// instance the javaMailSender depending on the configured impl
		JavaMailSenderImpl impl = _config.getImpl();
		
		// ==== MICROSOFT EXCHANGE
		if (impl == JavaMailSenderImpl.MICROSOFT_EXCHANGE) {
			JavaMailSenderConfigForMSExchange msExchangeCfg = _config.as(JavaMailSenderConfigForMSExchange.class);
			outJavaMailSender = MicrosoftExchangeSMTPMailSender.create(msExchangeCfg.getMailServerHost());
		}

		// ==== GOOGLE GMAIL API
		else if (impl == JavaMailSenderImpl.GOOGLE_API) {
			JavaMailSenderConfigForGoogleAPI gApiCfg = _config.as(JavaMailSenderConfigForGoogleAPI.class);
			JavaMailSenderGmailImpl gmailJavaMailSender = GMailAPIMailSender.create(gApiCfg.getServiceAccountClientData(),	// service account data
														   							gApiCfg.getProxySettings());			// proxy settings
			if (gApiCfg.isDisabled()) gmailJavaMailSender.setDisabled();
			outJavaMailSender = gmailJavaMailSender;
		}

		// ==== GOOGLE GMAIL SMTP
		else if (impl == JavaMailSenderImpl.GOOGLE_SMTP) {
			JavaMailSenderConfigForGoogleSMTP gSMTPCfg = _config.as(JavaMailSenderConfigForGoogleSMTP.class);
			outJavaMailSender = GMailSMTPMailSender.create(gSMTPCfg.getUserAndPassword().getUser(),
														   gSMTPCfg.getUserAndPassword().getPassword());
		// ==== THIRD_PARTY_MAIL_HTTPSERVICE
		} else if (impl == JavaMailSenderImpl.THIRD_PARTY_MAIL_HTTPSERVICE) {
			JavaMailSenderConfigForThirdPartyHttpService thirdPartyHttpCfg = _config.as(JavaMailSenderConfigForThirdPartyHttpService.class);
			outJavaMailSender =  ThridPartyHTTPMailSender.create(thirdPartyHttpCfg.getThirdPartyProviderUrl(),
																 thirdPartyHttpCfg.getProxySettings(),
																 thirdPartyHttpCfg.isSupportsMimeMessage());

		} else {
			throw new IllegalStateException("JavaMailSender implementation was NOT configured");
		}
		log.info("Created a {} instance",outJavaMailSender.getClass());
		return outJavaMailSender;
	}
}
