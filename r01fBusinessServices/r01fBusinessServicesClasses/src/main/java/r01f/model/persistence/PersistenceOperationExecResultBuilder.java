package r01f.model.persistence;

import com.google.common.annotations.GwtIncompatible;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;

/**
 * Builder type for {@link PersistenceOperationExecResult}-implementing types:
 * <ul>
 * 		<li>A successful operation execution result: {@link PersistenceOperationExecOK}</li>
 * 		<li>An error on a FIND operation execution: {@link PersistenceOperationExecError}</li>
 * </ul>
 * If the operation execution was successful:
 * <pre class='brush:java'>
 * 		PersistenceOperationExecOK<MyReturnedObjType> opOK = PersistenceOperationExecResultBuilder.using(securityContext)
 * 																	   			   				  .executed("an operation")
 * 																								  .returning(myReturnedObjTypeInstance);
 * </pre>
 * If an error is raised while executing the persistence operation:
 * <pre class='brush:java'>
 * 		PersistenceOperationExecError<MyReturnedObjType> opError = PersistenceOperationExecResultBuilder.using(securityContext)
 *			  																			 		    	.notExecuted("an operation")
 *			  																								.because(error);
 * </pre>
 */
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class PersistenceOperationExecResultBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static PersistenceOperationExecResultBuilderResultStep using(final SecurityContext securityContext) {
		return new PersistenceOperationExecResultBuilder() {/* nothing */ }
						.new PersistenceOperationExecResultBuilderResultStep(securityContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersistenceOperationExecResultBuilderResultStep {
		private final SecurityContext _securityContext;
		
		public PersistenceOperationExecResultBuilderReturnedObjStep executed(final String requestedOpName) {
			return new PersistenceOperationExecResultBuilderReturnedObjStep(_securityContext,
																			requestedOpName);
		}
		public PersistenceOperationExecResultBuilderErrorStep notExecuted(final String requestedOpName) {
			return new PersistenceOperationExecResultBuilderErrorStep(_securityContext,
																	  requestedOpName);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersistenceOperationExecResultBuilderReturnedObjStep {
		protected final SecurityContext _securityContext;
		protected final String _requestedOpName;
		
		public <T> PersistenceOperationExecOK<T> returning(final T instance) {
			PersistenceOperationExecOK<T> outOpOK = new PersistenceOperationExecOK<T>();
			outOpOK.setRequestedOperationName(_requestedOpName);
			outOpOK.setOperationExecResult(instance);
			return outOpOK;
		}
		
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PersistenceOperationExecResultBuilderErrorStep {
		protected final SecurityContext _securityContext;
		protected final String _requestedOpName;
		
		
		public <T> PersistenceOperationExecError<T> because(final Throwable th) {
			PersistenceOperationExecError<T> outError = new PersistenceOperationExecError<T>(PersistenceRequestedOperation.OTHER,
																							 th);
			outError.setRequestedOperationName(_requestedOpName);
			return outError;
		}
		public <T> PersistenceOperationExecError<T> because(final String error,
															final PersistenceErrorType errType) {
			PersistenceOperationExecError<T> outError = new PersistenceOperationExecError<T>(PersistenceRequestedOperation.OTHER,
																							 error,
																							 errType);
			outError.setRequestedOperationName(_requestedOpName);
			return outError;
		}
		public <T> PersistenceOperationExecError<T> becauseClientBadRequest(final String msg,final Object... vars) {
			PersistenceOperationExecError<T> outError = new PersistenceOperationExecError<T>(PersistenceRequestedOperation.OTHER,
																							 Strings.customized(msg,vars),			// the error message
											     		 					   		   	     PersistenceErrorType.BAD_REQUEST_DATA);// is a client error
			outError.setRequestedOperationName(_requestedOpName);
			return outError;
		}
		public <T,M> PersistenceOperationExecResult<T> because(final CRUDError<M> crudError) {
			PersistenceOperationExecError<T> outError = new PersistenceOperationExecError<T>(PersistenceRequestedOperation.OTHER,
																							 crudError.getErrorMessage(),
																							 crudError.getErrorType());
			outError.setRequestedOperationName(_requestedOpName);
			return outError;
		}
	}
}
