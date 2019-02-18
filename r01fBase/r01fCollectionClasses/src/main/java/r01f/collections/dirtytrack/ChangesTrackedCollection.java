package r01f.collections.dirtytrack;


import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.collections.dirtytrack.util.ChangesTrackedCollectionMethods;
import r01f.collections.dirtytrack.util.CollectionChangesTrackerImpl;
import r01f.debug.Debuggable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * Changes tracked {@link Collection}
 * @param <V>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor @EqualsAndHashCode
public class ChangesTrackedCollection<V> 
  implements ChangesTrackableCollection<V>,
  			 Collection<V>,
  			 Debuggable,
			 Serializable {
	private static final long serialVersionUID = 6436452238780837143L;
/////////////////////////////////////////////////////////////////////////////////////////
//	ENTRIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter final Collection<V> _currentEntries;
	
	@Override public int size() { 			return _currentEntries.size(); 			}
	@Override public boolean isEmpty() {	return _currentEntries.isEmpty();		}	
	@Override public boolean contains(Object o) {			return _currentEntries.contains(o);			}
	@Override public boolean containsAll(Collection<?> c) {	return _currentEntries.containsAll(c);		}
	@Override public Iterator<V> iterator() {		return _currentEntries.iterator();		}
	@Override public Object[] toArray() {			return _currentEntries.toArray();		}
	@Override public <T> T[] toArray(T[] a) {		return _currentEntries.toArray(a);		}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CHANGES TRACKER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter CollectionChangesTracker<V> _changesTracker = new CollectionChangesTrackerImpl<V>();
	
	@Override public DirtyTrackingStatus getTrackingStatus() {		return _changesTracker.getTrackingStatus(); 	}
	@Override public boolean isThisDirty() {			return _changesTracker.isThisDirty(); 		}
	@Override public DirtyStateTrackable touch() {		return _changesTracker.touch();				}
	@Override public DirtyStateTrackable setNew() {		return _changesTracker.setNew();		    }
	@Override public DirtyStateTrackable resetDirty() {	return _changesTracker.resetDirty();		}
	@Override public DirtyStateTrackable startTrackingChangesInState() {	return _changesTracker.startTrackingChangesInState();		}
	@Override public DirtyStateTrackable stopTrackingChangesInState() {		return _changesTracker.stopTrackingChangesInState();		}
	@Override public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds) {		return _changesTracker.startTrackingChangesInState(startTrackingInChilds);	}
	@Override public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds,
																	 final boolean checkIfOldValueChanges) {	return _changesTracker.startTrackingChangesInState(startTrackingInChilds,
																			 																					   checkIfOldValueChanges);	}
	@Override public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds) {	return _changesTracker.stopTrackingChangesInState(stopTrackingInChilds);	}
	@Override public <T> T getWrappedObject() {	return _changesTracker.<T>getWrappedObject();	}
	@Override public String debugInfo() {		return ((Debuggable)_changesTracker).debugInfo().toString();			}
	

/////////////////////////////////////////////////////////////////////////////////////////
//	DirtyStateTrackable NOT Delegated
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isDirty() {
		return ChangesTrackedCollectionMethods.isDirty(_currentEntries,_changesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Collection INTERFACE MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean add(final V e) {
		return ChangesTrackedCollectionMethods.add(e,
												   _currentEntries,_changesTracker);
	}
	@Override
	public boolean addAll(final Collection<? extends V> c) {
		return ChangesTrackedCollectionMethods.addAll(c,
													  _currentEntries,_changesTracker);
	}
	@Override
	public boolean remove(final Object o) {
		return ChangesTrackedCollectionMethods.remove(o,
													  _currentEntries,_changesTracker);
	}
	@Override
	public boolean removeAll(final Collection<?> c) {
		return ChangesTrackedCollectionMethods.removeAll(c,
														 _currentEntries,_changesTracker);
	}
	@Override
	public boolean retainAll(final Collection<?> c) {
		return ChangesTrackedCollectionMethods.retainAll(c,
														 _currentEntries,_changesTracker);
	}
	@Override
	public void clear() {
		ChangesTrackedCollectionMethods.clear(_currentEntries,_changesTracker);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RETRIEVING METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<V> newEntries() {
		return ChangesTrackedCollectionMethods.newEntries(_currentEntries,_changesTracker);
	}
	@Override
	public Set<V> removedEntries() {
		return ChangesTrackedCollectionMethods.removedEntries(_currentEntries,_changesTracker);
	}
	@Override
	public Set<V> notNewOrRemovedEntries() {
		return ChangesTrackedCollectionMethods.notNewOrRemovedEntries(_currentEntries,_changesTracker);
	}
	public Set<V> getNewEntries() {
		return _changesTracker.getNewEntries();
	}
	public Set<V> getRemovedEntries() {
		return _changesTracker.getRemovedEntries();
	}
	public void trackEntryInsertion(final V key) {
		_changesTracker.trackEntryInsertion(key);
	}
	public void trackEntryRemoval(final V key) {
		_changesTracker.trackEntryRemoval(key);
	}
	public Set<V> currentKeys(final Set<V> originalKeys) {
		return _changesTracker.currentKeys(originalKeys);
	}
}