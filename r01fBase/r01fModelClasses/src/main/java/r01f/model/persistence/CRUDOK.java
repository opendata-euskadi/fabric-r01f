package r01f.model.persistence;

import lombok.experimental.Accessors;
import r01f.model.services.COREServiceMethod;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="crudResult",typeId="ok")
@Accessors(prefix="_")
public class CRUDOK<T>
	 extends PersistenceOperationOnObjectOK<T>
  implements CRUDResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDOK() {
		super(COREServiceMethod.UNKNOWN,COREServiceMethod.UNKNOWN);
	}
	public CRUDOK(final Class<T> entityType,
				  final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp) {
		super(entityType,
			  reqOp,perfOp);
	}
	public CRUDOK(final Class<T> entityType,
				  final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp,
				  final T result) {
		super(entityType,
			  reqOp,perfOp,
			  result);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasBeenLoaded() {
		return this.getPerformedOperation() == PersistencePerformedOperation.LOADED;
	}
	public boolean hasBeenSaved() {
		return this.getPerformedOperation() == PersistencePerformedOperation.SAVED;
	}
	public boolean hasBeenCreated() {
		return this.getPerformedOperation() == PersistencePerformedOperation.CREATED;
	}
	public boolean hasBeenUpdated() {
		return this.getPerformedOperation() == PersistencePerformedOperation.UPDATED;
	}
	public boolean hasBeenDeleted() {
		return this.getPerformedOperation() == PersistencePerformedOperation.DELETED;
	}
	public boolean hasBeenModified() {
		return this.hasBeenCreated() || this.hasBeenUpdated();
	}
	public boolean hasNotBeenModified() {
		return !this.hasBeenModified();
	}
	public boolean hasBeenFound() {
		return this.getPerformedOperation() == PersistencePerformedOperation.FOUND;
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
		PersistencePerformedOperation supposedPerformed = PersistencePerformedOperation.from(this.getRequestedOperation());
		return Strings.customized("{} persistence operation requested on entity of type {} {}",
								  _calledMethod,_objectType,
								  supposedPerformed != this.getPerformedOperation() ? ("and performed " + _executedMethod + " persistence operation")
										  								   			: "");
	}
}
