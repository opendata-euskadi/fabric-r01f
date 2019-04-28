package r01f.model.persistence;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.model.SummarizedModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="FINDSummariesError")
@Accessors(prefix="_")
@SuppressWarnings("unchecked")
public class FindSummariesError<M extends PersistableModelObject<? extends OID>>
	 extends PersistenceOperationOnObjectError<Collection<? extends SummarizedModelObject<M>>>
  implements FindSummariesResult<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Info about the model object
	 * beware that the {@link PersistenceOperationOnObjectOK} wraps a {@link Collection}
	 * of model objects
	 */
	@MarshallField(as="modelObjectType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindSummariesError() {
		super(PersistenceRequestedOperation.FIND);
	}
	FindSummariesError(final Class<M> entityType,
			  		   final Throwable th) {
		super(PersistenceRequestedOperation.FIND,
			  entityType,
			  th);
	}
	FindSummariesError(final Class<M> entityType,
			  		   final String errMsg,final PersistenceErrorType errorCode) {
		super(PersistenceRequestedOperation.FIND,
			  entityType,
			  errMsg,errorCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindSummariesOK<M> asCRUDOK() {
		throw new ClassCastException();
	}
	@Override
	public FindSummariesError<M> asCRUDError() {
		return this;
	}
	@Override
	public Collection<? extends SummarizedModelObject<M>> getOrThrow() throws PersistenceException {
		if (this.hasFailed()) this.asOperationExecError()		
								  .throwAsPersistenceException();
		return this.asOperationExecOK()
				   .getOrThrow();
	}	
	
}



