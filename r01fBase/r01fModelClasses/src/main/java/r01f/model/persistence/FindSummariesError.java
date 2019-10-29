package r01f.model.persistence;

import java.util.Collection;

import com.google.common.reflect.TypeToken;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.model.services.COREServiceErrorType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="findResult",typeId="findSummariesError")
@Accessors(prefix="_")
@SuppressWarnings("unchecked")
public class FindSummariesError<M extends ModelObject>
	 extends PersistenceOperationExecError<Collection<? extends SummarizedModelObject<M>>>
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
			  th);
		_modelObjectType = entityType;
	}
	FindSummariesError(final Class<M> entityType,
					   final COREServiceErrorType errorType,
			  		   final Throwable th) {
		super(PersistenceRequestedOperation.FIND,
			  errorType,
			  th);
		_modelObjectType = entityType;
	}
	FindSummariesError(final Class<M> entityType,
			  		   final String errMsg) {
		super(PersistenceRequestedOperation.FIND,
			  errMsg);
		_modelObjectType = entityType;
	}
	FindSummariesError(final Class<M> entityType,
					   final COREServiceErrorType errorType,
			  		   final String errMsg) {
		super(PersistenceRequestedOperation.FIND,
			  errorType,
			  errMsg);
		_modelObjectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({ "serial" })
	public Class<Collection<? extends SummarizedModelObject<M>>> getObjectType() {
		return (Class<Collection<? extends SummarizedModelObject<M>>>)new TypeToken<Class<Collection<? extends SummarizedModelObject<M>>>>() { /* nothing */ }
																			.getComponentType()
																			.getRawType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<? extends SummarizedModelObject<M>> getOrThrow() throws PersistenceException {
		throw this.getPersistenceException();
	}
	@Override
	public <S extends SummarizedModelObject<M>> Collection<S> getSummariesOrThrow() {
		throw this.getPersistenceException();
	}
	@Override
	public <O extends OID> Collection<O> getOidsOrThrow() {
		throw this.getPersistenceException();
	}
	@Override
	public <O extends OID> O getSingleExpectedOidOrThrow() {
		throw this.getPersistenceException();
	}
	@Override
	public <S extends SummarizedModelObject<M>> S getSingleExpectedOrThrow() {
		throw this.getPersistenceException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindSummariesOK<M> asFindSummariesOK() {
		throw new ClassCastException();
	}
	@Override
	public FindSummariesError<M> asFindSummariesError() {
		return this;
	}
}



