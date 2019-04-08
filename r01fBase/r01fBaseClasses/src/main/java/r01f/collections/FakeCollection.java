package r01f.collections;

import java.util.Collection;
import java.util.Iterator;

/**
 * A fake collection that simply do nothing (does not store anything)
 * @param <T>
 */
public class FakeCollection<T>
  implements Collection<T> {
	@Override
	public boolean add(final T o) {
		return true;
	}
	@Override
	public boolean addAll(final Collection<? extends T> os) {
		return true;
	}
	@Override
	public void clear() {
		// nothing
	}
	@Override
	public boolean contains(final Object o) {
		return false;
	}
	@Override
	public boolean containsAll(final Collection<?> os) {
		return false;
	}
	@Override
	public boolean isEmpty() {
		return false;
	}
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
						@Override
						public boolean hasNext() {
							return false;
						}
						@Override
						public T next() {
							return null;
						}
						@Override
						public void remove() {
							// nothing
						}
			   };
	}
	@Override
	public boolean remove(final Object o) {
		return true;
	}
	@Override
	public boolean removeAll(final Collection<?> os) {
		return true;
	}
	@Override
	public boolean retainAll(final Collection<?> os) {
		return true;
	}
	@Override
	public int size() {
		return 0;
	}
	@Override
	public Object[] toArray() {
		return null;
	}
	@Override
	public <U> U[] toArray(final U[] arr) {
		return null;
	}
}
