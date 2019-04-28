package r01f.model.facets;

import java.util.Date;

import r01f.guids.CommonOIDs.UserCode;

public interface HasCreationData {
	/**
	 * Sets the user code of the creator (it comes from the user context)
	 * @param userCode
	 */
	public void setCreatorUserCode(final UserCode userCode);
	/**
	 * Gets the user code of the creator 
	 * @return
	 */
	public UserCode getCreatorUserCode();
	/**
	 * @return create operation timestamp
	 */
	public Date getCreateTimeStamp();
	/**
	 * Sets the create operation timestamp
	 * @param newTS the new timestamp
	 */
	public void setCreateTimeStamp(Date newTS);
}
