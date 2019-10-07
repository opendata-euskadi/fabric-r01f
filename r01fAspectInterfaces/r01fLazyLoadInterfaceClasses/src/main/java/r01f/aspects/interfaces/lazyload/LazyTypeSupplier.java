package r01f.aspects.interfaces.lazyload;

/**
 * Lazily-loaded type supplier
 * @param <T> lazily loaded type
 */
public interface LazyTypeSupplier<T> 
         extends LazyLoadedTypeSupplier<T> {
	/**
	 * Gets an instance of a lazily-loaded type contained in another object instance provided
	 * @param containerObj container object
	 * @return
	 */
	public <C> T instanceFor(final C containerObj);

}
