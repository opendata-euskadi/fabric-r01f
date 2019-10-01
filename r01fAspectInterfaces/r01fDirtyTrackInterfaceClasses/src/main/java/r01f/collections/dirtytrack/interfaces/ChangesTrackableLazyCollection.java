package r01f.collections.dirtytrack.interfaces;

import java.util.Set;

public interface ChangesTrackableLazyCollection<V> 
         extends ChangesTrackableCollection<V> {
	/**
	 * Returns a collection of NOT new and NOT removed entries 
	 * (only within those loaded -they were sometime accessed-)
	 * @return 
	 */
	public Set<V> notNewOrRemovedEntries(boolean onlyLoaded);
}
