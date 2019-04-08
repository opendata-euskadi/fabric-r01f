package r01f.services.persistence;

import r01f.securitycontext.SecurityContext;
import r01f.services.core.CoreService;
import r01f.services.interfaces.ServiceInterface;

/**
 * A provider os {@link ServiceInterface} delegates to be used at {@link CoreService}
 * @param <D>
 */
public interface ServiceDelegateProvider<D extends ServiceInterface> {
	/**
	 * Creates a delegate
	 * @param securityContext
	 * @return
	 */
	public D createDelegate(final SecurityContext securityContext);
}