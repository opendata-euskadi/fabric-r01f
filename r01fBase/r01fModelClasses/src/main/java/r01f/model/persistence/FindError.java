package r01f.model.persistence;

import java.util.Collection;

import com.google.common.reflect.TypeToken;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.services.COREServiceErrorType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="error")
@Accessors(prefix="_")
public class FindError<T>
	 extends PersistenceOperationExecError<Collection<T>>
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
	public FindError(final Class<T> entityType,
			  final Throwable th) {
		super(PersistenceRequestedOperation.FIND,
			  th);
		_findedObjectType = entityType;
	}
	public FindError(final Class<T> entityType,
			  		 final String msg) {
		super(PersistenceRequestedOperation.FIND,
			  msg);
		_findedObjectType = entityType;
	}
	public FindError(final Class<T> entityType,
			  		 final COREServiceErrorType errorType,
			  		 final Throwable th) {
		super(PersistenceRequestedOperation.FIND,
			  errorType,
			  th);
		_findedObjectType = entityType;
	}
	public FindError(final Class<T> entityType,
			  		 final COREServiceErrorType errorType,
			  		 final String errMsg) {
		super(PersistenceRequestedOperation.FIND,
			  errorType,
			  errMsg);
		_findedObjectType = entityType;
	}
	public FindError(final Class<T> entityType,
					 final PersistenceOperationExecError<?> otherError) {
		super(PersistenceRequestedOperation.FIND,
			  otherError);
		_findedObjectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({ "serial","unchecked" })
	public Class<Collection<T>> getObjectType() {
		return (Class<Collection<T>>)new TypeToken<Class<Collection<T>>>() { /* nothing */ }
											.getComponentType()
											.getRawType();
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
