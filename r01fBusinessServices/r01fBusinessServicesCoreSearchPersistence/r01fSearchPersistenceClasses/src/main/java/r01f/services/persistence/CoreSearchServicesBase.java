package r01f.services.persistence;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.TenantID;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.search.HasSearcherProvider;
import r01f.persistence.search.Searcher;
import r01f.persistence.search.SearcherProvider;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.SearchServices;

/**
 * Core service base for search services
 */
@Accessors(prefix="_")
public abstract class CoreSearchServicesBase<F extends SearchFilter,I extends SearchResultItem> 
     		  extends CoreServicesBase					  
     	   implements SearchServices<F,I>,
  			 		  HasSearcherProvider<F,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//	 FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Searcher factory 
	 */
	@Getter protected final SearcherProvider<F,I> _searcherProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param coreCfg 
	 * @param modelObjectsMarshaller annotated with @ModelObjectsMarshaller
	 * @param eventBus
	 * @param searcher
	 * @param modelObjectsMarshaller annotate with @ModelObjectsMarshaller
	 */
	public CoreSearchServicesBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
								  final Marshaller modelObjectsMarshaller,
						   	   	  final EventBus eventBus,
								  final SearcherProvider<F,I> searcherProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus);
		_searcherProvider = searcherProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCHER PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Searcher<F,I> getFreshNewSearcher() {
		return this.getSearcherProvider().get();
	}
	@Override
	public Searcher<F,I> getFreshNewSearcher(final TenantID tenantId) {
		return this.getSearcherProvider().get();		
	}
	@Override 
	public Searcher<F,I> getFreshNewSearcher(final SecurityContext securityContext) {
		return this.getSearcherProvider().get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public int countRecords(final SecurityContext securityContext,
							final F filter) {
		return  this.forSecurityContext(securityContext)
						.createDelegateAs(SearchServices.class)
							.countRecords(securityContext,
										  filter);
	}
	@Override @SuppressWarnings("unchecked")
	public SearchResults<F,I> filterRecords(final SecurityContext securityContext,
	                    		 			final F filter,final Collection<SearchResultsOrdering> ordering,
	                    		 			final int firstRowNum,final int numberOfRows) {
		return  this.forSecurityContext(securityContext)
						.createDelegateAs(SearchServices.class)
							.filterRecords(securityContext, 
										   filter,ordering,
										   firstRowNum,numberOfRows);
	}
}
