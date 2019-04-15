package r01f.services.delegates.persistence;

import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResults.ObjectValidationResultNOK;
import r01f.validation.ObjectValidationResults.ObjectValidationResultOK;

/**
 * This interface is intended to be used at {@link CRUDServicesForModelObject} sub-types that validates
 * the model object BEFORE it's created or updated
 * @param <M>
 */
public interface ValidatesModelObjectBeforeCreateOrUpdate<M > {
	/**
	 * Validates the model object BEFORE being created or updated
	 * If the model object is NOT valid, it MUST return a {@link ObjectValidationResultNOK} that encapsulates the reason
	 * If the model object is valid, it MUST return a {@link ObjectValidationResultOK}
	 * @param securityContext
	 * @param requestedOperation
	 * @param modelObj
	 * @return a {@link ObjectValidationResult}
	 */
	public abstract ObjectValidationResult<M> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																				   final PersistenceRequestedOperation requestedOp,
															 				  	   final M modelObj);
}
