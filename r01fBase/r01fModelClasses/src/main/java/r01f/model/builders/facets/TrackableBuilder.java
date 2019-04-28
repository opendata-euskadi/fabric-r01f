package r01f.model.builders.facets;

import java.util.Date;

import r01f.guids.CommonOIDs.UserCode;
import r01f.model.ModelObjectTracking;
import r01f.model.TrackableModelObject.HasTrackableFacet;

public class TrackableBuilder<CONTAINER_TYPE,
							  SELF_TYPE extends HasTrackableFacet> 
  	 extends FacetBuilderBase<CONTAINER_TYPE,SELF_TYPE> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableBuilder(final CONTAINER_TYPE parentBuilder,
							final SELF_TYPE hasTrackableFacet) {
		super(parentBuilder,
			  hasTrackableFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API: TRACKING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the creator and the create date to the actual date
	 * @param creator 
	 */
	public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> createdBy(final UserCode creatorUser) {
		if (creatorUser == null) return this;
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());
		_modelObject.getTrackingInfo().setCreatorUserCode(creatorUser);
		return this;
	}
	/**
	 * Sets the last modifier and the last update date to the actual date
	 * @param modifierUser
	 */
	public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> lastUpdatedBy(final UserCode modifierUser) {
		if (_modelObject.getTrackingInfo() == null) {
			return this.createdBy(modifierUser);
		} else if (modifierUser != null) {
			_modelObject.getTrackingInfo()
						.setModifiedBy(modifierUser);
		}
		return this;
	}
    /**
     * Sets the create date
     * @param date
     * @return
     */
    public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> createdIn(final Date date) {
    	if (date == null) return this;
    	if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());
		_modelObject.getTrackingInfo().setCreateDate(date);
		return this;
    }
    /**
     * Sets the last update date
     * @param date
     * @return
     */
    public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> lastUpdatedIn(final Date date) {
    	if (date == null) return this;
    	if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());
		_modelObject.getTrackingInfo().setLastUpdateDate(date);
		return this;
    }
}
