package r01f.services.delegates;

import javax.inject.Provider;

import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ServiceInterface;

public abstract class ServicesDelegateProvider<D extends ServiceInterface> 
	       implements Provider<D> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract D get(final SecurityContext securityContext);
	
	@Override
	public D get() {
		return this.get(null);	// no user context
	}
}
