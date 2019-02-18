package r01f.facets;

import r01f.types.tag.TagList;


/**
 * Interface for model objects that can be tagged 
 * Example: 
 * [1] - Type implementing HasTaggeableFacet:
 * <pre class='brush:java'>
 * 		public class MyTaggeableType
 * 		  implements HasTaggeableFacet<String> {
 * 			@Override
 *			public Tagged<String> asTaggeable() {
 *				// returns a type implementing Tagged<String>
 *				return new TaggeableDelegate<String,MyTaggeableType>(this);
 *			}
 *		}
 * </pre>
 */
public interface Tagged<T extends Comparable<T>> 
		 extends Taggeable<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasTaggeableFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasTaggeableFacet<T extends Comparable<T>> 
					extends Facet {
		public Tagged<T> asTaggeable();
		
		public TagList<T> getTags();
		public void setTags(TagList<T> tags);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the list of tags
	 */
	public TagList<T> getTags();
}
