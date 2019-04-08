package r01f.services.client.api.delegates;

import java.util.Collection;

import javax.inject.Provider;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.search.SearchFilterForModelObject;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.model.search.SearchResults;
import r01f.model.search.SearchResultsProvider;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.search.SearchResultsLoader;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.SearchServicesForModelObject;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * Adapts Search API method invocations to the service proxy that performs the core method invocations
 * @param <F>
 * @param <I>
 */
@Accessors(prefix="_")
public abstract class ClientAPIDelegateForModelObjectSearchServices<F extends SearchFilterForModelObject,I extends SearchResultItemForModelObject<?>> 
	 		  extends ClientAPIServiceDelegateBase<SearchServicesForModelObject<F,I>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int SEARCH_RESULT_PAGE_SIZE = 10;		// TODO parameterize the search result page size
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Class<F> _filterType;
	@Getter private final Class<I> _resultItemType;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForModelObjectSearchServices(final Provider<SecurityContext> securityContextProvider,
														 final Marshaller modelObjectsMarshaller,
														 final SearchServicesForModelObject<F,I> services,
														 final Class<F> filterType,final Class<I> resultItemType) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
		_filterType = filterType;
		_resultItemType = resultItemType;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Searches returning only the first page results
	 * @param filter
	 * @param ordering
	 * @return
	 */
	public ClientAPIDelegateForModelObjectSearchServicesPageStep1 search(final F filter,final SearchResultsOrdering... ordering) {
		return new ClientAPIDelegateForModelObjectSearchServicesPageStep1(filter,
																		  CollectionUtils.hasData(ordering) ? Lists.newArrayList(ordering) : null);
	}
	/**
	 * Searches returning only the first page results
	 * @param filter
	 * @param ordering
	 * @return
	 */
	public ClientAPIDelegateForModelObjectSearchServicesPageStep1 search(final F filter,final Collection<SearchResultsOrdering> ordering) {
		return new ClientAPIDelegateForModelObjectSearchServicesPageStep1(filter,
																		  ordering);
	}
	/**
	 * Searches returning only the first page results
	 * @param filter
	 * @return
	 */
	public ClientAPIDelegateForModelObjectSearchServicesPageStep1 search(final F filter) {
		return new ClientAPIDelegateForModelObjectSearchServicesPageStep1(filter,
																		  null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ClientAPIDelegateForModelObjectSearchServicesPageStep1 {
		private final F _filter;
		private final Collection<SearchResultsOrdering> _ordering;
		
		public ClientAPIDelegateForModelObjectSearchServicesPageStep2 fromItemAt(final int firstItemNum) {
			return new ClientAPIDelegateForModelObjectSearchServicesPageStep2(_filter,_ordering,
																			  firstItemNum);
		}
		public SearchResults<F,I> firstPageOfSize(final int numberOfItems) {
			return ClientAPIDelegateForModelObjectSearchServices.this.getServiceProxy()
																	 .filterRecords(ClientAPIDelegateForModelObjectSearchServices.this.getSecurityContext(),
																			 		_filter,_ordering,
																			 		0,numberOfItems);
		}
		public SearchResults<F,I> firstPage() {
			return ClientAPIDelegateForModelObjectSearchServices.this.getServiceProxy()
																	 .filterRecords(ClientAPIDelegateForModelObjectSearchServices.this.getSecurityContext(),
																			 		_filter,_ordering,
																			 		0,SEARCH_RESULT_PAGE_SIZE);
		}
		public Collection<I> allItems() {
			SearchResultsLoader<F,I> loader = SearchResultsLoader.create(new SearchResultsProvider<F,I>(_filter,
																										SEARCH_RESULT_PAGE_SIZE) {
																				@Override
																				public SearchResults<F, I> provide(int startPosition) {
																					return ClientAPIDelegateForModelObjectSearchServicesPageStep1.this.fromItemAt(startPosition)
																																					  .returning(this.getPageSize());
																				}
																		 });
			return loader.collectAll();
		}
		public <T> Collection<T> allItemsTransformed(final Function<I,T> transformingFunction) {
			if (this.allItems() == null) return Lists.newArrayList();
			return FluentIterable.from(this.allItems())
								 .transform(transformingFunction)
								 .toList();
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ClientAPIDelegateForModelObjectSearchServicesPageStep2 {		
		private final F _filter;
		private final Collection<SearchResultsOrdering> _ordering;
		private final int _firstItemNum;
		
		public SearchResults<F,I> returning(final int numberOfItems) {
			return ClientAPIDelegateForModelObjectSearchServices.this.getServiceProxy()
																	 .filterRecords(ClientAPIDelegateForModelObjectSearchServices.this.getSecurityContext(),
																			 		_filter,_ordering,
																			 		_firstItemNum,numberOfItems);
		}
		public SearchResults<F,I> returningTheDefaultNumberOfItems() {
			return ClientAPIDelegateForModelObjectSearchServices.this.getServiceProxy()
																	 .filterRecords(ClientAPIDelegateForModelObjectSearchServices.this.getSecurityContext(),
																			 		_filter,_ordering,
																			 		_firstItemNum,SEARCH_RESULT_PAGE_SIZE);
		}
	}
}
