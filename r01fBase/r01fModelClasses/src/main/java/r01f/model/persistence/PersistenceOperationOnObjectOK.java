package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.services.COREServiceMethod;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public abstract class PersistenceOperationOnObjectOK<T>
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
	public PersistenceOperationOnObjectOK(final COREServiceMethod reqOp,final COREServiceMethod perfOp) {
		super(reqOp,perfOp);
	}
	public PersistenceOperationOnObjectOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp) {
		super(reqOp,perfOp);
	}
	@SuppressWarnings("unchecked")
	PersistenceOperationOnObjectOK(final Class<?> entityType,
								   final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp) {
		this(reqOp,perfOp);
		_objectType = (Class<T>)entityType;
	}
	@SuppressWarnings("unchecked")
	PersistenceOperationOnObjectOK(final Class<?> entityType,
								   final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp,
								   final T result) {
		super(reqOp,perfOp,
			  result);
		_objectType = (Class<T>)entityType;
	}
}
