package r01f.model.persistence;

import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;

@MarshallPolymorphicTypeInfo(includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.ALWAYS))
public interface PersistenceOperationResult<T>
		 extends COREServiceMethodExecResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  REQUESTEED OPERATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the requested operation
	 */
	public PersistenceRequestedOperation getRequestedOperation();
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link PersistenceOperationExecOK} instance
	 */
	public PersistenceOperationExecOK<T> asPersistenceOperationOK();
	/**
	 * @return a {@link PersistenceOperationExecError} instance
	 */
	public PersistenceOperationExecError<T> asPersistenceOperationError();
}
