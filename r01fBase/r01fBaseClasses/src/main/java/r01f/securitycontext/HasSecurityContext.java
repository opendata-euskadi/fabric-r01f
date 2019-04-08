package r01f.securitycontext;

/**
 * Interface for objects containing a {@link SecurityContext}
 */
public interface HasSecurityContext {
	public <U extends SecurityContext> U getSecurityContext();
}
