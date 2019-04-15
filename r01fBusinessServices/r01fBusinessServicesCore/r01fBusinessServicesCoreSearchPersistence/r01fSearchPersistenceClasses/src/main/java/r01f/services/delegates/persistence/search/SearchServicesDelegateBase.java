package r01f.services.delegates.persistence.search;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.persistence.search.Searcher;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.SearchServices;
import r01f.validation.ObjectValidationResult;

/**
 * Base for services of model objects with indexing and searching
 */
@Accessors(prefix="_")
public abstract class SearchServicesDelegateBase<F extends SearchFilter,I extends SearchResultItem>
		   implements SearchServices<F,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Config
	 */
	@Getter protected final ServicesCoreBootstrapConfigWhenBeanExposed _coreConfig;
	/**
	 * FullText Searcher implementation
	 */
	@Getter protected final Searcher<F,I> _searcher;
	/**
	 * {@link EventBus} used to span events to subscribed event handlers
	 */
	@Getter protected final EventBus _eventBus;
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SearchServicesDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
									  final Searcher<F,I> searcher,
									  final EventBus eventBus) {
		_coreConfig = coreCfg;
		_searcher = searcher;
		_eventBus = eventBus;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int countRecords(final SecurityContext securityContext,
							final F filter) {
		// Validate the filer
		_validateSearchFilter(securityContext,
							  filter);
		
		int outCount = _searcher.countRecords(securityContext,
    										  filter);
		return outCount;
	}
	@Override
	public SearchResults<F,I> filterRecords(final SecurityContext securityContext, 
							         	 	final F filter,final Collection<SearchResultsOrdering> ordering,
							         	 	final int firstRowNum,final int numberOfRows) {
		// Validate the filer
		_validateSearchFilter(securityContext,
							  filter);
		
		// beware of this!!!!
    	int effFirstRowNum = firstRowNum < 0 ? 0 : firstRowNum;
    	int effNumberOfRows = numberOfRows <= 0 ? SearchResults.defaultPageSize() 
    											: numberOfRows;
    	
    	// Filter
		int count = 0;
    	Collection<I> items = null;
		SearchResults<F,I> results = _searcher.filterRecords(securityContext,
															 filter,ordering,
														     effFirstRowNum,effNumberOfRows);
		count = results != null ? results.getTotalItemsCount()
								: 0;
		items = results != null ? results.getPageItems()
								: null;
    	SearchResults<F,I> outSearchResults = new SearchResults<F,I>(filter,
    										   	    				 count,effFirstRowNum,
    										   	    				 numberOfRows,
    										   	    				 items);
    	return outSearchResults;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected void _validateSearchFilter(final SecurityContext securityContext,
									     final F filter) {
		if (this instanceof ValidatesSearchFilter) {
			ObjectValidationResult<F> validationResult = ((ValidatesSearchFilter<F>)this).validateSearchFilter(securityContext,
																  											   filter);
			if (validationResult.isNOTValid()) throw new IllegalArgumentException("The provided search filter is NOT valid: " + validationResult.asNOKValidationResult().getReason());
		}
	}
}
