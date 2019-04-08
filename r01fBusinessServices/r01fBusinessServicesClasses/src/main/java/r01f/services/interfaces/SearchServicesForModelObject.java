package r01f.services.interfaces;

import java.util.Collection;

import r01f.guids.PersistableObjectOID;
import r01f.model.IndexableModelObject;
import r01f.model.search.SearchFilterForModelObject;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.securitycontext.SecurityContext;

public interface SearchServicesForModelObject<F extends SearchFilterForModelObject,I extends SearchResultItemForModelObject<? extends IndexableModelObject>> 
		 extends SearchServices<F,I> {
    /**
     * Returns the OIDs of the records verifying the search filter
	 * <pre>
	 * IMPORTANT!!	Usually this method always filter at BBDD (never uses Lucene) 
	 * 				because it's purpose is normally re-generate the lucene index
	 * 				from BBDD data
	 * </pre>
     * @param filter the filter
     * @param ordering the order
     * @return the oids
     */
	public <O extends PersistableObjectOID> Collection<O> filterRecordsOids(final SecurityContext securityContext,
														   					final F filter);
}
