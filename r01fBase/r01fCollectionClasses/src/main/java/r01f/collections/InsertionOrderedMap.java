package r01f.collections;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

/**
 * Insertion ordered {@link Map} that adds methods like: 
 * <code>firstKey</code>
 * <code>lastKey</code>,
 * <code>nextKey</code>
 * <code>previousKey</code> 
 */
public class InsertionOrderedMap<K,V> 
     extends ForwardingMap<K,V> {
///////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////	
	private final LinkedHashMap<K,V> _delegate;
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR & FACTORY
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Constructor
	 * @param delegate 
	 */
	public InsertionOrderedMap() {
		_delegate = Maps.newLinkedHashMap();		
	}
	public InsertionOrderedMap(final int size) {
		_delegate = Maps.newLinkedHashMapWithExpectedSize(size);
	}
	/**
	 * Factory
	 * @return
	 */
	public static <K,V> Map<K,V> create() {
		return new InsertionOrderedMap<K,V>();
	}
///////////////////////////////////////////////////////////////////////////////
// 	OVERRIDE
///////////////////////////////////////////////////////////////////////////////	
	@Override
	protected Map<K,V> delegate() {		
		Map<K,V> outMap = _delegate;
		return outMap;
	}	
///////////////////////////////////////////////////////////////////////////////
// 	METHODS
///////////////////////////////////////////////////////////////////////////////
	public K firstKey() {	
		K firstKey = FluentIterable.from(_delegate.keySet())
								  .first()
								  .orNull();
		return firstKey;
	}
	public K lastKey() {
		K lastKey = FluentIterable.from(_delegate.keySet())
								  .last()
								  .orNull();
		return lastKey;
	}
	public K nextKey(final K ofKey) {
		K nextKey = null;
		boolean found = false;
		for (K key : _delegate.keySet()) {
			if (found) {
				nextKey = key;
				break;
			}
			if (key.equals(ofKey)) found = true;
		}
		return nextKey;
	}
	public K previousKey(final Object ofKey) {		
		K prevKey = null;
		for (K key : _delegate.keySet()) {
			if (key.equals(ofKey)) break;
			prevKey = key;
		}
		return prevKey;
	}	
}
