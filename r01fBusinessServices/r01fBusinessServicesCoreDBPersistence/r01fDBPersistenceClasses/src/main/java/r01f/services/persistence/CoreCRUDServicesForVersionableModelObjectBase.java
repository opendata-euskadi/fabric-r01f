package r01f.services.persistence;

import java.util.Date;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.guids.VersionIndependentOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.CRUDServicesForVersionableModelObjectDelegateBase;
import r01f.services.interfaces.CRUDServicesForVersionableModelObject;


/**
 * Implements the {@link HasVersionableFacet}-related services which in turn are 
 * delegated to {@link CRUDServicesForVersionableModelObjectDelegateBase}
 */
@Accessors(prefix="_")
public abstract class CoreCRUDServicesForVersionableModelObjectBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
			  extends CoreCRUDServicesForModelObjectBase<O,M>
		   implements CRUDServicesForVersionableModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 * @param coreCfg 
	 * @param modelObjectsMarshaller annotated with @ModelObjectsMarshaller
	 * @param eventBus
	 * @param entityManagerProvider
	 */
	public CoreCRUDServicesForVersionableModelObjectBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
														 final Marshaller modelObjectsMarshaller,
									   					 final EventBus eventBus,
									   					 final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD DELEGATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Transactional
	@Override 
	public CRUDResult<M> loadActiveVersionAt(final SecurityContext securityContext,
									   		 final VersionIndependentOID oid,final Date date) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(CRUDServicesForVersionableModelObject.class)
							.loadActiveVersionAt(securityContext,
									 	   		 oid,date);
	}
	@Transactional
	@Override 
	public CRUDResult<M> loadWorkVersion(final SecurityContext securityContext,
									   	 		    final VersionIndependentOID oid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(CRUDServicesForVersionableModelObject.class)
							.loadWorkVersion(securityContext,
									   		 oid);
	}
	@Transactional
	@Override 
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(CRUDServicesForVersionableModelObject.class)
							.activate(securityContext, 
									  entityToBeActivated);
	}
	@Transactional
	@Override 
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated,
								  final PersistenceOperationCallbackSpec callbackSpec) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(CRUDServicesForVersionableModelObject.class)
							.activate(securityContext, 
									  entityToBeActivated,
									  callbackSpec);		
	}
	@Transactional
	@Override 
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext, 
													 final VersionIndependentOID oid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(CRUDServicesForVersionableModelObject.class)
							.deleteAllVersions(securityContext,
										 	   oid);
	}
	@Transactional
	@Override 
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext, 
													 final VersionIndependentOID oid,
													 final PersistenceOperationCallbackSpec callbackSpec) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(CRUDServicesForVersionableModelObject.class)
							.deleteAllVersions(securityContext,
										 	   oid,
										 	   callbackSpec);
	}
}
