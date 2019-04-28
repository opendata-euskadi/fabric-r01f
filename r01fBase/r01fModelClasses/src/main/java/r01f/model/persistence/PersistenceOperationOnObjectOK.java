package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
abstract class PersistenceOperationOnObjectOK<T>
	   extends PersistenceOperationExecOK<T>
    implements PersistenceOperationOnObjectResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE DATA
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
	public PersistenceOperationOnObjectOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp) {
		super(reqOp,perfOp);
	}
	@SuppressWarnings("unchecked")
	PersistenceOperationOnObjectOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp,
								   final Class<?> entityType) {
		this(reqOp,perfOp);
		_objectType = (Class<T>)entityType;
		_requestedOperation = reqOp;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getRequestedOperationName() {
		return _requestedOperation != null ? _requestedOperation.name() 
										   : "unknown persistence operation";
	}
}
