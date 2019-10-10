package r01f.model.services;

import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;

@MarshallPolymorphicTypeInfo(includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.ALWAYS))
public interface COREServiceMethodExecResult<T>
		 extends Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the method execution result or throw an exception if it failed
	 * @return
	 */
	public T getOrThrow();
/////////////////////////////////////////////////////////////////////////////////////////
//  REQUESTEED OPERATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the called method
	 */
	public COREServiceMethod getCalledMethod();
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR CONDITIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the persistence operation has failed
	 */
	public boolean hasFailed();
	/**
	 * @return true if the persistence operation has succeeded
	 */
	public boolean hasSucceeded();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the operation result message, normally if the operation has failed, it contains an error description and if the
	 * 		   operation has succeed it contains some logging info
	 */
	public String getDetailedMessage();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public COREServiceMethodExecError<T> asCOREServiceMethodExecError();
	public COREServiceMethodExecOK<T> asCOREServiceMethodExecOK();
	public <R extends COREServiceMethodExecResult<T>> R as(final Class<R> type);
}
