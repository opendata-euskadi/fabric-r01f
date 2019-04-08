package r01f.model.search;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Provider of {@link SearchResults} for a page
 * <pre class='brush:java'>
 *	// Provider of search results pages (quite verbose for java6 WTF!)
 *	SearchResultsProvider<MyFilter,MySearchResultsItem> resultsProvider = 
 *			new SearchResultsProvider<MyFilter,MySearchResultsItem>(filter) {
 *						@Override
 *						public SearchResults<MyFilter,MySearchResultsItem> provide(final int startPosition) {
 *							// ... use some api to retrieve the results
 *							return searchResults;
 *						}
 *			};
 * </pre>
 * @param <F>
 * @param <I>
 */
@GwtIncompatible
@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class SearchResultsProvider<F extends SearchFilter,
								   		    I extends SearchResultItem> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The {@link SearchFilter}
	 */
	@Getter private final F _searchFilter;
	@Getter private final int _pageSize;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides 
	 * @param startPosition
	 * @param numberOfRows
	 * @return
	 */
	public abstract SearchResults<F,I> provide(final int startPosition);
}
