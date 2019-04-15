package r01f.services.delegates.persistence;

import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;

/**
 * This interface is intended to be used at {@link CRUDServicesForModelObject} sub-types that completes
 * the model object BEFORE it's created or updated
 * @param <M>
 */
public interface CompletesModelObjectBeforeCreateOrUpdate<M extends PersistableModelObject<? extends OID>> {
	/**
	 * Completes the model object BEFORE being created or updated: gives the service the oportunity to 
	 * add info (complete the object's state) before persisting it
	 * @param securityContext
	 * @param requestedOperation
	 * @param modelObj
	 * @return the completed model object
	 */
	public abstract M completeModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
														   final PersistenceRequestedOperation requestedOp,
														   final M modelObj);
}
