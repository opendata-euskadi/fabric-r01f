package r01f.securitycontext;

import java.io.Serializable;
import java.util.Date;

import com.google.common.reflect.TypeToken;

import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.OID;
import r01f.patterns.FactoryFrom;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.SecurityProviderID;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.securitycontext.SecurityIDS.UserRole;
import r01f.securitycontext.SecurityOIDs.UserOID;
import r01f.types.url.Url;

/**
 * Marker interface for every user context data
 */
public interface SecurityContext
		 extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//	CAST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return this object casted to a {@link SecurityContext} impl
	 */
	public <CTX extends SecurityContext> CTX as(final Class<CTX> type);
	/**
	 * @return the {@link SecurityContextAuthenticatedActor} casted
	 */
	public <A extends SecurityContextAuthenticatedActor> A getAuthenticatedActorAs(final Class<A> actorType);
	/**
	 * @return the {@link SecurityContextAuthenticatedActor} casted
	 */
	public <A extends SecurityContextAuthenticatedActor> A getAuthenticatedActorAs(final TypeToken<A> actorType);
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the [security provider] that created this [security context]
	 * @return
	 */
	public SecurityProviderID getSecurityProviderId();
	/**
	 * The login id
	 * @return
	 */
	public LoginID getLoginId();
	/**
	 * @return the Date when this user context was created
	 */
	public Date getCreateDate();
	/**
	 * @return a {@link SecurityToken} associated with this context
	 */
	public SecurityToken getSecurityToken();
	/**
	 * @return the login url
	 */
	public Url getLoginUrl();
/////////////////////////////////////////////////////////////////////////////////////////
//	USER ROLES (just for USER authenticated actors)
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the user has the given system-wide role at the given component
	 * @param appComp
	 * @param role
	 * @return
	 */
	public boolean userHasSystemWideRoleIn(final AppComponent appComp,
									   	   final UserRole role);
	/**
	 * Returns true if the user has ANY system-wide role at the given component
	 * @param module
	 * @return
	 */
	public boolean userHasAnySystemWideRoleIn(final AppComponent module);
	/**
	 * Returns the user's system-wide role (if any) at the given component
	 * @param module
	 * @return
	 */
	public UserRole getUserSystemWideRoleIn(final AppComponent module);
/////////////////////////////////////////////////////////////////////////////////////////
//	USER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * true if this is a user
	 * @return
	 */
	public boolean isForUser();
	/**
	 * Returns the {@link SecurityContext} as an object that has {@link UserOID}
	 * @return
	 */
	public SecurityContextForUser asForUser();
/////////////////////////////////////////////////////////////////////////////////////////
//	API KEY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if this is an api key context
	 * @return
	 */
	public boolean isForApiKey();
	/**
	 * Returns the {@link SecurityContext} as an [api key] {@link SecurityContext}
	 * @return
	 */
	public SecurityContextForApiKey asForApiKey();
/////////////////////////////////////////////////////////////////////////////////////////
//	APP
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if this is an app user context
	 */
	public boolean isForApp();
	/**
	 * Returns the {@link SecurityContext} as an [app] {@link SecurityContext}
	 * @return
	 */
	public SecurityContextForApp asForApp();
/////////////////////////////////////////////////////////////////////////////////////////
//	REGISTERED DEVICE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if this is a [registered device]
	 */
	public boolean isForRegisteredDevice();
	/**
	 * Returns the {@link SecurityContext} as a [registered device] {@link SecurityContext}
	 * @return
	 */
	public <O extends OID> SecurityContextForRegisteredDevice<O> asForRegisteredDevice(final FactoryFrom<LoginID,O> deviceOidFactory);
/////////////////////////////////////////////////////////////////////////////////////////
//	SYSTEM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if this is a SYSTEM context
	 */
	public boolean isForSystem();
}
