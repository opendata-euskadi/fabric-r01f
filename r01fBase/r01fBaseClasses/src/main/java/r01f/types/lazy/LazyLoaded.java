package r01f.types.lazy;

/**
 * Lazy loaded objects
 */
public interface LazyLoaded {
	/**
	 * @return true if the object is loaded
	 */
	public boolean isLoaded();
	/**
	 * Sets the component as loaded
	 * @param loaded
	 */
	public void setLoaded(boolean loaded);
}
