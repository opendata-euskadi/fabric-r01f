package r01f.services.delegates.persistence;


import com.google.common.eventbus.EventBus;

import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.FindResult;
import r01f.persistence.db.DBFindForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.FindServicesForVersionableModelObject;

/**
 * Service layer delegated type for CRUD find operations
 */
public abstract class FindServicesForVersionableModelObjectDelegateBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
		      extends FindServicesForModelObjectDelegateBase<O,M> 
		   implements FindServicesForVersionableModelObject<O,M> {


/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR  
/////////////////////////////////////////////////////////////////////////////////////////
	public FindServicesForVersionableModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
															 final Class<M> modelObjectType,
												  		 	 final DBFindForModelObject<O,M> findServices) {
		this(coreCfg,
			 modelObjectType,
			 findServices,
			 null);
	}
	public FindServicesForVersionableModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
															 final Class<M> modelObjectType,
												  		 	 final DBFindForModelObject<O,M> findServices,
												  		 	 final EventBus eventBus) {
		super(coreCfg,
			  modelObjectType,
			  findServices,
			  eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VERSIONABLE FIND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<M> findAllVersions(final SecurityContext securityContext) {
		throw new UnsupportedOperationException("NOT jet implemented!");
	}
}
