package r01f.collections.dirtytrack.interfaces;

import java.util.Map;
import java.util.Set;

public interface ChangesTrackableLazyMap<K,V>
         extends ChangesTrackableMap<K,V> {
	/**
	 * Returns a collection containing the keys that are NOT new or were removed from the original map,
	 * (only within those loaded -they were sometime accessed-)
	 * @return the NOT new and NOT removed keys (the original keys)
	 */
	public Set<K> notNewOrRemovedKeys(boolean onlyLoaded);
	/**
	 * Returns a collection containing the entries that are NOT new or were removed from the original map,
	 * (only within those loaded -they were sometime accessed-)
	 * @return the NOT new and NOT removed entries (the original keys)
	 */
	public Map<K,V> notNewOrRemovedEntries(boolean onlyLoaded);
	/**
	 * Returns a Map with all the entries that are NOT new or were removed from the original map 
	 * and also have been modified, (only within those loaded -they were sometime accessed-)
	 * BEWARE!! This method ONLY returns values if V is an instance of {@link r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable}
	 * @return the modified entries
	 */
	public Map<K,V> notNewOrRemovedDirtyEntries();
}
