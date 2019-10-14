package r01f.model.persistence;

import r01f.guids.OID;
import r01f.model.HasModelObjectTypeInfo;
import r01f.model.PersistableModelObject;

public interface FindOnModelObjectResult<M extends PersistableModelObject<? extends OID>> 
   		 extends FindResult<M>,
   		 		 HasModelObjectTypeInfo<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link FindOnModelObjectOK}
	 */
	public FindOnModelObjectOK<M> asFindOnModelObjectOK();
	/**
	 * @return a {@link FindOnModelObjectError}
	 */
	public FindOnModelObjectError<M> asFindOnModelObjectError();
}
