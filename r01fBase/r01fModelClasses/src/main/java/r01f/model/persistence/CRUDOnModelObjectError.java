package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.model.services.COREServiceErrorType;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="crudResult",typeId="crudOnModelObjectError")
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
	CRUDOnModelObjectError(final Class<M> entityType,
						   final PersistenceRequestedOperation requestedOp,
			  			   final Throwable th) {
		super(entityType,
			  requestedOp,
			  th);	
	}
	CRUDOnModelObjectError(final Class<M> entityType,
						   final PersistenceRequestedOperation requestedOp,
			  			   final COREServiceErrorType errType,
			  			   final Throwable th) {
		super(entityType,
			  requestedOp,
			  errType,
			  th);
	}
	CRUDOnModelObjectError(final Class<M> entityType,
						   final PersistenceRequestedOperation requestedOp,
						   final COREServiceErrorType errType,
						   final String errMsg) {
		super(entityType,
			  requestedOp,
			  errType,
			  errMsg);
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
		throw new ClassCastException();
	}
	@Override
	public CRUDOnModelObjectError<M> asCRUDOnModelObjectError() {
		return this;
	}

}
