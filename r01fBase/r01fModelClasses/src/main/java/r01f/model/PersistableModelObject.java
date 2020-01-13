package r01f.model;

import r01f.facets.HasOID;
import r01f.guids.PersistableObjectOID;
import r01f.model.TrackableModelObject.HasTrackableFacet;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;




/**
 * Interface for model objects that can be persisted in some kind of storage
 */
public interface PersistableModelObject<O extends PersistableObjectOID>
		 extends PersistableObject,			// can have facets (it's persistable)
 		 	     HasNumericID,				// all persistable model objects MUST have a numeric oid
 		 	     HasOID<O>,					// all persistable model objects MUST have an OID
 		 	     HasEntityVersion,			// all persistable model objects are persisted assuming persistence conflicts are unlike to happen (optimistic locking)
 		 	     HasTrackableFacet {		// all persistable model object has create / update dates and creator / last updator user code
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
