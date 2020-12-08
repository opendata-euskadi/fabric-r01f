package r01f.securitycontext;

import r01f.guids.OID;

public interface SecurityContextForRegisteredDevice<O extends OID> {
	public O getDeviceOid();
}
