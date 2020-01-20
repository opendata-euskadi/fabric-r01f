package r01f.securitycontext;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AuthenticatedActorID;
import r01f.guids.CommonOIDs.SecurityToken;
import r01f.guids.CommonOIDs.TenantID;
import r01f.guids.CommonOIDs.UserCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.url.Url;

/**
 * R01M API {@link SecurityContext} implementation
 */
@Accessors(prefix="_")
public abstract class SecurityContextBase 
           implements SecurityContext {

	private static final long serialVersionUID = 6313898883935383799L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="authActorId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected AuthenticatedActorID _authenticatedActorId;
	/**
	 * Tenant id (tenatA, tenantB, ...)
	 * Allows the database and file system data to be partitioned by this value
	 */
	@MarshallField(as="tenantId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected TenantID _tenantId; 	// TODO the tenant id should come with the authentication data (ie xlnets)
	/**
	 * The date this user context was created
	 */
	@MarshallField(as="createDate",
				   whenXml=@MarshallFieldAsXml(attr=true))	
	@Getter @Setter protected Date _createDate;
	/**
	 * A security token (a jwt, some signed token, etc)
	 * Usually this is used at MASTER security contexts that SHOULD ONLY be created by a legitimated system
	 * (NOT anyone can generate a MASTER security context)
	 * ... in order to ensure that this security context is a legitimate one, the provider that
	 * 	   generates it, signs a token with a private key that only that legitimate provider knows
	 * ... when the token is validated, the signed token can be checked to ensure it was generated by a 
	 *     legitimate system
	 */
	@MarshallField(as="securityToken",escape=true)
	@Getter @Setter protected SecurityToken _securityToken;
	/**
	 * True if this token is for a SYSTEN user (a system internal one with all privileges)
	 * Usually, when true, a [token] is provided 
	 */
	@MarshallField(as="systemUser",
				   whenXml=@MarshallFieldAsXml(attr=true))	
	@Getter @Setter protected boolean _systemUser;
	/**
	 * The login page url
	 */
	@MarshallField(as="loginUrl")
	@Getter @Setter protected Url _loginUrl;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextBase() {
		_createDate = new Date();
	}
	public SecurityContextBase(final AppCode appCode) {
		this(AuthenticatedActorID.forApp(appCode));
	}
	public SecurityContextBase(final AppCode appCode,
							   final SecurityToken securityToken,final boolean master) {
		this(AuthenticatedActorID.forApp(appCode),
			 securityToken,master);
	}
	public SecurityContextBase(final UserCode userCode) {
		this(AuthenticatedActorID.forUser(userCode));
	}
	public SecurityContextBase(final UserCode userCode,
							   final SecurityToken securityToken,final boolean master) {
		this(AuthenticatedActorID.forUser(userCode),
			 securityToken,master);
	}
	public SecurityContextBase(final AuthenticatedActorID authActor) {
		this(authActor,
			 TenantID.DEFAULT);
	}
	public SecurityContextBase(final AuthenticatedActorID authActor,
							   final SecurityToken securityToken,final boolean master) {
		this(authActor,
			 securityToken,master,
			 TenantID.DEFAULT);
	}
	public SecurityContextBase(final AppCode appCode,
						   	   final TenantID tenantId) {
		this(AuthenticatedActorID.forApp(appCode),
			 tenantId);
	}
	public SecurityContextBase(final AppCode appCode,
							   final SecurityToken securityToken,final boolean master,
						   	   final TenantID tenantId) {
		this(AuthenticatedActorID.forApp(appCode),
			 securityToken,master,
			 tenantId);
	}
	public SecurityContextBase(final UserCode userCode,
						   	   final TenantID tenantId) {
		this(AuthenticatedActorID.forUser(userCode),
			 tenantId);
	}
	public SecurityContextBase(final UserCode userCode,
							   final SecurityToken securityToken,final boolean master,
						   	   final TenantID tenantId) {
		this(AuthenticatedActorID.forUser(userCode),
			 securityToken,master,
			 tenantId);
	}
	public SecurityContextBase(final AuthenticatedActorID authActor,
						   	   final TenantID tenantId) {
		this(authActor,
			 null,false,	// no token / not master
			 tenantId);
	}
	public SecurityContextBase(final AuthenticatedActorID authActor,
							   final SecurityToken securityToken,final boolean master,
						   	   final TenantID tenantId) {
		this(securityToken,master,
			 tenantId);
		_authenticatedActorId = authActor;
	}
	public SecurityContextBase(final SecurityToken securityToken,final boolean master) {
		this(securityToken,master,
			 TenantID.DEFAULT);
	}
	public SecurityContextBase(final SecurityToken securityToken,final boolean master,
							   final TenantID tenantId) {
		this();
		_securityToken = securityToken;
		_systemUser = master;
		_tenantId = tenantId;
		_createDate = new Date();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public UserCode getUserCode() {
		UserCode outUserCode = null;
		if (_authenticatedActorId != null) {
//			if (this.isForApp()) throw new IllegalArgumentException("The user context is NOT for a user (it's an app)");
			outUserCode = UserCode.forAuthenticatedUserId(_authenticatedActorId);
		} 
		return outUserCode;
	}
	@Override
	public AppCode getAppCode() {
		AppCode outUserCode = null;
		if (_authenticatedActorId != null) {
			if (this.isForUser()) throw new IllegalArgumentException("The user context is NOT for an app (it's a user context)");
			outUserCode = AppCode.forAuthenticatedUserId(_authenticatedActorId);
		} 
		return outUserCode;
	}
	@Override
	public boolean isForApp() {
		return this.getAuthenticatedActorId() != null ? this.getAuthenticatedActorId().isApp()
													  : false;
	}
	@Override
	public boolean isForUser() {
		return this.getAuthenticatedActorId() != null ? this.getAuthenticatedActorId().isUser()
													  : false;		
	}
	@Override
	public boolean isAnonymousUser() {
		return this.isForUser()							// it's an user login 
		    && this.getUserCode().isAnonymous();		// and it's anonymous
	}
	@Override @Deprecated	// use isSystemUser()
	public boolean isMasterUser() {
		return this.isSystemUser();
	}
	@Deprecated	// use setSystemUser()
	public void setMasterUser(final boolean master) {
		this.setSystemUser(master);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <CTX extends SecurityContext> CTX as(final Class<CTX> type) {
		return (CTX)this;
	}
}
