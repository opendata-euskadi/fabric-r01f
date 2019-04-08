package r01f.persistence.db;

import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.services.interfaces.CRUDServicesForVersionableModelObject;

/**
 * Convenience interface to mark DBCRUD implementation of {@link CRUDServicesForVersionableModelObject}
 * @param <O>
 * @param <M>
 */
public interface DBCRUDForVersionableModelObject<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet> 
	     extends DBCRUDForModelObject<O,M>,
	     		 CRUDServicesForVersionableModelObject<O,M> {
	// nothing
}
