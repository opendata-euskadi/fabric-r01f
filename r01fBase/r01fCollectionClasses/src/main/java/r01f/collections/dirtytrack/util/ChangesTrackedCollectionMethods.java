package r01f.collections.dirtytrack.util;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

public class ChangesTrackedCollectionMethods 
	 extends ChangesTrackedMethdsBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	Methods NOT delegated to CollectionChangesTracker
/////////////////////////////////////////////////////////////////////////////////////////
	public static <V> boolean isDirty(final Collection<V> currentEntries,
								  	  final CollectionChangesTracker<V> changesTracker) {
		// This method cannot be delegated to CollectionChangesTracker since this type 
		// can detect if elements have been added / deleted BUT cannot detect changes in the 
		// collection elements
		//		ie: col.get(index).setXXX() <-- it's NOT detected... and the map has changed!!
		boolean outDirty = changesTracker.isDirty();
		if (!outDirty && currentEntries != null && currentEntries.size() > 0) {
			for (V v : currentEntries) {
				if (v instanceof DirtyStateTrackable) {
					if ( ((DirtyStateTrackable)v).isDirty() ) {
						outDirty = true;
						break;
					}
				}
			}
		}
		return outDirty;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Mutator methods of the Collection Interface
/////////////////////////////////////////////////////////////////////////////////////////
	public static <V> boolean add(final V e,
								  final Collection<V> currentEntries,
								  final CollectionChangesTracker<V> changesTracker) {
		// If the value is a DirtyStateTrackable instance and tracking is being done...
		if (e instanceof DirtyStateTrackable 
		 && changesTracker.getTrackingStatus().isThisDirtyTracking() 
		 && !_adaptToDirtyStateTrackable(e).getTrackingStatus().isThisDirtyTracking()) {
			_adaptToDirtyStateTrackable(e).startTrackingChangesInState(true);
		}
		// Add the new element
		boolean outResult = currentEntries.add(e);
		if (outResult) changesTracker.trackEntryInsertion(e);
		return outResult;
	}
	public static <V> boolean addAll(final Collection<? extends V> c,
									 final Collection<V> currentEntries,
								  	 final CollectionChangesTracker<V> changesTracker) {
		boolean outModif = false;
		if (c != null && c.size() > 0) {
			for (V v : c) {
				outModif = outModif | ChangesTrackedCollectionMethods.add(v,currentEntries,changesTracker);
			}
		}
		return outModif;
	}
	@SuppressWarnings("unchecked")
	public static <V> boolean remove(final Object o,
								 final Collection<V> currentEntries,
								 final CollectionChangesTracker<V> changesTracker) {
		boolean outResult = currentEntries.remove(o);
		if (outResult) changesTracker.trackEntryRemoval((V)o);
		return outResult;
	}
	public static <V> boolean removeAll(final Collection<?> c,
										final Collection<V> currentEntries,
										final CollectionChangesTracker<V> changesTracker) {
		boolean outModif = false;
		if (c != null && c.size() > 0) {
			for (Object v : c) {
				outModif = outModif | ChangesTrackedCollectionMethods.remove(v,currentEntries,changesTracker);
			}
		}
		return outModif;
	}
	public static <V> boolean retainAll(final Collection<?> c,
										final Collection<V> currentEntries,
										final CollectionChangesTracker<V> changesTracker) {
		boolean outModif = false;
		if (c != null && c.size() > 0) {
			for (Object v : currentEntries) {
				if (!c.contains(v)) outModif = outModif | ChangesTrackedCollectionMethods.remove(v,currentEntries,changesTracker);
			}
		}
		return outModif;
	}
	public static <V> void clear(final Collection<V> currentEntries,
							     final CollectionChangesTracker<V> changesTracker) {
		for (V v : currentEntries) {
			changesTracker.trackEntryRemoval(v);
		}
		currentEntries.clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENTRY ACCESSOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns new entries
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> Set<V> newEntries(final Collection<V> currentEntries,
							     		final CollectionChangesTracker<V> changesTracker) {
		return changesTracker != null ? changesTracker.getNewEntries()
									  : null;
	}
	/**
	 * Returns removed entries
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> Set<V> removedEntries(final Collection<V> currentEntries,
							     			final CollectionChangesTracker<V> changesTracker) {
		return changesTracker != null ? changesTracker.getRemovedEntries()
									  : null;
	}
	/**
	 * Returns not new or removed entries
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <V> Set<V> notNewOrRemovedEntries(final Collection<V> currentEntries,
							     					final CollectionChangesTracker<V> changesTracker) {
		// Remove the new & deleted entries from the actual entries (it's enough to remove the new entries since the deleted ones are not present)
		Collection<V> outCol = changesTracker != null ? Collections2.filter(currentEntries,changesTracker.getNewEntriesFilter())
													  : currentEntries;		// it's not modified
		return Sets.newHashSet(outCol);
	}
}
