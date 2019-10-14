package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.services.COREServiceErrorType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="countResult",typeId="countError")
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
	  		   final Throwable th) {
		super(PersistenceRequestedOperation.COUNT,
			  th);
		_objectType = entityType;
	}
	CountError(final Class<T> entityType,
	  		   final COREServiceErrorType errType,
	  		   final Throwable th) {
		super(PersistenceRequestedOperation.COUNT,
			  errType,
			  th);
		_objectType = entityType;
	}
	CountError(final Class<T> entityType,
	  		   final String errMsg) {
		super(PersistenceRequestedOperation.COUNT,
			  errMsg);
		_objectType = entityType;
	}
	CountError(final Class<T> entityType,
			   final COREServiceErrorType errType,
	  		   final String errMsg) {
		super(PersistenceRequestedOperation.COUNT,
			  errType,
			  errMsg);
		_objectType = entityType;
	}
	public CountError(final Class<T> entityType,
					  final CountError<?> otherCountError) {
		this();
		this.setRequestedOperation(otherCountError.getRequestedOperation());
		this.setError(otherCountError.getError());
		this.setErrorDebug(otherCountError.getErrorDebug());
		this.setErrorMessage(otherCountError.getErrorMessage());
		this.setErrorType(otherCountError.getErrorType());
		this.setErrorCode(otherCountError.getErrorCode());
		_objectType = entityType;
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
