package r01f.collections.dirtytrack.util;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * Utility methods for:
 * <ul>
 * 		<li>{@link r01f.collections.dirtytrack.ChangesTrackedList}</li>
 * 		<li>ChangesTrackableListInterfaceAspect</li>
 * </ul>
 */
public class ChangesTrackedListMethods
	 extends ChangesTrackedMethdsBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets an entry from the underlying list
	 * @param index
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> V get(final int index,
							final Collection<V> currentEntries,
							final CollectionChangesTracker<V> changesTracker) {
		return ((List<V>)currentEntries).get(index);
	}	
	/**
	 * Returns the index of an underlying element
	 * @param o
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> int indexOf(final Object o,
								  final Collection<V> currentEntries,
								  final CollectionChangesTracker<V> changesTracker) {
		return ((List<V>)currentEntries).indexOf(o);
	}
	/**
	 * @param o
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> int lastIndexOf(final Object o,
									  final Collection<V> currentEntries,
									  final CollectionChangesTracker<V> changesTracker) {
		return ((List<V>)currentEntries).lastIndexOf(o);
	}
	/**
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> ListIterator<V> listIterator(final Collection<V> currentEntries,
												   final CollectionChangesTracker<V> changesTracker) {
		return ((List<V>)currentEntries).listIterator();
	}
	/**
	 * @param index
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> ListIterator<V> listIterator(final int index,
												   final Collection<V> currentEntries,
												   final CollectionChangesTracker<V> changesTracker) {
		return ((List<V>)currentEntries).listIterator(index);
	}
	/**
	 * @param fromIndex
	 * @param toIndex
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> List<V> subList(final int fromIndex,final int toIndex,
									  final Collection<V> currentEntries,
									  final CollectionChangesTracker<V> changesTracker) {
		return ((List<V>)currentEntries).subList(fromIndex,toIndex);
	}	
	/**
	 * @param index
	 * @param element
	 * @param currentEntries
	 * @param changesTracker
	 */
	public static <V> void add(final int index,final V element,
							   final Collection<V> currentEntries,
							   final CollectionChangesTracker<V> changesTracker) {
		// If the added element is DirtyStateTrackable and tracking is done...
		if (element instanceof DirtyStateTrackable 
		 && changesTracker.getTrackingStatus().isThisDirtyTracking() 
		 && !_adaptToDirtyStateTrackable(element).getTrackingStatus().isThisDirtyTracking()) {
			_adaptToDirtyStateTrackable(element).startTrackingChangesInState(true);
		}
		((List<V>)currentEntries).add(index,element);
		changesTracker.trackEntryInsertion(element);
	}
	/**
	 * @param index
	 * @param element
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> V set(final int index,final V element,
							final Collection<V> currentEntries,
							final CollectionChangesTracker<V> changesTracker) {
		// If the added element is DirtyStateTrackable and tracking is done...
		if (element instanceof DirtyStateTrackable 
		 && changesTracker.getTrackingStatus().isThisDirtyTracking() 
		 && !_adaptToDirtyStateTrackable(element).getTrackingStatus().isThisDirtyTracking()) {
			_adaptToDirtyStateTrackable(element).startTrackingChangesInState(true);
		}
		V outResult = ((List<V>)currentEntries).set(index,element);
		if (outResult != null) {
			changesTracker.trackEntryInsertion(element);
		}
		return outResult;
	}
	/**
	 * @param fromIndex
	 * @param c
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> boolean addAll(final int fromIndex,final Collection<? extends V> c,
									 final Collection<V> currentEntries,
									 final CollectionChangesTracker<V> changesTracker) {
		if (c == null || c.size() == 0) return false;
		// If the added element is DirtyStateTrackable and tracking is done...
		for (V element : c) {
			if (element instanceof DirtyStateTrackable 
			 && changesTracker.getTrackingStatus().isThisDirtyTracking() 
			 && !_adaptToDirtyStateTrackable(element).getTrackingStatus().isThisDirtyTracking()) {
				_adaptToDirtyStateTrackable(element).startTrackingChangesInState(true);
			}
		}
		// Add
		boolean outResult = ((List<V>)currentEntries).addAll(fromIndex,c);
		if (outResult) {
			for (V v : c) {
				changesTracker.trackEntryInsertion(v);
			}
		}
		return outResult;
	}
	/**
	 * @param index
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> V remove(final int index,
							   final Collection<V> currentEntries,
							   final CollectionChangesTracker<V> changesTracker) {
		V outResult = ((List<V>)currentEntries).remove(index);
		if (outResult != null) {
			changesTracker.trackEntryRemoval(outResult);
		}
		return outResult;
	}

}
