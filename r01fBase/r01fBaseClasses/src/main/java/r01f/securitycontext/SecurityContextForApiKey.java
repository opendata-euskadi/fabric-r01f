package r01f.securitycontext;

import r01f.securitycontext.SecurityOIDs.LoginOID;
import r01f.types.AppVersion;

public interface SecurityContextForApiKey {
	public LoginOID getApiKeyOid();
	public AppVersion getApiKeyVersion();
}
