package r01f.model.persistence;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedRuntimeException;
import r01f.util.types.Strings;

/**
 * An error raised when performing any persistence-related operation
 */
@Accessors(prefix="_")
public class PersistenceException
	 extends EnrichedRuntimeException {

	private static final long serialVersionUID = -1161648233290893856L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The client's requested operation
	 */
	@Getter private final PersistenceRequestedOperation _requestedOperation;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
						 		   final Throwable th) {
		super(PersistenceErrorType.class,
			  Strings.customized("Persistence error when executing a {} operation: {}",
					  			 requestedOp,th.getMessage()),
			  th,
			  PersistenceErrorType.SERVER_ERROR);
		_requestedOperation = requestedOp;
	}
//	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
//						 		   final String msg,
//						 		   final PersistenceErrorType errorType,final int extendedCode) {
//		super(PersistenceErrorType.class,
//		      Strings.customized("Persistence error when executing a {} operation: {}",
//		    		  			 requestedOp,msg),
//		      errorType,extendedCode);
//		_requestedOperation = requestedOp;
//	}
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,String requestedOpName,
						 		   final String msg,
						 		   final PersistenceErrorType errorType,final int extendedCode) {
		super(PersistenceErrorType.class,
		      Strings.customized("Persistence error when executing a {} ({}: {})",
		    		  			 requestedOp,requestedOpName != null ? requestedOpName 
		    		  					 							 : requestedOp != null ? requestedOp.name() : "",
		    		  			 msg),
		      errorType,extendedCode);
		_requestedOperation = requestedOp;
	}
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
						 		   final String msg,
						 		   final PersistenceErrorType errorType) {
		this(requestedOp,null,
			 msg,
			 errorType,-1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceErrorType getPersistenceErrorType() {
		return PersistenceErrorType.from(_code);
	}
	public boolean isEntityNotFound() {
		return this.is(PersistenceErrorType.ENTITY_NOT_FOUND);
	}
	public boolean isRelatedEntityNotFound() {
		return this.is(PersistenceErrorType.RELATED_REQUIRED_ENTITY_NOT_FOUND);
	}
	public boolean isServerError() {
		return this.is(PersistenceErrorType.SERVER_ERROR);
	}
	public boolean isClientError() {
		return !this.isServerError();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersistenceException serverError(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										null,		// no message
										PersistenceErrorType.SERVER_ERROR);
	}
	public static PersistenceException serverError(final PersistenceRequestedOperation requestedOp,
												   final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.SERVER_ERROR);
	}
	public static PersistenceException serverError(final PersistenceRequestedOperation requestedOp,
												   final Throwable th) {
		return new PersistenceException(requestedOp,
										th);
	}
	public static PersistenceException badClientRequest(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceErrorType.BAD_REQUEST_DATA.name(),		// no message
										PersistenceErrorType.BAD_REQUEST_DATA);
	}
	public static PersistenceException badClientRequest(final PersistenceRequestedOperation requestedOp,
												 		final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.BAD_REQUEST_DATA);
	}
	public static PersistenceException entityNotFound(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceErrorType.ENTITY_NOT_FOUND.name(),
										PersistenceErrorType.ENTITY_NOT_FOUND);
	}
	public static PersistenceException entityNotFound(final PersistenceRequestedOperation requestedOp,
													  final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.ENTITY_NOT_FOUND);
	}
	public static PersistenceException entityAlreadyExists(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceErrorType.ENTITY_ALREADY_EXISTS.name(),
										PersistenceErrorType.ENTITY_ALREADY_EXISTS);
	}
	public static PersistenceException entityAlreadyExists(final PersistenceRequestedOperation requestedOp,
														   final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.ENTITY_ALREADY_EXISTS);
	}
	public static PersistenceException requiredRelatedEntityNotFound(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceErrorType.RELATED_REQUIRED_ENTITY_NOT_FOUND.name(),
										PersistenceErrorType.RELATED_REQUIRED_ENTITY_NOT_FOUND);
	}
	public static PersistenceException requiredRelatedEntityNotFound(final PersistenceRequestedOperation requestedOp,
															  		 final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.RELATED_REQUIRED_ENTITY_NOT_FOUND);
	}
	public static PersistenceException notValidEntity(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceErrorType.ENTITY_NOT_VALID.name(),
										PersistenceErrorType.ENTITY_NOT_VALID);
	}
	public static PersistenceException notValidEntity(final PersistenceRequestedOperation requestedOp,
											   		  final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.ENTITY_NOT_VALID);
	}
	public static PersistenceException illegalStatus(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceErrorType.ILLEGAL_STATUS.name(),
										PersistenceErrorType.ILLEGAL_STATUS);
	}
	public static PersistenceException illegalStatus(final PersistenceRequestedOperation requestedOp,
											  		 final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.ILLEGAL_STATUS);
	}
	public static PersistenceException optimisticLockingError(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceErrorType.OPTIMISTIC_LOCKING_ERROR.name(),
										PersistenceErrorType.OPTIMISTIC_LOCKING_ERROR);
	}
	public static PersistenceException optimisticLockingError(final PersistenceRequestedOperation requestedOp,
											  		   		  final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										Strings.customized(msg,vars),
										PersistenceErrorType.OPTIMISTIC_LOCKING_ERROR);
	}
}
