package r01f.model.facets;

import java.util.Date;

import r01f.guids.CommonOIDs.UserCode;

public interface HasLastUpdateData {
	/**
	 * Sets the user code of the last updator (it comes from the user context)
	 * @param userCode
	 */
	public void setLastUpdatorUserCode(final UserCode userCode);
	/**
	 * Gets the user code of the last updator
	 * @return
	 */
	public UserCode getLastUpdatorUserCode();
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
