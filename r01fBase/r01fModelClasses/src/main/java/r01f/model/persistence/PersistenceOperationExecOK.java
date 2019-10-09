package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.services.COREServiceException;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecOK;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="persistenceOperationResult",typeId="ok")
@Accessors(prefix="_")
public class PersistenceOperationExecOK<T>
	 extends COREServiceMethodExecOK<T> 
  implements PersistenceOperationResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The performed operation
	 * Sometimes the requested operation is NOT the same as the requested operation since
	 * for example, the client requests a create operation BUT an update operation is really 
	 * performed because the record already exists at the persistence store
	 */
	@MarshallField(as="executedMethod",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected COREServiceMethod _executedMethod;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceOperationExecOK() {
		// default no-args constructor
	}
	public PersistenceOperationExecOK(final COREServiceMethod reqOp) {
		super(reqOp);
	}
	public PersistenceOperationExecOK(final COREServiceMethod reqOp,final COREServiceMethod perfOp) {
		super(reqOp);
		_executedMethod = perfOp;
	}
	public PersistenceOperationExecOK(final COREServiceMethod reqOp,
									  final T instance) {
		super(reqOp,
			  instance);
	}
	public PersistenceOperationExecOK(final COREServiceMethod reqOp,final COREServiceMethod perfOp,
									  final T instance) {
		super(reqOp,
			  instance);
		_executedMethod = perfOp;
	}
	public PersistenceOperationExecOK(final PersistenceRequestedOperation reqOp) {
		super(reqOp.getCOREServiceMethod());
	}
	public PersistenceOperationExecOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp) {
		this(reqOp.getCOREServiceMethod(),perfOp.getCOREServiceMethod());
	}
	public PersistenceOperationExecOK(final PersistenceRequestedOperation reqOp,
									  final T instance) {
		super(reqOp.getCOREServiceMethod(),
			  instance);
	}
	public PersistenceOperationExecOK(final PersistenceRequestedOperation reqOp,final PersistencePerformedOperation perfOp,
									  final T result) {
		super(reqOp.getCOREServiceMethod(),
			  result);
		_executedMethod = perfOp.getCOREServiceMethod();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PersistenceRequestedOperation getRequestedOperation() {
		return PersistenceRequestedOperation.from(_calledMethod);
	}
	public void setRequestedOperation(final PersistenceRequestedOperation calledOp) {
		_calledMethod = calledOp != null ? calledOp.getCOREServiceMethod() : null;
	}
	public PersistencePerformedOperation getPerformedOperation() {
		return PersistencePerformedOperation.from(_executedMethod);
	}
	public void setPerformedOperation(final PersistencePerformedOperation perfOp) {
		_executedMethod = perfOp != null ? perfOp.getCOREServiceMethod() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public T getOrThrow() throws COREServiceException {
		return _methodExecResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PersistenceOperationExecOK<T> asPersistenceOperationOK() {
		return this;
	}
	@Override
	public PersistenceOperationExecError<T> asPersistenceOperationError() {
		throw new ClassCastException();
	}
}
