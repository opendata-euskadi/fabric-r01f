package r01f.model.metadata;

import r01f.guids.OID;
import r01f.model.ModelObject;

/**
 * Interface for types that describes {@link ModelObject}s
 */
public interface HasMetaDataForPersistableModelObject<O extends OID> 
		 extends HasMetaDataForModelObject,
		 		 HasMetaDataForHasOIDModelObject<O>,
		 		 HasMetaDataForHasEntityVersionModelObject,
		 		 HasMetaDataForHasTrackableFacetForModelObject {
	
	public OID getDOCID();
	
}
