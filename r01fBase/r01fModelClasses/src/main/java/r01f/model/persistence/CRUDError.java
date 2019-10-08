package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceMethod;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="crudResult",typeId="error")
@Accessors(prefix="_")
public class CRUDError<T>
	 extends PersistenceOperationOnObjectError<T>
  implements CRUDResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * When the un-successful operation is a create or update operation, this
	 * field contains the client-sent data
	 */
	@MarshallField(as="targetEntity")
	@Getter @Setter private T _targetEntity;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDError() {
		super(COREServiceMethod.UNKNOWN);
	}
	CRUDError(final Class<T> entityType,
			  final PersistenceRequestedOperation reqOp,
	  		  final Throwable th) {
		super(entityType,
			  reqOp,
			  th);	
	}
	CRUDError(final Class<T> entityType,
			  final PersistenceRequestedOperation reqOp,
	  		  final COREServiceErrorType errType,
	  		  final Throwable th) {
		super(entityType,
			  reqOp,
			  errType,
			  th);
	}
	CRUDError(final Class<T> entityType,
			  final PersistenceRequestedOperation reqOp,
	  		  final String errMsg) {
		super(entityType,
			  reqOp,
			  errMsg);
	}
	CRUDError(final Class<T> entityType,
			  final PersistenceRequestedOperation reqOp,
			  final COREServiceErrorType errType,
	  		  final String errMsg) {
		super(entityType,
			  reqOp,
			  errType,
			  errMsg);
	}
	public CRUDError(final Class<T> entityType,
					 final PersistenceRequestedOperation reqOp,
					 final PersistenceOperationExecError<?> otherError) {
		super(entityType,
			  reqOp,
			  otherError);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDError<T> asCRUDError() {
		return this;
	}
	@Override
	public CRUDOK<T> asCRUDOK() {
		throw new ClassCastException();
	}
}
