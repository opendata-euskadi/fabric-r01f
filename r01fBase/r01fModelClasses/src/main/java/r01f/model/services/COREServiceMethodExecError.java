package r01f.model.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowable;
import r01f.exceptions.Throwables;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.Strings.StringCustomizerVarsProvider;


@MarshallType(as="coreServiceMethodExecResult",typeId="error")
@Accessors(prefix="_")
public class COREServiceMethodExecError<T>
	 extends COREServiceMethodExecResultBase<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The error type: client / core
	 */
	@MarshallField(as="errorType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected COREServiceErrorType _errorType;
	/**
	 * If it's a bad client request this member stores an error number
	 * that should be used at the client side to present the user with
	 * some useful information about the action to be taken
	 */
	@MarshallField(as="errorCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected int _errorCode;
	/**
	 * Some message about the overall operation, usually used when there's an error
	 */
	@MarshallField(as="message",escape=true)
	@Getter @Setter protected String _errorMessage;
	/**
	 * Contains details about the error, usually a java stack trace
	 */
	@MarshallField(as="debug",escape=true)
	@Getter @Setter protected String _errorDebug;
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT-SERIALIZABLE STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Contains the error in the case that there is a general error that prevents 
	 * the find operation to be executed
	 */
	@MarshallIgnoredField
	@Getter @Setter(AccessLevel.PROTECTED) protected transient Throwable _error;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public COREServiceMethodExecError() {
		this(COREServiceMethod.UNKNOWN);
	}
	public COREServiceMethodExecError(final COREServiceMethod method) {
		super(method);
	}
	public COREServiceMethodExecError(final COREServiceMethod reqOp,
							   		  final Throwable th) {
		this(reqOp,
			 null,		// no error type
			 th);
	}
	public COREServiceMethodExecError(final COREServiceMethod reqOp,
									  final COREServiceErrorType errorType,
							   		  final Throwable th) {
		this(reqOp);
		_error = th;		
		_errorMessage = th != null ? th.getMessage() 
								   : errorType.debugInfo().toString();
		_errorDebug = th != null ? Throwables.getStackTraceAsString(th) : null;
		if (th instanceof EnrichedThrowable) {
			EnrichedThrowable enrichedTh = (EnrichedThrowable)th;
			_errorCode = enrichedTh.getExtendedCode() > 0 ? enrichedTh.getExtendedCode()
														  : null;
			_errorType = errorType != null ? errorType
										   : enrichedTh.getType();
		} else {
			_errorType = errorType != null ? errorType
										   : COREServiceErrorTypes.SERVER_ERROR;		// a server error by default
			
		}
	}
	public COREServiceMethodExecError(final COREServiceMethod reqOp,
							   		  final String errMsg) {
		this(reqOp);
		_errorMessage = errMsg;
		_errorDebug = null;
	}
	public COREServiceMethodExecError(final COREServiceMethod reqOp,
							   		  final COREServiceErrorType errorType,
							   		  final String errMsg) {
		this(reqOp);
		_errorMessage = errMsg;
		_errorType = errorType;
		_errorDebug = null;
	}
	public <E extends COREServiceMethodExecError<?>> COREServiceMethodExecError(final COREServiceMethod reqOp,
																	 		    final E otherError) {
		this(reqOp);
		this.setCalledMethod(otherError.getCalledMethod());
		this.setError(otherError.getError());
		this.setErrorDebug(otherError.getErrorDebug());
		this.setErrorMessage(otherError.getErrorMessage());
		this.setErrorType(otherError.getErrorType());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <E extends Throwable> E getErrorAs(final Class<E> errorType) {
		return (E)_error;
	}
	public void throwAsCOREServiceException() throws COREServiceException {
		throw this.getCOREServiceException();
	}
	public COREServiceException getCOREServiceException() {
		if (_error != null) return new COREServiceException(_calledMethod,
															_error);
		String errorMsg = Strings.isNOTNullOrEmpty(_errorMessage) ? _errorMessage
																  : this.getDetailedMessage();
		COREServiceErrorType errorType = _errorType != null ? _errorType : COREServiceErrorTypes.UNKNOWN;
		return new COREServiceException(_calledMethod,
										errorType,_errorCode,
										errorMsg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean wasBecauseAServerError() {
		return !this.wasBecauseAClientError();
	}
	public boolean wasBecauseAClientError() {
		if (_errorType == null) throw new IllegalStateException(Strings.customized("The {} object does NOT have error info!",
																				   COREServiceMethodExecError.class));
		return _errorType.isClientError();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public T getOrThrow() throws COREServiceException {
		throw this.getCOREServiceException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecError<T> asCOREServiceMethodExecError() {
		return this;
	}
	@Override
	public COREServiceMethodExecOK<T> asCOREServiceMethodExecOK() {
		throw new ClassCastException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getDetailedMessage() {
		String outMsg = Strings.customizedWith("{} operation could NOT be performed because of a {} error{}: {}",
						       				   new StringCustomizerVarsProvider() {
														@Override
														public Object[] provideVars() {
															COREServiceMethodExecError<T> err = COREServiceMethodExecError.this;
															Object[] outVars = new Object[4];
															outVars[0] = err.getCalledMethod();
															outVars[1] = err.wasBecauseAClientError() ? "CLIENT"
																									  : "SERVER";
															if (err.getErrorType() != null) {
																outVars[2] = " (code=" + err.getErrorType().getName() + ")";
																
															} else {
																outVars[2] = "";
															}
															outVars[3] = Strings.isNOTNullOrEmpty(err.getErrorMessage()) ? err.getErrorMessage() 
																					 						     		 : err.getErrorType() != null ? err.getErrorType().toString() : "";
															return outVars;
														}
						       				  });
		return outMsg;
	} 
	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
		outDbgInfo.append(this.getDetailedMessage());
		if (_error != null) {
			outDbgInfo.append("\n")
					  .append(Throwables.getStackTraceAsString(_error));
		}
		return outDbgInfo.toString();
	}
}
