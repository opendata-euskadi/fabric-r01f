package r01f.aspects.interfaces.lazyload;

import java.util.Collection;

/**
 * Lazy loaded map provider
 * @param <K> key 
 * @param <V> value
 */
public interface LazyCollectionSupplier<V> 
         extends LazyLoadedTypeSupplier<V> {
	/**
	 * Builds an instance of the lazily loaded Collection to be contained at the provided container object
	 * @param containerObj the map container
	 * @return the built map
	 */
	public <C> Collection<V> instanceFor(C containerObj);
}
