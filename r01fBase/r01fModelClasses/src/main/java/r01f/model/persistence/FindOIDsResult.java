package r01f.model.persistence;

import r01f.guids.PersistableObjectOID;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;

@MarshallPolymorphicTypeInfo(includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.ALWAYS))
public interface FindOIDsResult<O extends PersistableObjectOID> 
       	 extends FindResult<O> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link FindOIDsError}
	 */
	public FindOIDsError<O> asFindOIDsError();
	/**
	 * @return a {@link FindOIDsOK}
	 */
	public FindOIDsOK<O> asFindOIDsOK();
}
