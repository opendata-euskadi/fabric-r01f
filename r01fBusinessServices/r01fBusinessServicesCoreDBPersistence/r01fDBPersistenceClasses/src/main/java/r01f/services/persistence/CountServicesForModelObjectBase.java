package r01f.services.persistence;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CountResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CountServicesForModelObject;


/**
 * Implements the {@link R01MStructure} persistence-related services which in turn are delegated to {@link R01YCRUDServicesDelegateForStructure}
 */
@Singleton
@Accessors(prefix="_")
public abstract class CountServicesForModelObjectBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>> 
     		  extends CorePersistenceServicesBase
     	   implements CountServicesForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public CountServicesForModelObjectBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   final Marshaller modelObjectsMarshaller,
										   final EventBus eventBus,
										   final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////						  	
	
	@Override @SuppressWarnings("unchecked")
	public CountResult<M> countAll(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
				   		.createDelegateAs(CountServicesForModelObject.class)
				   		.countAll(securityContext);
	}
}
