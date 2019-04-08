package r01f.services.interfaces;

import r01f.securitycontext.SecurityContext;
import r01f.service.ServiceHandler;
import r01f.types.jobs.EnqueuedJob;


/**
 * Services for managing search engine indexes
 */
public interface IndexManagementServices 
		 extends ServiceInterface,
		 		 ServiceHandler {	// used to start & stop services (see ServletContextListenerBase)
/////////////////////////////////////////////////////////////////////////////////////////
//  INDEX MANAGEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Opens the index
	 * @param securityContext
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob openIndex(final SecurityContext securityContext);
	/**
	 * Closes the index
	 * @param securityContext
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob closeIndex(final SecurityContext securityContext);
	/**
	 * Optimizes the index
	 * @param securityContext
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob optimizeIndex(final SecurityContext securityContext);
	/**
	 * Truncates the index removing all records
	 * @param securityContext
	 * @return a job oid that provides a way to later know the job status
	 */
	public EnqueuedJob truncateIndex(final SecurityContext securityContext);
}
