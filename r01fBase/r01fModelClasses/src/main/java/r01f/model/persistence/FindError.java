package r01f.model.persistence;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="FINDError")
@Accessors(prefix="_")
public class FindError<T>
	 extends PersistenceOperationOnObjectError<Collection<T>>
  implements FindResult<T>  {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The found object type
	 * (beware that {@link PersistenceOperationOnObjectOK} wraps a {@link Collection} 
	 *  of this objects)
	 */
	@MarshallField(as="findedObjType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _findedObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindError() {
		super(PersistenceRequestedOperation.FIND);
	}
	FindError(final Throwable th) {
		super(PersistenceRequestedOperation.FIND,
			  Collection.class,
			  th);
	}
	FindError(final String errMsg,final PersistenceErrorType errorCode) {
		super(PersistenceRequestedOperation.FIND,
			  Collection.class,
			  errMsg,errorCode);
	}
	FindError(final Class<T> entityType,
			  final Throwable th) {
		this(th);
		_findedObjectType = entityType;
	}
	FindError(final Class<T> entityType,
			  final String errMsg,final PersistenceErrorType errorCode) {
		this(errMsg,errorCode);
		_findedObjectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOK<T> asFindOK() {
		throw new ClassCastException();
	}
	@Override
	public FindError<T> asFindError() {
		return this;
	}
}
