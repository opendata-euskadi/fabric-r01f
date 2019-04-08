package r01f.collections.dirtytrack;


import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableMap;
import r01f.collections.dirtytrack.util.ChangesTrackedMapMethods;
import r01f.collections.dirtytrack.util.CollectionChangesTrackerImpl;
import r01f.debug.Debuggable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

@Accessors(prefix="_")
public class ChangesTrackedMap<K,V>
  implements ChangesTrackableMap<K,V>,
  			 Map<K,V>,
			 Serializable, 
			 Debuggable {
	private static final long serialVersionUID = -7817465496832765428L;
/////////////////////////////////////////////////////////////////////////////////////////
//	ENTRIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Map<K,V> _currentEntries;
	
	@Override public int size() {			return _currentEntries.size();		}
	@Override public boolean isEmpty() {	return _currentEntries.isEmpty();	}
	@Override public boolean containsKey(Object key) {			return _currentEntries.containsKey(key);		}
	@Override public boolean containsValue(Object value) {		return _currentEntries.containsValue(value);	}
	@Override public V get(Object key) {	return _currentEntries.get(key);	}
	@Override public Set<K> keySet() {							return _currentEntries.keySet();	}
	@Override public Collection<V> values() {					return _currentEntries.values();	}
	@Override public Set<java.util.Map.Entry<K,V>> entrySet() {	return _currentEntries.entrySet();		}


/////////////////////////////////////////////////////////////////////////////////////////
//  CHANGES TRACKER
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter CollectionChangesTracker<K> _changesTracker = new CollectionChangesTrackerImpl<K>();
	
	@Override public DirtyTrackingStatus getTrackingStatus() {	return _changesTracker.getTrackingStatus();		}
	@Override public boolean isThisDirty() {					return _changesTracker.isThisDirty();			}
	@Override public DirtyStateTrackable touch() {				return _changesTracker.touch();					}
	@Override public DirtyStateTrackable setNew() {				return _changesTracker.setNew();				}
	@Override public DirtyStateTrackable resetDirty() {			return _changesTracker.resetDirty();			}
	@Override public DirtyStateTrackable startTrackingChangesInState() {	return _changesTracker.startTrackingChangesInState();		}
	@Override public DirtyStateTrackable stopTrackingChangesInState() {		return _changesTracker.stopTrackingChangesInState();		}
	@Override public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds) {		return _changesTracker.startTrackingChangesInState(startTrackingInChilds);	}
	@Override public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds,
																	 final boolean checkIfOldValueChanges) { 	return _changesTracker.startTrackingChangesInState(startTrackingInChilds,
																			 																					   checkIfOldValueChanges); }
	@Override public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds) {		return _changesTracker.stopTrackingChangesInState(stopTrackingInChilds);	}
	@Override public <T> T getWrappedObject() {		return _changesTracker.<T>getWrappedObject();		}
	
			  public Set<K> getNewEntries() {		return _changesTracker.getNewEntries();			}
			  public Set<K> getRemovedEntries() {	return _changesTracker.getRemovedEntries();		}
			  public void trackEntryInsertion(K key) {	_changesTracker.trackEntryInsertion(key);	}
			  public void trackEntryRemoval(K key) {	_changesTracker.trackEntryRemoval(key);		}
			  public Set<K> currentKeys(Set<K> originalKeys) {	return _changesTracker.currentKeys(originalKeys);	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ChangesTrackedMap(final Map<K,V> mapToWrap) {
		_currentEntries = mapToWrap;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DirtyStateTrackable NOT DELEGATED
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isDirty() {
		return ChangesTrackedMapMethods.isDirty(_currentEntries,_changesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Map INTERFACE MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public V put(final K key,final V value) {
		return ChangesTrackedMapMethods.put(key,value,
											_currentEntries,_changesTracker);
	}
	@Override
	public V remove(final Object key) {
		return ChangesTrackedMapMethods.remove(key,
											   _currentEntries,_changesTracker);
	}
	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		ChangesTrackedMapMethods.putAll(m,
										_currentEntries,_changesTracker);
	}
	@Override
	public void clear() {
		ChangesTrackedMapMethods.clear(_currentEntries,_changesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	CURRENT, REMOVED, CHANGED AND NEW KEYS
/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<K> newKeys() {
		return ChangesTrackedMapMethods.newKeys(_currentEntries,_changesTracker);
	}
	@Override
	public Set<K> removedKeys() {
		return ChangesTrackedMapMethods.removedKeys(_currentEntries,_changesTracker);
	}
	@Override
	public Set<K> notNewOrRemovedKeys() {
		return ChangesTrackedMapMethods.notNewOrRemovedKeys(_currentEntries,_changesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	CURRENT, REMOVED, CHANGED AND NEW ENTRIES
/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Map<K,V> newEntries() {
		return ChangesTrackedMapMethods.newEntries(_currentEntries,_changesTracker);
	}
	@Override
	public Map<K,V> notNewOrRemovedEntries() {
		return ChangesTrackedMapMethods.notNewOrRemovedEntries(_currentEntries,_changesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String debugInfo() {
		return ChangesTrackedMapMethods.debugInfo(_currentEntries,_changesTracker);
	}
}
