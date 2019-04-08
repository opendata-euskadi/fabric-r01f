package r01f.services.interfaces;

import java.util.Collection;

import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.securitycontext.SecurityContext;

public interface SearchServices<F extends SearchFilter,I extends SearchResultItem> 
		 extends ServiceInterface {
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the total number of results that verifies the filter
     * @param securityContext
     * @param the filter
     * @return the total number of results
     */
	public int countRecords(final SecurityContext securityContext,
							final F filter);
	/**
	 * Searches records using a provided filter
	 * @param securityContext the user auth data & context info
	 * @param filter the filter
	 * @param ordering the order
	 * @param firstRowNum order number of the first row to be returned
	 * @param numberOfRows number of rows to be returned
	 * @return the result items
	 */
	public SearchResults<F,I> filterRecords(final SecurityContext securityContext,
									        final F filter,final Collection<SearchResultsOrdering> ordering,
									     	final int firstRowNum,final int numberOfRows);
}
