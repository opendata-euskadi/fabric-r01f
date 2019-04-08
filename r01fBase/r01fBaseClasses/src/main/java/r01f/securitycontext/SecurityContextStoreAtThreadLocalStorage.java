package r01f.securitycontext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Manages the {@link SecurityContext} attached to the [thread local] storage
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SecurityContextStoreAtThreadLocalStorage {
/////////////////////////////////////////////////////////////////////////////////////////
//	STATIC FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private static ThreadLocal<SecurityContext> SECURITY_CONTEXT = new ThreadLocal<SecurityContext>();

/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	// Remove
	public static void remove() {
		SECURITY_CONTEXT.remove();
	}
	// set
	public static void set(final SecurityContext ctx) {
		SECURITY_CONTEXT.set(ctx);
	}
	public static SecurityContext get() {
	  return SECURITY_CONTEXT.get();
	}
}
