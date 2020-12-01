package r01f.securitycontext;

import r01f.types.contact.Phone;

public interface SecurityContextForPairedPhone 
		 extends SecurityContextForHasUserOID {
	public Phone getPhone();
}
