package r01f.model.persistence;

import r01f.guids.OID;
import r01f.model.HasModelObjectTypeInfo;
import r01f.model.PersistableModelObject;

public interface CRUDOnModelObjectResult<M extends PersistableModelObject<? extends OID>>
		 extends CRUDResult<M>,
		 		 HasModelObjectTypeInfo<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link CRUDOnModelObjectOK} instance
	 */
	public CRUDOnModelObjectOK<M> asCRUDOnModelObjectOK();
	/**
	 * @return a {@link CRUDOnModelObjectError} instance
	 */
	public CRUDOnModelObjectError<M> asCRUDOnModelObjectError();
}
