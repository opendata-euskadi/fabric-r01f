package r01f.model.persistence;

import java.util.Collection;

import r01f.guids.OID;
import r01f.model.HasModelObjectTypeInfo;
import r01f.model.ModelObject;
import r01f.model.SummarizedModelObject;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;

@MarshallPolymorphicTypeInfo(includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.ALWAYS))
public interface FindSummariesResult<M extends ModelObject> 
       	 extends PersistenceOperationOnObjectResult<Collection<? extends SummarizedModelObject<M>>>,
       	 		 HasModelObjectTypeInfo<M> {
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link FindSummariesOK}
	 */
	public FindSummariesOK<M> asFindSummariesOK();
	/**
	 * @return a {@link FindSummariesError}
	 */
	public FindSummariesError<M> asFindSummariesError();
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param <S>
	 * @return the summaries
	 */
	public <S extends SummarizedModelObject<M>> Collection<S> getSummariesOrThrow();
	/**
	 * @return the found entities' oids if the persistence find operation was successful or a PersistenteException if not
	 */
	public <O extends OID> Collection<O> getOidsOrThrow();
	/**
	 * When a single result is expected, this method returns this entity's oid
	 * @return
	 */
	public <O extends OID> O getSingleExpectedOidOrThrow();
	/**
	 * When a single result is expected, this method returns this entity
	 * @return
	 */
	public <S extends SummarizedModelObject<M>> S getSingleExpectedOrThrow();
}
