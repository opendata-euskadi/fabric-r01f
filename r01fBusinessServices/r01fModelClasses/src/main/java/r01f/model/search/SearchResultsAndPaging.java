package r01f.model.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.pager.Paging;

/**
 * Wraps a search session results {@link SearchResults} alongside the {@link Paging}
 * @param <F>
 * @param <I>
 */
@MarshallType(as="searchResultsAndPaging")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class SearchResultsAndPaging<F extends SearchFilter,
						   		    I extends SearchResultItem> 
  implements SearchModelObject {
	private static final long serialVersionUID = 6118277331162729579L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Search Results
	 */
	@MarshallField(as="results")
	@Getter @Setter private SearchResults<F,I> _results;
	/**
	 * Paging
	 */
	@MarshallField(as="paging")
	@Getter @Setter private Paging _paging;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <F extends SearchFilter,
				   I extends SearchResultItem> SearchResultsAndPaging<F,I> create(final SearchResults<F,I> results,final Paging pag) {
		return new SearchResultsAndPaging<F,I>(results,pag);
	}
}
