package r01f.util.types.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

/**
 * Fluent-API for Map operations
 */
public class MapsWrappers {		
///////////////////////////////////////////////////////////////////////////////
//	ENCAPSULATES MAP OPERATIONS
///////////////////////////////////////////////////////////////////////////////
	public static class WrappedMap<K,V> 
			 implements Map<K,V> {
		/**
		 * The delegated map
		 */
		Map<K,V> _theMap;
				
		public WrappedMap(Map<K,V> newMap) {
			_theMap = newMap;
		}
		
		
		@Override public int size() {				return _theMap.size(); 		}
		@Override public boolean isEmpty() {		return _theMap.isEmpty();	}
		@Override public boolean containsKey(Object key) {		return _theMap.containsKey(key);		}
		@Override public boolean containsValue(Object value) {	return _theMap.containsValue(value);	}
		@Override public V get(Object key) {			return _theMap.get(key);			}
		@Override public V put(K key,V value) {		return _theMap.put(key,value);		}
		@Override public V remove(Object key) {		return _theMap.remove(key);			}
		@Override public void putAll(Map<? extends K,? extends V> m) {	_theMap.putAll(m);		}
		@Override public void clear() {		_theMap.clear();		}
		@Override public Set<K> keySet() {			return _theMap.keySet();		}
		@Override public Collection<V> values() {	return _theMap.values();		}
		@Override public Set<java.util.Map.Entry<K,V>> entrySet() {	return _theMap.entrySet();		}
		@Override public boolean equals(Object o) {	return _theMap.equals(o);		}
		@Override public int hashCode() {			return _theMap.hashCode();		}
		
		public Map<K,V> map() {
			return _theMap;
		}	
		void set(Map<K,V> newMap) {
			_theMap = newMap;
		}
	    /**
	     * Compares the map with another
	     * @param otherMap the other map
	     * @return the map differences encapsulated in a {@link MapDifferences} type
	     */
		public MapDifferences<K,V> compareWith(final Map<K,V> otherMap) {
			if (_theMap == null || otherMap == null) return new MapDifferences<K,V>(null);
			return new MapDifferences<K,V>(Maps.difference(_theMap,otherMap));
		}
		/**
		 * Transforms the map values elements type to other type
		 * @param function the function to transform from type V to type W
		 * @return the transformed Map wrapped 
		 */
		public <W> WrappedMap<K,W> transform(final Function<? super V,W> function) {
			Map<K,W> transformedMap = null;
			if (CollectionUtils.hasData(_theMap)) transformedMap = Maps.transformValues(_theMap,function);
			return CollectionUtils.hasData(transformedMap) ? new WrappedMap<K,W>(transformedMap)
														   : null;
		}
	    /**
	     * Returns the one and only entry present in a Map
	     * (if the map contains more than one entry an {@link IllegalStateException} is thrown)
	     * @return the one and only entry
	     */
	    public Map.Entry<K,V> pickOneAndOnlyEntry() {
	    	if (CollectionUtils.isNullOrEmpty(_theMap)) return null;
			if (_theMap.size() > 1) throw new IllegalStateException("The one and only element of a map has been requested but the map has MORE THAN ONE element");
			return this.pickOneEntry();
	    }
	    /**
	     * Returns the value of the one and ony entry present in the Map
	     * (if the map contains more than one entry an {@link IllegalStateException} is thrown)
	     * @return the value
	     */
	    public V pickOneAndOnlyEntryValue() {
			if (_theMap != null && _theMap.size() > 1) throw new IllegalStateException("The one and only element of a map has been requested but the map has MORE THAN ONE element");
			Map.Entry<K,V> me = this.pickOneEntry();
			return me != null ? me.getValue() : null;
	    }
	    /**
	     * Returns the key of the one and only entry present in the Map
	     * (if the map contains more than one entry an {@link IllegalStateException} is thrown)
	     * @return the key
	     */
	    public K pickOneAndOnlyEntryKey() {
			if (_theMap != null && _theMap.size() > 1) throw new IllegalStateException("The one and only element of a map has been requested but the map has MORE THAN ONE element");
			Map.Entry<K,V> me = this.pickOneEntry();
			return me != null ? me.getKey() : null;
	    }
	    /**
	     * Returns one Map entry  
	     * @return a {@link Map.Entry} object or null if the Map is empty
	     */
	    public Map.Entry<K,V> pickOneEntry() {
	    	Map.Entry<K,V> outEntry = null;
	        if (CollectionUtils.hasData(_theMap)) {
		        for (Map.Entry<K,V> me : _theMap.entrySet()) {
		        	outEntry = me;
		            break;
		        }
	        }
	        return outEntry;
	    }
	    /**
	     * Returns one Map entries value 
	     * @return the value or null if the Map is empty
	     */
	    public V pickOneEntryValue() {
	        Map.Entry<K,V> me = this.pickOneEntry();
	        return me != null ? me.getValue() : null;
	    }
	    /**
	     * Returns one Map entries key 
	     * @return the key or null if the Map is empty
	     */
	    public K pickOneEntryKey() {
	        Map.Entry<K,V> me = this.pickOneEntry();
	        return me != null ? me.getKey() : null;
	    }
		/**
		 * Finds the first entry in the map whose value matches the condition
		 * @param predicate the condicion
		 * @return the map entry
		 */
		public Map.Entry<K,V> findFirstEntryWhomValueMatches(final Predicate<? super V> predicate) {
			Map.Entry<K,V> outEntry = null;
			if (CollectionUtils.hasData(_theMap)) {
				for (Map.Entry<K,V> me : _theMap.entrySet()) {
					if (predicate.apply(me.getValue())) {
						outEntry = me;
						break;
					}
				}
			}
			return outEntry;
		}
		/**
		 * Finds the first entry in the map whose key matches the condition
		 * @param predicate the condicion
		 * @return the map entry
		 */
		public Map.Entry<K,V> findFirstEntryWhomKeyMatches(final Predicate<? super K> predicate) {
			Map.Entry<K,V> outEntry = null;
			if (CollectionUtils.hasData(_theMap)) {
				for (Map.Entry<K,V> me : _theMap.entrySet()) {
					if (predicate.apply(me.getKey())) {
						outEntry = me;
						break;
					}
				}
			}
			return outEntry;
		}
		/**
		 * Filters the map to keep only the entries which key is included in the provided set 
		 * @param keysToInclude the keys to mantain
		 * @return the filtered map
		 */
		public WrappedMap<K,V> filterKeys(final K... keysToInclude) {
	    	Map<K,V> outEntries = null;
	    	if (CollectionUtils.hasData(_theMap)) {
	    		outEntries = Maps.filterKeys(_theMap,
	    									 new Predicate<K>() {
	    												private Collection<K> keysCol = CollectionUtils.of(keysToInclude)
	    																							   .asCollection();
	    												@Override
	    												public boolean apply(final K key) {
	    													return keysCol.contains(key);
	    												}
	    										 });
	    	}
	    	return CollectionUtils.hasData(outEntries) ? new WrappedMap<K,V>(outEntries)
	    											   : null;
		}
		/**
		 * Filters the Map entries so only the ones that matches the predicate are retained in the 
		 * returned map
		 * @param predicate the {@link Predicate} to filter
		 * @return the filtered Map wrapped so more operations can be done with it
		 */
		public WrappedMap<K,V> filter(final Predicate<? super V> predicate) {
			Map<K,V> filteredMap = CollectionUtils.hasData(_theMap) ? Maps.filterValues(_theMap,predicate)
																	: null;
			return CollectionUtils.hasData(filteredMap) ? new WrappedMap<K,V>(filteredMap)
														: null;
		}
		/**
		 * Checks if the map contains ALL given keys
		 * @param keys 
		 * @return 
		 */
		public boolean containsAllTheseKeys(final K... keys) {
			boolean outContains = true;
			if (_theMap != null) {
				if (keys != null) {
					for (K key : keys) {
						if (!_theMap.containsKey(key)) {
							outContains = false;
							break;
						}
					}
				}
			} else {
				outContains = false;
			}
			return outContains;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static class MapDifferences<K,V> {
		private final MapDifference<K,V> _diferences;
		public MapDifferences(MapDifference<K,V> theDifferences) {
			_diferences = theDifferences;
		}		
		public Map<K,V> entriesInCommonWith() {
			if (_diferences == null) return null;
			return _diferences.entriesInCommon();
		}	
		public Map<K,V> deletedEntries() {
			if (_diferences == null) return null;
			return _diferences.entriesOnlyOnLeft();
		}
		public Map<K,V> newEntries() {
			if (_diferences == null) return null;			
			return _diferences.entriesOnlyOnRight();
		}
	}   	
}
