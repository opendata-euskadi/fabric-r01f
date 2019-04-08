package r01f.aspects.core.dirtytrack;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;
import r01f.util.types.collections.CollectionUtils;

/**
 * Changes tracked {@link Collection} that holds a reference to a {@link DirtyStateTrackable} that contains
 * this {@link Collection}
 * @param <V> value
 */
@Accessors(prefix="_")
public class ChangesTrackedCollectionInTrackableObject<V> 
  implements ChangesTrackableCollection<V>,
  			 Collection<V>,
			 Serializable {
	private static final long serialVersionUID = -4671613466187095360L;
/////////////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////////////
	final DirtyStateTrackable _trckContainerObj;
	
	final ChangesTrackableCollection<V> _changesTrackedCollection;
/////////////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////
	public ChangesTrackedCollectionInTrackableObject(final DirtyStateTrackable container,
													 final ChangesTrackableCollection<V> theCol) {
		_trckContainerObj = container;
		_changesTrackedCollection = theCol;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the object that contains this collection
	 * @return
	 */
	public DirtyStateTrackable getContainerObj() {
		return _trckContainerObj;
	}
	/**
	 * UnWraps the collection wrapped by this object 
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public <T extends ChangesTrackableCollection<V>> T unwrap() {
		return (T)_changesTrackedCollection;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public boolean add(final V e) {
		boolean outResult = ((Collection<V>)_changesTrackedCollection).add(e);
		if (outResult) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outResult;
	}
	@Override @SuppressWarnings("unchecked")
	public boolean addAll(final Collection<? extends V> c) {
		if (!CollectionUtils.isNullOrEmpty(c)) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return ((Collection<V>)_changesTrackedCollection).addAll(c);
	}
	@Override @SuppressWarnings("unchecked")
	public void clear() {
		if (!this.isEmpty()) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		((Collection<V>)_changesTrackedCollection).clear();
	}
	@Override @SuppressWarnings("unchecked")
	public boolean remove(final Object o) {
		boolean outResult = ((Collection<V>)_changesTrackedCollection).remove(o);
		if (outResult) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outResult;
	}
	@Override @SuppressWarnings("unchecked")
	public boolean removeAll(final Collection<?> c) {
		boolean outResult = ((Collection<V>)_changesTrackedCollection).removeAll(c);
		if (outResult) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outResult;
	}
	@Override @SuppressWarnings("unchecked")
	public boolean retainAll(final Collection<?> c) {
		boolean outResult = ((Collection<V>)_changesTrackedCollection).retainAll(c);
		if (outResult) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outResult;
	}
	@Override @SuppressWarnings("unchecked")
	public int size() {
		return ((Collection<V>)_changesTrackedCollection).size();
	}
	@Override @SuppressWarnings("unchecked")
	public boolean isEmpty() {
		return  ((Collection<V>)_changesTrackedCollection).isEmpty();
	}
	@Override @SuppressWarnings("unchecked")
	public boolean contains(final Object o) {
		return  ((Collection<V>)_changesTrackedCollection).contains(o);
	}
	@Override @SuppressWarnings("unchecked")
	public Iterator<V> iterator() {
		return  ((Collection<V>)_changesTrackedCollection).iterator();
	}
	@Override @SuppressWarnings("unchecked")
	public Object[] toArray() {
		return  ((Collection<V>)_changesTrackedCollection).toArray();
	}
	@Override @SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		return  ((Collection<V>)_changesTrackedCollection).toArray(a);
	}
	@Override @SuppressWarnings("unchecked")
	public boolean containsAll(final Collection<?> c) {
		return  ((Collection<V>)_changesTrackedCollection).containsAll(c);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE
/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isThisDirty() {
		return _changesTrackedCollection.isThisDirty();
	}
	@Override
	public CollectionChangesTracker<V> getChangesTracker() {
		return _changesTrackedCollection.getChangesTracker();
	}
	@Override
	public Set<V> newEntries() {
		return _changesTrackedCollection.newEntries();
	}
	@Override
	public Set<V> removedEntries() {
		return _changesTrackedCollection.removedEntries();
	}
	@Override
	public boolean isDirty() {
		return _changesTrackedCollection.isDirty();
	}
	@Override
	public Set<V> notNewOrRemovedEntries() {
		return _changesTrackedCollection.notNewOrRemovedEntries();
	}
	@Override
	public DirtyTrackingStatus getTrackingStatus() {
		return _changesTrackedCollection.getTrackingStatus();
	}
	@Override
	public DirtyStateTrackable touch() {
		return _changesTrackedCollection.touch();
	}
	@Override
	public DirtyStateTrackable setNew() {
		return _changesTrackedCollection.setNew();
	}
	@Override
	public DirtyStateTrackable resetDirty() {
		return _changesTrackedCollection.resetDirty();
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds, 
														   final boolean checkIfOldValueChanges) {
		return _changesTrackedCollection.startTrackingChangesInState(startTrackingInChilds,
																	 checkIfOldValueChanges);
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds) {
		return _changesTrackedCollection.startTrackingChangesInState(startTrackingInChilds);
	}
	@Override
	public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds) {
		return _changesTrackedCollection.stopTrackingChangesInState(stopTrackingInChilds);
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState() {
		return _changesTrackedCollection.startTrackingChangesInState();
	}
	@Override	
	public DirtyStateTrackable stopTrackingChangesInState() {
		return _changesTrackedCollection.stopTrackingChangesInState();
	}
	@Override	
	public <T> T getWrappedObject() {
		return _changesTrackedCollection.<T>getWrappedObject();
	}
}
