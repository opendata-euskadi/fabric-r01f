package r01f.collections.lazy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.collections.lazy.LazyCollectionsInterfaces.CollectionValuesSupplier;

/**
 * Base class for {@link LazyCollection} implementing collections
 * @param <V> collection elemente's type
 */
@Accessors(prefix="_")
abstract class LazyCollectionBase<V> 
       extends LazyChangesTrackerBase<V>
    implements Collection<V>,
    		   LazyCollection<V> {
	
////////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
////////////////////////////////////////////////////////////////////////////////////////////
	@NotDirtyStateTrackable
	protected Collection<V> _entries;
	
	@NotDirtyStateTrackable
	protected transient CollectionValuesSupplier<V> _valuesSupplier;
	
	@NotDirtyStateTrackable
	protected boolean _isLoaded = false;
	
////////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
////////////////////////////////////////////////////////////////////////////////////////////
	protected LazyCollectionBase(final Collection<V> backEnd,
								 final CollectionValuesSupplier<V> valuesSupplier) {
		if (backEnd == null) throw new IllegalArgumentException("The backend Collection instance cannot be null!");
		_entries = backEnd;
		_valuesSupplier = valuesSupplier;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides all values
	 * @return the {@link Collection}
	 */
	protected  Collection<V> _supplyValues() {
		if (_valuesSupplier == null) throw new IllegalStateException("The values supplier for the lazily-loaded collection is NOT defined!!");
		Collection<V> suppliedValues = _valuesSupplier.loadValues();
		return suppliedValues;
	}
	/**
	 * Adds a new entry into the underlying {@link Collection}
	 * @param value the value to be added
	 * @return true if the {@link Collection} has been modified (the new entry has been added)
	 */
	private boolean _addInCurrentEntries(final V value) {
		if (_entries == null) throw new IllegalStateException("The backend collection is null; the LazyCollection was NOT properly initialized");
		return _entries.add(value);
	}
	/**
	 * Removes an entry from the underlying {@link Collection}
	 * @param value the value to be removed
	 * @return true if the {@link Collection} has been modified (the new entry has been added)
	 */
	private boolean _removeFromCurrentEntries(final V value) {
		if (_entries == null) throw new IllegalStateException("The backend collection is null; the LazyCollection was NOT properly initialized");
		return _entries.remove(value);
	}
	/**
	 * Removes all entries from the underlying {@link Collection} 
	 */
	private void clearCurrentEntries() {
		if (_entries == null) throw new IllegalStateException("The backend collection is null; the LazyCollection was NOT properly initialized");
		_entries.clear();
	}
	/**
	 * Returns the underlying {@link Collection}
	 * @return the {@link Collection}
	 */
	private Collection<V> _currentEntries() {
		if (_entries == null) throw new IllegalStateException("The backend collection is null; the LazyCollection was NOT properly initialized");
		return _entries;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
////////////////////////////////////////////////////////////////////////////////////////////
	private void _supply() {
		if (_isLoaded) return;
		Collection<V> values = _supplyValues();
		_entries.addAll(values);
		_isLoaded = true;
	}
////////////////////////////////////////////////////////////////////////////////////////////
//	Collection Interface
////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int size() {
		_supply();		// force the load...
		Collection<V> currentEntries = _currentEntries();
		return currentEntries != null ? currentEntries.size() : 0;
	}
	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}
	@Override
	public boolean add(final V e) {
		// If the value to be added is a DirtyStateTrackable instance and tracking is being done... 
		// extend the status
		if (e instanceof DirtyStateTrackable 
		 && this.getTrackingStatus().isThisDirtyTracking() 
		 && !_asDirtyStateTrackable(e).getTrackingStatus().isThisDirtyTracking()) {
			_asDirtyStateTrackable(e).startTrackingChangesInState(true);
		}
		boolean outResult = _addInCurrentEntries(e);
		if (outResult) _changesTracker.trackEntryInsertion(e);
		return outResult;
	}
	@Override
	public boolean addAll(final Collection<? extends V> c) {
		boolean outModif = false;
		if (c != null && c.size() > 0) {
			for (V v : c) {
				outModif = outModif | this.add(v);
			}
		}
		return outModif;
	}
	@Override	@SuppressWarnings("unchecked")
	public boolean remove(final Object o) {
		boolean outResult = _removeFromCurrentEntries((V)o);
		if (!outResult) {
			// The entry was NOT in the loaded entries... try to load all values
			_supply();		// force the entries load
			outResult = _removeFromCurrentEntries((V)o);
		}
		if (outResult) _changesTracker.trackEntryRemoval((V)o);
		return outResult;
	}
	@Override
	public boolean removeAll(final Collection<?> c) {
		boolean outModif = false;
		if (c != null && c.size() > 0) {
			for (Object v : c) {
				outModif = outModif | this.remove(v);
			}
		}
		return outModif;
	}
	@Override
	public boolean retainAll(final Collection<?> c) {
		_supply();		// force the entries load
		boolean outModif = false;
		if (c != null && c.size() > 0) {
			for (Object v : _currentEntries()) {
				if (!c.contains(v)) outModif = outModif | this.remove(v);
			}
		}
		return outModif;
	}
	@Override
	public void clear() {
		_supply();
		Collection<V> currentEntries = _currentEntries();
		if (currentEntries != null) {
			for (V v : currentEntries) {
				_changesTracker.trackEntryRemoval(v);
			}
		}
		this.clearCurrentEntries();
	}
	@Override
	public boolean contains(final Object o) {
		_supply();
		Collection<V> currentEntries = _currentEntries();
		return currentEntries != null ? currentEntries.contains(o) : false;
	}
	@Override
	public boolean containsAll(final Collection<?> c) {
		_supply();
		Collection<V> currentEntries = _currentEntries();
		return currentEntries != null ? currentEntries.containsAll(c) : false;
	}
	@Override
	public Iterator<V> iterator() {	
		_supply();
		Collection<V> currentEntries = _currentEntries();
		return currentEntries != null ? currentEntries.iterator() : null;
	}
	@Override
	public Object[] toArray() {
		_supply();
		Collection<V> currentEntries = _currentEntries();
		return currentEntries != null ? currentEntries.toArray() : null;
	}
	@Override
	public <T> T[] toArray(final T[] a) {
		_supply();
		Collection<V> currentEntries = _currentEntries();
		return currentEntries != null ? currentEntries.toArray(a) : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO RETRIEVE THE ENTRIES DEPENDING ON THEY'RE NEW, REMOVED OR ORIGINAL
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Set<V> newEntries() {
		return _changesTracker.getNewEntries();
	}
	@Override
	public Set<V> removedEntries() {
		return _changesTracker.getRemovedEntries();
	}
	@Override
	public Set<V> notNewOrRemovedEntries() {
		// Filter deleted entries from the original entries
		return  Sets.newHashSet(Collections2.filter(_currentEntries(),
						   							Predicates.not(_changesTracker.getNewOrDeletedEntriesFilter())));
	}
	@Override
	public Set<V> notNewOrRemovedEntries(final boolean onlyLoaded) {
		if (!onlyLoaded) _supply();
		return this.notNewOrRemovedEntries();
	}
	@Override
	public Collection<V> loadedValues() {
		return _currentEntries();
	}

}
