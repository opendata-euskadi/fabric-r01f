package r01f.aspects.core.dirtytrack;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.collections.dirtytrack.ChangesTrackedList;
import r01f.collections.dirtytrack.interfaces.ChangesTrackableCollection;

public class ChangesTrackedListInTrackableObject<V> 
     extends ChangesTrackedCollectionInTrackableObject<V> 
  implements List<V> {
	private static final long serialVersionUID = -8196335074553302594L;
/////////////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////
	public ChangesTrackedListInTrackableObject(final DirtyStateTrackable container,
											   final ChangesTrackableCollection<V> theCol) {
		super(container,theCol);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public V get(final int index) {
		return ((ChangesTrackedList<V>)_changesTrackedCollection).get(index);
	}	
	@Override
	public int indexOf(final Object o) {
		return ((ChangesTrackedList<V>)_changesTrackedCollection).indexOf(o);
	}
	@Override
	public int lastIndexOf(final Object o) {
		return ((ChangesTrackedList<V>)_changesTrackedCollection).lastIndexOf(o);
	}
	@Override
	public ListIterator<V> listIterator() {
		return ((ChangesTrackedList<V>)_changesTrackedCollection).listIterator();
	}
	@Override
	public ListIterator<V> listIterator(final int index) {
		return ((ChangesTrackedList<V>)_changesTrackedCollection).listIterator(index);
	}
	@Override
	public List<V> subList(final int fromIndex,final int toIndex) {
		return ((ChangesTrackedList<V>)_changesTrackedCollection).subList(fromIndex,toIndex);
	}	
	@Override
	public V set(final int index,final V element) {
		V outResult = ((ChangesTrackedList<V>)_changesTrackedCollection).set(index,element);
		if (outResult != null) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outResult;
	}	
	@Override
	public void add(final int index,final V element) {
		((ChangesTrackedList<V>)_changesTrackedCollection).add(index,element);
		_trckContainerObj.getTrackingStatus().setThisDirty(true);
	}	
	@Override
	public boolean addAll(final int index,final Collection<? extends V> c) {
		boolean outResult = ((ChangesTrackedList<V>)_changesTrackedCollection).addAll(index,c);
		if (outResult) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outResult;
	}
	@Override
	public V remove(final int index) {
		V outResult = ((ChangesTrackedList<V>)_changesTrackedCollection).remove(index);
		if (outResult != null) _trckContainerObj.getTrackingStatus().setThisDirty(true);
		return outResult;
	}

}
