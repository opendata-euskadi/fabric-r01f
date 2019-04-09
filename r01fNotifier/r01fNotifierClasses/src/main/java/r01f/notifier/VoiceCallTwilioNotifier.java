package r01f.notifier;

import r01f.notifier.VoiceCallNotifierBase.VoiceCallNotifierRequest;
import r01f.notifier.VoiceCallNotifierBase.VoiceCallNotifierResponse;
import r01f.types.contact.Phone;

public class VoiceCallTwilioNotifier 
  implements SingleDestinationNotifierService<Phone,
  											  VoiceCallNotifierRequest,VoiceCallNotifierResponse> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public VoiceCallNotifierResponse notify(final VoiceCallNotifierRequest request) {
		return null;
	}
}
