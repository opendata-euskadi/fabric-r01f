package r01f.model.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.exceptions.ExceptionSeverity;

/**
 * core service error codes
 */
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class COREServiceErrorTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	// a server error (ie pool exhausted, no connection, etc)
	public static COREServiceErrorType UNKNOWN = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.UNKNOWN)
																  .withName("UNKNOWN")
																  .coded(1,0)
																  .severity(ExceptionSeverity.FATAL)
																  .build();
	// the request data is not enough to execute the CORE service operation
	public static COREServiceErrorType BAD_CLIENT_REQUEST = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.CLIENT)
																  .withName("BAD_CLIENT_REQUEST")
																  .coded(1,1)
																  .severity(ExceptionSeverity.RECOVERABLE)
																  .build();
	// a server error (ie pool exhausted, no connection, etc)
	public static COREServiceErrorType SERVER_ERROR = COREServiceErrorType.originatedAt(COREServiceErrorOrigin.SERVER)
																  .withName("SERVER_ERROR")
																  .coded(1,2)
																  .severity(ExceptionSeverity.FATAL)
																  .build();
}