package r01f.services.client;

import java.util.Map;

import javax.inject.Provider;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ServiceInterface;

/**
 * Base for every sub-api
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class ClientSubAPIBase
		   implements HasMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  STATUS (injected by constructor)
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * User context
	 */
	protected final Provider<SecurityContext>  _securityContextProvider;
	/**
	 * Marshaller
	 */
	protected final Marshaller _modelObjectsMarshaller;
	/**
	 * A guice's Map binder that provides a {@link ServiceInterface}'s core impl or proxy to the core impl
	 */
	@SuppressWarnings("rawtypes")
	protected final Map<Class,ServiceInterface> _srvcIfaceMappings;
/////////////////////////////////////////////////////////////////////////////////////////
//  SECURITY CONTEXT
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a provider of the security context
	 */
	public Provider<SecurityContext> getSecurityContextProvider() {
		return _securityContextProvider;
	}
	/**
	 * @return the provided security context
	 */
	@SuppressWarnings("unchecked")
	public <U extends SecurityContext> U getSecurityContext() {
		return (U)_securityContextProvider.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MARSHALLER
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Marshaller getModelObjectsMarshaller() {
		return _modelObjectsMarshaller;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICE INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <S extends ServiceInterface> S getServiceInterfaceCoreImplOrProxy(final Class<S> serviceInterfaceType) {
		S outSrvcIfaceCoreImplOrProxy = _srvcIfaceMappings != null ? (S)_srvcIfaceMappings.get(serviceInterfaceType)
										  						   : null;
		return outSrvcIfaceCoreImplOrProxy;
	}
}
