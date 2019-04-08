package r01f.model.persistence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class PersistenceOperationResults {
/////////////////////////////////////////////////////////////////////////////////////////
// 	UTILITY METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns some debug info about a {@link PersistenceOperationResult}
	 * @param result
	 * @return
	 */
	public static CharSequence debugInfoOf(final PersistenceOperationResult result) {
		String dbg = null;
		// [Success]
		if (result.hasSucceeded()) {
			if (result instanceof PersistenceOperationOnObjectResult) {
				PersistenceOperationOnObjectResult<?> opOK = (PersistenceOperationOnObjectResult<?>)result;
				dbg = Strings.customized("Successful '{}' operation about {}",
							 			 opOK.getRequestedOperation(),
									 	 opOK.getObjectType());
			} else {
				PersistenceOperationOK opOK = (PersistenceOperationOK)result;
				dbg = Strings.customized("Successful '{}' persistence operation",
								 		 opOK.getRequestedOperationName());
			}
		} 
		// [Error]
		else {
			if (result instanceof PersistenceOperationOnObjectResult) {
				PersistenceOperationError opError = (PersistenceOperationError)result;
				PersistenceOperationOnObjectResult<?> opErrOnModelObj = (PersistenceOperationOnObjectResult<?>)result;
				dbg = Strings.customized("Failed '{}' operation about {}: ({} error) --> {}" + 
									     "\t-Client Error: {}\n" + 
									     "\t-Message: {}",
									     opErrOnModelObj.getRequestedOperation(),
									     opErrOnModelObj.getObjectType(),
									     (opError.wasBecauseAClientError() ? "CLIENT" : "SERVER"),
									     opError.getErrorMessage());
			} else {
				PersistenceOperationError opError = (PersistenceOperationError)result;
				dbg = Strings.customized("Failed '{}' operation: ({} error) --> {}" + 
									     "\t-Message: {}",
									     opError.getRequestedOperationName(),
									     (opError.wasBecauseAClientError() ? "CLIENT" : "SERVER"),
									     opError.getErrorMessage());
			}
		}
		return dbg;
	}
}
