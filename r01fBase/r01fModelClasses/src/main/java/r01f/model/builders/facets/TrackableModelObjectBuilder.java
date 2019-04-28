package r01f.model.builders.facets;

import java.util.Date;

import r01f.guids.CommonOIDs.UserCode;
import r01f.model.ModelObjectTracking;
import r01f.model.TrackableModelObject.HasTrackableFacet;


public class TrackableModelObjectBuilder<CONTAINER_TYPE,
										 M extends HasTrackableFacet> 
  	 extends FacetBuilderBase<CONTAINER_TYPE,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableModelObjectBuilder(final CONTAINER_TYPE parentBuilder,
									   final M hasTrackableFacet) {
		super(parentBuilder,
			  hasTrackableFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE
/////////////////////////////////////////////////////////////////////////////////////////    
	public TrackableModelObjectBuilderCreatedAtStep createdBy(final UserCode creatorUser) {
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());	// lazy create object
		_modelObject.getTrackingInfo().setCreatorUserCode(creatorUser);
		return new TrackableModelObjectBuilderCreatedAtStep();
	}
	public class TrackableModelObjectBuilderCreatedAtStep {
		public TrackableModelObjectBuilder<CONTAINER_TYPE,M> at(final Date createDate) {
			_modelObject.getTrackingInfo().setCreateDate(createDate);
			return TrackableModelObjectBuilder.this;
		}
		public TrackableModelObjectBuilder<CONTAINER_TYPE,M> now() {
			_modelObject.getTrackingInfo().setCreateDate(new Date());
			return TrackableModelObjectBuilder.this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableModelObjectBuilderLastUpdatedAtStep lastUpdatedBy(final UserCode updateUser) {
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());	// lazy create object
		_modelObject.getTrackingInfo().setModifiedBy(updateUser);
		return new TrackableModelObjectBuilderLastUpdatedAtStep();
	}
	public class TrackableModelObjectBuilderLastUpdatedAtStep {
		public TrackableModelObjectBuilder<CONTAINER_TYPE,M> at(final Date lastUpdateDate) {
			_modelObject.getTrackingInfo().setLastUpdateDate(lastUpdateDate);
			return TrackableModelObjectBuilder.this;
		}
		public TrackableModelObjectBuilder<CONTAINER_TYPE,M> now() {
			_modelObject.getTrackingInfo().setLastUpdateDate(new Date());
			return TrackableModelObjectBuilder.this;
		}
	}
}
