package r01f.twilio;


import javax.inject.Provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides a {@link TwilioService} using a properties file info
 * The properties file MUST contain a config section like:
 * <pre class='xml'>
 *		<twilio>
 *			<accountSID>xxx</accountSID>
 *			<authToken>yyy</authToken>
 *			<voicePhoneNumber>+34538160343</voicePhoneNumber>
 *			<messagingPhoneNumber>+34538160343</messagingPhoneNumber>
 *		</twilio>
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class TwilioServiceProvider 
  implements Provider<TwilioService> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final TwilioConfig _config;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public TwilioService get() {	
		
		// Create the service
		TwilioService outTwilioCallService = new TwilioService(_config);
		if (_config.isDisabled()) outTwilioCallService.setDisabled();
		
		log.info("Created a {} instance",outTwilioCallService.getClass());
		return outTwilioCallService;
	}
}
