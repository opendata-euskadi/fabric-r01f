package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="crudResult",typeId="crudOnModelObjectOK")
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
	CRUDOnModelObjectOK(final Class<M> entityType,
						final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation performedOp) {
		super(entityType,
			  reqOp,performedOp);
	}
	CRUDOnModelObjectOK(final Class<M> entityType,
						final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation performedOp,
						final M result) {
		super(entityType,
			  reqOp,performedOp,
			  result);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("cast")
	public Class<M> getModelObjectType() {
		return _objectType;
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
