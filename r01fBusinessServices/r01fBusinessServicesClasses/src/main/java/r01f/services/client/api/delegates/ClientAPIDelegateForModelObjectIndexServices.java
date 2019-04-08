package r01f.services.client.api.delegates;

import java.util.Collection;

import javax.inject.Provider;

import r01f.guids.OID;
import r01f.model.IndexableModelObject;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.IndexServicesForModelObject;
import r01f.types.jobs.EnqueuedJob;

/**
 * Adapts index API method invocations to the service proxy that performs the core method invocations
 * @param <F>
 * @param <I>
 */
public abstract class ClientAPIDelegateForModelObjectIndexServices<O extends OID,M extends IndexableModelObject>
	 		  extends ClientAPIServiceDelegateBase<IndexServicesForModelObject<O,M>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForModelObjectIndexServices(final Provider<SecurityContext> securityContextProvider,
														final Marshaller modelObjectsMarshaller,
								   			 			final IndexServicesForModelObject<O,M> services) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INDEX
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Indexes the provided model object
     * @param modelObject the model object
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob index(final M modelObject) {
		return this.getServiceProxy()
						.index(this.getSecurityContext(),
							   modelObject);
	}
    /**
     * Updates the indexed info for the provided model object
     * @param modelObject the model object
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob updateIndex(final M modelObject) {
		return this.getServiceProxy()
						.updateIndex(this.getSecurityContext(),
									 modelObject);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REMOVE FROM INDEX
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Un-Indexes the model object whose oid is provided
     * @param oid the model object's oid
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob removeFromIndex(final O oid) {
		return this.getServiceProxy()
						.removeFromIndex(this.getSecurityContext(),
								 		 oid);
	}
	/**
	 * Un-Indexes all records 
	 * @return
	 */
	public EnqueuedJob removeAllFromIndex() {
		return this.getServiceProxy()
						.removeAllFromIndex(this.getSecurityContext());
	}
		/**
	 * Un-Indexes the model objects provided
	 * @param all
	 * @return
	 */
	public EnqueuedJob removeAllFromIndex(final Collection<O> all) {
		return this.getServiceProxy()
						.removeAllFromIndex(this.getSecurityContext(),
											all);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RE-INDEX
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Updates the indexed info for the model object whose oid is provided
     * @param oid the model object's oid
     * @return a ticket for the enqueued job
     */
	public EnqueuedJob reIndex(final O oid) {
		return this.getServiceProxy()
						.reIndex(this.getSecurityContext(),
							     oid);
	}
	/**
	 * Indexes the model objects provided
	 * @param all
	 * @return
	 */
	public EnqueuedJob reIndexAll(final Collection<O> all) {
		return this.getServiceProxy()
						.reIndexAll(this.getSecurityContext(),
								  all);
	}
	/**
	 * Indexes all records 
	 * @param securityContext
	 * @return
	 */
	public EnqueuedJob reIndexAll() {
		return this.getServiceProxy()
						.reIndexAll(this.getSecurityContext());
	}
}
