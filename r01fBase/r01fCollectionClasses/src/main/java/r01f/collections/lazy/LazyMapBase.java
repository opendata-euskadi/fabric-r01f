package r01f.collections.lazy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.KeyIntrospector;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * {@link LazyMap} base type
 * @param <K> Map key type
 * @param <V> Map value type
 */
abstract class LazyMapBase<K,V> 
       extends LazyChangesTrackerBase<K>
    implements Map<K,V>,
    		   LazyMap<K,V> {
////////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
////////////////////////////////////////////////////////////////////////////////////////////
	@NotDirtyStateTrackable
	protected final boolean _fullLoadedOnCreation;		// Sets if the map is FULLY loaded when it's created
	
	@NotDirtyStateTrackable
	protected final KeyIntrospector<K,V> _keyIntrospector;
	
	@NotDirtyStateTrackable
	protected final Supplier<Set<K>> _keySetSupplier;
	
////////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE CONSTRUCTOR 
////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inits the {@link Map}
	 * @param fullLoadedOnCreation sets if the map is COMPLETELLY loaded the first time it's accessed (when created)
	 * @param keyIntrospector a type implementing {@link r01f.collections.lazy.LazyCollectionsInterfaces.KeyIntrospector}
	 * 						  and whose mission is to get the key from a Map's element
	 * @param keySetSupplier supplier of the keys of the map
	 */
	LazyMapBase(final boolean fullLoadedOnCreation,
				final KeyIntrospector<K,V> keyIntrospector,
				final Supplier<Set<K>> keySetSupplier) {
		// is the map fully load upon creating it
		_fullLoadedOnCreation = fullLoadedOnCreation;

		// keys intronspector: gets an object's key in the map
		_keyIntrospector = keyIntrospector;
		
		// Cache
		_keySetSupplier = keySetSupplier;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFACE ChangesTrackableLazyMap
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CollectionChangesTracker<K> getChangesTracker() {
		return _changesTracker;
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	METODOS ABSTRACTOS
////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides a value from a key
	 * @param key the key
	 * @return the value
	 */
	protected abstract V supplyValue(final K key);
	/**
	 * Inserts a NEW entry in the underlying map
	 * @param key the key
	 * @param value the value
	 * @return the value that was stored at the Map before the new one is stored
	 */
	protected abstract V putInCurrentEntries(final K key,final V value);
	/**
	 * Removes an entry
	 * @param key the key of the entry to be removed
	 * @return the removed entiry if it was stored or null otherwise
	 */
	protected abstract V removeFromCurrentEntries(final K key);
	/**
	 * Removes all map's entries
	 */
	protected abstract void clearCurrentEntries();
	/**
	 * Retuns the underlying map as a "normal" map that contains the LOADED entries
	 * @return the map with the LOADED entries
	 */
	protected abstract Map<K,V> currentEntries();
////////////////////////////////////////////////////////////////////////////////////////////
//	INTERFAZ MAP
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int size() {
		int outSize = 0;
		Set<K> actualKeys = _actualKeys();
		outSize = actualKeys != null ? actualKeys.size() : 0;
		return outSize;
	}
	@Override
	public boolean isEmpty() {
		boolean outEmpty = true;
		Set<K> actualKeys = _actualKeys();
		outEmpty = actualKeys != null ? actualKeys.size() == 0 : true;
		return outEmpty;
	}
	@Override
	public boolean containsKey(Object key) {
		boolean outContains = false;
		Set<K> actualKeys = _actualKeys();
		outContains = actualKeys != null ? actualKeys.contains(key) : false;
		return outContains;
	}
	@Override 	@SuppressWarnings("unchecked")
	public boolean containsValue(Object value) {
		boolean outContains = false;
		K key = _keyIntrospector.keyFor((V)value);
		if (key != null) outContains = (this.get(key) != null);
		return outContains; 
	}
	@Override @SuppressWarnings("unchecked")
	public V get(Object key) {
		if (key == null) return null;
		V outVal = null;	
		// do NOT allow a key that's NOT within the initial keys or the new keys
		boolean isSuppliedKey = _fullLoadedOnCreation ? false 
													  : _keySetSupplier.get().contains(key);
		boolean isNewOrUpdatedKey = this.isLoaded((K)key);
		boolean isValidKey = isSuppliedKey || isNewOrUpdatedKey;
		if (isValidKey && !this.isLoaded((K)key)) {
			outVal = this.supplyValue((K)key);
		} else {
			Map<K,V> currentEntries = this.currentEntries();
			outVal = currentEntries != null ? currentEntries.get(key) : null;
		}
		return outVal;
	}
	@Override
	public V put(K key,V value) {
		V outVal = null;
		if (key != null) {
			// If value is a DirtyStateTrackable instance and Map's changes are being tracked 
			// extends the status to the value
			if (value instanceof DirtyStateTrackable 
			 && this.getTrackingStatus().isThisDirtyTracking() 
			 && !_asDirtyStateTrackable(value).getTrackingStatus().isThisDirtyTracking()) {
				_asDirtyStateTrackable(value).startTrackingChangesInState(true);
			}
			// Put the value in the map and keep track of the change
			outVal = this.putInCurrentEntries(key,value);	
			_changesTracker.trackEntryInsertion(key);		
		}
		return outVal;
	}
	@Override
	public void putAll(Map<? extends K,? extends V> m) {
		if (m != null && m.size() > 0) {
			for (Map.Entry<? extends K,? extends V> me : m.entrySet()) {
				this.put(me.getKey(),me.getValue());
			}
		}
	}
	@Override
	public Set<K> keySet() {
		Set<K> outKeySet = null;
		outKeySet = _actualKeys(); 	// outKeySet = _keySetSupplier.get();
		return outKeySet;
	}
	@Override
	public Collection<V> values() {
		Collection<V> outCol = null;
		outCol = new AbstractUnmodifiableEntriesSet<V>(_actualKeys()) {
						@Override
						public Iterator<V> iterator() {
							return new Iterator<V>() {
											@SuppressWarnings("unchecked")
											private Iterator<K> _keySetIterator = ((Set<K>)_keys).iterator();
											@Override
											public boolean hasNext() {
												return _keySetIterator.hasNext();
											}
											@Override
											public V next() {
												K nextKey = _keySetIterator.next();
												// BEWARE!!! here the key is forced to be loaded if it was not
												return LazyMapBase.this.get(nextKey);
											}
											@Override
											public void remove() {
												throw new UnsupportedOperationException ("The values() collection of a lazily loaded cache does not supports remove() method");
											}
										};
						}
						@Override
						public boolean contains(Object o) {
							return LazyMapBase.this.containsValue(o);
						}
						@Override
						public boolean containsAll(Collection<?> c) {
							boolean outContains = false;
							if (c != null && c.size() > 0 
							 && !_keySetSupplier.get().isEmpty()) {
								outContains = true;
								for (Object val : c) {
									if (!LazyMapBase.this.containsValue(val)) {
										outContains = false;
										break;
									}
								}
							}
							return outContains;
						}
						@Override
						public Object[] toArray() {
							return LazyMapBase.this.currentEntries().values().toArray();
						}
						@Override
						public <T> T[] toArray(T[] a) {
							Collection<V> values = LazyMapBase.this.currentEntries().values();
							return values.toArray(a);
						}
			 	};
		return outCol;
	}
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		Set<Map.Entry<K,V>> outEntrySet = null;
		outEntrySet = new AbstractUnmodifiableEntriesSet<Map.Entry<K,V>>(_actualKeys()) {
							@Override
							public Iterator<Map.Entry<K,V>> iterator() {
								return new Iterator<Map.Entry<K,V>>() {
												@SuppressWarnings("unchecked")
												private Iterator<K> _keySetIterator = ((Set<K>)_keys).iterator();
												@Override
												public boolean hasNext() {
													return _keySetIterator.hasNext();
												}
												@Override
												public Map.Entry<K,V> next() {
													K nextKey = _keySetIterator.next();
													// BEWARE!!! here the key is forced to be loaded if it was not
													return Maps.immutableEntry(nextKey,
																			   LazyMapBase.this.get(nextKey));
												}
												@Override
												public void remove() {
													throw new UnsupportedOperationException ("The values() collection of a lazily loaded cache does not supports remove() method");
												}
											};
							}
							@Override	@SuppressWarnings("unchecked")
							public boolean contains(Object o) {
								Map.Entry<K,V> me = (Map.Entry<K,V>)o;
								return LazyMapBase.this.containsKey(me.getKey());
							}
							@Override 	@SuppressWarnings("unchecked")
							public boolean containsAll(Collection<?> c) {
								boolean outContains = false;
								if (c != null && c.size() > 0
								 && !_keySetSupplier.get().isEmpty()) {
									outContains = true;
									for (Object entry : c) {
										Map.Entry<K,V> me = (Map.Entry<K,V>)entry;
										if (!LazyMapBase.this.containsKey(me.getKey())) {
											outContains = false;
											break;
										}
									}
								}
								return outContains;
							}
							@Override
							public Object[] toArray() {
								return LazyMapBase.this.currentEntries().entrySet().toArray();
							}
							@Override
							public <T> T[] toArray(T[] a) {
								Set<Map.Entry<K,V>> entries = LazyMapBase.this.currentEntries().entrySet();
								return entries.toArray(a);
							}
					 };
		return outEntrySet;
	}
	@Override	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		if (key == null) return null;
		V outVal = this.get(key);		// Force the loading of the element to be deleted
		if (outVal != null) {
			outVal = this.removeFromCurrentEntries((K)key);						// take the element out of the map
			if (outVal != null) _changesTracker.trackEntryRemoval((K)key);		// keep track of the removal
		}
		return outVal;
	}
	@Override
	public void clear() {
		for (K k : this.currentEntries().keySet()) {
			_changesTracker.trackEntryRemoval(k);		// keep track of the removal
		}
		this.clearCurrentEntries();
	}
	@Override
	public boolean removeLazily(final K key) {
		if (key == null) return false;
		// It's NOT necessary to load the value if it was not
		boolean existsKey = this.containsKey(key);
		if (existsKey) {
			if (this.isLoaded(key)) this.removeFromCurrentEntries(key);
			_changesTracker.trackEntryRemoval(key);
		}
		return existsKey;
	}	
////////////////////////////////////////////////////////////////////////////////////////////
// 	ACTUAL Keys: initially the set of the keys is obtained using the keySetSupplier BUT as keys
//				 are put and removed, the set of actual keys vary
//	REAL Keys:   Initial keys + new keys - deleted keys
////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return initial keys + new keys - removed keys
	 */
	protected Set<K> _actualKeys() {
		Set<K> originalKeys = null;
		if (_fullLoadedOnCreation) {
			 originalKeys = this.currentEntries().keySet();
		} else {
			originalKeys = _keySetSupplier != null ? _keySetSupplier.get() : null;
		}
		return _changesTracker.currentKeys(originalKeys);	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  IS THE MAP COMPLETELLY LOADED?
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isFullyLoaded() {
		if (_fullLoadedOnCreation) return true;
		boolean outLoaded = true;
		Set<K> keys = _keySetSupplier.get();
		if (keys != null && keys.size() > 0) {
			for (K key : keys) {
				boolean thisLoaded = this.isLoaded(key);
				if (!thisLoaded) {
					outLoaded = false;
					break;
				}
			}
		}
		return outLoaded;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RETRIEVE THE LOADED DATA
//	Returns ONLY the entries that has been loaded (have been requested 
//	Does NOT return any value that might be in the Map BUT has not been requested and 
// 	consequently it has NOT been loaded
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public Map<K,V> loadedEntriesAsMap() {
		return this.currentEntries();
	}
	@Override
	public Set<Map.Entry<K,V>> loadedEntrySet() {
		Map<K,V> currentEntries = this.currentEntries();
		return currentEntries != null ? currentEntries.entrySet() : null;
	}
	@Override
	public Set<K> loadedKeySet() {
		Map<K,V> currentEntries = this.currentEntries();
		return currentEntries != null ? currentEntries.keySet() : null;
	}
	@Override
	public Collection<V> loadedValues() {
		Map<K,V> currentEntries = this.currentEntries();
		return currentEntries != null ? currentEntries.values() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RETURN NEW, DELETED OR ORIGINAL ENTRIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<K> newKeys() {
		return _changesTracker.getNewEntries();
	}
	@Override
	public Set<K> removedKeys() {
		return _changesTracker.getRemovedEntries();
	}
	@Override
	public Set<K> notNewOrRemovedKeys() {
		return this.notNewOrRemovedKeys(false);
	}
	@Override
	public Set<K> notNewOrRemovedKeys(boolean onlyLoaded) {
		// if onlyLoaded = true the deleted entries are filtered from the original entries
		Map<K,V> currentEntries = this.currentEntries();
		Set<K> setToFilter = onlyLoaded ? (currentEntries != null ? currentEntries.keySet() : null)	// only loaded entries
										: _keySetSupplier.get();									// all entries
		// Remove deleted entries
		Set<K> outNotNewOrRemoved = Sets.filter(setToFilter,
						   						Predicates.not(_changesTracker.getNewOrDeletedEntriesFilter()));	// not within deleted
		return outNotNewOrRemoved;
	}
	@Override
	public Map<K,V> newEntries() {
		Map<K,V> outEntries = null;		
		final Set<K> newKeys = this.newKeys();
		if (newKeys != null && newKeys.size() > 0) {
			Predicate<K> filter = new Predicate<K>() {
											@Override
											public boolean apply(K key) {
												return newKeys.contains(key);
											}
									};
			outEntries = Maps.filterKeys(this.loadedEntriesAsMap(),	// BEWARE!! do NOT use <this> because Maps.filterKeys() will call entrySet() meth
										 filter);					//			forces all value load
		}
		return outEntries;
	}
	@Override
	public Map<K,V> notNewOrRemovedEntries() {
		// BEWARE!!! Forces the loading of every entry that verify the filter (see entrySet() method)
		//			 if the value loading is NOT necessary, use notNewOrRemovedKeys() instead of notNewOrRemovedEntries()
		return this.notNewOrRemovedEntries(false);
	}
	@Override
	public Map<K,V> notNewOrRemovedEntries(boolean onlyLoaded) {
		Map<K,V> outEntries = null;
		final Set<K> notNewOrRemovedKeys = this.notNewOrRemovedKeys(onlyLoaded);	// only not new nor deleted keys
		if (notNewOrRemovedKeys != null && notNewOrRemovedKeys.size() > 0) {
			Predicate<K> filter = new Predicate<K>() {
											@Override
											public boolean apply(K key) {
												return notNewOrRemovedKeys.contains(key);
											}
									};
			Map<K,V> mapToFilter = onlyLoaded ? this.loadedEntriesAsMap()	// only loaded
											  : this;						// all --> BEWARE!!! Forces the loading of every entry that verify the filter (see entrySet() method)
																			//			 		  if the value loading is NOT necessary, use notNewOrRemovedKeys() instead of notNewOrRemovedEntries()
			outEntries = Maps.filterKeys(mapToFilter,filter);
		}
		return outEntries;
	}
	@Override 
	public Map<K,V> notNewOrRemovedDirtyEntries() {
		Map<K,V> outNotNewOrRemovedDirtyEntries = null;
		
		Map<K,V> notNewOrRemovedEntries = this.notNewOrRemovedEntries(true);	// only loaded entries
		final Collection<K> dirtyKeys = Sets.newHashSet();
		if (notNewOrRemovedEntries != null && notNewOrRemovedEntries.size() > 0) {
			// Get a collection of the keys of the dirty values
			for (Map.Entry<K,V> me : notNewOrRemovedEntries.entrySet()) {
				// If value is an instance of DirtyStateTrackable...
				if (me.getValue() != null 
				 && me.getValue() instanceof DirtyStateTrackable && _asDirtyStateTrackable(me.getValue()).isDirty()) {
					dirtyKeys.add(me.getKey());
				}
			}
			// Filter the original map to retain only the entries NOT at dirtyKeys
			if (dirtyKeys != null && dirtyKeys.size() > 0) {
				outNotNewOrRemovedDirtyEntries = Maps.filterKeys(notNewOrRemovedEntries,new Predicate<K>() {
																								@Override
																								public boolean apply(K key) {
																									return dirtyKeys.contains(key);
																								}
																							});
			}
		}
		return outNotNewOrRemovedDirtyEntries;
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	CONCILIATE KeySet WITH CLIENT CHANGES
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<K> conciliateKeySetOnValues(final Set<K> suppliedKeySet,
								   		   final Predicate<V> filterOnValues) {
		Set<K> outKeys = suppliedKeySet;
		
		// Beware that at the client side:
		//		1.- Entries might have been removed
		//		2.- New entries matching the filter might be created
		// ... and all changes might NOT be saved so the keySet returned by the supplier MUST be conciliated with the 
		//	   changes at the client side
		//		1.- Remove the server (keySet supplier)-returned entries that were removed at the client side
		//		2.- Add new entries at the client that are not yet persisted at the server (the keySet supplier will NOT return them)
		if ( _changesTracker.isDirty() ) {
			// [1]-Remove removed entries
			outKeys = _removeRemovedKeys(suppliedKeySet);
			
			// [2]-Add new entries
			final Set<K> newKeys = _changesTracker.getNewEntries();
			if (newKeys != null && newKeys.size() > 0) {
				// Add to the list of new entries those verifying the conditions
				Map<K,V> matchingValues = Maps.filterValues(this.loadedEntriesAsMap(),	// Only LOADED entries
															filterOnValues);
				if (outKeys == null) outKeys = Sets.newHashSet();
				outKeys.addAll(matchingValues.keySet());
			}
		} 
		return outKeys;
	}
	@Override
	public Set<K> conciliateKeySetOnKeys(final Set<K> suppliedKeySet,
								   		 final Predicate<K> filterOnKeys) {
		Set<K> outKeys = suppliedKeySet;
		
		// Beware that at the client side:
		//		1.- Entries might have been removed
		//		2.- New entries matching the filter might be created
		// ... and all changes might NOT be saved so the keySet returned by the supplier MUST be conciliated with the 
		//	   changes at the client side
		//		1.- Remove the server (keySet supplier)-returned entries that were removed at the client side
		//		2.- Add new entries at the client that are not yet persisted at the server (the keySet supplier will NOT return them)
		if ( _changesTracker.isDirty() ) {
			// [1]-Remove removed entries
			outKeys = _removeRemovedKeys(suppliedKeySet);
			
			// [2]-Add new entries
			final Set<K> newKeys = _changesTracker.getNewEntries();
			if (newKeys != null && newKeys.size() > 0) {
				// Add to the list of new entries those verifying the conditions
				Map<K,V> matchingValues = Maps.filterKeys(this.loadedEntriesAsMap(),	// Only LOADED entries
														  filterOnKeys);
				if (outKeys == null) outKeys = Sets.newHashSet();
				outKeys.addAll(matchingValues.keySet());
			}
		} 
		return outKeys;
	}
	/**
	 * Removes the deleted entries from the keySet
	 * @param suppliedKeySet
	 * @return the keySet without the deleted entries
	 */
	private Set<K> _removeRemovedKeys(final Set<K> suppliedKeySet) {
		Set<K> outKeys = suppliedKeySet;
		final Set<K> removedKeys = _changesTracker.getRemovedEntries();
		if (removedKeys != null && removedKeys.size() > 0
		 && outKeys != null && outKeys.size() > 0) {
			// Remove from the keySet returned by the supplier all removed entries
			outKeys = Sets.filter(outKeys,
							      new Predicate<K>() {
											@Override
											public boolean apply(K oid) {
												return !removedKeys.contains(oid);
											}
							   	  });
		}
		return outKeys;
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	FILTROS
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Map<K,V> filterEntriesWithKeys(final Set<K> keys,
										  final boolean onlyLoaded) {
		Predicate<K> filter = new Predicate<K>() {
										@Override
										public boolean apply(final K oid) {
											return keys != null ? keys.contains(oid) : false;
										}
								};
		Map<K,V> mapToFilter = onlyLoaded ? this.loadedEntriesAsMap()
										  : this;
		Map<K,V> outMap = Maps.filterKeys(mapToFilter,
										  filter);
		return outMap;
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * entrySet() and values() {@link Iterator} used at {@link LazyLoaded} maps
	 * (see {@link CachedLazyMap} y {@link LazyMap})
	 * @param <T>
	 */
	@AllArgsConstructor
	static abstract class AbstractUnmodifiableSet<T>
	           implements Set<T> {
		
		private String _errMsg = "This map";
		
		@Override
		public boolean add(T o) {
			throw new UnsupportedOperationException (_errMsg + " does not supports add() method");
		}
		@Override
		public boolean addAll(Collection<? extends T> c) {
			throw new UnsupportedOperationException (_errMsg + " does not supports addAll() method");
		}
		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException (_errMsg + " does not supports retainAll() method");
		}
		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException (_errMsg + " does not supports remove() method");
		}
		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException (_errMsg + " does not supports removeAll() method");
		}
		@Override
		public void clear() {
			throw new UnsupportedOperationException (_errMsg + " does not supports clear() method");
		}
	}
	static abstract class AbstractUnmodifiableEntriesSet<T> 
				  extends AbstractUnmodifiableSet<T> {
		
		protected final Set<?> _keys;
		
		public AbstractUnmodifiableEntriesSet(Set<?> keys) {
			super("The values() / entrySet() collections of a lazily loaded cache");
			_keys = keys;
		}
		@Override
		public int size() {
			return _keys.size();
		}
		@Override
		public boolean isEmpty() {
			return _keys.isEmpty();
		}
	}
}
