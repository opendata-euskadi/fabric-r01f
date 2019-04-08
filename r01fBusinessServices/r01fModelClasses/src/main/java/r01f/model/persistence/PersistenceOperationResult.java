package r01f.model.persistence;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;

@MarshallPolymorphicTypeInfo(includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.ALWAYS))
public interface PersistenceOperationResult {
/////////////////////////////////////////////////////////////////////////////////////////
//  OPERATION NAME
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the requested operation
	 */
	public PersistenceRequestedOperation getRequestedOperation();
	/**
 	 * @return the requested operation in a human-friendly format
	 */
	public String getRequestedOperationName();
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
	/**
	 * Returns the {@link PersistenceOperationResult} as another {@link PersistenceOperationResult} subtype
	 * @param type
	 * @return
	 */
	public <R extends PersistenceOperationResult> R as(final Class<R> type);
}
