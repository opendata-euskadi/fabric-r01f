package r01f.securitycontext;

import java.io.Serializable;

import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.WorkPlaceCode;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

/**
 * Data about a [user] stored at the {@link SecurityContext}
 * Beware that this object is very similar to the [user] object from the [security system] (google, local users db,...)
 * but it does NOT have to be the same 
 */
public interface SecurityContextUserData 
		 extends Serializable {
	public UserCode getUser();
	public WorkPlaceCode getWorkPlace();
	
	public String getName();
	public String getSurname();
	public String getDisplayName();
	
	public Language getPrefLang();
	
	public EMail getEmail();
	public Phone getPhone();
}
