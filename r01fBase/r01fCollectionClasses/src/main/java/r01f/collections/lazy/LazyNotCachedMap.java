package r01f.collections.lazy;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Supplier;

import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.KeyIntrospector;
import r01f.collections.lazy.LazyCollectionsInterfaces.MapValuesSupplier;

/**
 * {@link Map} wrapper that implements Lazy loading
 * @param <K> Map keys type
 * @param <V> Map values type
 */
public class LazyNotCachedMap<K,V> 
     extends LazyMapBase<K,V> {
////////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * underlying map
	 */
	@NotDirtyStateTrackable
	protected final Map<K,V> _currentEntries;
	/**
	 * values supplier
	 */
	@NotDirtyStateTrackable
	protected final MapValuesSupplier<K,V> _valuesSupplier;
////////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////////////////
	LazyNotCachedMap(final boolean fullLoadedOnCreation,
					 final KeyIntrospector<K,V> keyIntrospector,
					 final Supplier<Set<K>> keySetSupplier,final MapValuesSupplier<K,V> valuesSupplier,
					 final Map<K,V> currentEntriesMapInstance) {
		super(fullLoadedOnCreation,
			  keyIntrospector, 
			  keySetSupplier);
		_valuesSupplier = valuesSupplier;
		_currentEntries = currentEntriesMapInstance;
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	LazyMap
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isLoaded(final K key) {
		return _currentEntries.containsKey(key);
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	Abstract methods
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected V supplyValue(final K key) {
		// Use the value supplier to lazily load the value
		V outVal = null;
		try {
			outVal = _valuesSupplier.loadValue(key);
			if (outVal != null) _currentEntries.put(key,outVal);
		} catch(Exception ex) {
			throw new RuntimeException(ex);		// Transform any exception to a RuntimeException
		}
		return outVal;
	}
	@Override
	protected V putInCurrentEntries(final K key,final V value) {
		V outVal = _currentEntries.put(key,value);		// PUT at the underlying map
		return outVal;
	}
	@Override
	protected V removeFromCurrentEntries(final K key) {
		V outVal = _currentEntries.remove(key);
		return outVal;
	}
	@Override
	protected void clearCurrentEntries() {
		_currentEntries.clear();
	}
	@Override
	protected Map<K,V> currentEntries() {
		return _currentEntries;
	}	
}
