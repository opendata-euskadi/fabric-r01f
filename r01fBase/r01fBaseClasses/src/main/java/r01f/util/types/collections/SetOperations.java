package r01f.util.types.collections;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

public class SetOperations<T> {
	Set<T>[] _sets;
	Comparator<? super T> _comparator;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public SetOperations(final Set<T>... sets) {
		_sets = sets;
	}
	public SetOperations<T> usingComparator(final Comparator<T> comp) {
		_comparator = comp;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la interseccion de todos los conjuntos: aquellos elementos que son
	 * comunes a todos ellos
	 * @return
	 */
	public Set<T> intersection() {
		if (_sets == null || _sets.length == 0) return Sets.newHashSet();
		// Tomar un elemento del array e interseccionarlo con el resto
		SetView<T> intersect = new SetView<T>() {	// ... comenzar con una vista del primer elemento
									@Override public Iterator<T> iterator() { return _sets[0].iterator(); }
									@Override public int size() { return _sets[0].size(); }
									@Override public boolean isEmpty() { return _sets[0].isEmpty(); }
									@Override public boolean contains(Object object) { return _sets[0].contains(object); }
									@Override public boolean containsAll(Collection<?> collection) { return _sets[0].containsAll(collection); }
								};
		for (int i=1; i<_sets.length; i++) {
			intersect = _intersection(intersect,_sets[i]);
			if (intersect.size() == 0) break;	// no seguir...
		}
		return intersect;	// devolver la interseccion
	}
	/**
	 * Obtiene la union de todos los conjuntos: un conjunto con los elementos de todos los demas pero sin repetir
	 * @return 
	 */
	public Set<T> union() {
		if (_sets == null || _sets.length == 0) return Sets.newHashSet();
		// Tomar un elemento del array y unirlo con el resto
		SetView<T> union = new SetView<T>() {	// ... comenzar con una vista del primer elemento
									@Override public Iterator<T> iterator() { return _sets[0].iterator(); }
									@Override public int size() { return _sets[0].size(); }
									@Override public boolean isEmpty() { return _sets[0].isEmpty(); }
									@Override public boolean contains(Object object) { return _sets[0].contains(object); }
									@Override public boolean containsAll(Collection<?> collection) { return _sets[0].containsAll(collection); }
								};
		for (int i=1; i<_sets.length; i++) {
			union = _union(_sets[i],union);
		}
		return union;	// devolver la interseccion				
	}
	/**
	 * Obtiene la diferencia de todos los conjuntos: aquellos elementos que NO son comunes
	 * @return
	 */
	public Set<T> difference() {
		// Simplemente es la diferencia de la union (todos) y la interseccion (comunes)
		return Sets.difference(this.union(),this.intersection());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////	
	private SetView<T> _intersection(final Set<T> set1,final Set<T> set2) {
		final Predicate<T> inSet2 = new InSetPredicate(set2);
		return new SetView<T>() {
			@Override public Iterator<T> iterator() {
				return Iterators.filter(set1.iterator(),inSet2);
			}
			@Override public int size() {
				return Iterators.size(iterator());
			}
			@Override public boolean isEmpty() {
				return !iterator().hasNext();
			}
			@Override public boolean contains(Object object) {
				return set1.contains(object) && set2.contains(object);
			}
			@Override public boolean containsAll(Collection<?> collection) {
				return set1.containsAll(collection) && set2.containsAll(collection);
			}
		};		
	}
	private SetView<T> _union(final Set<T> set1,final Set<T> set2) {
		final Set<T> set2minus1 = Sets.difference(set2, set1);	// aquellos elementos que NO son comunes		
		return new SetView<T>() {
			@Override public int size() {
				return set1.size() + set2minus1.size();
			}
			@Override public boolean isEmpty() {
				return set1.isEmpty() && set2.isEmpty();
			}
			@Override public Iterator<T> iterator() {
				return Iterators.unmodifiableIterator(Iterators.concat(set1.iterator(),set2minus1.iterator()));
			}
			@Override public boolean contains(Object object) {
				return set1.contains(object) || set2.contains(object);
			}
			@Override public <S extends Set<T>> S copyInto(S set) {
				set.addAll(set1);
				set.addAll(set2);
				return set;
			}
			@Override public ImmutableSet<T> immutableCopy() {
				return new ImmutableSet.Builder<T>().addAll(set1).addAll(set2).build();
			}
		};
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  INNER CLASSES
/////////////////////////////////////////////////////////////////////////////////////////	
	private final class InSetPredicate 
			 implements Predicate<T> {
		private Set<T> _theSet; 			
		public InSetPredicate(Set<T> set) {
			_theSet = set;
		}
		@Override
		public boolean apply(T el) {
			try {				
				boolean inCol = false;				
				if (_comparator != null) {
					for (T currEl : _theSet) {
						if (_comparator.compare(currEl,el) == 0) {
							inCol = true;
							break;
						}
					}
				} else {
					inCol = _theSet.contains(el);	// por defecto utilizando equals()
				}
				return inCol;
			} catch (NullPointerException nullEx) {
			  return false;
			} catch (ClassCastException castEx) {
			  return false;
			}			
		}				
	}	
	/**
	 * An unmodifiable view of a set which may be backed by other sets; this view
	 * will change as the backing sets do. Contains methods to copy the data into
	 * a new set which will then remain stable. There is usually no reason to
	 * retain a reference of type {@code SetView}; typically, you either use it
	 * as a plain {@link Set}, or immediately invoke {@link #immutableCopy} or
	 * {@link #copyInto} and forget the {@code SetView} itself.
	 */
	public abstract class SetView<E> 
				  extends AbstractSet<E> {
		SetView() {} // no subclasses but our own
		/**
		 * Returns an immutable copy of the current contents of this set view.
		 * Does not support null elements.
		 *
		 * <p><b>Warning:</b> this may have unexpected results if a backing set of
		 * this view uses a nonstandard notion of equivalence, for example if it is
		 * a {@link TreeSet} using a comparator that is inconsistent with {@link
		 * Object#equals(Object)}.
		 */
		public ImmutableSet<E> immutableCopy() {
			return ImmutableSet.copyOf(this);
		}
		/**
		 * Copies the current contents of this set view into an existing set. This
		 * method has equivalent behavior to {@code set.addAll(this)}, assuming that
		 * all the sets involved are based on the same notion of equivalence.
		 *
		 * @return a reference to {@code set}, for convenience
		 */
		public <S extends Set<E>> S copyInto(S set) {
			set.addAll(this);
			return set;
		}
	}	
}
