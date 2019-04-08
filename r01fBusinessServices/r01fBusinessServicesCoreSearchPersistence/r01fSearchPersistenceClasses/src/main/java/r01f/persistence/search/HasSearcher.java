package r01f.persistence.search;

import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;

/**
 * Interface to be implemented by types that holds a {@link Searcher}
 */
public interface HasSearcher<F extends SearchFilter,
		    				 I extends SearchResultItem> {
	/**
	 * @return the repository {@link Searcher}
	 */
	public Searcher<F,I> getSearcher();
}
