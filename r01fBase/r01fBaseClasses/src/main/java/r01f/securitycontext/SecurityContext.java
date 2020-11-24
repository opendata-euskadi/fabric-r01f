package r01f.securitycontext;

import java.io.Serializable;
import java.util.Date;

import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.SecurityProviderID;
import r01f.securitycontext.SecurityIDS.SecurityToken;
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
	 * @return true if this is an app user context
	 */
	public boolean isForApp();
	/**
	 * Returns the {@link SecurityContext} as an [app] {@link SecurityContext}
	 * @return
	 */
	public SecurityContextForApp asForApp();
	/**
	 * @return true if this is an user context
	 */
	public boolean isForUser();
	/**
	 * Returns the {@link SecurityContext} as an [user] {@link SecurityContext}
	 * @return
	 */
	public SecurityContextForUser asForUser();
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
}
