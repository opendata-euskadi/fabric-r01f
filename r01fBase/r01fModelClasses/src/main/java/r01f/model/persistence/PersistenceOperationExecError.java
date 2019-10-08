package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceErrorTypes;
import r01f.model.services.COREServiceException;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecError;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;


@MarshallType(as="persistenceOperationResult",typeId="error")
@Accessors(prefix="_")
public class PersistenceOperationExecError<T>
	 extends COREServiceMethodExecError<T> 
  implements PersistenceOperationResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceOperationExecError() {
		// default no-args constructor
	}
	public PersistenceOperationExecError(final COREServiceMethod reqOp) {
		super(reqOp);
	}
	public PersistenceOperationExecError(final PersistenceRequestedOperation reqOp) {
		this(reqOp.getCOREServiceMethod());
	}
	PersistenceOperationExecError(final COREServiceMethod reqOp,
								  final Throwable th) {
		super(reqOp,
			  th);
	}
	PersistenceOperationExecError(final PersistenceRequestedOperation reqOp,
								  final Throwable th) {
		super(reqOp.getCOREServiceMethod(),
			  th);
	}
	PersistenceOperationExecError(final COREServiceMethod reqOp,
								  final COREServiceErrorType errType,
								  final Throwable th) {
		super(reqOp,
			  errType,
			  th);
	}
	PersistenceOperationExecError(final PersistenceRequestedOperation reqOp,
								  final COREServiceErrorType errType,
								  final Throwable th) {
		super(reqOp.getCOREServiceMethod(),
			  errType,
			  th);
	}
	PersistenceOperationExecError(final COREServiceMethod reqOp,
								  final String msg) {
		super(reqOp,
			  msg);
	}
	PersistenceOperationExecError(final PersistenceRequestedOperation reqOp,
								  final String msg) {
		super(reqOp.getCOREServiceMethod(),
			  msg);
	}
	PersistenceOperationExecError(final COREServiceMethod reqOp,
								  final COREServiceErrorType errType,
								  final String errMsg) {
		super(reqOp,
			  errType,
			  errMsg);
	}
	PersistenceOperationExecError(final PersistenceRequestedOperation reqOp,
								  final COREServiceErrorType errType,
								  final String errMsg) {
		super(reqOp.getCOREServiceMethod(),
			  errType,
			  errMsg);
	}
	public <E extends PersistenceOperationExecError<?>> PersistenceOperationExecError(final PersistenceRequestedOperation reqOp,
																	 				  final E otherError) {
		this(reqOp);
		this.setCalledMethod(otherError.getCalledMethod());
		this.setError(otherError.getError());
		this.setErrorDebug(otherError.getErrorDebug());
		this.setErrorMessage(otherError.getErrorMessage());
		this.setErrorType(otherError.getErrorType());
		this.setRequestedOperation(otherError.getRequestedOperation());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public T getOrThrow() throws COREServiceException {
		// BEWARE!! override getOrThrow method! otherwise a COREServiceExeception
		//			instance will be thrown
		throw this.getPersistenceException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PersistenceRequestedOperation getRequestedOperation() {
		return PersistenceRequestedOperation.from(_calledMethod);
	}
	public void setRequestedOperation(final PersistenceRequestedOperation calledOp) {
		_calledMethod = calledOp != null ? calledOp.getCOREServiceMethod() : null;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PersistenceOperationExecOK<T> asPersistenceOperationOK() {
		throw new ClassCastException();
	}
	@Override
	public PersistenceOperationExecError<T> asPersistenceOperationError() {
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void throwAsPersistenceException() throws PersistenceException {
		throw this.getPersistenceException();
	}
	public PersistenceException getPersistenceException() {
		String errorMsg = Strings.isNOTNullOrEmpty(_errorMessage) ? _errorMessage
																  : this.getDetailedMessage();
		COREServiceErrorType errorType = _errorType != null ? _errorType : COREServiceErrorTypes.UNKNOWN;
		return new PersistenceException(this.getRequestedOperation(),
										errorType,_errorCode,
										errorMsg);
	} 
/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean wasBecauseClientCouldNotConnectToServer() {
		if (_errorType == null) throw new IllegalStateException(Strings.customized("The {} object instance does NOT have error info!",
																				   this.getClass().getSimpleName()));
		return _errorType.is(PersistenceServiceErrorTypes.CLIENT_CANNOT_CONNECT_SERVER);
	}
}
