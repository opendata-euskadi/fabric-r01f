package r01f.services.delegates.persistence.index;


import java.util.Collection;

import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;

import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.PersistableObjectOID;
import r01f.model.IndexableModelObject;
import r01f.model.persistence.PersistenceException;
import r01f.persistence.index.Indexer;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.FindServicesForModelObject;
import r01f.services.interfaces.IndexServicesForModelObject;
import r01f.services.interfaces.ServiceProviders.CRUDServiceByModelObjectOIDTypeProvider;
import r01f.services.interfaces.ServiceProviders.FindServiceByModelObjectTypeProvider;
import r01f.types.jobs.EnqueuedJob;
import r01f.types.jobs.EnqueuedJobStatus;
import r01f.types.jobs.SuppliesJobOID;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Service layer delegated type for index operations
 */
@Slf4j
public abstract class IndexServicesDelegateBase<O extends PersistableObjectOID,M extends IndexableModelObject>
		   implements IndexServicesForModelObject<O,M>,
					  SuppliesJobOID {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Class<M> _modelObjectType;
	protected final Indexer<M> _indexer;
	protected final CRUDServiceByModelObjectOIDTypeProvider _crudServiceByModelObjectOidTypeProvider;
	protected final FindServiceByModelObjectTypeProvider _findServiceByModelObjectTypeProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected IndexServicesDelegateBase(final Class<M> modelObjectType,
										final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
									  	final Indexer<M> indexer,
									  	final EventBus eventBus,
									  	final CRUDServiceByModelObjectOIDTypeProvider crudServiceByModelObjectOidTypeProvider,
									  	final FindServiceByModelObjectTypeProvider findServiceByModelObjectTypeProvider) {
		_modelObjectType = modelObjectType;
		_indexer = indexer;
		_crudServiceByModelObjectOidTypeProvider = crudServiceByModelObjectOidTypeProvider;
		_findServiceByModelObjectTypeProvider = findServiceByModelObjectTypeProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INDEX
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public EnqueuedJob index(final SecurityContext securityContext,
							 final M modelObject) {
		return _processOne(securityContext,
						   modelObject,
						   IndexOperation.INDEX);
	}
	@Override
	public EnqueuedJob updateIndex(final SecurityContext securityContext,
							 	   final M modelObject) {
		return _processOne(securityContext,
						   modelObject,
						   IndexOperation.UPDATE_INDEX);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UN-INDEX
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public EnqueuedJob removeFromIndex(final SecurityContext securityContext,
							   		   final O oid) {
		return _processOne(securityContext,
				   		   oid,
				   		   IndexOperation.UNINDEX);
	}
	@Override
	public EnqueuedJob removeAllFromIndex(final SecurityContext securityContext) {
		return _processAll(securityContext,
						   IndexOperation.UNINDEX);
	}
	@Override
	public EnqueuedJob removeAllFromIndex(final SecurityContext securityContext,
								  		  final Collection<O> all) {
		return _processAll(securityContext,
						   all,
						   IndexOperation.UNINDEX);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RE-INDEX
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public EnqueuedJob reIndex(final SecurityContext securityContext,
							   final O oid) {
		return _processOne(securityContext,
						   oid,
						   IndexOperation.REINDEX);
	}
	@Override
	public EnqueuedJob reIndexAll(final SecurityContext securityContext) {
		return _processAll(securityContext,
						   IndexOperation.REINDEX);
	}
	@Override
	public EnqueuedJob reIndexAll(final SecurityContext securityContext,
								  final Collection<O> oids) {
		return _processAll(securityContext,
						   oids,
						   IndexOperation.REINDEX);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected EnqueuedJob _processOne(final SecurityContext securityContext,
							   		  final M modelObject,
							   		  final IndexOperation operation) {
		// Indexing is a synchronous task that does NOT (for the moment) throw any exception
		EnqueuedJob outJob = null;
		switch(operation) {
		case INDEX:
		case REINDEX:
			_indexer.index(securityContext,
						   modelObject);
			break;
		case UPDATE_INDEX:
			_indexer.updateIndex(securityContext,
						   		 modelObject);
			break;
		default:
			throw new IllegalStateException();
		}
		outJob = new EnqueuedJob(this.supplyJobOID(),
     			 				 EnqueuedJobStatus.FINALIZED_OK);		// TODO implement in an asynchronous way
		return outJob;
	}
	@SuppressWarnings("unchecked")
	private EnqueuedJob _processOne(final SecurityContext securityContext,
							   		final O oid,
							   		final IndexOperation operation) {
		EnqueuedJob outJob = null;
		// index / un-index
		// Indexing is a synchronous task that does NOT (for the moment) throw any exception
		switch(operation) {
		case UNINDEX:
			_indexer.removeFromIndex(securityContext,
				 	 				 oid);
			break;
		case REINDEX:
			try {
				// Load the model object using a crud that's guessed by the model object's oid type
				CRUDServicesForModelObject<O,?> versionablePersistServices = _crudServiceByModelObjectOidTypeProvider.getFor(oid.getClass());
				M modelObject = (M) versionablePersistServices.load(securityContext,
								      		  	  				oid)
								      		  	  		  .getOrThrow();
				// Index
				_indexer.updateIndex(securityContext,
						       		 modelObject);
			} catch(PersistenceException persistEx) {
				outJob = new EnqueuedJob(this.supplyJobOID(),
		     	 						 EnqueuedJobStatus.FINALIZED_ERROR,
		     	 						 Throwables.getStackTraceAsString(persistEx));
			}
			break;
		default:
			throw new IllegalStateException();
		}
		outJob = new EnqueuedJob(this.supplyJobOID(),
		     			 		 EnqueuedJobStatus.FINALIZED_OK);		// TODO implement in an asynchronous way
		return outJob;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	private EnqueuedJob _processAll(final SecurityContext securityContext,
									final IndexOperation operation) {
		EnqueuedJob outJob = null;
		try {
			// Load all persisted entities oids
			FindServicesForModelObject<O,?>  findServices= _findServiceByModelObjectTypeProvider.getFor(_modelObjectType);
			Collection<O> all = findServices.findAll(securityContext)
											.getOrThrow();
			// index / un-index
			switch(operation) {
			case REINDEX:
				outJob = _processAll(securityContext,
						   			 all,
						   			 IndexOperation.REINDEX);
				break;
			case UNINDEX:
				outJob = _processAll(securityContext,
						   			 all,
						   			 IndexOperation.UNINDEX);
				break;
			default:
				throw new IllegalStateException();
			}
		} catch(PersistenceException persistEx) {
			outJob = new EnqueuedJob(this.supplyJobOID(),
	     	 						 EnqueuedJobStatus.FINALIZED_ERROR,
	     	 						 Throwables.getStackTraceAsString(persistEx));
		}
		return outJob;
	}
	private EnqueuedJob _processAll(final SecurityContext securityContext,
									final Collection<O> all,
									final IndexOperation operation) {
		EnqueuedJob outJob = null;
		if (CollectionUtils.isNullOrEmpty(all)) {
			log.warn("NOT {}ing any {} since the provided oid set is null",operation,_modelObjectType);
			outJob = new EnqueuedJob(this.supplyJobOID(),
								     EnqueuedJobStatus.FINALIZED_OK,
								     Strings.customized("NOT reindexing any {} since the provided oid set is null",_modelObjectType));
		} else {
			// TODO implement in an async way WTF!
			log.warn("{}ing all {} records ({} to be processed)",operation,_modelObjectType,all.size());

			int i = 1;
			for (O oid : all) {
				log.warn("[{} of {}]: {} a {} record with oid: {}",
						 i,all.size(),operation,_modelObjectType.getSimpleName(),oid);
				// index / un-index
				switch(operation) {
				case REINDEX:
					_processOne(securityContext,
								oid,
								IndexOperation.REINDEX);
					break;
				case UNINDEX:
					_processOne(securityContext,
								oid,
								IndexOperation.UNINDEX);
					break;
				default:
					throw new IllegalStateException();
				}
				i++;
			}
			// All OK
			outJob = new EnqueuedJob(this.supplyJobOID(),
				     			 	 EnqueuedJobStatus.FINALIZED_OK);
		}
		return outJob;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected enum IndexOperation {
		INDEX,
		UPDATE_INDEX,
		UNINDEX,
		REINDEX;
	}
}
