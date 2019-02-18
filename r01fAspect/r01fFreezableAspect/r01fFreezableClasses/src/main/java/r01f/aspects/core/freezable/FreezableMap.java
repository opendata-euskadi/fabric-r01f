package r01f.aspects.core.freezable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Wraps a {@link Map} overriding the mutator methods so that it throws an exception 
 * if the collection is frozen
 * @param <K>
 * @param <V>
 */
public class FreezableMap<K,V>
  implements Map<K,V>,
			 Serializable {
	private static final long serialVersionUID = 7785966352446057153L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Map<K,V> _map;
	
	private final boolean _frozen;
	
/////////////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////
	public FreezableMap(final Map<K,V> theMap,final boolean frozen) {
		_map = theMap;
		_frozen = frozen;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean containsKey(final Object key) {
		return _map.containsKey(key);
	}
	@Override
	public boolean containsValue(final Object value) {
		return _map.containsValue(value);
	}
	@Override
	public Set<Entry<K,V>> entrySet() {
		return _map.entrySet();
	}
	@Override
	public V get(final Object key) {
		return _map.get(key);
	}
	@Override
	public boolean isEmpty() {
		return _map.isEmpty();
	}
	@Override
	public Set<K> keySet() {
		return _map.keySet();
	}
	@Override
	public int size() {
		return _map.size();
	}
	@Override
	public Collection<V> values() {
		return _map.values();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public V put(final K key,final V value) {
		if (_frozen) throw new IllegalStateException("The map is FROZEN! you cannot put anything in it. This is because the obj where the map is contained is frozen");
		return _map.put(key,value);
	}
	@Override
	public void putAll(final Map<? extends K,? extends V> m) {
		if (_frozen) throw new IllegalStateException("The map is FROZEN! you cannot put anything in it. This is because the obj where the map is contained is frozen");
		_map.putAll(m);
	}
	@Override
	public void clear() {
		if (_frozen) throw new IllegalStateException("The map is FROZEN! you cannot clear its values. This is because the obj where the map is contained is frozen");
		_map.clear();
	}
	@Override
	public V remove(final Object key) {
		if (_frozen) throw new IllegalStateException("The map is FROZEN! you cannot remove any key. This is because the obj where the map is contained is frozen");
		return _map.remove(key);
	}

}
