package r01f.notifier;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.notifier.VoiceCallNotifierBase.VoiceCallNotifierRequest;
import r01f.notifier.VoiceCallNotifierBase.VoiceCallNotifierResponse;
import r01f.types.contact.Phone;

public abstract class VoiceCallNotifierBase 
		      extends NotifierServiceBase<Phone>
           implements SingleDestinationNotifierService<Phone,
  											  		   VoiceCallNotifierRequest,VoiceCallNotifierResponse> {

/////////////////////////////////////////////////////////////////////////////////////////
//  VoiceCall Request
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public class VoiceCallNotifierRequest
		 extends NotifierRequestBase 
	  implements SingleDestinationNotifierRequest<Phone> {
		@Getter @Setter private Phone _to;		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VoiceCall Response
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public class VoiceCallNotifierResponse 
		 extends NotifierResponseBase
	  implements NotifierResponse<Phone> {
		// nothing
	}
}
