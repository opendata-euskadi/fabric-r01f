package r01f.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import lombok.RequiredArgsConstructor;
import r01f.exceptions.Throwables;
import r01f.model.persistence.PersistenceErrorType;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceRequestedOperation;

/**
 * {@link ExceptionMapper}(s) used to map {@link Exception}s to {@link Response}s
 * 
 * <pre>
 * IMPORTANT!	Do NOT forget to include this types at the getClasses() method of {@link {AppCode}RESTApp} type
 * </pre>
 */
public class RESTExceptionMappers {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public abstract static class RESTPersistenceExceptionMapper 
	         		  implements ExceptionMapper<PersistenceException> {
		
		@Override
		public Response toResponse(final PersistenceException persistenceException) {			
			Response outResponse = _handleThrowable(persistenceException);
			return outResponse;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class RESTUncaughtExceptionMapper 
	         		  implements ExceptionMapper<Throwable> {
		@Override
		public Response toResponse(final Throwable th) {
			return _handleThrowable(th);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Maps an exception to an {@link HttpResponse}
	 * The exception is built back at client side type: r01f.services.client.servicesproxy.rest.RESTResponseToCRUDResultMapperForModelObject
	 * @param th
	 * @return
	 */
	private static Response _handleThrowable(final Throwable th) {
		// Print stack trace before any treatment (cause this could fail and mask the original Exception!!!!!) 
		th.printStackTrace();
		// serialize 
		Response outResponse = null;
		// Persistence exceptions
		if (th instanceof PersistenceException) {
			PersistenceException persistEx = (PersistenceException)th;
			// server errors
			if (persistEx.getPersistenceErrorType()
						 .isServerError()) {			// Server Error
				// force exception stack trace print
				outResponse = Response.status(Status.INTERNAL_SERVER_ERROR)
									  .header("x-r01-errorCode",PersistenceErrorType.SERVER_ERROR)
									  .header("x-r01-extErrorCode",persistEx.getExtendedCode())
									  .header("x-r01-errorMessage",persistEx.getMessage())
									  .header("x-r01-requestedOperation",persistEx.getRequestedOperation())
									  .header("x-r01-errorType",persistEx.getClass().getName())
									  .entity(Throwables.getStackTraceAsString(th))
									  .type(MediaType.TEXT_HTML)
									  .build();
			} 
			// client errors
			else if (persistEx.getPersistenceErrorType()
						 	  .isClientError()) {	
				// record not found
				if (persistEx.getPersistenceErrorType() == PersistenceErrorType.ENTITY_NOT_FOUND) {		
					outResponse = Response.status(Status.NOT_FOUND)						
										  .header("x-r01-errorCode",persistEx.getPersistenceErrorType())
										  .header("x-r01-extErrorCode",persistEx.getExtendedCode())
										  .header("x-r01-errorMessage",persistEx.getMessage())
										  .header("x-r01-requestedOperation",persistEx.getRequestedOperation())
										  .header("x-r01-errorType",persistEx.getClass().getName())
										  .entity(Throwables.getStackTraceAsString(th))
										  .type(MediaType.TEXT_HTML)
										  .build();		
				} 
				// update requested but record existed OR the server version is different (optimistic locking)
				else if (persistEx.getRequestedOperation().isIn(PersistenceRequestedOperation.UPDATE,
																PersistenceRequestedOperation.CREATE)
				      && persistEx.getPersistenceErrorType().isIn(PersistenceErrorType.ENTITY_ALREADY_EXISTS,
				    		  								      PersistenceErrorType.OPTIMISTIC_LOCKING_ERROR)) {	
					outResponse = Response.status(Status.CONFLICT)
										  .header("x-r01-errorCode",persistEx.getPersistenceErrorType())
										  .header("x-r01-extErrorCode",persistEx.getExtendedCode())
										  .header("x-r01-errorMessage",persistEx.getMessage())
										  .header("x-r01-requestedOperation",persistEx.getRequestedOperation())
										  .header("x-r01-errorType",persistEx.getClass().getName())
										  .entity(Throwables.getStackTraceAsString(th))
										  .type(MediaType.TEXT_HTML)
										  .build();						
				}
				// another bad client request
				else {															
					outResponse = Response.status(Status.BAD_REQUEST)
										  .header("x-r01-errorCode",persistEx.getPersistenceErrorType())
										  .header("x-r01-extErrorCode",persistEx.getExtendedCode())
										  .header("x-r01-errorMessage",persistEx.getMessage())
										  .header("x-r01-requestedOperation",persistEx.getRequestedOperation())
										  .header("x-r01-errorType",persistEx.getClass().getName())
										  .entity(Throwables.getStackTraceAsString(th))
										  .type(MediaType.TEXT_HTML)
										  .build();
				}
			}
		}
		// Illegal argument exception
		else if (th instanceof IllegalArgumentException) {
			IllegalArgumentException illArgEx = (IllegalArgumentException)th;
			outResponse = Response.status(Status.BAD_REQUEST)
								  .header("x-r01-errorCode",PersistenceErrorType.BAD_REQUEST_DATA)
								  .header("x-r01-errorMessage",illArgEx.getMessage())
								  .header("x-r01-errorType",illArgEx.getClass().getName())
								  .entity(Throwables.getStackTraceAsString(illArgEx))
								  .type(MediaType.TEXT_HTML)
								  .build();
		}
		// any other exception type
		else {
			//th.printStackTrace();
			outResponse = Response.status(Status.INTERNAL_SERVER_ERROR)
								  .header("x-r01-errorCode",PersistenceErrorType.SERVER_ERROR)
								  .header("x-r01-errorMessage",th.getMessage())
								  .header("x-r01-errorType",th.getClass().getName())
								  .entity(Throwables.getStackTraceAsString(th))
								  .type(MediaType.TEXT_HTML)
								  .build();
		}
		return outResponse;
	}
}
