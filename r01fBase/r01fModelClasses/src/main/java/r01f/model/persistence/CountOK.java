package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="countResult",typeId="countOK")
@Accessors(prefix="_")
public class CountOK<T>
	 extends PersistenceOperationExecOK<Long>
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
	public CountOK() {
		_requestedOperation = PersistenceRequestedOperation.COUNT;
		_performedOperation = PersistencePerformedOperation.COUNTED;
	}
	CountOK(final Class<T> entityType) {
		this();
		_objectType = entityType;
	}
	CountOK(final Class<T> entityType,
		    final Long countResult) {
		this(entityType);
		_operationExecResult = countResult;
	}
	CountOK(final String reqOp,
			final Class<T> entityType,
		    final Long countResult) {
		this(entityType,
			 countResult);
		_requestedOperationName = reqOp;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CountOK<T> asCountOK() {
		return this;
	}
	@Override
	public CountError<T> asCountError() {
		throw new ClassCastException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} count operation requested on entity of type {}: count={}",
								  _requestedOperationName,_objectType,_operationExecResult);
	}
}
