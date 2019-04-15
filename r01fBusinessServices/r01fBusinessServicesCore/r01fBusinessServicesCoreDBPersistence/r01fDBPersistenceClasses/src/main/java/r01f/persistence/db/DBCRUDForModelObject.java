package r01f.persistence.db;

import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.services.interfaces.CRUDServicesForModelObject;

/**
 * Convenience interface to mark DBCRUD implementation of {@link CRUDServicesForModelObject}
 * @param <O>
 * @param <M>
 */
public interface DBCRUDForModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>> 
	     extends CRUDServicesForModelObject<O,M> {
	// nothing
}
