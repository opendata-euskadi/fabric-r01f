package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.model.services.COREServiceErrorType;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="findOnOnModelObjectError")
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
						   final COREServiceErrorType errorType,
			  			   final Throwable th) {
		super(entityType,
			  errorType,
			  th);
	}
	FindOnModelObjectError(final Class<M> entityType,
			  			   final String errMsg) {
		super(entityType,
			  errMsg);
	}
	FindOnModelObjectError(final Class<M> entityType,
						   final COREServiceErrorType errorType,
			  			   final String errMsg) {
		super(entityType,
			  errorType,
			  errMsg);
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
