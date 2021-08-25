package r01f.util.types.collections;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Computes the changes between two collections
 * Usage:
 * Given an object like:
 * <pre class='brush:java'> 
 *		@Accessors(prefix="_")
 *		@RequiredArgsConstructor
 *		public static class MyType {
 *			@Getter private final String _key;
 *			@Getter private final String _value;
 *		}
 * </pre>
 * and two collections like:
 * <pre class='brush:java'>
 *		Collection<MyType> col1 = Lists.newArrayList(new MyType("a","val"),	// deleted
 *													 new MyType("b","val"),	// both
 *													 new MyType("c","val"));// deleted
 *		
 *		Collection<MyType> col2 = Lists.newArrayList(new MyType("b","val"),	// both
 *													 new MyType("d","val"));// new
 * </pre>
 * get the differences using:
 * <pre class='brush:java'>
 * 		CollectionChanges<MyType> changes = CollectionChanges.changesBetween(col1,col2)
 *															 .using(MyType::getKey);	// use the key to compare items
 * </pre>
 * @param <T>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class CollectionChanges<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final Collection<T> _deleted;
	@Getter private final Collection<T> _new;
	@Getter private final Collection<T> _inBothCollections;
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> CollectionChangesBuilderCompare<T> changesBetween(final Collection<T> col1,final Collection<T> col2) {
		return new CollectionChangesBuilderCompare<>(col1,col2);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class CollectionChangesBuilderCompare<T> {
		private final Collection<T> _col1;
		private final Collection<T> _col2;
		
		public <TC> CollectionChanges<T> using(final Function<T,TC> fun) {
			// create two Set associated with each of the Collections
			Set<CollectionItemWrapper<T,TC>> set1 = _col1.stream()
														 .map(i -> new CollectionItemWrapper<>(fun,i))
														 .collect(Collectors.toSet());
			Set<CollectionItemWrapper<T,TC>> set2 = _col2.stream()
														 .map(i -> new CollectionItemWrapper<>(fun,i))
														 .collect(Collectors.toSet());

			// get the differences
			Collection<CollectionItemWrapper<T,TC>> deletedItems = Sets.difference(set1,set2);				// items contained by set1 and NOT contained by set2
			Collection<CollectionItemWrapper<T,TC>> newItems = Sets.difference(set2,set1);					// items contained by set2 and NOT contained by set1
			Collection<CollectionItemWrapper<T,TC>> alreadyExistingItems = Sets.intersection(set1,set2);	// items contained by both set1 and set2
			
			// return
			return new CollectionChanges<>(deletedItems.stream()
													   .map(CollectionItemWrapper::getWrappedObj)
													   .collect(Collectors.toList()),
										   newItems.stream()
												   .map(CollectionItemWrapper::getWrappedObj)
												   .collect(Collectors.toList()),
										   alreadyExistingItems.stream()
												   			   .map(CollectionItemWrapper::getWrappedObj)
												   			   .collect(Collectors.toList()));					
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	private static class CollectionItemWrapper<T,TC> {
		private final Function<T,TC> _fun;	// are two objects the same obj?
		private final T _wrappedObj;
		
		public T getWrappedObj() {
			return _wrappedObj;
		}
		
		@Override @SuppressWarnings("unchecked")
		public boolean equals(final Object o) {
			if (o == null) return false;
			if (this == o) return true;
			if (!(o instanceof CollectionItemWrapper)) return false;

			T other = ((CollectionItemWrapper<T,TC>)o).getWrappedObj();
			if (_wrappedObj == other) return true;
			
			TC thisComp = _fun.apply(_wrappedObj);
			TC otherComp = _fun.apply(other);
			return thisComp.equals(otherComp);
		}
		@Override
		public int hashCode() {
			TC thisComp = _fun.apply(_wrappedObj);
			return thisComp.hashCode();
		}
		@Override
		public String toString() {
			return _wrappedObj.toString();
		}
	}
}
