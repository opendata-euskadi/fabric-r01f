package r01f.collections.lazy;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;

import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.KeyIntrospector;

/**
 * Guava's {@link LoadingCache} wrapper to implement lazy-loaded {@link Map}
 * @param <K> keys type
 * @param <V> values type
 */
public class LazyCachedMap<K,V> 
     extends LazyMapBase<K,V> {
////////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Underlying map
	 */
	@NotDirtyStateTrackable
	protected final LoadingCache<K,V> _currentEntries;
////////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////////////////
	LazyCachedMap(final boolean fullLoadedOnCreation,
				  final KeyIntrospector<K,V> keyIntrospector,
				  final Supplier<Set<K>> keySetSupplier,
				  final LoadingCache<K,V> currentEntriesMapInstance) {
		super(fullLoadedOnCreation,
			  keyIntrospector, 
			  keySetSupplier);
		
		_currentEntries = currentEntriesMapInstance;
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	LAZY MAP
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isLoaded(final K key) {
		return _currentEntries.getIfPresent(key) != null;
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	ABSTRACT METHODS
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected V supplyValue(final K key) {
		// Simply ask for a new key to force Guava Cache ask for the value
		try {
			return _currentEntries.get(key);
		} catch(Exception ex) {
			throw new RuntimeException(ex);		// convert to RuntimeException
		}
	}
	@Override
	protected V putInCurrentEntries(final K key,final V value) {
		V outVal = _currentEntries.getIfPresent(key);		// Get the current value (if loaded)
		_currentEntries.put(key,value);						// put at the cache...
		return outVal;
	}
	@Override
	protected V removeFromCurrentEntries(final K key) {
		V outVal = _currentEntries.getIfPresent(key);
		if (outVal != null) _currentEntries.invalidate(key);	// remove from the cache
		return outVal;
	}
	@Override
	protected void clearCurrentEntries() {
		_currentEntries.invalidateAll();
	}
	@Override
	protected Map<K,V> currentEntries() {
		return _currentEntries.asMap();
	}
}
