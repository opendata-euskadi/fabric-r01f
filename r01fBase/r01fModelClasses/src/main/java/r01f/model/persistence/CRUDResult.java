package r01f.model.persistence;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;

@MarshallPolymorphicTypeInfo(includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.ALWAYS))
public interface CRUDResult<T>
		 extends PersistenceOperationOnObjectResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link CRUDOK} instance
	 */
	public CRUDOK<T> asCRUDOK();
	/**
	 * @return a {@link CRUDError} instance
	 */
	public CRUDError<T> asCRUDError();
}
