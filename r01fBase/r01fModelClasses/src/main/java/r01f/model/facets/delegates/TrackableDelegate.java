package r01f.model.facets.delegates;

import r01f.facets.delegates.FacetDelegateBase;
import r01f.model.TrackableModelObject;
import r01f.model.TrackableModelObject.HasTrackableFacet;

public class TrackableDelegate<SELF_TYPE extends HasTrackableFacet> 
	 extends FacetDelegateBase<SELF_TYPE>
  implements TrackableModelObject {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableDelegate(final SELF_TYPE hasTrackableFacet) {
		super(hasTrackableFacet);
	}
}
