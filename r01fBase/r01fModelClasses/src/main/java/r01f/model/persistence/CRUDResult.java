package r01f.model.persistence;

import r01f.debug.Debuggable;

public interface CRUDResult<T>
		 extends PersistenceOperationResult,
		 		 Debuggable {
	/**
	 * Returns the CRUD operation's target object or throws a {@link PersistenceException}
	 * if the operation resulted on an error
	 * @return
	 * @throws PersistenceException
	 */
	public T getOrThrow() throws PersistenceException;
	/**
	 * @return a {@link CRUDOK} instance
	 */
	public CRUDOK<T> asCRUDOK();
	/**
	 * @return a {@link CRUDError} instance
	 */
	public CRUDError<T> asCRUDError();
}
