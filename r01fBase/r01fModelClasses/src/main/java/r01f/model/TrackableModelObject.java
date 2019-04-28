package r01f.model;

import com.google.common.annotations.GwtIncompatible;

import r01f.model.facets.ModelObjectFacet;

public interface TrackableModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasTrackableFacet
/////////////////////////////////////////////////////////////////////////////////////////
	@GwtIncompatible
	public static interface HasTrackableFacet 
					extends ModelObjectFacet {
		
		public TrackableModelObject asTrackable();
		
		public ModelObjectTracking getTrackingInfo();
		public void setTrackingInfo(ModelObjectTracking trackingInfo);
	}

}