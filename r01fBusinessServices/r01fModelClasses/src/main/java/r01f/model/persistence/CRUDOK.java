package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="crudResult",typeId="CRUDOK")
@Accessors(prefix="_")
public class CRUDOK<T>
	 extends PersistenceOperationOnObjectOK<T>
  implements CRUDResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDOK() {
		super(PersistenceRequestedOperation.OTHER,PersistencePerformedOperation.OTHER);
	}
	CRUDOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp,
		   final Class<T> entityType) {
		super(reqOp,perfOp,
			  entityType);
	}
	CRUDOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp,
		   final Class<T> entityType,
		   final T entity) {
		this(reqOp,perfOp,
			 entityType);
		_operationExecResult = entity;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasBeenLoaded() {
		return _performedOperation == PersistencePerformedOperation.LOADED;
	}
	public boolean hasBeenCreated() {
		return _performedOperation == PersistencePerformedOperation.CREATED;
	}
	public boolean hasBeenUpdated() {
		return _performedOperation == PersistencePerformedOperation.UPDATED;
	}
	public boolean hasBeenDeleted() {
		return _performedOperation == PersistencePerformedOperation.DELETED;
	}
	public boolean hasBeenModified() {
		return this.hasBeenCreated() || this.hasBeenUpdated();
	}
	public boolean hasNotBeenModified() {
		return !this.hasBeenModified();
	}
	public boolean hasBeenFound() {
		return _performedOperation == PersistencePerformedOperation.FOUND;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDOK<T> asCRUDOK() {
		return this;
	}
	@Override
	public CRUDError<T> asCRUDError() {
		throw new ClassCastException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		PersistencePerformedOperation supposedPerformed = PersistencePerformedOperation.from(_requestedOperation);
		return Strings.customized("{} persistence operation requested on entity of type {} {}",
								  _requestedOperation,_objectType,_performedOperation,
								  supposedPerformed != _performedOperation ? ("and performed " + _performedOperation + " persistence operation")
										  								   : "");
	}
}
