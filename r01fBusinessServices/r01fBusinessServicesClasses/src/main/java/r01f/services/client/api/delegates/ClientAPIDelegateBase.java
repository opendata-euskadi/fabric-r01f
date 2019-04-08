package r01f.services.client.api.delegates;

import java.util.Map;

import r01f.securitycontext.HasSecurityContext;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ServiceInterface;

public abstract class ClientAPIDelegateBase 
		   implements HasSecurityContext {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The user context
	 */
	protected final SecurityContext _securityContext;
	/**
	 * A guice's Map binder that provides a {@link ServiceInterface}'s core impl or proxy to the core impl
	 */
	@SuppressWarnings("rawtypes")
	protected final Map<Class,ServiceInterface> _srvcIfaceMappings;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes") 
	protected ClientAPIDelegateBase(final SecurityContext securityContext,
								 	final Map<Class,ServiceInterface> srvcIfaceMappings) {
		_securityContext = securityContext;
		_srvcIfaceMappings = srvcIfaceMappings;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <U extends SecurityContext> U getSecurityContext() {
		return (U)_securityContext;
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
