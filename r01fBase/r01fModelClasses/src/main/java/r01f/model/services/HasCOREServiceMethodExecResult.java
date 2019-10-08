package r01f.model.services;

/**
 * An object that contains a {@link PersistenceOperationOK}
 */
public interface HasCOREServiceMethodExecResult {
/////////////////////////////////////////////////////////////////////////////////////////
// 	ACCESSOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the persistence operation result
	 */
	public <T> COREServiceMethodExecResult<T> getCOREServiceMethodExecResult();
	/**
	 * @return true if the persistence operation has succeeded
	 */
	public boolean hasSucceeded();
	/**
	 * @return true if the persistence operation has failed
	 */
	public boolean hasFailed();
}
