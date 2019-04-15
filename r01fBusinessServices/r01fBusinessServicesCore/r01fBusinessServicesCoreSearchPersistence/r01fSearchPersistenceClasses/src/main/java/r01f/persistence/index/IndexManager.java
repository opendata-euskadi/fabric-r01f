package r01f.persistence.index;

import r01f.securitycontext.SecurityContext;

/**
 * Interface to be implemented by types in charge to manage indexes
 * @param <M>
 */
public interface IndexManager {
	/**
	 * Closes the index
	 * @param securityContext
	 */
	public void open(final SecurityContext securityContext);
	/**
	 * Closes the index
	 * @param securityContext
	 */
	public void close(final SecurityContext securityContext);
	/**
	 * Optimizes the index
	 * @param securityContext
	 */
	public void optimize(final SecurityContext securityContext);
	
	/**
	 * Truncates the index (removes all documents)
	 * @param securityContext
	 */
	public void truncate(final SecurityContext securityContext);
}
