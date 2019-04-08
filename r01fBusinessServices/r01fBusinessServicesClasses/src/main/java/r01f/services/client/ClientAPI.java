package r01f.services.client;

import java.util.Map;

import javax.inject.Provider;

import r01f.model.API;
import r01f.objectstreamer.HasMarshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ServiceInterface;


/**
 * Client API 
 */
public interface ClientAPI
		 extends API,
		 		 HasMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link ClientAPI} typed
	 * @param type
	 * @return
	 */
	public <A extends ClientAPI> A as(final Class<A> type);
/////////////////////////////////////////////////////////////////////////////////////////
//  SECURITY CONTEXT
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the user context
	 */
	public <U extends SecurityContext> U getSecurityContext();
	/**
	 * @return the user context provider
	 */
	public Provider<SecurityContext> getSecurityContextProvider();
/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICE INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the service interface to core impl or proxy matchings
	 */
	@SuppressWarnings("rawtypes")
	public Map<Class,ServiceInterface> getServiceInterfaceMappings();
	/**
	 * Returns a {@link ServiceInterface}'s core impl or proxy to the core impl
	 * @param serviceInterfaceType
	 * @return
	 */
	public <S extends ServiceInterface> S getServiceInterfaceCoreImplOrProxy(final Class<S> serviceInterfaceType);
}