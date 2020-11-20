package r01f.securitycontext;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.SecurityProviderID;
import r01f.securitycontext.SecurityOIDs.UserOID;

@MarshallType(as="authenticatedActor")
@Accessors(prefix="_")
public class SecurityContextAuthenticatedActor 
  implements Serializable {
	private static final long serialVersionUID = -7186228864961079493L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="securityProviderId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final SecurityProviderID _securityProviderId;
	
	@MarshallField(as="loginId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final LoginID _loginId;
	
	@MarshallField(as="userOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final UserOID _userOid;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextAuthenticatedActor(@MarshallFrom("securityProviderId") final SecurityProviderID securityProviderId,@MarshallFrom("loginId") final LoginID loginId,
											 @MarshallFrom("userOid") final UserOID userOid) {
		_securityProviderId = securityProviderId;
		_loginId = loginId;
		
		_userOid = userOid;
	}
	public static SecurityContextAuthenticatedActor forSystemUserLogin() {
		return SecurityContextAuthenticatedActor.forUserLogin(SecurityProviderID.SYSTEM,LoginID.SYSTEM,
															  null);	// no user oid
	}
	public static SecurityContextAuthenticatedActor forUserLogin(final SecurityProviderID providerId,final LoginID id,
											 			  		 final UserOID userOid) {
		return new SecurityContextAuthenticatedActor(providerId,id,
													 userOid);
	}
	public static SecurityContextAuthenticatedActor forAppLogin(final AppCode appCode) {
		return new SecurityContextAuthenticatedActor(SecurityProviderID.SYSTEM,LoginID.fromAppCode(appCode),
													 null);		// app login = no user
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean isApp() {
		return _userOid == null
			&& _loginId.isNOT(LoginID.SYSTEM);	// when system login userOid = null
	}
	public boolean isUser() {
		return !this.isApp();
	}
	public boolean isSystem() {
		return _loginId.isSystem();
	}
}
