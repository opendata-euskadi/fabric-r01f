package r01f.services.interfaces;

import java.util.Collection;

import r01f.guids.OID;
import r01f.model.IndexableModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.types.jobs.EnqueuedJob;

public interface IndexServicesForModelObject<O extends OID,M extends IndexableModelObject> 
		 extends ServiceInterface {
/////////////////////////////////////////////////////////////////////////////////////////
//  ENTITY INDEX
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Indexes the provided model object
     * @param securityContext
     * @param modelObject the model object
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob index(final SecurityContext securityContext,
							 final M modelObject);
    /**
     * Updates the index data for the provided model object
     * @param securityContext
     * @param modelObject the model object
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob updateIndex(final SecurityContext securityContext,
							 	   final M modelObject);
/////////////////////////////////////////////////////////////////////////////////////////
//  UN-INDEX
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Un-Indexes the model object whose oid is provided
     * @para securityContext
     * @param oid the model object's oid
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob removeFromIndex(final SecurityContext securityContext,
							   		   final O oid);
	/**
	 * Un-Indexes all records 
	 * @param securityContext
	 * @return
	 */
	public EnqueuedJob removeAllFromIndex(final SecurityContext securityContext);
	/**
	 * Un-Indexes the model objects provided
	 * @param securityContext
	 * @param all
	 * @return
	 */
	public EnqueuedJob removeAllFromIndex(final SecurityContext securityContext,
							      		  final Collection<O> all);
/////////////////////////////////////////////////////////////////////////////////////////
//  RE-INDEXING	
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Updates the index data fir the model object whose oid is provided
     * @param securityContext
     * @param oid the model object's oid
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob reIndex(final SecurityContext securityContext,
							   final O oid);
	/**
	 * Indexes the model objects provided
	 * @param securityContext
	 * @param all
	 * @return
	 */
	public EnqueuedJob reIndexAll(final SecurityContext securityContext,
								  final Collection<O> all);
	/**
	 * Indexes all records 
	 * @param securityContext
	 * @return
	 */
	public EnqueuedJob reIndexAll(final SecurityContext securityContext);

}
