package r01f.types.hierarchy;

/**
 * Models objects that have a single direct ancestor
 * @param <T>
 */
public interface HasSingleParent<T> {
	/**
	 * Sets the parent object
	 * @param parent
	 */
	public void setDirectAncestor(T parent);	// do not rename to setParent (colides with GWT Widget's type setParent(..)
	/**
	 * Gets te parent object
	 * @return the parent object
	 */
	public T getDirectAncestor();				// do not rename to setParent (colides with GWT Widget's type setParent(..)
}
