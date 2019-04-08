package r01f.securitycontext;

import java.io.Serializable;
import java.util.Date;

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.SecurityToken;
import r01f.guids.CommonOIDs.UserCode;

/**
 * Marker interface for every user context data
 */
public interface SecurityContext 
		 extends Serializable {
	
	/**
	 * @return this object casted to a {@link SecurityContext} impl
	 */
	public <CTX extends SecurityContext> CTX cast();
	/**
	 * If this user context is for a physical user, it returns his/her user code
	 * if it's an app user context it throws an {@link IllegalStateException}
	 * @return
	 */
	public UserCode getUserCode();
	/**
	 * If this user context is for an app, it returns the appCode
	 * if it's an user context it throws an {@link IllegalStateException}
	 * @return
	 */
	public AppCode getAppCode();
	/**
	 * @return true if this is an app user context
	 */
	public boolean isForApp();
	/**
	 * @return true if this is an user context
	 */
	public boolean isForUser();
	/**
	 * @return true if the authenticated user is anonymous
	 */
	public boolean isAnonymousUser();
	/**
	 * @return true if the security context is for the MASTER user (an internal user will all privileges)
	 */
	public boolean isMasterUser();
	/**
	 * @return the Date when this user context was created
	 */
	public Date getCreateDate();
	/**
	 * @return a {@link SecurityToken} associated with this context
	 */
	public SecurityToken getSecurityToken();
}
