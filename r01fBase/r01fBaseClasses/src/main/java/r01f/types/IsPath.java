package r01f.types;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



/**
 * Interface for every path types
 */
public interface IsPath 
		 extends CanBeRepresentedAsString,
		 		 Serializable {
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link PathFactory} to create a path instance from it's elements
	 */
	public <P extends IsPath> PathFactory<P> getPathFactory();
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the path elements as a {@link LinkedList}
	 * @return
	 */
	public Collection<String> getPathElements();
	/**
	 * @return an {@link Iterator} over the path elements
	 */
	public Iterator<String> getPathElementsIterator();
	/**
	 * The number of path items
	 * @return
	 */
	public int getPathElementCount();
	/**
	 * @return true if the path has elements
	 */
	public boolean hasPathElements();
	/**
	 * @return the path as a relative String (does not start with /)
	 */
	public String asRelativeString();
	/**
	 * @return the path as an absolute String (starts with /)
	 */
	public String asAbsoluteString();
	/**
	 * Returns the path as a String prepending the parent path
	 * @param parentPath the parent path
	 * @return the path as a String
	 */
	public <P extends IsPath> String asRelativeStringFrom(final P parentPath);
	/**
	 * Returns the path as a String prepending the parent path
	 * @param parentPath the parent path
	 * @return the path as a String
	 */
	public <P extends IsPath> String asAbsoluteStringFrom(final P parentPath);
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	/**
	 * Removes an element from the tail
	 */
	public <P extends IsPath> P withoutLastPathElement();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the last path element
	 */
	public String getLastPathElement();
	/**
	 * @return the first path element
	 */
	public String getFirstPathElement();
	/**
	 * Return the path element at the provided position (zeroed-based)
	 * @param pos
	 * @return
	 */
	public String getPathElementAt(final int pos);
	/**
	 * Returns the N first path elements
	 * @param num the number of elements to return
	 * @return
	 */
	public List<String> getFirstNPathElements(final int num);
	/**
	 * Returns all the path elements except the last one
	 * @return
	 */
	public List<String> getPathElementsExceptLast();
	/**
	 * Returns the elements starting at provided position (zeroed-based) to the end of the list
	 * @param pos the start position (zeroed-based)
	 * @return
	 */
	public List<String> getPathElementsFrom(final int pos);
	/**
	 * Returns the remaining path fragment begining where the given
	 * starting path ends
	 * ie: if path=/a/b/c/d
	 *     and startingPath = /a/b
	 *     ... this function will return /c/d
	 * @param startingPath
	 * @return
	 */
	public <P extends PathBase<?>> List<String> getPathElementsAfter(final P startingPath);
	/**
	 * Search first position of element
	 * @param pathElement the searched path element
	 * @return the position (0-based), -1 if not contains the element
	 */
	public int getPathElementFirstPosition(final String pathElement);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if this path starts with the given path
	 * @param other
	 * @return
	 */
	public <P extends PathBase<?>> boolean startsWith(final P other);
	/**
	 * Checks if this path starts with the given path
	 * @param other
	 * @return
	 */
	public <P extends PathBase<?>> boolean endsWith(final P other);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if the path contains the provided element 
	 * @param pathEl
	 * @return
	 */
	public boolean containsPathElement(final String pathEl);
	/**
	 * Checks if the path contains the provided elements in the provided order
	 * @param pathEl
	 * @return
	 */
	public boolean containsAllPathElements(final String... pathElsToCheck);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Joins a variable length path elements to a given path
	 * @param elements the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	public <P extends IsPath> P joinedWith(final Object... elements);
	/**
	 * Prepends a variable length path elements to a given path
	 * @param elements the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	public <P extends IsPath> P prependedWith(final Object... elements);
}
