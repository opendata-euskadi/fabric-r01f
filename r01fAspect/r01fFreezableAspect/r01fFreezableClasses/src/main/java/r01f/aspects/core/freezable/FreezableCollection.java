package r01f.aspects.core.freezable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Wraps a {@link Collection} overriding the mutator methods so that it throws an exception 
 * if the collection is frozen
 * @param <V> value
 */
public class FreezableCollection<V> 
  implements Collection<V>,
			 Serializable {
	private static final long serialVersionUID = -2955026845211623617L;
/////////////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////////////
	private final Collection<V> _col;
	
	private final boolean _frozen;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FreezableCollection(final Collection<V> theCol,final boolean frozen) {
		_col = theCol;
		_frozen = frozen;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT MUTABLE METHODS DELEGATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean contains(final Object o) {
		return _col.contains(o);
	}
	@Override
	public boolean containsAll(final Collection<?> c) {
		return _col.containsAll(c);
	}
	@Override
	public boolean isEmpty() {
		return _col.isEmpty();
	}
	@Override
	public Iterator<V> iterator() {
		return _col.iterator();
	}
	@Override
	public int size() {
		return _col.size();
	}
	@Override
	public Object[] toArray() {
		return _col.toArray();
	}
	@Override
	public <T> T[] toArray(final T[] a) {
		return _col.toArray(a);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MUTABLE METHODS OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean add(V e) {
		if (_frozen) throw new IllegalStateException("The collection is FROZEN! you cannot add anything on it. This is because the obj where the collection is contained is frozen");
		return _col.add(e);
	}
	@Override
	public boolean addAll(final Collection<? extends V> c) {
		if (_frozen) throw new IllegalStateException("The collection is FROZEN! you cannot add anything on it. This is because the obj where the collection is contained is frozen");	
		return _col.addAll(c);
	}
	@Override
	public void clear() {
		if (_frozen) throw new IllegalStateException("The collection is FROZEN! you cannot clear its contents. This is because the obj where the collection is contained is frozen");
		_col.clear();
	}
	@Override
	public boolean remove(Object o) {
		if (_frozen) throw new IllegalStateException("The collection is FROZEN! you cannot remove anything. This is because the obj where the collection is contained is frozen");
		return _col.remove(o);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		if (_frozen) throw new IllegalStateException("The collection is FROZEN! you cannot remove anything. This is because the obj where the collection is contained is frozen");
		return _col.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		if (_frozen) throw new IllegalStateException("The collection is FROZEN! you cannot remove anything. This is because the obj where the collection is contained is frozen");
		return _col.retainAll(c);
	}
	
}
