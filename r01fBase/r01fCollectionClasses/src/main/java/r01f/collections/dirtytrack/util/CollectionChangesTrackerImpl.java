package r01f.collections.dirtytrack.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyTrackingStatus;
import r01f.debug.Debuggable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * Tracks changes in a {@link Set}
 * @param <K>
 */
@Accessors(prefix="_")
@NoArgsConstructor
public class CollectionChangesTrackerImpl<K> 
  implements CollectionChangesTracker<K>,
  			 Debuggable,
			 Serializable {
	private static final long serialVersionUID = -190729036750079312L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private transient Set<K> _newEntries;
	@Getter private transient Set<K> _removedEntries;
	
	private transient boolean _dirty = false;
	private transient boolean _tracking = false;
	
	// Tracking status... 
	// (this is necessary in order to implement the method DirtyStateTrackable.getTrackingStatus()
	private transient DirtyTrackingStatus _trackingStatus = new DirtyTrackingStatus() {
																	private static final long serialVersionUID = 5816420380285360322L;
																	@Override
																	public void setThisNew(boolean newObj) {
																		// do nothing
																	}
																	@Override
																	public boolean isThisNew() {
																		return false;
																	}
																	@Override
																	public void setThisDirty(boolean thisDirty) {
																		if (thisDirty == false) {
																			CollectionChangesTrackerImpl.this.resetDirty();		// BEWARE!! the Map values can have the newDirty value changed
																		} else {
																			CollectionChangesTrackerImpl.this._dirty = true;	// ???
																		}
																	}
																	@Override
																	public boolean isThisDirty() {
																		return CollectionChangesTrackerImpl.this.isDirty();
																	}
																	@Override
																	public void setThisDirtyTracking(boolean dirtyTrack) {
																		CollectionChangesTrackerImpl.this._tracking = dirtyTrack;
																	}
																	@Override
																	public boolean isThisDirtyTracking() {
																		return CollectionChangesTrackerImpl.this._tracking;
																	}
																	@Override
																	public void setThisCheckIfValueChanges(boolean check) {
																		// do not apply
																	}
																	@Override
																	public boolean isThisCheckIfValueChanges() {
																		return false;
																	}
																	@Override
																	public void _resetDirty(DirtyStateTrackable trck) {
																		/* empty */
																	}
																	@Override
																	public boolean _isThisDirty(DirtyStateTrackable trck) {
																		return false;
																	}
																	@Override
																	public boolean _isDirty(DirtyStateTrackable trck) {
																		return false;
																	}
																	@Override
																	public void _startTrackingChangesInState(DirtyStateTrackable trck) {
																		/* empty */
																	}
																	@Override
																	public void _stopTrackingChangesInState(DirtyStateTrackable trck) {
																		/* empty */
																	}
																	@Override
																	public void _startTrackingChangesInState(DirtyStateTrackable trck,
																											 boolean startTrackingInChilds) {
																		/* empty */
																	}
																	@Override
																	public void _startTrackingChangesInState(DirtyStateTrackable trck,
																											 boolean startTrackingInChilds,
																											 boolean checkIfOldValueChanges) {
																		/* empty */
																	}
																	@Override
																	public void _stopTrackingChangesInState(DirtyStateTrackable trck,
																											boolean stopTrackingInChilds) {
																		/* empty */
																	}
														  };
/////////////////////////////////////////////////////////////////////////////////////////
//	FILTERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Filter new entries
	 */
	@Getter private transient Predicate<K> _newEntriesFilter = new Predicate<K>() {
																	@Override
																	public boolean apply(K key) {
																		Set<K> newEntries = CollectionChangesTrackerImpl.this.getNewEntries();
																		return newEntries != null ? newEntries.contains(key) : false;
																	}
														 	   };
	/**
	 * Filter deleted entries
	 */
	@Getter private transient Predicate<K> _removedEntriesFilter = new Predicate<K>() {
																			@Override
																			public boolean apply(K key) {
																				Set<K> removedEntries = CollectionChangesTrackerImpl.this.getRemovedEntries();
																				return removedEntries != null ? removedEntries.contains(key) : false;
																			}
																   };
	/**
	 * Filter new or deleted entries
	 */
	@Getter private transient Predicate<K> _newOrDeletedEntriesFilter = Predicates.<K>or(_newEntriesFilter,
																  			   			 _removedEntriesFilter);
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD NEW OR DELETED ENTRIES 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void trackEntryInsertion(final K key) {
		if (!_tracking) return;	
		
		if (_removedEntries != null && _removedEntries.contains(key)) {
			// Remove the key from the deleted entries
			_removedEntries.remove(key);
		} else if (_newEntries != null && !_newEntries.contains(key)) {
			// Add the key to the new entries
			if (!_newEntries.contains(key)) _newEntries.add(key);
		} else {
			// Add to the new entries
			if (_newEntries == null) _newEntries = new HashSet<K>();
			if (!_newEntries.contains(key)) _newEntries.add(key);
		}
		_dirty = true;	// the map was modified
	}
	@Override
	public void trackEntryRemoval(final K key) {
		if (!_tracking) return;		
		
		if (_newEntries != null && _newEntries.contains(key)) {
			// Remove from the new entries
			_newEntries.remove(key);
		} else if (_removedEntries != null && !_removedEntries.contains(key)) {
			// Add to the deleted entries
			if (!_removedEntries.contains(key)) _removedEntries.add(key);
		} else {
			// Add to the deleted entries
			if (_removedEntries == null) _removedEntries = new HashSet<K>();
			if (!_removedEntries.contains(key)) _removedEntries.add(key);
		}
		_dirty = true;
	}
	@Override
	public Set<K> currentKeys(final Set<K> originalKeys) {
		Set<K> originalPlusNew = _newEntries != null && _newEntries.size() > 0 ? Sets.union(originalKeys,_newEntries)
																			   : originalKeys;
		Set<K> originalPlusNewWithoutRemoved = _removedEntries != null && _removedEntries.size() > 0 ? Sets.filter(originalPlusNew,
																												   new Predicate<K>() {
																															@Override
																															public boolean apply(K key) {
																																return !CollectionChangesTrackerImpl.this.getRemovedEntries().contains(key);
																															}
																												   })
																							         : originalPlusNew;
		return originalPlusNewWithoutRemoved;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	DIRTY
/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public DirtyTrackingStatus getTrackingStatus() {
		return _trackingStatus;
	}
	@Override
	public boolean isThisDirty() {
		return _dirty;
	}
	@Override
	public boolean isDirty() {
		return _dirty;
	}
	@Override
	public DirtyStateTrackable touch() {
		_dirty = true;
		return this;
	}
	@Override
	public DirtyStateTrackable setNew() {		
		throw new UnsupportedOperationException();
	}
	@Override
	public DirtyStateTrackable resetDirty() {
		if (_newEntries != null) _newEntries = null;
		if (_removedEntries != null) _removedEntries = null;
		_dirty = false;
		return this;
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState() {
		_tracking = true;
		return this;
	}
	@Override
	public DirtyStateTrackable stopTrackingChangesInState() {
		_tracking = false;
		return this;
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds) {
		_tracking = true;
		return this;
	}
	@Override
	public DirtyStateTrackable startTrackingChangesInState(final boolean startTrackingInChilds,
														   final boolean checkIfOldValueChanges) {
		_tracking = true;
		return this;
	}
	@Override
	public DirtyStateTrackable stopTrackingChangesInState(final boolean stopTrackingInChilds) {
		_tracking = false;
		return this;
	}
	@Override @SuppressWarnings("unchecked")
	public <T> T getWrappedObject() {
		return (T)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String debugInfo() {
		return new StringBuilder()
					  .append("\r\n\t-         New entries : ").append(_newEntries != null ? _newEntries.size() : 0 ).append(" > ").append(_mapKeysToString(_newEntries))
					  .append("\r\n\t-     Removed entries : ").append(_removedEntries != null ? _removedEntries.size() : 0).append(" > ").append(_mapKeysToString(_removedEntries))
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
