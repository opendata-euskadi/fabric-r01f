package r01f.types.dirtytrack.internal;

import java.util.Set;

import com.google.common.base.Predicate;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;

public interface CollectionChangesTracker<K> 
		 extends DirtyStateTrackable {
	
	public Set<K> getNewEntries();
	public Set<K> getRemovedEntries();
	public void trackEntryInsertion(final K key);
	public void trackEntryRemoval(final K key);
	public Set<K> currentKeys(final Set<K> originalKeys);
	
	public Predicate<K> getNewEntriesFilter();
	public Predicate<K> getRemovedEntriesFilter();
	public Predicate<K> getNewOrDeletedEntriesFilter();
	
}
