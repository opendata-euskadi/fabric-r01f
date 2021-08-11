package r01f.model.builders.facets;

import java.util.Date;

import r01f.model.ModelObjectTracking;
import r01f.model.TrackableModelObject.HasTrackableFacet;
import r01f.patterns.FactoryFrom;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityOIDs.UserOID;


public class TrackableModelObjectBuilder<NEXT_BUILDER,
										 M extends HasTrackableFacet> 
  	 extends FacetBuilderBase<NEXT_BUILDER,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableModelObjectBuilder(final FactoryFrom<M,NEXT_BUILDER> nextBuilderFactory,
									   final M hasTrackableFacet) {
		super(nextBuilderFactory,
			  hasTrackableFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE
/////////////////////////////////////////////////////////////////////////////////////////    
	public TrackableModelObjectBuilderCreatedAtStep createdBy(final UserOID creatorUserOid,final LoginID creatorUser) {
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());	// lazy create object
		_modelObject.getTrackingInfo().setCreatorUserOid(creatorUserOid);
		_modelObject.getTrackingInfo().setCreatorUserCode(creatorUser);
		return new TrackableModelObjectBuilderCreatedAtStep();
	}
	public class TrackableModelObjectBuilderCreatedAtStep {
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> at(final Date createDate) {
			_modelObject.getTrackingInfo().setCreateDate(createDate);
			return TrackableModelObjectBuilder.this;
		}
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> now() {
			_modelObject.getTrackingInfo().setCreateDate(new Date());
			return TrackableModelObjectBuilder.this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableModelObjectBuilderLastUpdatedAtStep lastUpdatedBy(final UserOID updatorUserOid,final LoginID updatorUser) {
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());	// lazy create object
		_modelObject.getTrackingInfo().setModifiedBy(updatorUserOid,updatorUser);
		return new TrackableModelObjectBuilderLastUpdatedAtStep();
	}
	public class TrackableModelObjectBuilderLastUpdatedAtStep {
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> at(final Date lastUpdateDate) {
			_modelObject.getTrackingInfo().setLastUpdateDate(lastUpdateDate);
			return TrackableModelObjectBuilder.this;
		}
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> now() {
			_modelObject.getTrackingInfo().setLastUpdateDate(new Date());
			return TrackableModelObjectBuilder.this;
		}
	}
}
