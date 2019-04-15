package r01f.rest.resources.delegates;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.core.Response;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.IndexableModelObject;
import r01f.rest.RESTOperationsResponseBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.IndexServicesForModelObject;
import r01f.types.jobs.EnqueuedJob;
import r01f.util.types.collections.CollectionUtils;

/**
 * Base type for REST services that encapsulates the common search index ops: indexing, searching
 */
@Accessors(prefix="_")
public abstract class RESTIndexDelegateBase<O extends OID,M extends IndexableModelObject> 
              extends RESTDelegateForModelObjectBase<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final IndexServicesForModelObject<O,M> _indexServices;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected <T extends IndexServicesForModelObject<O,M>> T indexeServicesAs(@SuppressWarnings("unused") final Class<T> type) {
		return (T)_indexServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTIndexDelegateBase(final Class<M> modelObjType,
								 final IndexServicesForModelObject<O,M> indexServices) {
		super(modelObjType);
		_indexServices = indexServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INDEX
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Indexes the provided model object
     * @param securityContext
	 * @param resourcePath
     * @param modelObject the model object
     * @return a ticket for the enqueued job
     */
	public Response index(final SecurityContext securityContext,final String resourcePath,
						  final M modelObject) {
		EnqueuedJob job = _indexServices.index(securityContext,
									      	   modelObject);
		Response outResponse = Response.ok()
									   .entity(job)
									   .build();			
		return outResponse;
	}
    /**
     * Updates the index data for the provided model object
     * @param securityContext
	 * @param resourcePath
     * @param modelObject the model object
     * @return a ticket for the enqueued job
     */
	public Response updateIndex(final SecurityContext securityContext,final String resourcePath,
							 	final M modelObject) {
		EnqueuedJob job = _indexServices.updateIndex(securityContext,
											    	 modelObject);
		Response outResponse  = RESTOperationsResponseBuilder.searchIndex()
															 .at(URI.create(resourcePath))
															 .build(job);			
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REMOVE FROM INDEX
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Un-Indexes the model object whose oid is provided
     * @param securityContext
	 * @param resourcePath
     * @param oid the model object's oid
     * @return a ticket for the enqueued job
     */
	public Response removeFromIndex(final SecurityContext securityContext,final String resourcePath,
							   		final O oid) {
		EnqueuedJob job = _indexServices.removeFromIndex(securityContext,
											   	    	 oid);
		Response outResponse  = RESTOperationsResponseBuilder.searchIndex()
															 .at(URI.create(resourcePath))
															 .build(job);			
		return outResponse;
	}
	/**
	 * Un-Indexes the model objects provided
	 * @param securityContext
	 * @param resourcePath
	 * @param all
	 * @return
	 */
	public Response removeAllFromIndex(final SecurityContext securityContext,final String resourcePath,
							      	   final Collection<O> all) {
		EnqueuedJob job = null;
		if (CollectionUtils.isNullOrEmpty(all)) {
			job = _indexServices.removeAllFromIndex(securityContext);
		} else {
			job = _indexServices.removeAllFromIndex(securityContext,
												   	all);		
		}
		Response outResponse  = RESTOperationsResponseBuilder.searchIndex()
															 .at(URI.create(resourcePath))
															 .build(job);
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REINDEX	
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Indexes the provided model object oid
     * @param securityContext
	 * @param resourcePath
     * @param oid the model object
     * @return a ticket for the enqueued job
     */
	public Response reIndex(final SecurityContext securityContext,final String resourcePath,
						    final O oid) {
		EnqueuedJob job = _indexServices.reIndex(securityContext,
									      	     oid);
		Response outResponse  = RESTOperationsResponseBuilder.searchIndex()
															 .at(URI.create(resourcePath))
															 .build(job);			
		return outResponse;
	}
	/**
	 * Indexes the model objects provided
	 * @param securityContext
	 * @param resourcePath
	 * @param all
	 * @return
	 */
	public Response reIndexAll(final SecurityContext securityContext,final String resourcePath,
						       final Collection<O> all) {
		EnqueuedJob job = null;
		if (CollectionUtils.isNullOrEmpty(all)) {
			job = _indexServices.reIndexAll(securityContext);
		} else {
			job = _indexServices.reIndexAll(securityContext,
											all);
		}
		Response outResponse  = RESTOperationsResponseBuilder.searchIndex()
															 .at(URI.create(resourcePath))
															 .build(job);
		return outResponse;
	}
}
