package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="crudResult",typeId="CRUDOKOnModelObject")
@Accessors(prefix="_")
public class CRUDOnModelObjectOK<M extends PersistableModelObject<? extends OID>>
	 extends CRUDOK<M> 
  implements CRUDOnModelObjectResult<M> {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDOnModelObjectOK() {
		/* nothing */
	}
	CRUDOnModelObjectOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation performedOp,
						final Class<M> entityType) {
		super(reqOp,performedOp,
			  entityType);
	}
	CRUDOnModelObjectOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation performedOp,
						final Class<M> entityType,
						final M entity) {
		super(reqOp,performedOp,
			  entityType);
		_operationExecResult = entity;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("cast")
	public Class<M> getModelObjectType() {
		return (Class<M>)_objectType;
	}
	@Override
	public void setModelObjectType(final Class<M> type) {
		_objectType = type;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDOnModelObjectOK<M> asCRUDOnModelObjectOK() {
		return this;
	}
	@Override
	public CRUDOnModelObjectError<M> asCRUDOnModelObjectError() {
		throw new ClassCastException();
	}

}
