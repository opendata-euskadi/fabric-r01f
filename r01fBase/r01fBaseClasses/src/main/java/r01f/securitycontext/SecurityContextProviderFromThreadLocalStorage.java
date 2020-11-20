package r01f.securitycontext;

import javax.inject.Provider;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides a {@link SecurityContext} previously attached to the 
 * [thread local] storage
 * Usually is used like:
 * <pre>
 * 			[security filter]  <-- attaches the [security context] to the [thread local] storage
 *                  |
 *               [CORE] <-- uses this [provider] to get the [security context] from the [thread local] storage
 * </pre>
 */
@Slf4j
public class SecurityContextProviderFromThreadLocalStorage 
  implements Provider<SecurityContext> {
/////////////////////////////////////////////////////////////////////////////////////////
//  Provider
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SecurityContext get() {
		// The Auth attached to the ThreadLocal storage at the security filter
		SecurityContext outSecurityContext = SecurityContextStoreAtThreadLocalStorage.get();
		if (outSecurityContext != null) {
			log.trace("got a [security context] attached to the [thread local] storage for login={}",
					  outSecurityContext.getLoginId());
		} else {
			log.warn("NO [security context] attached to the [thread local] storage: no security filter in use!!");
		}
		return outSecurityContext;
	}

}