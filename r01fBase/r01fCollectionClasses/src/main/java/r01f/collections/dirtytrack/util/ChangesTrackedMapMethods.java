package r01f.collections.dirtytrack.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.debug.Debuggable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * Methods used at:
 * 	- {@link r01f.collections.dirtytrack.ChangesTrackedMap} type
 * 	- ChangesTrackableMapInterfaceAspect aspect
 */
public class ChangesTrackedMapMethods 
	 extends ChangesTrackedMethdsBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	DirtyStateTrackable NOT DELEGATED
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> boolean isDirty(final Map<K,V> currentEntries,
										final CollectionChangesTracker<K> changesTracker) {
		// This method cannot be delegated to CollectionChangesTracker since this type 
		// can detect if elements have been added / deleted BUT cannot detect changes in the 
		// collection elements
		//		ie: map.get(index).setXXX() <-- it's NOT detected... and the map has changed!!
		boolean outDirty = changesTracker.isDirty();
		if (!outDirty && currentEntries != null && currentEntries.size() > 0) {
			for (V v : currentEntries.values()) {
				if (v instanceof DirtyStateTrackable) {
					if ( ((DirtyStateTrackable)v).isDirty()) {
						outDirty = true;
						break;
					}
				}
			}
		}
		return outDirty;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MUTATOR METHDOS OF Map INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> V put(final K key,final V value,
							  final Map<K,V> currentEntries,
							  final CollectionChangesTracker<K> changesTracker) {
		// If the value is a DirtyStateTrackable instance and tracking is ongoing, 
		// extend the state
		if (value instanceof DirtyStateTrackable 
		 && changesTracker.getTrackingStatus().isThisDirtyTracking() 
		 && !_adaptToDirtyStateTrackable(value).getTrackingStatus().isThisDirtyTracking()) {
			_adaptToDirtyStateTrackable(value).startTrackingChangesInState(true);
		}
		V outValue = currentEntries.put(key,value);
		changesTracker.trackEntryInsertion(key);
		return outValue;
	}
	@SuppressWarnings("unchecked")
	public static <K,V> V remove(final Object key,
								 final Map<K,V> currentEntries,
								 final CollectionChangesTracker<K> changesTracker) {
		V outValue = currentEntries.remove(key);
		if (outValue != null) {
			changesTracker.trackEntryRemoval((K)key);
		}
		return outValue;
	}
	public static <K,V> void putAll(final Map<? extends K, ? extends V> m,
									final Map<K,V> currentEntries,
									final CollectionChangesTracker<K> changesTracker) {
		if (m != null && m.size() > 0) {
			for (Map.Entry<? extends K,? extends V> me : m.entrySet()) {
				ChangesTrackedMapMethods.put(me.getKey(),me.getValue(),
											 currentEntries,changesTracker);
			}
		}
	}
	public static <K,V> void clear(final Map<K,V> currentEntries,
								   final CollectionChangesTracker<K> changesTracker) {
		for (K k : currentEntries.keySet()) {
			changesTracker.trackEntryRemoval(k);
		}
		currentEntries.clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	GET THE CURRENT, DELETED, MODIFIED AND NEW KEYS
/////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the new keys
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <K,V> Set<K> newKeys(final Map<K,V> currentEntries,
									   final CollectionChangesTracker<K> changesTracker) {
		return changesTracker.getNewEntries();
	}
	/**
	 * Returns the removed keys
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <K,V> Set<K> removedKeys(final Map<K,V> currentEntries,
										   final CollectionChangesTracker<K> changesTracker) {
		return changesTracker.getRemovedEntries();
	}
	/**
	 * Returns the not new or removed keys
	 * @param currentEntries
	 * @param changesTracker
	 * @return
	 */
	public static <K,V> Set<K> notNewOrRemovedKeys(final Map<K,V> currentEntries,
												   final CollectionChangesTracker<K> changesTracker) {
		// From the actual entries, ,remove the new and deleted ones (it's just necessary to remove new keys because removed keys are not pressent)
		return changesTracker.getNewEntriesFilter() != null ? Sets.filter(currentEntries.keySet(),
																	  	   Predicates.not(changesTracker.getNewEntriesFilter()))
									   				   		: currentEntries.keySet();		// not modified
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	ACTUAL, REMOVED, MODIFIED AND NEW ENTRIES
/////////////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> Map<K,V> newEntries(final Map<K,V> currentEntries,
											final CollectionChangesTracker<K> changesTracker) {
		Map<K,V> outEntries = null;
		final Set<K> newKeys = ChangesTrackedMapMethods.newKeys(currentEntries,changesTracker);
		if (newKeys != null && newKeys.size() > 0) {
			Predicate<K> filter = new Predicate<K>() {
											@Override
											public boolean apply(K key) {
												return newKeys.contains(key);
											}
									};
			outEntries = Maps.filterKeys(currentEntries,filter);
		}
		return outEntries;
	}
	public static <K,V> Map<K,V> notNewOrRemovedEntries(final Map<K,V> currentEntries,
														final CollectionChangesTracker<K> changesTracker) {
		Map<K,V> outEntries = null;
		final Set<K> notNewOrRemovedKeys = ChangesTrackedMapMethods.notNewOrRemovedKeys(currentEntries,changesTracker);
		if (notNewOrRemovedKeys != null && notNewOrRemovedKeys.size() > 0) {
			Predicate<K> filter = new Predicate<K>() {
											@Override
											public boolean apply(K key) {
												return notNewOrRemovedKeys.contains(key);
											}
									};
			outEntries = Maps.filterKeys(currentEntries,filter);
		}
		return outEntries;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> String debugInfo(final Map<K,V> currentEntries,
										 final CollectionChangesTracker<K> changesTracker) {
		return new StringBuilder()
						.append("MapChangesTracker debug info:")
						.append("\r\n\t-Original map entries : ")
								.append(currentEntries != null && currentEntries.size() > 0 ? currentEntries.size() : 0).append(" > ")
								.append(_mapKeysToString((currentEntries != null && currentEntries.size() > 0 ? currentEntries.keySet() : null)))
								.append(changesTracker != null ? ((Debuggable)changesTracker).debugInfo() : "")
						.toString();
	}
	private static <K> StringBuffer _mapKeysToString(final Collection<K> keys) {
		StringBuffer sb = new StringBuffer();
		if (keys != null) {
			for (Iterator<K> it = keys.iterator(); it.hasNext(); ) {
				sb.append(it.next());
				if (it.hasNext()) sb.append(", ");
			}
		}
		return sb;
	}
}
