package r01f.services.delegates.persistence.search;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.eventbus.EventBus;

import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.facets.HasOID;
import r01f.guids.PersistableObjectOID;
import r01f.model.search.SearchFilterForModelObject;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.model.search.SearchResults;
import r01f.model.search.SearchResultsProvider;
import r01f.persistence.search.SearchResultsLoader;
import r01f.persistence.search.Searcher;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.SearchServicesForModelObject;
import r01f.util.types.collections.CollectionUtils;

public abstract class SearchServicesForModelObjectDelegateBase<F extends SearchFilterForModelObject,I extends SearchResultItemForModelObject<?>> 
			  extends SearchServicesDelegateBase<F,I> 
		   implements SearchServicesForModelObject<F,I> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SearchServicesForModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
									  				final Searcher<F,I> searcher,
									  				final EventBus eventBus) {
		super(coreCfg,
			  searcher,
			  eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public <O extends PersistableObjectOID> Collection<O> filterRecordsOids(final SecurityContext securityContext, 
														   					final F filter) {
		// BEWARE!!!!
		//		This default impl is NOT OPTIMIZED since it just filter all records 
		//		and returns the oids
		
		// Validate the filer
		_validateSearchFilter(securityContext,
							  filter);
		
		// Collect all results
	 	SearchResultsProvider<F,I> resultsProvider = new SearchResultsProvider<F,I>(filter,10) {
										 						@Override
										 						public SearchResults<F,I> provide(final int startPosition) {
										 							// Retrieve the page results
										 							return SearchServicesForModelObjectDelegateBase.super.filterRecords(securityContext,
										 										  			   											filter,null,	// no ordering
										 										  			   											startPosition,SearchResults.defaultPageSize());
										 						}
										 			 };
	    SearchResultsLoader<F,I> loader = SearchResultsLoader.create(resultsProvider);
	    Collection<I> allResults = loader.collectAll();
		
		// return 
		Collection<O> outRecords = null;
		if (CollectionUtils.hasData(allResults)) {
			outRecords = Collections2.transform(allResults,
												new Function<I,O>() {														
														@Override @SuppressWarnings("unchecked")
														public O apply(final I item) {
															if (!(item instanceof HasOID)) throw new IllegalStateException(item.getClass() + " does NOT have an OID!");
															HasOID<O> hasOid = (HasOID<O>)item;
															return hasOid.getOid();
														}
												});
		}
		return outRecords;
	}
}
