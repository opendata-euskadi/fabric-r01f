package r01f.model;

import r01f.model.facets.HasCreationData;
import r01f.model.facets.HasLastUpdateData;

public interface HasTrackingInfo 
		 extends HasCreationData,HasLastUpdateData {
	/**
	 * @return the {@link ModelObjectTracking} info
	 */
	public ModelObjectTracking getTrackingInfo();
}
