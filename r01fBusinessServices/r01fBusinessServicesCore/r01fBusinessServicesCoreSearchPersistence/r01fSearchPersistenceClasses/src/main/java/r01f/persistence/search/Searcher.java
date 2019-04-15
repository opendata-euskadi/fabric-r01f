package r01f.persistence.search;

import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.services.interfaces.SearchServices;

/**
 * Interface to be implemented by types in charge to execute searches
 * @param <F>
 * @param <I>
 */
public interface Searcher<F extends SearchFilter,I extends SearchResultItem>
		 extends SearchServices<F,I> {
	// just extend
}
