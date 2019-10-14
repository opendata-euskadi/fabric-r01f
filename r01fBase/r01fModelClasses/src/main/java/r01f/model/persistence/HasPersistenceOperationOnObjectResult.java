package r01f.model.persistence;

import r01f.model.services.HasCOREServiceMethodExecResult;

/**
 * An object that contains a {@link PersistenceOperationOnObjectResult}
 */
public interface HasPersistenceOperationOnObjectResult<T>
		 extends HasCOREServiceMethodExecResult {
/////////////////////////////////////////////////////////////////////////////////////////
// 	ACCESSOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the persistence operation result
	 */
	public PersistenceOperationOnObjectResult<T> getPersistenceOperationOnObjectResult();
	/**
	 * @return the model object type 
	 */
	public Class<T> getObjectType();
}
