package r01f.services.persistence;

import com.google.common.eventbus.EventBus;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.index.IndexManager;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.IndexManagementServices;
import r01f.services.interfaces.ServiceInterface;
import r01f.types.jobs.EnqueuedJob;
import r01f.types.jobs.EnqueuedJobStatus;
import r01f.types.jobs.SuppliesJobOID;


/**
 * Implements {@link IndexManagementServices} 
 */
@Accessors(prefix="_")
public abstract class CoreIndexManagementServicesBase
              extends CoreServicesBase					  
           implements IndexManagementServices,
           			  SuppliesJobOID {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private IndexManager _indexManager;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 * @param coreCfg
	 * @param modelObjectsMarshaller annotated with @ModelObjectsMarshaller
	 * @param indexManager
	 * @param eventBus
	 */
	public CoreIndexManagementServicesBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   final Marshaller modelObjectMarshaller,
										   final EventBus eventBus,
										   final IndexManager indexManager) {
		super(coreCfg,
			  modelObjectMarshaller,
			  eventBus);
		_indexManager = indexManager;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected ServiceDelegateProvider<? extends ServiceInterface> getDelegateProvider() {
		throw new UnsupportedOperationException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ServiceHandler
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start() {
		this.openIndex(null);	// no user context
	}
	@Override
	public void stop() {
		this.closeIndex(null);	// no user context
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public EnqueuedJob openIndex(final SecurityContext securityContext) {
		try {
			_indexManager.open(securityContext);
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_OK);
		} catch (Throwable th) {
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_ERROR,
								   th.getMessage()); 
		}
	}
	@Override
	public EnqueuedJob closeIndex(final SecurityContext securityContext) {
		try {
			_indexManager.close(securityContext);
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_OK);
		} catch (Throwable th) {
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_ERROR,
								   th.getMessage()); 
		}
	}
	@Override
	public EnqueuedJob optimizeIndex(final SecurityContext securityContext) {
		try {
			_indexManager.optimize(securityContext);
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_OK);
		} catch (Throwable th) {
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_ERROR,
								   th.getMessage()); 
		}
	}
	@Override
	public EnqueuedJob truncateIndex(final SecurityContext securityContext) {
		try {
			_indexManager.truncate(securityContext);
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_OK);
		} catch (Throwable th) {
			return new EnqueuedJob(this.supplyJobOID(),
								   EnqueuedJobStatus.FINALIZED_ERROR,
								   th.getMessage()); 
		}
	}

	
}
