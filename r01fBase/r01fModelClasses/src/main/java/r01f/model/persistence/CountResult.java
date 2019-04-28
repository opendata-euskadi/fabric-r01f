package r01f.model.persistence;

import r01f.debug.Debuggable;

public interface CountResult<T>
		 extends PersistenceOperationResult,
		 		 Debuggable {
	/**
	 * Returns the count result or throws a {@link PersistenceException}
	 * if the operation resulted on an error
	 * @return
	 * @throws PersistenceException
	 */
	public Long getOrThrow() throws PersistenceException;
	/**
	 * @return a {@link CountOK} instance
	 */
	public CountOK<T> asCountOK();
	/**
	 * @return a {@link CountError} instance
	 */
	public CountError<T> asCountError();
}
