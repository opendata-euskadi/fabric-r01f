package r01f.persistence.search;

import javax.inject.Provider;

import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;

public interface SearcherProvider<F extends SearchFilter,I extends SearchResultItem> 
		 extends Provider<Searcher<F,I>> {
	// a provider
}
