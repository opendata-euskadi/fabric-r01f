package r01f.services.client.servicesproxy.rest;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderForModelObjectPersistence;

@Accessors(prefix="_")
public abstract class RESTServicesForModelObjectProxyBase<O extends OID,M extends ModelObject> 
              extends RESTServicesProxyBase {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The model object type
	 */
	@Getter protected final Class<M> _modelObjectType;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public <P extends RESTServiceResourceUrlPathBuilderForModelObjectPersistence<O>> 
		   RESTServicesForModelObjectProxyBase(final Marshaller marshaller,
											   final Class<M> modelObjectType,
											   final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  servicesRESTResourceUrlPathBuilder);
		_modelObjectType = modelObjectType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link SecurityContext} as XML
	 * @param securityContext
	 * @return
	 */
	protected String _securityContextXml(final SecurityContext securityContext) {
		return _marshaller.forWriting().toXml(securityContext);
	}
}
