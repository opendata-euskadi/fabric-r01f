package r01f.services.persistence;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.TenantID;
import r01f.guids.PersistableObjectOID;
import r01f.model.IndexableModelObject;
import r01f.model.ModelObject;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.index.HasIndexerProvider;
import r01f.persistence.index.Indexer;
import r01f.persistence.index.IndexerProvider;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.FindServicesForModelObject;
import r01f.services.interfaces.IndexServicesForModelObject;
import r01f.services.interfaces.ServiceProviders.CRUDServiceByModelObjectOIDTypeProvider;
import r01f.services.interfaces.ServiceProviders.FindServiceByModelObjectTypeProvider;
import r01f.types.jobs.EnqueuedJob;


/**
 * Implements the {@link ModelObject} index-related services which in turn are delegated to 
 * a delegated object
 */
@Accessors(prefix="_")
public abstract class CoreIndexServicesForModelObjectBase<O extends PersistableObjectOID,M extends IndexableModelObject>
              extends CoreServicesBase					  
           implements HasIndexerProvider<M>,
           			  IndexServicesForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final IndexerProvider<M> _indexerProvider;
	
	protected final CRUDServiceByModelObjectOIDTypeProvider _crudServiceByModelObjectOidTypeProvider;
	protected final FindServiceByModelObjectTypeProvider _findServiceByModelObjectTypeProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 * @param coreCfg
	 * @param modelObjectsMarshaller annotated with @ModelObjectsMarshaller
	 * @param eventBus
	 * @param indexer
	 * @param crudServiceByModelObjectOidTypeProvider
	 * @param findServiceByModelObjectTypeProvider
	 */
	public CoreIndexServicesForModelObjectBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											   final Marshaller marshaller,
										   	   final EventBus eventBus,
										   	   final IndexerProvider<M> indexerProvider,
											   final CRUDServiceByModelObjectOIDTypeProvider crudServiceByModelObjectOidTypeProvider,
											   final FindServiceByModelObjectTypeProvider findServiceByModelObjectTypeProvider) {
		super(coreCfg,
			  marshaller,
			  eventBus);
		_indexerProvider = indexerProvider;
		_crudServiceByModelObjectOidTypeProvider = crudServiceByModelObjectOidTypeProvider;
		_findServiceByModelObjectTypeProvider = findServiceByModelObjectTypeProvider;
	}
	public <P extends PersistableModelObject<O> & IndexableModelObject> CoreIndexServicesForModelObjectBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
																											final Marshaller marshaller,
										   	   																final EventBus eventBus,
										   	   																final IndexerProvider<M> indexerProvider,
											   																final CRUDServicesForModelObject<O,P> crudService,
											   																final FindServicesForModelObject<O,P> findService) {
		super(coreCfg,
		      marshaller,
			  eventBus);
		_indexerProvider = indexerProvider;
		_crudServiceByModelObjectOidTypeProvider = new CRUDServiceByModelObjectOIDTypeProvider() {
															@Override @SuppressWarnings("unchecked")
															public <O2 extends PersistableObjectOID,M2 extends PersistableModelObject<O2>> CRUDServicesForModelObject<O2,M2> getFor(final Class<? extends PersistableObjectOID> type) {
																return (CRUDServicesForModelObject<O2,M2>)crudService;
															}
							   					   };
		_findServiceByModelObjectTypeProvider = new FindServiceByModelObjectTypeProvider() {
															@Override @SuppressWarnings("unchecked")
															public <O2 extends PersistableObjectOID,M2 extends PersistableModelObject<O2>> FindServicesForModelObject<O2,M2> getFor(final Class<?> type) {
																return (FindServicesForModelObject<O2,M2>)findService;
															}
												};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HAS INDEXER PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Indexer<M> getFreshNewIndexer() {
		return _indexerProvider.get();
	}
	@Override
	public Indexer<M> getFreshNewIndexer(final TenantID tenantId) {
		return _indexerProvider.get();		
	}
	@Override
	public Indexer<M> getFreshNewIndexer(final SecurityContext securityContext) {
		return _indexerProvider.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PERSISTENCE ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDServiceByModelObjectOIDTypeProvider getCRUDServiceByModelObjectOIDTypeProvider() {
		return _crudServiceByModelObjectOidTypeProvider;
	}
	public FindServiceByModelObjectTypeProvider getFindServiceByModelObjectTypeProvider() {
		return _findServiceByModelObjectTypeProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INDEX
/////////////////////////////////////////////////////////////////////////////////////////
	
	@Override @SuppressWarnings("unchecked")
	public EnqueuedJob index(final SecurityContext securityContext,
							 final M modelObject) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
								.index(securityContext,
									   modelObject);
	}
	@Override @SuppressWarnings("unchecked")
	public EnqueuedJob updateIndex(final SecurityContext securityContext,
							 	   final M modelObject) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
							.updateIndex(securityContext,
										 modelObject);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UN-INDEX
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public EnqueuedJob removeFromIndex(final SecurityContext securityContext,
							   		   final O oid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
							.removeFromIndex(securityContext,
						 			 		 oid);
	}
	@Override 
	public EnqueuedJob removeAllFromIndex(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
							.removeAllFromIndex(securityContext);
	}
	@Override @SuppressWarnings("unchecked")
	public EnqueuedJob removeAllFromIndex(final SecurityContext securityContext,
								  		  final Collection<O> all) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
							.removeAllFromIndex(securityContext,
												all);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RE-INDEX
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public EnqueuedJob reIndex(final SecurityContext securityContext,
							   final O oid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
							.reIndex(securityContext,
								     oid);
	}
	@Override @SuppressWarnings("unchecked")
	public EnqueuedJob reIndexAll(final SecurityContext securityContext,
								  final Collection<O> all) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
							.reIndexAll(securityContext,
									  all);
	}
	@Override
	public EnqueuedJob reIndexAll(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(IndexServicesForModelObject.class)
								.reIndexAll(securityContext);
	}
}
