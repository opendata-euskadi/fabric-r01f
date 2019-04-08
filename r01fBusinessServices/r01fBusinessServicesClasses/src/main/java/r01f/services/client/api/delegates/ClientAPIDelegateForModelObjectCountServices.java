package r01f.services.client.api.delegates;

import javax.inject.Provider;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CountResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CountServicesForModelObject;

/**
 * Adapts Persistence API method invocations to the service proxy that performs the core method invocations
 * @param <O>
 * @param <M>
 */
@Slf4j
public abstract class ClientAPIDelegateForModelObjectCountServices<O extends PersistableObjectOID,M extends PersistableModelObject<O>> 
	          extends ClientAPIServiceDelegateBase<CountServicesForModelObject<O,M>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForModelObjectCountServices(final Provider<SecurityContext> securityContextProvider,
													    final Marshaller modelObjectsMarshaller,
												   	    final CountServicesForModelObject<O,M> services) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Conuts all entities
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public long countAll() {
		CountResult<M> countResult = this.getServiceProxy()
											.countAll(this.getSecurityContext());
		
		log.debug(countResult.debugInfo().toString());
		
		long num = countResult.getOrThrow();
		return num;
	}	
}
