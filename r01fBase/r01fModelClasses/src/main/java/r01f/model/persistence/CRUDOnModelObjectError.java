package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="crudResult",typeId="CRUDErrorOnModelObject")
@Accessors(prefix="_")
public class CRUDOnModelObjectError<M extends PersistableModelObject<? extends OID>>
	 extends CRUDError<M>
  implements CRUDOnModelObjectResult<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDOnModelObjectError() {
		// nothing
	}
	CRUDOnModelObjectError(final PersistenceRequestedOperation requestedOp,
						   final Class<M> entityType,
			  			   final Throwable th) {
		super(requestedOp,
			  entityType,
			  th);	
	}
	CRUDOnModelObjectError(final PersistenceRequestedOperation requestedOp,
						   final Class<M> entityType,
			  			   final PersistenceErrorType errCode) {
		super(requestedOp,
			  entityType,
			  errCode);
	}
	CRUDOnModelObjectError(final PersistenceRequestedOperation requestedOp,
						   final Class<M> entityType,
						   final String errMsg,final PersistenceErrorType errCode) {
		super(requestedOp,
			  entityType,
			  errMsg,errCode);
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
		throw new ClassCastException();
	}
	@Override
	public CRUDOnModelObjectError<M> asCRUDOnModelObjectError() {
		return this;
	}

}
