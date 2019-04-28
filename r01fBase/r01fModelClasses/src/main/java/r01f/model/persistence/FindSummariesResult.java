package r01f.model.persistence;

import java.util.Collection;

import r01f.debug.Debuggable;
import r01f.guids.OID;
import r01f.model.HasModelObjectTypeInfo;
import r01f.model.PersistableModelObject;
import r01f.model.SummarizedModelObject;

public interface FindSummariesResult<M extends PersistableModelObject<? extends OID>> 
       	 extends PersistenceOperationResult,
       	 		 HasModelObjectTypeInfo<M>,
       	 		 Debuggable {
	/**
	 * Returns the found entities' summarized or throws a {@link PersistenceException} 
	 * if the find operation resulted on an error
	 * @return
	 */
	public <S extends SummarizedModelObject<? extends M>> Collection<S> getOrThrow() throws PersistenceException;
	
	/**
	 * @return a {@link FindSummariesOK}
	 */
	public FindSummariesOK<M> asCRUDOK();
	/**
	 * @return a {@link FindSummariesError}
	 */
	public FindSummariesError<M> asCRUDError();
}
