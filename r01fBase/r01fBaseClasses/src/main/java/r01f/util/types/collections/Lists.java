package r01f.util.types.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;


public class Lists {
	/**
	 * Checks if a list is null or empty
	 */
	public static boolean isNullOrEmpty(final List<?> list) {
		if (list == null || list.isEmpty()) return true;
		return false;
	}
	/**
	 * Creates a mutable ArrayList instance containing the given elements.
	 */
	public static <E> List<E> newArrayList(final E... elements) {
		return com.google.common.collect.Lists.newArrayList(elements);
	}
	/**
	 * Creates a mutable ArrayList instance containing the given elements.
	 */
	public static <E> List<E> newArrayList(final Iterable<? extends E> elements) {
		return com.google.common.collect.Lists.newArrayList(elements);
	}
	/**
	 * Creates a mutable ArrayList instance containing the given elements.
	 */
	public static <E> List<E> newArrayList(final Iterator<? extends E> elements) {
		return com.google.common.collect.Lists.newArrayList(elements);
	}
	/**
	 * Creates a mutable, empty ArrayList instance.
	 */
	public static <E> List<E> newArrayList() {
		return com.google.common.collect.Lists.newArrayList();
	}
	/**
	 * Creates an ArrayList instance backed by an array of the exact size specified; equivalent to ArrayList.ArrayList(int).
	 */
	public static <E> List<E> newArrayListWithCapacity(final int size) {
		return com.google.common.collect.Lists.newArrayListWithCapacity(size);
	}
	/**
	 * Creates an empty LinkedList instance.
	 */
	public static <E> List<E> newLinkedList() {
		return com.google.common.collect.Lists.newLinkedList();
	}
	/**
	 * Creates a LinkedList instance containing the given elements.
	 */
	public static <E> List<E> newLinkedList(final Iterable<? extends E> elements) {
		return com.google.common.collect.Lists.newLinkedList(elements);
	}
	/**
	 * Creates a LinkedList instance containing the given elements.
	 */
	public static <E> LinkedList<E> newLinkedList(final Iterator<? extends E> elements) {
		if (elements == null || !elements.hasNext()) return null;
		@SuppressWarnings("unchecked")
		LinkedList<E> list = (LinkedList<E>)newLinkedList();
		do {
			list.add(elements.next());
		} while (elements.hasNext());
	    return list;
	}
	/**
	 * Returns consecutive sublists of a list, each of the same size (the final list may be smaller).
	 */
	public static <E> List<List<E>> partition(final List<E> list,final int size) {
		return com.google.common.collect.Lists.partition(list,size);
	}
	/**
	 * Returns a reversed view of the specified list.
	 */
	public static <E> List<E> reverse(final List<E> list) {
		return com.google.common.collect.Lists.reverse(list);
	}
	/**
	 * Returns a list that applies function to each element of fromList.
	 */
	public static <E,T> List<T> transform(final List<E> fromList,final Function<? super E,? extends T> function) {
		return com.google.common.collect.Lists.transform(fromList,function);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns all the element's siblings in the list (every other element in the list)
	 * @param theList
	 * @param element
	 * @return
	 */
	public static <E> List<E> getSiblings(final List<E> theList,final E element) {
		if (theList.size() == 1) return null;		// there's only one child (this item), so this item do not have any siblings
		List<E> outSlibings = new ArrayList<E>(theList.size()-1);
		for (E item : theList) {
			if (item == element) continue;	// this item does not belong to the siblings collection
			outSlibings.add(item);
		}
		return outSlibings;
	}
	/**
	 * Returns a list with all siblings elements AFTER the provided one
	 * If the element is the one and only element or the last element in the list null is returned
	 * @param allSlibings
	 * @param element
	 * @return
	 */
	public static <E> List<E> getSiblingsAfter(final List<E> allSlibings,final E element) {
		if (allSlibings.size() == 1) return null;	// there's only item (this item), so this item do not have any siblings
		
		int thisItemIndex = allSlibings.indexOf(element);
		int numOfSlibingsAfter = allSlibings.size() - (thisItemIndex+1);
		if (numOfSlibingsAfter == 0) return null;		// this is the last child... no siblings after
		
		List<E> outSlibingsAfter = new ArrayList<E>(numOfSlibingsAfter);
		for (int i=thisItemIndex+1; i < allSlibings.size(); i++) {
			outSlibingsAfter.add(allSlibings.get(i));
		}
		return outSlibingsAfter;
	}
	/**
	 * Returns the next element in the list AFTER the provided one
	 * If the element is the one and only element or the last element in the list null is returned
	 * @param allSlibings
	 * @param element
	 * @return
	 */
	public static <E> E getNextSibling(final List<E> allSlibings,final E element) {
		List<E> siblingsAfter = Lists.getSiblingsAfter(allSlibings,element);
		return siblingsAfter != null ? siblingsAfter.get(0) : null;
	}
	/**
	 * Returns a list with all the siblings elements BEFORE the provided one
	 * If the element is the one and only element or the first element in the list null is returned
	 * @param allSlibings
	 * @param element
	 * @return
	 */
	public static <E> List<E> getSiblingsBefore(final List<E> allSlibings,final E element) {
		if (allSlibings.size() == 1) return null;	// there's only one item (this item), so this item do not have any siblings
		
		int thisItemIndex = allSlibings.indexOf(element);
		int numOfSlibingsBefore = thisItemIndex;
		if (numOfSlibingsBefore == 0) return null;		// this is the first child... no siblings before
		
		List<E> outSlibingsAfter = new ArrayList<E>(numOfSlibingsBefore);
		for (int i=0; i < thisItemIndex; i++) {
			outSlibingsAfter.add(allSlibings.get(i));
		}
		return outSlibingsAfter;
	}
	/**
	 * Returns the previous element in the list BEFORE the provided one
	 * If the element is the one and only element or the first element in the list null is returned
	 * @param allSlibings
	 * @param element
	 * @return
	 */
	public static <E> E getPrevSibling(final List<E> allSlibings,final E element) {
		List<E> siblingsBefore = Lists.getSiblingsBefore(allSlibings,element);
		return siblingsBefore != null ? siblingsBefore.get(siblingsBefore.size() - 1) : null;
	}
}
