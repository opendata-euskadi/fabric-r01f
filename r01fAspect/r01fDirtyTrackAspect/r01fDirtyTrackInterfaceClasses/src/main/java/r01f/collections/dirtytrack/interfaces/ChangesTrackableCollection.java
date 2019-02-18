package r01f.collections.dirtytrack.interfaces;

import java.util.Collection;
import java.util.Set;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * An interface for a {@link Collection} that keeps tracks of the changes (the insertions and deletions)
 * <pre>
 * BEWARE!!		This type DOES NOT implements the {@link Collection} inteface because it it did, it could NOT be used at the
 * 				aspect ConvertToDirtyStateTrackableAspect to add the {@link ChangesTrackableCollection} to any type extending
 * 				Collection<V>; the weave does NOT allow adding an interface to a type that already implements it.
 * 					Cannot declare parent ChangesTrackableCollection onto type XXX since it already has java.util.Collection<V> in its hierarchy
 * </pre> 
 * @param <K>
 * @param <V>
 */
public interface ChangesTrackableCollection<V> 
         extends DirtyStateTrackable { 
//         		 Collection<V> {	// DO NOT make this type implement Collection interface (see note above)
	/**
	 * @return the {@link CollectionChangesTracker} object in charge of collection changes tracking
	 */
	public CollectionChangesTracker<V> getChangesTracker();
	/**
	 * Returns a {@link Collection} with the new entries added to the original {@link Collection}
	 * @return a view of the original {@link Collection} that contains ONLY new entries
	 */
	public Set<V> newEntries();
	/**
	 * Returns a {@link Collection} with the new entries removed from the original {@link Collection}
	 * @return a view of the original {@link Collection} that contains ONLY removed entries
	 */
	public Set<V> removedEntries();
	/**
	 * Returns a {@link Collection} with the not new or removed entries added to the original {@link Collection}
	 * @return a view of the original {@link Collection} that contains ONLY not new or removed entries
	 */
	public Set<V> notNewOrRemovedEntries();
}
