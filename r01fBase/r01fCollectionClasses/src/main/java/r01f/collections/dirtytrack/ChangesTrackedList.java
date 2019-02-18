package r01f.collections.dirtytrack;


import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import lombok.experimental.Accessors;
import r01f.collections.dirtytrack.util.ChangesTrackedListMethods;

/**
 * Changest tracked {@link List}
 * @param <V>
 */
@Accessors(prefix="_")
public class ChangesTrackedList<V> 
    extends ChangesTrackedCollection<V>
 implements List<V> {
	private static final long serialVersionUID = 3456205602112568030L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ChangesTrackedList(final Collection<V> entries) {
		super(entries);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE (underlying List delegated methods)
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public V get(final int index) {
		return ChangesTrackedListMethods.get(index,
											 _currentEntries,_changesTracker);
	}	
	@Override
	public int indexOf(final Object o) {
		return ChangesTrackedListMethods.indexOf(o,
												 _currentEntries,_changesTracker);
	}
	@Override
	public int lastIndexOf(final Object o) {
		return ChangesTrackedListMethods.lastIndexOf(o,
													 _currentEntries,_changesTracker);
	}
	@Override
	public ListIterator<V> listIterator() {
		return ChangesTrackedListMethods.listIterator(_currentEntries,_changesTracker);
	}
	@Override
	public ListIterator<V> listIterator(final int index) {
		return ChangesTrackedListMethods.listIterator(index,
													  _currentEntries,_changesTracker);
	}
	@Override
	public List<V> subList(final int fromIndex,final int toIndex) {
		return ChangesTrackedListMethods.subList(fromIndex,toIndex,
										  		 _currentEntries, _changesTracker);
	}	
	@Override
	public void add(final int index,final V element) {
		ChangesTrackedListMethods.add(index,element,
									  _currentEntries,_changesTracker);
	}
	@Override
	public V set(final int index,final V element) {
		return ChangesTrackedListMethods.set(index,element,
											 _currentEntries,_changesTracker);
	}
	@Override
	public boolean addAll(final int fromIndex,final Collection<? extends V> c) {
		return ChangesTrackedListMethods.addAll(fromIndex,c, 
												_currentEntries,_changesTracker);
	}
	@Override
	public V remove(final int index) {
		return ChangesTrackedListMethods.remove(index,
												_currentEntries,_changesTracker);
	}

}
