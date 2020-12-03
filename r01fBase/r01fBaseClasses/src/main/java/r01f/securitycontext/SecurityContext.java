package r01f.securitycontext;

import java.io.Serializable;
import java.util.Date;

import r01f.guids.OID;
import r01f.patterns.FactoryFrom;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.SecurityProviderID;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.securitycontext.SecurityOIDs.UserOID;
import r01f.types.url.Url;

/**
 * Marker interface for every user context data
 */
public interface SecurityContext
		 extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return this object casted to a {@link SecurityContext} impl
	 */
	public <CTX extends SecurityContext> CTX as(final Class<CTX> type);
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
//	USER OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the [security context] has {@link UserOID} info
	 * @return
	 */
	public boolean hasUserOid();
	/**
	 * Returns the {@link SecurityContext} as an object that has {@link UserOID}
	 * @return
	 */
	public SecurityContextForHasUserOID asForHasUserOid();
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
//	USER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return true if this is an user context
	 */
	public boolean isForUser();
	/**
	 * Returns the {@link SecurityContext} as an [user] {@link SecurityContext}
	 * @return
	 */
	public SecurityContextForUser asForUser();
/////////////////////////////////////////////////////////////////////////////////////////
//	PAIRED PHONE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return true f this is a paired phone security context
	 */
	public boolean isForPairedPhone();
	/**
	 * Returns the {@link SecurityContext} as a [phone] {@link SecurityContext}
	 * @return
	 */
	public SecurityContextForPairedPhone asForPairedPhone();
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
}
