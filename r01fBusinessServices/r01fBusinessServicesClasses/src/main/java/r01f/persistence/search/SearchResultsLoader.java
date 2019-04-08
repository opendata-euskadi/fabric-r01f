package r01f.persistence.search;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResults;
import r01f.model.search.SearchResultsProvider;

/**
 * Utility type that pages across all search results pages to collect all the result items
 * Usage: 
 * <pre class='brush:java'>
 *	// Provider of search results pages (quite verbose for java6 WTF!)
 *	SearchResultsProvider<MyFilter,MySearchResultsItem> resultsProvider = 
 *			new SearchResultsProvider<MyFilter,MySearchResultsItem>(filter,10) {
 *						@Override
 *						public SearchResults<MyFilter,MySearchResultsItem> provide(final int startPosition) {
 *							// ... use some api to retrieve the results
 *							return searchResults;
 *						}
 *			};
 *	// Loader
 *	SearchResultsLoader<MyFilter,MySearchResultsItem> loader = SearchResultsLoader.create(resultsProvider);
 *	Collection<MySearchResultsItem> outItems = loader.collectAll();
 *
 *  // If the results need to be transformed:
 *	// Search result item to model object transformer
 *	Function<MySearchResultsItem,MyModelObject> transformer = 
 *			new Function<MySearchResultsItem,MyModelObject>() {
 *					@Override
 *					public MyModelObject apply(final MySearchResultsItem item) {
 *						// ... transform from MySearchResultsItem to MyModelObject
 *						return item;
 *					}
 *			};
 * Collection<MyModelObject> outResults = loader.collectAll(transformer);
 * </pre>
 * @param <F>
 * @param <I>
 */
@RequiredArgsConstructor
public class SearchResultsLoader<F extends SearchFilter,				// search result filter
						 		 I extends SearchResultItem> {	// search result item
	private final SearchResultsProvider<F,I> _provider;
	
	public static <F extends SearchFilter,
				   I extends SearchResultItem> SearchResultsLoader<F,I> create(final SearchResultsProvider<F,I> resultsProvider) {
		return new SearchResultsLoader<F,I>(resultsProvider);
	}
	
	/**
	 * @return all page results as T objects
	 */
	public Collection<I> collectAll() {
		SearchResults<F,I> searchResults = _provider.provide(0);	// first page
		Collection<I> outItems = null;
		if (searchResults != null && searchResults.getTotalItemsCount() > 0) {
			outItems = Lists.newArrayListWithExpectedSize(searchResults.getTotalItemsCount());
			
			// Iterate over the results page
			while (searchResults != null && searchResults.hasData()) {
				// Current page results
				Collection<I> pageResults = searchResults.getPageItems();
				outItems.addAll(pageResults);
				
				// Goto the next page (if it exists)
				if (searchResults.hasMorePages()) {
					searchResults =_provider.provide(searchResults.getNextPageStartPosition());	// next page
				} else {
					searchResults = null;
				}
			}
		}
		return outItems;
	}
	/**
	 * @return all page results as T objects
	 */
	public <T> Collection<T> collectAll(final Function<I,T> transformFunction) {
		SearchResults<F,I> searchResults = _provider.provide(0);	// first page
		Collection<T> outObjs = null;
		if (searchResults != null && searchResults.getTotalItemsCount() > 0) {
			outObjs = Lists.newArrayListWithExpectedSize(searchResults.getTotalItemsCount());
			
			// Iterate over the results page
			while (searchResults != null && searchResults.hasData()) {
				// Transform current page results
				Collection<I> pageResults = searchResults.getPageItems();
				for (I result : pageResults) {
					T obj = transformFunction.apply(result);
					outObjs.add(obj);
				}
				// Goto the next page (if it exists)
				if (searchResults.hasMorePages()) {
					searchResults =_provider.provide(searchResults.getNextPageStartPosition());	// next page
				} else {
					searchResults = null;
				}
			}
		}
		return outObjs;
	}
}