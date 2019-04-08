package r01f.persistence.db;

import r01f.model.ModelObject;
import r01f.securitycontext.SecurityContext;

/**
 * This interface is intended to be used at DAO-level and ensures that some immutable object's properties 
 * are NOT changed when performing an UPDATE operation
 * @param <M>
 */
public interface ChecksChangesInModelObjectIimmutableFieldsBeforeUpdate<M extends ModelObject> {
	/**
	 * Inspects the updated object to check if any of the immutable properties is 
	 * changed from the stored object's ones 
	 * @param securityContext
	 * @param actualObj the current stored object's data
	 * @param updatedObj the object whose data will replace the currently stored ones
	 * @return true if any immutable property is changed
	 */
	public abstract boolean isAnyImmutablePropertyChanged(final SecurityContext securityContext,
														  final M actualObj,final M updatedObj);
}
