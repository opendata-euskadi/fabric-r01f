package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="FINDErrorOnModelObject")
@Accessors(prefix="_")
public class FindOnModelObjectError<M extends PersistableModelObject<? extends OID>>
	 extends FindError<M>
  implements FindOnModelObjectResult<M> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindOnModelObjectError() {
		super();
	}
	FindOnModelObjectError(final Class<M> entityType,
			  final Throwable th) {
		super(entityType,
			  th);
	}
	FindOnModelObjectError(final Class<M> entityType,
			  final String errMsg,final PersistenceErrorType errorCode) {
		super(entityType,
			  errMsg,errorCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public Class<M> getModelObjectType() {
		return _findedObjectType;
	}
	@Override
	public void setModelObjectType(final Class<M> type) {
		_findedObjectType = type;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOnModelObjectOK<M> asFindOnModelObjectOK() {
		throw new ClassCastException();
	}
	@Override
	public FindOnModelObjectError<M> asFindOnModelObjectError() {
		return this;
	}
}
