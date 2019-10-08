package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceErrorTypes;
import r01f.model.services.COREServiceException;
import r01f.util.types.Strings;

/**
 * An error raised when performing any persistence-related operation
 */
@Accessors(prefix="_")
public class PersistenceException
	 extends COREServiceException {

	private static final long serialVersionUID = -1161648233290893856L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
								   final COREServiceErrorType errorType,final int errorCode,
						 		   final Throwable th) {
		super(requestedOp.getCOREServiceMethod(),
			  errorType,errorCode,
			  th);
	}
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
								   final COREServiceErrorType errorType,
						 		   final Throwable th) {
		super(requestedOp.getCOREServiceMethod(),
			  errorType,-1,
			  th);
	}
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
						 		   final Throwable th) {
		super(requestedOp.getCOREServiceMethod(),
			  th);
	}
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
								   final COREServiceErrorType errorType,final int errorCode,
						 		   final String msg) {
		super(requestedOp.getCOREServiceMethod(),
		      errorType,errorCode,
		      msg);
	}
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
								   final COREServiceErrorType errorType,
						 		   final String msg) {
		super(requestedOp.getCOREServiceMethod(),
			  errorType,-1,
			  msg);
	}
	protected PersistenceException(final PersistenceRequestedOperation requestedOp,
						 		   final String msg) {
		super(requestedOp.getCOREServiceMethod(),
			  msg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isEntityNotFound() {
		return this.is(PersistenceServiceErrorTypes.ENTITY_NOT_FOUND);
	}
	public boolean isRelatedEntityNotFound() {
		return this.is(PersistenceServiceErrorTypes.RELATED_REQUIRED_ENTITY_NOT_FOUND);
	}
	@Override
	public boolean isClientError() {
		return !this.isServerError();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersistenceException serverError(final PersistenceRequestedOperation requestedOp,
												   final Throwable th) {
		return new PersistenceException(requestedOp,
										COREServiceErrorTypes.SERVER_ERROR,
										th);
	}
	public static PersistenceException serverError(final PersistenceRequestedOperation requestedOp,
												   final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										COREServiceErrorTypes.SERVER_ERROR,
										Strings.customized(msg,vars));
	}
	public static PersistenceException badClientRequest(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										COREServiceErrorTypes.BAD_CLIENT_REQUEST,
										(Throwable)null);
	}
	public static PersistenceException badClientRequest(final PersistenceRequestedOperation requestedOp,
												 		final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										COREServiceErrorTypes.BAD_CLIENT_REQUEST,
										Strings.customized(msg,vars));
	}
	public static PersistenceException entityNotFound(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ENTITY_NOT_FOUND,		
										(Throwable)null);
	}
	public static PersistenceException entityNotFound(final PersistenceRequestedOperation requestedOp,
													  final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ENTITY_NOT_FOUND,
										Strings.customized(msg,vars));
	}
	public static PersistenceException entityAlreadyExists(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS,
										(Throwable)null);
	}
	public static PersistenceException entityAlreadyExists(final PersistenceRequestedOperation requestedOp,
														   final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS,
										Strings.customized(msg,vars));
	}
	public static PersistenceException requiredRelatedEntityNotFound(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.RELATED_REQUIRED_ENTITY_NOT_FOUND,
										(Throwable)null);
	}
	public static PersistenceException requiredRelatedEntityNotFound(final PersistenceRequestedOperation requestedOp,
															  		 final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.RELATED_REQUIRED_ENTITY_NOT_FOUND,
										Strings.customized(msg,vars));
	}
	public static PersistenceException notValidEntity(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ENTITY_NOT_VALID,
										(Throwable)null);
	}
	public static PersistenceException notValidEntity(final PersistenceRequestedOperation requestedOp,
											   		  final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ENTITY_NOT_VALID,
										Strings.customized(msg,vars));
	}
	public static PersistenceException illegalStatus(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ILLEGAL_STATUS,
										(Throwable)null);
	}
	public static PersistenceException illegalStatus(final PersistenceRequestedOperation requestedOp,
											  		 final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.ILLEGAL_STATUS,
										Strings.customized(msg,vars));
	}
	public static PersistenceException optimisticLockingError(final PersistenceRequestedOperation requestedOp) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.OPTIMISTIC_LOCKING_ERROR,
										(Throwable)null);
	}
	public static PersistenceException optimisticLockingError(final PersistenceRequestedOperation requestedOp,
											  		   		  final String msg,final Object... vars) {
		return new PersistenceException(requestedOp,
										PersistenceServiceErrorTypes.OPTIMISTIC_LOCKING_ERROR,
										Strings.customized(msg,vars));
	}
}
