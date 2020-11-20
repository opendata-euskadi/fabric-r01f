package r01f.model.facets;

import java.util.Date;

import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityOIDs.UserOID;

public interface HasLastUpdateData {
	/**
	 * Gets the user code of the last updator
	 * @return
	 */
	public LoginID getLastUpdatorUserCode();
	/**
	 * Sets the user code of the last updator (it comes from the user context)
	 * @param userCode
	 */
	public void setLastUpdatorUserCode(final LoginID userCode);
	/**
	 * Gets the user oid of the last updator
	 * @return
	 */
	public UserOID getLastUpdatorUserOid();
	/**
	 * Sets the user oid of the last updatos (it comes from the user context)
	 * @param userOid
	 */
	public void setLastUpdatorUserOid(final UserOID userOid);
	/**
	 * @return the last saving operation timestamp
	 */
	public Date getLastUpdateTimeStamp();
	/**
	 * Sets the last saving operation timestamp
	 * @param newTS the new timestamp
	 */
	public void setLastUpdateTimeStamp(Date newTS);
}
