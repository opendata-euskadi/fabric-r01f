package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public abstract class PersistenceOperationExecResult<T> 
    	   implements PersistenceOperationResult,
    	   			  Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The requested operation
	 */
	@MarshallField(as="requestedOperation",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected PersistenceRequestedOperation _requestedOperation;
	
	@MarshallField(as="requestedOperationName",
				   whenXml=@MarshallFieldAsXml(attr=true))
		    @Setter protected String _requestedOperationName;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceOperationExecResult(final PersistenceRequestedOperation reqOp,final String reqOpName) {
		_requestedOperation = reqOp;
		_requestedOperationName = reqOpName;
	}
	public PersistenceOperationExecResult(final PersistenceRequestedOperation reqOp) {
		_requestedOperation = reqOp;
		_requestedOperationName = reqOp.name();
	} 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the operation execution returned object
	 * @return the persistence operation returned object or throw a {@link PersistenceException} if the 
	 *  	   operation execution was not successful
	 * @throws PersistenceException
	 */
	public T getOrThrow() throws PersistenceException {
		if (this.hasFailed()) this.asOperationExecError()		
								  .throwAsPersistenceException();
		return this.asOperationExecOK()
				   .getOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getRequestedOperationName() {
		return _requestedOperation != null ? _requestedOperation.name() 
										   : "unknown persistence operation";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasFailed() {
		return this instanceof PersistenceOperationError;
	}

	@Override
	public boolean hasSucceeded() {
		return this instanceof PersistenceOperationOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract PersistenceOperationExecError<T> asOperationExecError();
	public abstract PersistenceOperationExecOK<T> asOperationExecOK();
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <R extends PersistenceOperationResult> R as(final Class<R> type) {
		return (R)this;
	}
}
