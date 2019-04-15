package r01f.services.delegates.persistence;


import com.google.common.eventbus.EventBus;

import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CountResult;
import r01f.persistence.db.DBCountForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CountServicesForModelObject;

/**
 * Service layer delegated type for COUNT operations
 */
public abstract class CountServicesForModelObjectDelegateBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
		      extends PersistenceServicesForModelObjectDelegateBase<O,M>
		   implements CountServicesForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public CountServicesForModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												   final Class<M> modelObjectType,
											 	   final DBCountForModelObject<O,M> dbCrud,
											 	   final EventBus eventBus) {
		super(coreCfg,
			  modelObjectType,
			  dbCrud,
			  eventBus);
	}
	public CountServicesForModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												   final Class<M> modelObjectType,
											 	   final DBCountForModelObject<O,M> dbCrud) {
		this(coreCfg,
			 modelObjectType,
		     dbCrud,
		     null);		// no event bus
	}
////////////////////////////////////////////////////////////////////////////////////////
//  LOAD | EXISTS
////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public CountResult<M> countAll(final SecurityContext securityContext) {
		CountResult<M> outCount = this.getServiceImplAs(CountServicesForModelObject.class)
											.countAll(securityContext);
		return outCount;
	}
}
