package r01f.model;

import r01f.guids.CommonOIDs.UserCode;

public interface HasTrackingInfo {
	/**
	 * @param updator
	 */
	public void setLastUpdatorUserCode(final UserCode updator);
	/**
	 * @param creator
	 */
	public void setCreatorUserCode(final UserCode creator);
	/**
	 * @return the {@link ModelObjectTracking} info
	 */
	public ModelObjectTracking getTrackingInfo();
}
