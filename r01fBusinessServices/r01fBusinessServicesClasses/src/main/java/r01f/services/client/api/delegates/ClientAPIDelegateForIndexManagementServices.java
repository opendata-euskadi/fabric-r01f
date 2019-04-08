package r01f.services.client.api.delegates;

import javax.inject.Provider;

import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.IndexManagementServices;
import r01f.types.jobs.EnqueuedJob;

/**
 * Adapts Index management API method invocations to the service proxy that performs the core method invocations
 */
public final class ClientAPIDelegateForIndexManagementServices<S extends IndexManagementServices>
	 	   extends ClientAPIServiceDelegateBase<S> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForIndexManagementServices(final Provider<SecurityContext> securityContextProvider,
													   final Marshaller modelObjectsMarshaller,
													   final S services) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Opens the index
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob openIndex() {
		return this.getServiceProxy()
						.openIndex(this.getSecurityContext());
	}
	/**
	 * Closes the index
	 * @param securityContext
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob closeIndex() {
		return this.getServiceProxy()
						.closeIndex(this.getSecurityContext());		
	}
	/**
	 * Optimizes the index
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob optimizeIndex() {
		return this.getServiceProxy()
						.optimizeIndex(this.getSecurityContext());
	}
	/**
	 * Truncates the index removing all records
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob truncateIndex() {
		return this.getServiceProxy()
						.truncateIndex(this.getSecurityContext());
	}
}
