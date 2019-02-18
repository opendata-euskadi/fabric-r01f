package r01f.types.hierarchy;

import java.util.Collection;


public interface HasChildren<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a child 
	 * @param child
	 * @return the added child object
	 */
	public T addChild(final T item);
	/**
	 * Adds a cuple of childs
	 * @param items
	 */
	public void addChildren(final Collection<T> items);
	/**
	 * Inserts a child at the specified index 
	 * @param index the index where the child will be inserted
	 * @param child 
	 * @return the added child
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public T insertChildAt(final T child,
						   final int index);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Removes a child 
	 * @param child
	 */
	public void removeChild(final T child);
	/**
	 * Removes a child located at the specified index
	 * @param item
	 */
	public void removeChildAt(final int index);
	/**
	 * Removes all children
	 */
	public void removeAllChilds();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are child
	 */
	public boolean hasChildren();
	/**
	 * Gets the child at the specified index.
	 * @param index the index to be retrieved
	 * @return the child at that index
	 */
	public T getChildAt(int index);
	/**
	 * Gets the number of children contained.
	 * @return child count.
	 */
	public int getChildCount();
	/**
	 * Gets the index of the specified child.
	 * @param child the child to be found
	 * @return the child's index, or <code>-1</code> if none is found
	 */
	public int getChildIndex(final T child);
	/**
	 * @return the child items collection
	 */
	public Collection<T> getChildren();
}
