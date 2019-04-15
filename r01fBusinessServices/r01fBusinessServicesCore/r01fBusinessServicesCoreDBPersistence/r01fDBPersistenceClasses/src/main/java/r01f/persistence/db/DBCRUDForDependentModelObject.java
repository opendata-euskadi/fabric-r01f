package r01f.persistence.db;

import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.services.interfaces.CRUDServicesForDependentModelObject;
import r01f.services.interfaces.CRUDServicesForModelObject;

/**
 * Convenience interface to mark DBCRUD implementation of {@link CRUDServicesForModelObject}
 * @param <O>
 * @param <M>
 */
public interface DBCRUDForDependentModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>,
											   P extends PersistableModelObject<?>> 
	     extends CRUDServicesForDependentModelObject<O,M,P> {
	// nothing
}
