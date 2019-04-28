package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="countResult",typeId="CountError")
@Accessors(prefix="_")
public class CountError<T>
	 extends PersistenceOperationExecError<Long>
  implements CountResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The type of the entity subject of the requested operation 
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _objectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CountError() {
		// nothing
	}
	CountError(final Class<T> entityType,
			   final String reqOp,
	  		   final Throwable th) {
		super(PersistenceRequestedOperation.COUNT,
			  th);
		_requestedOperationName = reqOp;
	}
	CountError(final Class<T> entityType,
			   final String reqOp,
	  		   final PersistenceErrorType errType) {
		this();
		_requestedOperationName = reqOp;
		_objectType = entityType;
		_errorType = errType;
	}
	CountError(final Class<T> entityType,
			   final String reqOp,
	  		   final String errMsg,final PersistenceErrorType errCode) {
		super(PersistenceRequestedOperation.COUNT,
			  errMsg,errCode);
		_requestedOperationName = reqOp;
		_objectType = entityType;
	}
	public CountError(final Class<T> entityType,
					  final CountError<?> otherCountError) {
		this(entityType,
			 null,
			 otherCountError);
	}
	public CountError(final Class<T> entityType,
					  final String reqOp,
					  final CountError<?> otherCountError) {
		this();
		_requestedOperationName = reqOp;
		_objectType = entityType;
		this.setError(otherCountError.getError());
		this.setErrorDebug(otherCountError.getErrorDebug());
		this.setErrorMessage(otherCountError.getErrorMessage());
		this.setErrorType(otherCountError.getErrorType());
		this.setExtendedErrorCode(otherCountError.getExtendedErrorCode());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CountError<T> asCountError() {
		return this;
	}
	@Override
	public CountOK<T> asCountOK() {
		throw new ClassCastException();
	}
}
