package r01f.model.services;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedRuntimeException;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.types.Strings;

/**
 * An error raised when performing any persistence-related operation
 */
@Accessors(prefix="_")
public class COREServiceException
	 extends EnrichedRuntimeException {

	private static final long serialVersionUID = -1161648233290893856L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The requested operation
	 */
	@MarshallField(as="calledMethod",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected COREServiceMethod _calledMethod;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public COREServiceException(final COREServiceMethod requestedOp,
						 		final Throwable th) {
		this(requestedOp,
			 Strings.customized("CORE service error when executing '{}' method: {}",
					  			 requestedOp,th.getMessage()),
			  th);
	}
	public COREServiceException(final COREServiceMethod requestedOp,
						 		final String msg,final Throwable th) {
		super(msg,
			  th);
		_calledMethod = requestedOp;
	}
	public COREServiceException(final COREServiceMethod requestedOp,
						 		final String msg) {
		super(Strings.customized("Persistence error when executing '{}' method: {}",
		    		  			 requestedOp,
		    		  			 msg));
		_calledMethod = requestedOp;
	}
	public COREServiceException(final COREServiceMethod requestedOp,
						 		final COREServiceErrorType type,final int errorCode,
						 		final String msg) {
		super(type,errorCode,
			  Strings.customized("Persistence error when executing '{}' method: {}",
		    		  			 requestedOp,
		    		  			 msg));
		_calledMethod = requestedOp;
	}
	public COREServiceException(final COREServiceMethod requestedOp,
								final COREServiceErrorType type,final int errorCode,
						 		final Throwable th) {
		this(requestedOp,
			 type,errorCode,
			 Strings.customized("CORE service error when executing '{}' method: {}",
					  			requestedOp,th.getMessage()),
			  th);
	}
	protected COREServiceException(final COREServiceMethod requestedOp,
								   final COREServiceErrorType type,final int errorCode,
						 		   final String msg,
						 		   final Throwable th) {
		super(type,errorCode,
			  msg,
			  th);
		_calledMethod = requestedOp;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean isServerError() {
		COREServiceErrorType type = this.getType();
		return type != null
					? type.isServerError()
					: false;
	}
	public boolean isClientError() {
		COREServiceErrorType type = this.getType();
		return type != null
					? type.isClientError()
					: false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static COREServiceException serverError(final COREServiceMethod requestedOp,
												   final Throwable th) {
		return new COREServiceException(requestedOp,
										COREServiceErrorTypes.SERVER_ERROR,-1,
										th);
	}
	public static COREServiceException serverError(final COREServiceMethod requestedOp,
												   final String msg,final Object... vars) {
		return new COREServiceException(requestedOp,
										COREServiceErrorTypes.SERVER_ERROR,-1,
										Strings.customized(msg,vars));
	}
	public static COREServiceException badClientRequest(final COREServiceMethod requestedOp,
													    final Throwable th) {
		return new COREServiceException(requestedOp,
										COREServiceErrorTypes.BAD_CLIENT_REQUEST,-1,
										th);
	}
	public static COREServiceException badClientRequest(final COREServiceMethod requestedOp,
												 		final String msg,final Object... vars) {
		return new COREServiceException(requestedOp,
										COREServiceErrorTypes.BAD_CLIENT_REQUEST,-1,
										Strings.customized(msg,vars));
	}
}
