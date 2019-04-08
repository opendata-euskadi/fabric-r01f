package r01f.util.types.collections;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import r01f.collections.KeyIntrospector;
import r01f.util.types.Strings;

/**
 * Fluent APIs to operate with Collections
 */
public class CollectionWrappers {	
///////////////////////////////////////////////////////////////////////////////
//	Encapsulates a Collection
///////////////////////////////////////////////////////////////////////////////
	public static class WrappedCollection<T> 
			 implements Collection<T> {
		/**
		 * The delegated collection
		 */
		Collection<T> _theCol;
		
		public WrappedCollection(final Collection<T> newCollection) {
			_theCol = newCollection;
		}
		public WrappedCollection(final Iterator<T> colIt) {
			_theCol = Lists.newArrayList(colIt);
		}
		public WrappedCollection(final Iterable<T> colIt) {
			_theCol = Lists.newArrayList(colIt);
		}
		
		@Override public int size() {			return _theCol.size();			}
		@Override public boolean isEmpty() {	return _theCol.isEmpty(); 		}
		@Override public boolean contains(Object o) {	return _theCol.contains(o);		}
		@Override public boolean containsAll(Collection<?> c) {		return _theCol.containsAll(c);		}
		@Override public Iterator<T> iterator() {		return _theCol.iterator();		}
		@Override public Object[] toArray() {			return _theCol.toArray();		}
		@Override public <E> E[] toArray(E[] a) {		return _theCol.toArray(a);		}
		@Override public boolean add(T e) {				return _theCol.add(e);			}
		@Override public boolean remove(Object o) {		return _theCol.remove(o);		}
		@Override public boolean addAll(Collection<? extends T> c) {	return _theCol.addAll(c);			}
		@Override public boolean removeAll(Collection<?> c) {			return _theCol.removeAll(c);		}
		@Override public boolean retainAll(Collection<?> c) {			return _theCol.retainAll(c);		}
		@Override public void clear() {	_theCol.clear();	}
		@Override public boolean equals(Object o) {	return _theCol.equals(o);		}
		@Override public int hashCode() {			return _theCol.hashCode();		}

		public Collection<T> collection() {
			return _theCol;
		}
		public Collection<T> asCollection() {
			return _theCol;
		}
		public Set<T> asSet() {
			return Sets.newHashSet(_theCol);
		}
		public <K> Map<K,T> toMap(final KeyIntrospector<K,T> keyExtractor) {
			return _toMap(_theCol,keyExtractor);
		}
	    /**
	     * Cast a {@link Collection} of a type to a {@link Collection} of another type
	     * ie: Given a type T that implements an interface I, if a <pre>Collection<T> myCol</pre> is to be 
	     *     converted to a <pre>Collection<I></pre> a simple casting <pre>(Collection<I>)myCol</pre>
	     *     would NOT compile, so this method can be used
	     * <pre class='brush:java'>
	     * 		Collection<I> myColOfI = CollectionUtils.of(myCol)
	     * 												.asCollectionOf(I.class);
	     * </pre>
	     * @param type
	     * @return
	     */
	    @GwtIncompatible("cast reflection method of a type is NOT supported by GWT")
	    public <I> Collection<I> asCollectionOf(final Class<I> type) {
			Collection<I> castedCollection = null;
			if (CollectionUtils.hasData(_theCol)) {
				castedCollection = Collections2.transform(_theCol,
														  new Function<T,I>() {
																	@Override
																	public I apply(final T el) {
																		return type.cast(el);
																	}
														  });
			}
			return castedCollection;
	    }
		public <F> WrappedCollection<F> transform(final Function<? super T,F> function) {
			Collection<F> transformedList = Collections2.transform(_theCol,function);
			WrappedCollection<F> transformedListWrapped = new WrappedCollection<F>(transformedList);
			return transformedListWrapped;
		}
		public WrappedCollection<T> filter(final Predicate<? super T> predicate) {
			Set<T> filteredSet = Sets.filter(Sets.newLinkedHashSet(_theCol),predicate);
			List<T> list = Lists.newArrayList(filteredSet);
			WrappedCollection<T> filteredListWrapped = new WrappedCollection<T>(list);
			return filteredListWrapped;
		}
		public T findFirstElementMatching(final Predicate<? super T> predicate) {
			if (CollectionUtils.isNullOrEmpty(_theCol)) throw new IllegalStateException("The collection is empty");
			T outElement = null;
			for (T el : _theCol) {
				if (predicate.apply(el)) {
					outElement = el;
					break;
				}
			}
			return outElement;
		}
		public T findFirstElementMatchingOrNull(final Predicate<? super T> predicate) {
			if (CollectionUtils.isNullOrEmpty(_theCol)) return null;
			return this.findFirstElementMatching(predicate);
		}
		public int indexOf(final T elem) {
			if (CollectionUtils.isNullOrEmpty(_theCol)) throw new IllegalStateException("The collection is empty");
			int outIndex = -1;
			int i=0;
			for (T currEl : _theCol) {
				if (currEl.equals(elem)) {
					outIndex = i;
					break;
				}
				i++;
			}
			return outIndex;
		}
	    /**
	     * Returns the one and only element of the {@link Collection}
	     * if the {@link Collection} contains more than one element an {@link IllegalStateException} is thrown
	     * @return the element
	     */
	    public T pickOneAndOnlyElement() {
	    	return this.pickOneAndOnlyElement("The one and only element of a collection has been requested but the collection has MORE THAN ONE element");
	    }
	    /**
	     * Returns the one and only element of the {@link Collection}
	     * if the {@link Collection} contains more than one element an {@link IllegalStateException} is thrown with the message provided	
	     * @param errMsg
	     * @param params
	     * @return
	     */
	    public T pickOneAndOnlyElement(final String errMsg,final Object... params) {
			if (CollectionUtils.isNullOrEmpty(_theCol)) throw new IllegalStateException("The collection is empty");
			if (_theCol.size() > 1) throw new IllegalStateException(Strings.customized(errMsg,params));
			return this.pickOneElement();
	    }
	    /**
	     * Returns the one and only element of the {@link Collection}
	     * if the {@link Collection} contains more than one element an {@link IllegalStateException} is thrown
	     * if the collection is empty it returns null
	     * @return 
	     */
	    public T pickOneAndOnlyElementOrNull() {
	    	if (CollectionUtils.isNullOrEmpty(_theCol)) return null;
	    	return this.pickOneAndOnlyElement();
	    }
	    /**
	     * Returns the one and only element of the {@link Collection}
	     * if the {@link Collection} contains more than one element an {@link IllegalStateException} is thrown with the message provided
	     * if the collection is empty it returns null
	     * @param errMsg
	     * @param params
	     * @return
	     */
	    public T pickOneAndOnlyElementOrNull(final String errMsg,final Object... params) {
	    	if (CollectionUtils.isNullOrEmpty(_theCol)) return null;
	    	return this.pickOneAndOnlyElement(errMsg,params);
	    }
	    /**
	     * Returns one element (normally the first of the {@link Collection}, but this depends on the underlying {@link Collection} type)
	     * @return the element
	     */
	    public T pickOneElement() {
			if (CollectionUtils.isNullOrEmpty(_theCol)) throw new IllegalStateException("The collection is empty");
	    	return this.pickElementAt(0);
	    }
	    /**
	     * Returns one element (normally the first of the {@link Collection}, but this depends on the underlying {@link Collection} type)
	     * if the collection is empty it returns null
	     * @return the element
	     */
	    public T pickOneElementOrNull() {
	    	if (CollectionUtils.isNullOrEmpty(_theCol)) return null;
	    	return this.pickOneElement();
	    }
	    /**
	     * Returns the last element 
	     * @return
	     */
	    public T pickElementAt(final int index) {
			return CollectionUtils.pickElementAt(_theCol,index);
	    }
	    /**
	     * Returns the {@link Collection} elements as a comma-separated {@link String}
	     * @return 
	     */
	    public String toStringCommaSeparated() {
	    	return this.toStringSeparatedWith(',');
	    }
	    /**
	     * Returns the {@link Collection} elements as a char-separated {@link String}
	     * @return 
	     */
	    public String toStringSeparatedWith(final char ch) {
	    	return this.toStringSeparatedWith(Character.toString(ch));
	    }
	    /**
	     * Returns the {@link Collection} elements as a String-separated {@link String}
	     * @param joiner
	     * @return
	     */
	    public String toStringSeparatedWith(final String joiner) {
	    	return CollectionUtils.toStringSeparatedWith(_theCol,joiner);    	
	    }
	}
///////////////////////////////////////////////////////////////////////////////
//	Encapsulates a Sortable Collection
///////////////////////////////////////////////////////////////////////////////
	@GwtIncompatible("GWT does NOT supports java.text.Collator")
	public static class WrappedSortableCollection<T extends Comparable<? super T>> 
				extends WrappedCollection<T> {		
		public WrappedSortableCollection(final Collection<T> newCol) {
			super(newCol);
		}
		public WrappedSortableCollection<T> sort() {
			_theCol = _sort(_theCol);
			return this;
		}
		@GwtIncompatible("GWT does NOT supports java.util.Locale")
		public WrappedSortableCollection<T> sort(final Locale loc) {
			_theCol = _sort(_theCol,loc);
			return this;
		}
		public WrappedSortableCollection<T> sort(final String langLoc) {
			_theCol = _sort(_theCol,langLoc);
			return this;
		}
		@GwtIncompatible("GWT does NOT supports java.text.Collator")
		public WrappedSortableCollection<T> sort(final Collator collator) {
			_theCol = _sort(_theCol,collator);
			return this;			
		}
		public WrappedSortableCollection<T> sort(final Comparator<T> comp) {
			_theCol = _sort(_theCol,comp);
			return this;	
		}		
	}		
///////////////////////////////////////////////////////////////////////////////
// 	Ordering
///////////////////////////////////////////////////////////////////////////////
    /**
     * Sorting of a {@link Collection}
     * @param unorderedCollection the {@link Collection} to be ordered
     * @return the ordered collection
     */
	@GwtIncompatible("GWT does NOT supports java.util.Locale")
    static <T extends Comparable<? super T>> Collection<T> _sort(final Collection<T> unorderedCollection) {
        return _sort(unorderedCollection,Locale.getDefault());
    } 
    /**
     * Sorting of a {@link Collection}
     * @param unorderedCollection the {@link Collection} to be ordered
     * @param String locale as {@link String}
     * @return the ordered collection
     */
	@GwtIncompatible("GWT does NOT supports java.util.Locale and java.text.Collator")
    static <T extends Comparable<? super T>> Collection<T> _sort(final Collection<T> unorderedCollection,
    															 final String langLocale) {
    	Collator collator = Collator.getInstance(new Locale(langLocale));
    	return _sort(unorderedCollection,collator);
    }
    /**
     * Sorting of a {@link Collection}
     * @param unorderedCollection the {@link Collection} to be ordered
     * @param locale
     * @return the ordered collection
     */
	@GwtIncompatible("GWT does NOT supports java.util.Locale and java.text.Collator")
    static <T extends Comparable<? super T>> Collection<T> _sort(final Collection<T> unorderedCollecion,
    															 final Locale locale) {
    	Collator collator = Collator.getInstance(locale);
    	return _sort(unorderedCollecion,collator);
    } 
    /**
     * Sorting of a {@link Collection}
     * @param unorderedCollection the {@link Collection} to be ordered
     * @param collator
     * @return the ordered collection
     */
	@GwtIncompatible("GWT does NOT supports java.text.Collator")
    static <T extends Comparable<? super T>> Collection<T> _sort(final Collection<T> unorderedCollection,
    															 final Collator collator) {
    	if (unorderedCollection == null || unorderedCollection.isEmpty()) return null;	// for code cleaning purposes only
        return Ordering.from(collator).sortedCopy(unorderedCollection);       
    }   
    /**
     * Sorting of a {@link Collection}
     * @param unorderedCollection the {@link Collection} to be ordered
     * @param comparator
     * @return the ordered collection
     */
    @GwtIncompatible("GWT does NOT supports java.util.Locale and java.text.Collator")
    static <T extends Comparable<? super T>> List<T> _sort(final Collection<T> unorderedCollection,
    													   final Comparator<? super T> comparator) {
    	if (unorderedCollection == null) return null;
    	Comparator<? super T> theComp = comparator;
    	if (theComp == null) theComp = Collator.getInstance(Locale.getDefault());
    	return Ordering.from(theComp).sortedCopy(unorderedCollection);
    }
///////////////////////////////////////////////////////////////////////////////
// CONVERSION
///////////////////////////////////////////////////////////////////////////////
    /**
     * Converts a {@link Collection} into a {@link Map} indexed by the key extracted using the provided {@link MapEntryKeyExtractor}
     * @param theCollection the {@link Collection} to be converted to a {@link Map}
     * @param keyExtractor the key extractor
     * @return the {@link Map}
     */
    static <K,V> Map<K,V> _toMap(final Collection<V> theCollection,
    							 final KeyIntrospector<K,V> keyExtractor) {
        Map<K,V> outMap = null;
        if (theCollection != null) {
            outMap = new HashMap<K,V>(theCollection.size());
            // iterate over array elements and put them in the Map           
            for (V currObj : theCollection) {
            	K currObjOid = keyExtractor.keyFor(currObj);
                outMap.put(currObjOid,currObj);
            }
        }
        return outMap;
    } 
//    /**
//     * Converts a {@link Collection} into a {@link Map} indexed by the value
//     * in a field of each element of the {@link Collection}
//     * @param theCollection the {@link Collection} to be converted to a {@link Map}
//     * @param oidFieldName the field of the {@link Collection}'s elements that acts as {@link Map}'s key
//     * @return the {@link Map}
//     */
//    @GwtIncompatible("GWT does NOT supports reflection")
//    static <K,V> Map<K,V> _toMap(final Collection<V> theCollection,
//    							 final String oidFieldName) {
//        final String theOidFieldName = oidFieldName != null ? oidFieldName : "key";
//    	return _toMap(theCollection,
//    				  new KeyIntrospector<K,V>() {
//							@Override
//							public K keyFor(final V value) {
//				            	K currObjOid = FluentReflection.field(theOidFieldName)
//				            								   .ofType(new TypeRef<K>() {/*nothing*/})
//				            								   .in(value).get();
//				            	return currObjOid;
//							}
//    				  });
//    }     
}
