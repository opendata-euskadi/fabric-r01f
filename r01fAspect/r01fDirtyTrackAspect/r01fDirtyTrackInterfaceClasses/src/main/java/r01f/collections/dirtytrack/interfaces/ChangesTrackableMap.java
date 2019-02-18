package r01f.collections.dirtytrack.interfaces;

import java.util.Map;
import java.util.Set;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;


/**
 * An interface for a {@link Map} that keeps tracks of the changes (the insertions and deletions)
 * <pre>
 * BEWARE!!		This type DOES NOT implements the {@link Map} inteface because it it did, it could NOT be used at the
 * 				aspect ConvertToDirtyStateTrackableAspect to add the {@link ChangesTrackableMap} to any type extending
 * 				Map<K,V>; the weave does NOT allow adding an interface to a type that already implements it.
 * 					Cannot declare parent ChangesTrackableMap onto type XXX since it already has java.util.Map<K,V> in its hierarchy
 * </pre> 
 * @param <K>
 * @param <V>
 */
public interface ChangesTrackableMap<K,V> 
	     extends DirtyStateTrackable {
//	     		 Map<K,V> {		// DO NOT make this type implement Map interface (see note above)
/////////////////////////////////////////////////////////////////////////////////////////
//  CHANGES TRACKER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the {@link CollectionChangesTracker} object that keep track of the changes in the {@link Map}
	 */
	public CollectionChangesTracker<K> getChangesTracker();
/////////////////////////////////////////////////////////////////////////////////////////////////
//	KEY RETRIEVING
/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the keys of the NEW entries that were added to the original collection
	 * @return 
	 */
	public Set<K> newKeys();
	/**
	 * Returns a collection of the removed keys from the original collection
	 * @return
	 */
	public Set<K> removedKeys();
	/**
	 * Returns a collection of the NOT new and NOT removed keys
	 * (only within those loaded -they were sometime accessed-)
	 * @return 
	 */
	public Set<K> notNewOrRemovedKeys();
/////////////////////////////////////////////////////////////////////////////////////////////////
//	ENTRIES 
//	NOTE: 	The REMOVED entries cannot be obtained because only the key is stored (not the value)
/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the NEW entries within the actual collection
	 * @return
	 */
	public Map<K,V> newEntries();
	/**
	 * Returns a collection that contains the NOT new nor removed entries from the original map
	 * (only within those loaded -they were sometime accessed-)
	 * @return 
	 */
	public Map<K,V> notNewOrRemovedEntries();
}
