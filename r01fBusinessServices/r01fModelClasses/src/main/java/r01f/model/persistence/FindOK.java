package r01f.model.persistence;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="findResult",typeId="FINDOK")
@Accessors(prefix="_")
public class FindOK<T>
	 extends PersistenceOperationOnObjectOK<Collection<T>>
  implements FindResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The found object type
	 * (beware that {@link PersistenceOperationOnObjectOK} wraps a {@link Collection} 
	 *  of this objects)
	 */
	@MarshallField(as="foundObjType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _foundObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindOK() {
		super(PersistenceRequestedOperation.FIND,PersistencePerformedOperation.FOUND,
			  Collection.class);
	}
	protected FindOK(final Class<T> entityType) {
		this();
		_foundObjectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * When a single result is expected, this method returns this entity
	 * @return
	 */
	public T getSingleExpectedOrThrow() {
		T outEntity = null;
		Collection<T> entities = this.getOrThrow();
		if (CollectionUtils.hasData(entities)) {
			outEntity = CollectionUtils.of(entities).pickOneAndOnlyElement("A single instance of {} was expected to be found BUT {} were found",_foundObjectType,entities.size());
		} else {
			throw new IllegalStateException(Throwables.message("A single instance of {} was expected to be found BUT NONE were found",_foundObjectType));
		}
		return outEntity;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOK<T> asFindOK() {
		return this;
	}
	@Override
	public FindError<T> asFindError() {
		throw new ClassCastException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} persistence operation requested on entity of type {} and found {} results",
								  _requestedOperation,_objectType,CollectionUtils.safeSize(_operationExecResult));
	}
}
