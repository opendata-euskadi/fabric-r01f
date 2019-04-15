package r01f.services.client.servicesproxy.rest;

import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.FindResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase;
import r01f.services.interfaces.FindServicesForVersionableModelObject;

public abstract class RESTServicesForVersionableFindProxyBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
              extends RESTServicesForDBFindProxyBase<O,M>
           implements FindServicesForVersionableModelObject<O,M> {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public <P extends RESTServiceResourceUrlPathBuilderForVersionableModelObjectPersistenceBase<O>>
		   RESTServicesForVersionableFindProxyBase(final Marshaller marshaller,
											       final Class<M> modelObjectType,
											   	   final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  modelObjectType,
			  servicesRESTResourceUrlPathBuilder);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<M> findAllVersions(final SecurityContext securityContext) {
		return null;
	}
}
