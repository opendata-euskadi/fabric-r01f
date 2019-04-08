package r01f.aspects.core.dirtytrack;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableMap;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;
import r01f.util.types.collections.CollectionUtils;

/**
 * Changes-tracked {@link Map} that holds a reference to a {@link DirtyStateTrackable} object
 * that contains this {@link Map}
 * @param <K> key
 * @param <V> value
 */
@Accessors(prefix="_")
public class ChangesTrackedMapInTrackableObject<K,V> 
  implements ChangesTrackableMap<K,V>,
  			 Map<K,V>,
			 Serializable {
	private static final long serialVersionUID = -4079213663214127908L;
/////////////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////////////
	private final DirtyStateTrackable _trckContainerObj;
	
//	@Delegate(excludes=MapMutatorMethods.class)	// Map interface cannot be delegated since ChangesTrackableMap does NOT extends Map
	private final ChangesTrackableMap<K,V> _changesTrackedMap;

/////////////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////
	public ChangesTrackedMapInTrackableObject(final DirtyStateTrackable container,
											  final ChangesTrackableMap<K,V> theMap) {
		_trckContainerObj = container;
		_changesTrackedMap = theMap;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////////////
	public DirtyStateTrackable getContainerObj() {
		return _trckContainerObj;
	}
	@SuppressWarnings("unchecked")
	public <T extends ChangesTrackableMap<K,V>> T unwrap() {
		return (T)_changesTrackedMap;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE Map
/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public V put(final K key,final V value) {
		V outObj = ((Map<K,V>)_changesTrackedMap).put(key,value);
		if (outObj != null) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outObj;
	}
	@Override @SuppressWarnings("unchecked")
	public void putAll(final Map<? extends K,? extends V> m) {
		if (!CollectionUtils.isNullOrEmpty(m)) _trckContainerObj.getTrackingStatus().setThisDirty(true);	
		((Map<K,V>)_changesTrackedMap).putAll(m);
	}
	@Override @SuppressWarnings("unchecked")
	public void clear() {
		if (!this.keySet().isEmpty()) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		((Map<K,V>)_changesTrackedMap).clear();
	}
	@Override @SuppressWarnings("unchecked")
	public V remove(final Object key) {
		V outObj = ((Map<K,V>)_changesTrackedMap).remove(key);
		if (outObj != null) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outObj;
	}
	@Override @SuppressWarnings("unchecked")
	public int size() {
		return ((Map<K,V>)_changesTrackedMap).size();
	}
	@Override @SuppressWarnings("unchecked")
	public boolean isEmpty() {
		return ((Map<K,V>)_changesTrackedMap).isEmpty();
	}
	@Override @SuppressWarnings("unchecked")
	public boolean containsKey(final Object key) {
		return ((Map<K,V>)_changesTrackedMap).containsKey(key);
	}
	@Override @SuppressWarnings("unchecked")
	public boolean containsValue(final Object value) {
		return ((Map<K,V>)_changesTrackedMap).containsValue(value);
	}
	@Override @SuppressWarnings("unchecked")
	public V get(final Object key) {
		return ((Map<K,V>)_changesTrackedMap).get(key);
	}
	@Override @SuppressWarnings("unchecked")
	public Set<K> keySet() {
		return ((Map<K,V>)_changesTrackedMap).keySet();
	}
	@Override @SuppressWarnings("unchecked")
	public Collection<V> values() {
		return ((Map<K,V>)_changesTrackedMap).values();
	}
	@Override @SuppressWarnings("unchecked")
	public Set<Map.Entry<K, V>> entrySet() {
		return ((Map<K,V>)_changesTrackedMap).entrySet();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isThisDirty() {
		return _changesTrackedMap.isThisDirty();
	}
	@Override
	public boolean isDirty() {
		return _changesTrackedMap.isDirty();
	}
	@Override
	public CollectionChangesTracker<K> getChangesTracker() {
		return _changesTrackedMap.getChangesTracker();
	}
	@Override
	public Set<K> newKeys() {
		return _changesTrackedMap.newKeys();
	}
	@Override
	public DirtyTrackingStatus getTrackingStatus() {
		return _changesTrackedMap.getTrackingStatus();
	}
	@Override
	public DirtyStateTrackable touch() {
		return _changesTrackedMap.touch();
	}
	@Override
	public DirtyStateTrackable setNew() {
		return _changesTrackedMap.setNew();
	}
	@Override
	public Set<K> removedKeys() {
		return _changesTrackedMap.removedKeys();
	}
	@Override
	public DirtyStateTrackable resetDirty() {
		return _changesTrackedMap.resetDirty();
	}
	@Override
	public Set<K> notNewOrRemovedKeys() {
		return _changesTrackedMap.notNewOrRemovedKeys();
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds, 
														   final boolean checkIfOldValueChanges) {
		return _changesTrackedMap.startTrackingChangesInState(startTrackingInChilds,
															  checkIfOldValueChanges);
	}
	@Override
	public Map<K, V> newEntries() {
		return _changesTrackedMap.newEntries();
	}
	@Override
	public Map<K, V> notNewOrRemovedEntries() {
		return _changesTrackedMap.notNewOrRemovedEntries();
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds) {
		return _changesTrackedMap.startTrackingChangesInState(startTrackingInChilds);
	}
	@Override
	public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds) {
		return _changesTrackedMap.stopTrackingChangesInState(stopTrackingInChilds);
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState() {
		return _changesTrackedMap.startTrackingChangesInState();
	}
	@Override
	public DirtyStateTrackable stopTrackingChangesInState() {
		return _changesTrackedMap.stopTrackingChangesInState();
	}
	@Override
	public <T> T getWrappedObject() {
		return _changesTrackedMap.<T>getWrappedObject();
	}
}
