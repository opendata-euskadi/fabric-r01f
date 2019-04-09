package r01f.twilio;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.config.ContainsConfigData;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.Password;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpClientProxySettings;
import r01f.twilio.TwilioService.TwilioAPIClientID;
import r01f.twilio.TwilioService.TwilioAPIData;
import r01f.types.contact.Phone;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class TwilioConfig 
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final TwilioAPIData _apiData;
	@Getter private final HttpClientProxySettings _proxySettings;
	@Getter private final boolean _disabled;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public TwilioConfig(final TwilioAPIData apiData,
						final HttpClientProxySettings proxySettings,
						final boolean disabled) {
		_proxySettings = proxySettings;
		_apiData = apiData;
		_disabled = disabled;
	}
	public static TwilioConfig createFrom(final XMLPropertiesForAppComponent xmlProps) {
		return TwilioConfig.createFrom(xmlProps,
									   "twilio");
	}
	public static TwilioConfig createFrom(final XMLPropertiesForAppComponent xmlProps,
										  final String propsRootNode) {
		// ensure the root node
		String thePropsRootNode = Strings.isNullOrEmpty(propsRootNode) ? "twilio" : propsRootNode;
		
		boolean disableTwilio = false;
		
		// Test proxy connection to see if proxy is needed
		HttpClientProxySettings proxySettings = null;
		try {
			proxySettings = HttpClient.guessProxySettings(xmlProps,
														  thePropsRootNode);
		} catch(Throwable th) {
			log.error("Error while guessing the proxy settings to use Twilio: {}",th.getMessage(),th);
			disableTwilio = true;	// the mail sender cannot be used
		}
				
		// Get the twilio api info from the properties file
		TwilioAPIData apiData = TwilioConfig.apiDataFromProperties(xmlProps,
																   thePropsRootNode);
		
		// return the config
		return new TwilioConfig(apiData,
								proxySettings,
							    disableTwilio);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static TwilioAPIData apiDataFromProperties(final XMLPropertiesForAppComponent props,
											   final String propsRootNode) {
		String accountSID = props.propertyAt(propsRootNode + "/twilio/accountSID")
								 .asString();
		String authToken  = props.propertyAt(propsRootNode + "/twilio/authToken")
								 .asString();
		String twilioVoicePhoneNumber = props.propertyAt(propsRootNode + "/twilio/voicePhoneNumber")
											 .asString();
		String twilioMessagingPhoneNumber = props.propertyAt(propsRootNode + "/twilio/messagingPhoneNumber")
										    	 .asString();
		
		// Check
		if (accountSID == null || authToken == null) {
			throw new IllegalStateException(Throwables.message("Cannot configure Twilio API: the properties file does NOT contains a the accountSID / authToken at {} in {} properties file",
															   propsRootNode + "/twilio/accountSID|authToken",props.getAppCode()));
		}
		if (Strings.isNullOrEmpty(twilioVoicePhoneNumber) && Strings.isNullOrEmpty(twilioMessagingPhoneNumber)) {
			throw new IllegalStateException(Throwables.message("Cannot configure Twilio API: there's neither a voice-enabled twilio phone number nor a messaging-enabled twilio phone number configured at {} in {} properties file",
															   propsRootNode + "/twilio/voicePhoneNumber|messagingPhoneNumber",props.getAppCode()));
		}
		if (Strings.isNullOrEmpty(twilioVoicePhoneNumber)) log.warn("There's NO voice-enabled twilio phone number configured at {} in {} properties file: VOICE CALLS ARE NOT ENABLED!",
																	propsRootNode + "/twilio/voicePhoneNumber",props.getAppCode());
		if (Strings.isNullOrEmpty(twilioMessagingPhoneNumber)) log.warn("There's NO messaging-enabled twilio phone number configured at {} in {} properties file: MESSAGING IS NOT ENABLED!",
																		propsRootNode + "/twilio/messagingPhoneNumber",props.getAppCode());
		
		// Create the Twilio service
		TwilioAPIData apiData = new TwilioAPIData(TwilioAPIClientID.of(accountSID),Password.forId(authToken),
												  Strings.isNOTNullOrEmpty(twilioVoicePhoneNumber) ? Phone.of(twilioVoicePhoneNumber) : null,
												  Strings.isNOTNullOrEmpty(twilioMessagingPhoneNumber) ? Phone.of(twilioMessagingPhoneNumber) : null);
		return apiData;
	}
}
