package r01f.facets;

import java.util.Collection;





/**
 * Taggeable objects interface
 * @param <T> tag type: it can be a simple String or any other complex type
 */
public interface Taggeable<T> {
	/**
	 * Returns true if the tag list contains the provided tag
	 * @param tag
	 * @return  
	 */
	public boolean containsTag(final T tag);
	/**
	 * Returns true if the tag list contains ALL provided tags
	 * @param tags
	 * @return 
	 */
	public boolean containsAllTags(@SuppressWarnings("unchecked") final T... tags);
	/**
	 * Returns true if the tag list contains ALL provided tags
	 * @param tags
	 * @return 
	 */
	public boolean containsAllTags(final Collection<T> tags);
	/**
	 * Adds a tag to the tag list
	 * @param tag
	 * @return true if the tag was added to the list
	 */
	public boolean addTag(final T tag);
	/**
	 * Adds a list of tags
	 * @param tags 
	 * @return true if all the tags were added to the list
	 */
	public boolean addTags(final Collection<T> tags);
	/**
	 * Adds a list of tags
	 * @param tags 
	 * @return true if all the tags were added to the list
	 */
	public boolean addTags(final T... tags);
	/**
	 * Removes a tag from the tag list
	 * @param tag 
	 * @return true if the tag was removed from the list
	 */
	public boolean removeTag(final T tag);
	/**
	 * Removes all the tags
	 */
	public void clearTags();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns a string created by joining each tag separated by the given char
	 * @param sep
	 * @return
	 */
	public String asStringSeparatedWith(char sep);
	/**
	 * Returns a string created by joining each tag quoted by the given start & end chars 
	 * and separated by the also given char
	 * Example:
	 * 		Given the tagList = A,B,C,D
	 * 		calling asStringQuotedAndSeparatedWith('[',']','/') will result into [A]/[B]/[C]
	 * @param startQuote
	 * @param endQuote
	 * @param sep
	 * @return
	 */
	public String asStringQuotedAndSeparatedWith(final char startQuote,final char endQuote,
												 final char sep);
}
