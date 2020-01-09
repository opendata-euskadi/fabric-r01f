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
	public static <S extends SecurityContext> void set(final S ctx) {
		SECURITY_CONTEXT.set(ctx);
	}
	@SuppressWarnings("unchecked")
	public static <S extends SecurityContext> S get() {
		return (S)SECURITY_CONTEXT.get();
	}
}
