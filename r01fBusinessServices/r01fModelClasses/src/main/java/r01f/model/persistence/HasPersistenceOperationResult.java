package r01f.model.persistence;

/**
 * An object that contains a {@link PersistenceOperationResult}
 */
public interface HasPersistenceOperationResult {
/////////////////////////////////////////////////////////////////////////////////////////
// 	ACCESSOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the persistence operation result
	 */
	public PersistenceOperationResult getPersistenceOperationResult();
	/**
	 * @return true if the persistence operation has succeeded
	 */
	public boolean hasSucceeded();
	/**
	 * @return true if the persistence operation has failed
	 */
	public boolean hasFailed();
}
