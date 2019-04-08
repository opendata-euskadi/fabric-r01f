package r01f.services.client.api.delegates;

import javax.inject.Provider;

import com.google.common.reflect.TypeToken;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.generics.TypeRef;
import r01f.model.ModelObject;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ServiceInterface;

@Accessors(prefix="_")
public abstract class ClientAPIServiceDelegateBase<S extends ServiceInterface> 
		   implements HasMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The user context
	 */
	@Getter protected final Provider<SecurityContext> _securityContextProvider;
	/**
	 * {@link ModelObject}s {@link Marshaller}
	 */
	@Getter protected final Marshaller _modelObjectsMarshaller;
	/**
	 * The service interface 
	 */
	@Getter protected final S _serviceProxy;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIServiceDelegateBase(final Provider<SecurityContext> securityContextProvider,
										final Marshaller modelObjectsMarshaller,
										final S serviceProxy) {
		_securityContextProvider = securityContextProvider;
		_modelObjectsMarshaller = modelObjectsMarshaller;
		_serviceProxy = serviceProxy;		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <U extends SecurityContext> U getSecurityContext() {
		return (U)_securityContextProvider.get();
	}
	@SuppressWarnings("unchecked")
	public <T extends ServiceInterface> T getServiceProxyAs(final Class<T> type) {
		return (T)_serviceProxy;
	}
	@SuppressWarnings("unchecked")
	public <T extends ServiceInterface> T getServiceProxyAs(final TypeToken<T> type) {
		return (T)_serviceProxy;
	}
	@SuppressWarnings("unchecked")
	public <T extends ServiceInterface> T getServiceProxyAs(final TypeRef<T> type) {
		return (T)_serviceProxy;
	}
}

