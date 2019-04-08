package r01f.model.persistence;

/**
 * An object that contains a {@link PersistenceOperationOnObjectResult}
 */
public interface HasPersistenceOperationOnObjectResult<T>
		 extends HasPersistenceOperationResult {
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
