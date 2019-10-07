package r01f.aspects.interfaces.lazyload;

import java.util.Map;

/**
 * Lazy loaded map provider
 * @param <K> key 
 * @param <V> value
 */
public interface LazyMapSupplier<K,V> 
         extends LazyLoadedTypeSupplier<V> {
	/**
	 * Builds an instance of the lazily loaded Map to be contained at the provided container object
	 * @param containerObj the map container
	 * @return the built map
	 */
	public <C> Map<K,V> instanceFor(C containerObj);
}
