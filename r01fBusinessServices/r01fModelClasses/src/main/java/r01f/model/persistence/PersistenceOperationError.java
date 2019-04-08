package r01f.model.persistence;

/**
 * An interface for a persistence operation that could not be completed successfully
 */
public interface PersistenceOperationError 
		 extends PersistenceOperationResult {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isCRUDError();
	public boolean isFindError();
	public boolean isFindSummariesError();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the underlying error
	 */
	public Throwable getError();
	/**
	 * returns the underlying erro as a concrete {@link Throwable} type
	 * @param errorType
	 * @return
	 */
	public <E extends Throwable> E getErrorAs(final Class<E> errorType);
	/**
	 * @return a brief resume of the error
	 */
	public String getErrorMessage();
	/**
	 * @return some detailed message about the error
	 */
	public String getErrorDebug();
	/**
	 * @return the error type on a pre-defined typology basis
	 */
	public PersistenceErrorType getErrorType();
	/**
	 * @return An application-specific extended code that provides additional information  
	 * 		   to what _errorType gives 
	 */
	public int getExtendedErrorCode();
/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if it was a server error
	 */
	public boolean wasBecauseAServerError();
	/**
	 * @return true if it was a client error
	 */
	public boolean wasBecauseAClientError();
	/**
	 * @return true if the client could NOT connect to server
	 */
	public boolean wasBecauseClientCouldNotConnectToServer();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Throws this object as an exception
	 * @throws PersistenceException
	 */
	public void throwAsPersistenceException() throws PersistenceException;
	/**
	 * Gets a {@link PersistenceException} from this object
	 * @return
	 */
	public PersistenceException getPersistenceException();
}
