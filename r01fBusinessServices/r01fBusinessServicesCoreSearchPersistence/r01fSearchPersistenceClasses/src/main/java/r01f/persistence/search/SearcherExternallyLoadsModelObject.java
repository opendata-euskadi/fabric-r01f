package r01f.persistence.search;

import r01f.guids.OID;
import r01f.model.IndexableModelObject;
import r01f.securitycontext.SecurityContext;

/**
 * Interface to be implemented by {@link Searcher} subtypes that loads externally the result item encapsulated model object instance
 * (the model object's instance inside the search result item)
 * Load externally means to use the model object's oid to for example query the BBDD 
 * @param <O>
 * @param <P>
 */
public interface SearcherExternallyLoadsModelObject<O extends OID,
										 		    P extends IndexableModelObject> 
	     extends SearcherSearchResultItemFromIndexDataTransformStrategy {
	/**
	 * Loads the model object from a search index external source (ie a BBDD)
	 * @param securityContext
	 * @param oid
	 * @return
	 */
	public P loadModelObject(final SecurityContext securityContext,
				  			 final O oid);
}
