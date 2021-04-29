package r01f.util.types.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

import r01f.patterns.CommandOn;
import r01f.patterns.ProviderFrom;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionWrappers.WrappedCollection;
import r01f.util.types.collections.CollectionWrappers.WrappedSortableCollection;
import r01f.util.types.collections.MapsWrappers.WrappedMap;


/**
 * Collections / Map / Sets utilities
 */
public class CollectionUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR INTERFACES OF Maps or Collections
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface CollectionMutatorMethods<V> {
		public boolean add(V obj);
		public boolean addAll(Collection<? extends V> c); 
		public void clear();
		public boolean remove(Object o);
		public boolean removeAll(Collection<?> c);
		public boolean retainAll(Collection<?> c);
	}
	public static interface MapMutatorMethods<K,V> {
		public void clear();
		public V put(K key,V value);
		public void putAll(Map<? extends K,? extends V> m);
		public V remove(Object key); 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an {@link Iterable} from an {@link Iterator} instance
	 * @param iterator
	 * @return
	 */
	public static <T> Iterable<T> iterableFrom(final Iterator<T> iterator) {
		return new Iterable<T>() {
						@Override
						public Iterator<T> iterator() {
							return iterator;
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API for Lists
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> WrappedCollection<T> of(final WrappedCollection<T> aCol) {
		return aCol;
	}
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection
	 * @param aCol the collection to wrap
	 */
	public static <T> WrappedCollection<T> of(final Collection<T> aCol) {
		if (aCol instanceof WrappedCollection) return CollectionUtils.of((WrappedCollection<T>)aCol);
		return new WrappedCollection<T>(aCol);
	}
	/**
	 * Wraps an iterator 
	 * @param colIt
	 * @return
	 */
	public static <T> WrappedCollection<T> of(final Iterator<T> colIt) {		
		return new WrappedCollection<T>(colIt);
	}
	/**
	 * Wraps an iterable 
	 * @param colIt
	 * @return
	 */
	public static <T> WrappedCollection<T> of(final Iterable<T> colIt) {		
		return new WrappedCollection<T>(colIt);
	}
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection 
	 * @param aCol the collection to wrap
	 */
	@GwtIncompatible("It uses java.text.Collator")
	public static <T extends Comparable<? super T>> WrappedSortableCollection<T> ofSortable(final Collection<T> aCol) {
		return new WrappedSortableCollection<T>(aCol);
	}	
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection
	 * @param objCol the collection to wrap
	 */	
	public static <T> WrappedCollection<T> of(final T... objCol) {
		List<T> theList = Lists.newArrayList(objCol);
		return new WrappedCollection<T>(theList);
	}
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection 
	 * @param objCol the collection to wrap
	 */	
	@GwtIncompatible("It uses java.text.Collator")
	public static <T extends Comparable<? super T>> WrappedSortableCollection<T> ofSortable(final T... objCol) {
		List<T> theList = Lists.newArrayList(objCol);
		return new WrappedSortableCollection<T>(theList);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API for Maps
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Wraps a Map providing a fluent-api with some functions over the Map
	 * @param aMap the Map to wrap
	 */		
	public static <K,V> WrappedMap<K,V> of(final Map<K,V> aMap) {
		return new WrappedMap<K,V>(aMap);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API for Sets
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Wraps some Sets providing a fluent-api with some functions over the Sets
	 * @param sets the sets 
	 */
	public static <T> SetOperations<T> setOperationsOf(final Set<T>... sets) {
		return new SetOperations<T>(sets);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ALL ITEMS NULL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if all array's items are null
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> boolean allItemsNull(final T[] array) {
		if (CollectionUtils.isNullOrEmpty(array)) return false;
		return FluentIterable.from(array)
							 .filter(Predicates.notNull())
							 .isEmpty();
	}
	/**
	 * Checks if all collection's items are null
	 * @param theCol
	 * @return
	 */
	public static boolean allItemsNull(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) return false;
		return FluentIterable.from(theCol)
							 .filter(Predicates.notNull())
							 .isEmpty();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS TO CHECK COLLECTIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if an array is null or empty
	 * @param array the array
	 * @return true if the array is null or empty
	 */
	public static <T> boolean isNullOrEmpty(final T[] array) {
		return array == null || array.length == 0;
	}
	/**
	 * Checks if a Map is null or empty
	 * @param theMap the Map
	 * @return true if the Map is null or empty
	 */
	public static boolean isNullOrEmpty(final Map<?,?> theMap) {
		return (theMap == null || theMap.size() == 0);
	}
	/**
	 * Checks if the {@link Multimap} is null or empty
	 * @param theMultimap the Map
	 * @return true if the {@link Multimap} is null or empty
	 */
	public static boolean isNullOrEmpty(final Multimap<?,?> theMultimap) {
		return (theMultimap == null || theMultimap.size() == 0);
	}
	/**
	 * Checks if the {@link List} is null or empty
	 * @param theMap the list
	 * @return true if the {@link List} is null or empty
	 */	
	public static boolean isNullOrEmpty(final Collection<?> theCol) {
		return (theCol == null || theCol.size() == 0);
	}
	/**
	 * Checks if the {@link Map} has entries
	 * @param theMap the {@link Map}
	 * @return true if the {@link Map} has at least one entry
	 */
	public static boolean hasData(final Map<?,?> theMap) {
		return !CollectionUtils.isNullOrEmpty(theMap);
	}
	/**
	 * Checks if the {@link Multimap} has entries
	 * @param theMap the {@link Multimap}
	 * @return true if the {@link Multimap} has at least one entry
	 */
	public static boolean hasData(final Multimap<?,?> theMultimap) {
		return !CollectionUtils.isNullOrEmpty(theMultimap);
	}
	/**
	 * Checks if the array has elements
	 * @param array the array
	 * @return true if the array has at least one element
	 */
	public static <T> boolean hasData(final T[] array) {
		return !CollectionUtils.isNullOrEmpty(array);
	}
	/**
	 * Checks if the {@link Collection} has elements
	 * @param theCol the {@link Collection}
	 * @return true if the {@link Collection} has at least one element
	 */
	public static boolean hasData(final Collection<?> theCol) {
		return !CollectionUtils.isNullOrEmpty(theCol);
	}
	public static boolean hasData(final int[] array) {
		return array != null && array.length > 0;
	}
	public static boolean hasData(final long[] array) {
		return array != null && array.length > 0;
	}
	public static boolean hasData(final double[] array) {
		return array != null && array.length > 0;
	}
	public static boolean hasData(final float[] array) {
		return array != null && array.length > 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SIZE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Safely returns the size of a {@link Collection}
	 * @param col
	 * @return
	 */
	public static int safeSize(final Collection<?> col) {
		return CollectionUtils.hasData(col) ? col.size() : 0;
	}
	/**
	 * Safely returns the size of a {@link Map}
	 * @param map
	 * @return
	 */
	public static int safeSize(final Map<?,?> map) {
		return CollectionUtils.hasData(map) ? map.size() : 0;
	}
	/**
	 * Safely returns the size of an array
	 * @param arr
	 * @return
	 */
	public static int SafeSize(final Object[] arr) {
		return CollectionUtils.hasData(arr) ? arr.length : 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the first element of a collection
	 * @param theCol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T firstOf(final Collection<?> theCol) {
		Preconditions.checkArgument(theCol != null,"The collection MUST not be null");
		return (T)FluentIterable.from(theCol)
					  		 	.first().orNull();		
	}
	/**
	 * Returns the last element of a collection
	 * @param theCol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T lastOf(final Collection<?> theCol) {
		Preconditions.checkArgument(theCol != null,"The collection MUST not be null");
		return (T)FluentIterable.from(theCol)
					  		 	.last().orNull();		
	}
	/**
	 * Picks the one and only element form the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is thrown
	 * @param theCol
	 * @return
	 */
	public static <T> T pickOneAndOnlyElement(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) throw new IllegalStateException("The collection is null or empty");
		if (theCol.size() > 1) throw new IllegalStateException("The one and only element of a collection has been requested but the collection has MORE THAN ONE element");
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Picks the one and only element form the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is thrown
	 * if the collection is empty it returns null
	 * @param theCol
	 * @return 
	 */
	public static <T> T pickOneAndOnlyElementOrNull(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) return null;
		if (theCol.size() > 1) throw new IllegalStateException("The one and only element of a collection has been requested but the collection has MORE THAN ONE element");
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Picks the one and only element from the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is throw with the message provided
	 * @param theCol
	 * @param errMsg
	 * @param vars
	 * @return
	 */
	public static <T> T pickOneAndOnlyElement(final Collection<?> theCol,
											  final String errMsg,final Object... vars) {
		if (CollectionUtils.isNullOrEmpty(theCol)) throw new IllegalStateException("The collection is null or empty");
		if (theCol.size() > 1) throw new IllegalStateException(Strings.customized(errMsg,vars));
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Picks the one and only element from the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is throw with the message provided
	 * if the collection is empty it returns null
	 * @param theCol
	 * @param errMsg
	 * @param vars
	 * @return
	 */
	public static <T> T pickOneAndOnlyElementOrNull(final Collection<?> theCol,
											  		final String errMsg,final Object... vars) {
		if (CollectionUtils.isNullOrEmpty(theCol)) return null;
		return CollectionUtils.<T>pickOneAndOnlyElement(theCol,errMsg,vars);
	}
	/**
	 * Picks the one and only element from the collection
	 * If the collection has more than one element, a custom exception is thrown
	 * @param <T>
	 * @param theCol
	 * @param throableProvider
	 * @return
	 * @throws Throwable 
	 */
	public static <T,E extends Throwable> T pickOneAndOnlyElementOrThrow(final Collection<?> theCol,
													 					 final ProviderFrom<String,E> throableProvider) throws E {
		if (CollectionUtils.isNullOrEmpty(theCol)) throw throableProvider.from("The collection is null or empty");
		if (theCol.size() > 1) throw throableProvider.from("The one and only element of a collection has been requested but the collection has MORE THAN ONE element");
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/** 
	 * Picks one element from the collection 
	 * @param theCol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T pickOneElement(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) throw new IllegalStateException("The collection is null or empty");
		T outEntry = null;
		for (Object entry : theCol) {
			outEntry = (T)entry;
			break;
		}
		return outEntry;
	}
	/**
	 * Picks one element from the collection 
	 * @param theCol
	 * @return
	 */
	public static <T> T pickOneElementOrNull(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) return null;
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Returns the last element 
	 * @return
	 */
	public static <T> T pickElementAt(final Collection<T> theCol,
									  final int index) {
		if (CollectionUtils.isNullOrEmpty(theCol)) throw new IllegalStateException("The collection is empty");
		int i=0;
		T outEl = null;
		for (T el : theCol) {
			outEl = el;
			if (i == index) break;
			i++;
		}
		return outEl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PARA CHECKEAR EL TIPO DE COLECCIONES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if a type is an array
	 * @param type the type
	 * @return true if the type is an array, false if not
	 */
	public static boolean isArray(final Class<?> type) {
		return type.isArray();
	}
	/**
	 * Checks if an array is composed of elements of a primitive type
	 * @param type the array type
	 * @return true if the elements type is a primitive type
	 */
	public static boolean isObjectsArray(final Class<?> type) {
		return type.isArray() && !type.getComponentType().isPrimitive();
	}	
	/**
	 * Checks if a type implements the {@link Map} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	@GwtIncompatible("uses reflection which is NOT supported by GWT")
	public static boolean isMap(final Class<?> type) {
		return Map.class.isAssignableFrom(type);		// ReflectionUtils.isImplementing
	}
	/**
	 * Checks if a type implements the {@link Collection} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	@GwtIncompatible("uses reflection which is NOT supported by GWT")
	public static boolean isCollection(final Class<?> type) {
		return Collection.class.isAssignableFrom(type);	// ReflectionUtils.isImplementing
	} 
	/**
	 * Checks if a type implements the {@link List} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	@GwtIncompatible("uses reflection which is NOT supported by GWT")
	public static boolean isList(final Class<?> type) {
		return List.class.isAssignableFrom(type);		// ReflectionUtils.isImplementing
	}
	/**
	 * Checks if a type implements the {@link Set} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	@GwtIncompatible("uses reflection which is NOT supported by GWT")
	public static boolean isSet(final Class<?> type) {
		return Set.class.isAssignableFrom(type);		// ReflectionUtils.isImplementing
	} 
	/**
	 * Checks if a type implements the {@link Map} or the {@link Collection} interfaces
	 * @param type the type
	 * @return true if one of the interface is implemented
	 */
	@GwtIncompatible("uses reflection which is NOT supported by GWT")
	public static boolean isMapOrCollection(final Class<?> type) {
		return CollectionUtils.isMap(type) || CollectionUtils.isCollection(type);
	}
	/**
	 * Returns the collection type as it's inteface, ie, if the type is extending a Map, it returns {@link Map}
	 * or if the type is extending a Collection, it returns {@link Collection}
	 * @param obj 
	 * @return List.class if the object is a list and Map.class if the object is a Map; null if it's not neither a List, nor a Map
	 */
	@GwtIncompatible("uses reflection which is NOT supported by GWT")
	public static Class<?> getCollectionType(final Class<?> type) {
		Class<?> colType = null;
		if (CollectionUtils.isMap(type)) {
			colType = Map.class;
		} else if (CollectionUtils.isSet(type)) {
			colType = Set.class;
		} else if (CollectionUtils.isList(type)) {
			colType = List.class;
		} else if (CollectionUtils.isCollection(type)) {
			colType = List.class;
		}
		return colType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Converts a Map of Strings into a Properties type
	 * @param mapOfStrings
	 * @return
	 */
	@GwtIncompatible("Properties is NOT supported by GWT")
	public static Properties toProperties(final Map<String,String> mapOfStrings) {
		Properties outProps = null;
		if (CollectionUtils.hasData(mapOfStrings)) {
			outProps = new Properties();
			for (Map.Entry<String,String> me : mapOfStrings.entrySet()) {
				outProps.setProperty(me.getKey(),me.getValue());
			}
		}
		return outProps;
	}
	/**
	 * Converts a Properties into a Map<String,String> type
	 * @param props properties
	 * @return
	 */
	@GwtIncompatible("Properties are NOT supported by GWT")
	public static Map<String,String> toMap(final Properties props) {
		Map<String,String> outMap = null;
		if (CollectionUtils.hasData(props)) {
			outMap = new HashMap<String,String>(props.size());
			Set<Map.Entry<Object,Object>> entries = props.entrySet();
			for (Map.Entry<Object,Object> entry : entries) {
				outMap.put(entry.getKey().toString(),
						   entry.getValue().toString());
			}
		}
		return outMap;
	}
	/**
	 * Converts a Collection to an array
	 * @param col the {@link Collection}
	 * @return the array
	 */
	@GwtIncompatible("Array NOT supported by GWT")
	@SuppressWarnings({"unchecked","serial"})
	public static <T> T[] toArray(final Collection<T> col) {
		TypeToken<T> tr = new TypeToken<T>() {/* nothing */};
		T[] outArray = CollectionUtils.hasData(col) ? col.toArray((T[])Array.newInstance(tr.getRawType(),col.size()))
													: null;
		return outArray;
	}
	@GwtIncompatible("Array NOT supported by GWT")
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final Collection<?> col,final Class<?> type) {
		T[] outArray = CollectionUtils.hasData(col) ? col.toArray((T[])Array.newInstance(type,col.size()))
													: null;
		return outArray;
	}
	@GwtIncompatible("Array NOT supported by GWT")
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final Collection<T> col,final TypeToken<T> typeRef) {
		T[] outArray = CollectionUtils.hasData(col) ? col.toArray((T[])Array.newInstance(typeRef.getRawType(),col.size()))
													: null;
		return outArray;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link Collection} elements as a comma-separated {@link String}
	 * @param col
	 * @return
	 */
	public static String toStringCommaSeparated(final Object[] col) {
		return CollectionUtils.toStringCommaSeparated(Lists.newArrayList(col));
	}
	/**
	 * Returns the {@link Collection} elements as a comma-separated {@link String}
	 * @param col
	 * @return
	 */
	public static String toStringCommaSeparated(final Collection<?> col) {
		return CollectionUtils.toStringSeparatedWith(col,',');
	}
	/**
	 * Returns the {@link Collection} elements as a char-separated {@link String}
	 * @param col
	 * @param ch
	 * @return 
	 */
	public static String toStringSeparatedWith(final Object[] col,final char ch) {
		return CollectionUtils.toStringSeparatedWith(Lists.newArrayList(col),ch);
	}
	/**
	 * Returns the {@link Collection} elements as a char-separated {@link String}
	 * @param col
	 * @param ch
	 * @return 
	 */
	public static String toStringSeparatedWith(final Collection<?> col,final char ch) {
		return CollectionUtils.toStringSeparatedWith(col,Character.toString(ch));
	}
	/**
	 * Returns the {@link Collection} elements as a {@link String}-separated {@link String}
	 * @param col
	 * @param joiner
	 * @return
	 */
	public static String toStringSeparatedWith(final Collection<?> col,final String joiner) {
		if (CollectionUtils.isNullOrEmpty(col)) return "";
		StringBuilder sb = new StringBuilder();
		for (Iterator<?> it = col.iterator(); it.hasNext(); ) {
			Object el = it.next();
			if (el == null) {
				sb.append("null");
			} else {
				if (el instanceof CanBeRepresentedAsString) {
					sb.append(((CanBeRepresentedAsString)el).asString());
				} else {
					sb.append(el.toString());
				}
			}
			if (it.hasNext()) sb.append(joiner);
		}
		return sb.toString();			
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Cast GUAVA's function to be used to cast all elements in a Collection:
	 * <pre class='brush:java'>
	 * 		List<TypeParent> originalList = Lists.newArrayList();
	 * 		List<TypeChild> theList = Collections2.transform(originalList, 
	 *												  		 new CastFunction<TypeParent,TypeChild>());
	 * </pre>
	 */
	private static class CastFunction<F,T>
	  implements Function<F,T> {
		@Override @SuppressWarnings("unchecked")
		public T apply(final F from) {
			return (T)from;
		}
	}
	/**
	 * Unsafe cast from Collection<F> to Collection<T>
	 * @param col
	 * @return
	 */
	public static <F,T> Collection<T> cast(final Collection<F> col) {
		return Collections2.transform(col,
									  new CastFunction<F,T>());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Executes a command on every object of a collection
	 * @param col
	 * @param cmd
	 */
	public static <T> void executeOn(final Collection<T> col,
								 	 final CommandOn<T> cmd) {
		if (cmd != null && CollectionUtils.hasData(col)) {
			for (T obj : col) cmd.executeOn(obj);
		}
	}
}
