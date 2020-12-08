package r01f.securitycontext;

import java.io.Serializable;

import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.SecurityProviderID;

public interface SecurityContextAuthenticatedActor 
  		 extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityProviderID getSecurityProviderId();
	public LoginID getLoginId();
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CAST
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isForApp();
	public boolean isForUser();
	public boolean isForRegisteredDevice();
	public boolean isForSystem();
	public boolean isForApiKey();
	
	public <A extends SecurityContextAuthenticatedActor> A as(final Class<A> actorType);
}
