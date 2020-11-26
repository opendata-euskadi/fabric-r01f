package r01f.securitycontext;

import r01f.securitycontext.SecurityOIDs.UserOID;
import r01f.types.contact.Phone;

public interface SecurityContextForPairedPhone {
	public UserOID getUserOid();
	public Phone getPhone();
}
