package r01f.persistence.search;

import r01f.guids.CommonOIDs.TenantID;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.securitycontext.SecurityContext;

/**
 * Interface to be implemented by types that holds a {@link SearcherProvider}
 */
public interface HasSearcherProvider<F extends SearchFilter,I extends SearchResultItem> {
	/**
	 * @return the repository {@link Searcher}
	 */
	public SearcherProvider<F,I> getSearcherProvider();
	/**
	 * Uses the {@link SearcherProvider} to get a fresh new {@link Searcher} instance
	 * @return
	 */
	public Searcher<F,I> getFreshNewSearcher();
	/**
	 * Uses the {@link SearcherProvider} to get a fresh new {@link Searcher} instance
	 * @param tenantId
	 * @return
	 */
	public Searcher<F,I> getFreshNewSearcher(final TenantID tenantId);
	/**
	 * Uses the {@link SearcherProvider} to get a fresh new {@link Searcher} instance
	 * @param securityContext
	 * @return
	 */
	public Searcher<F,I> getFreshNewSearcher(final SecurityContext securityContext);
}
