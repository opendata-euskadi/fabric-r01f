package r01f.persistence.search.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.locale.Language;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.patterns.Factory;
import r01f.persistence.db.DBEntity;
import r01f.persistence.search.Searcher;
import r01f.securitycontext.SecurityContext;

/**
 * Base type for types that implements db searching ({@link Searcher} interface)
 * @param <F>
 * @param <I>
 * @param <DB>
 */
@Slf4j
@Accessors(prefix="_")
public abstract class DBSearcherBase<F extends SearchFilter,I extends SearchResultItem,
									 DB extends DBEntity> 
           implements Searcher<F,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The entity manager MUST be provided by the higher level layer because there is where the 
	 * transaction begins and that transaction could span more than one persistence types 
	 * (ie the CRUD persistence and the relations persistence)
	 */
	@Getter(AccessLevel.PROTECTED) protected final EntityManager _entityManager;	
	/**
	 * Creates a {@link DBSearchQuery} 
	 */
	@Getter(AccessLevel.PROTECTED) protected final Factory<? extends DBSearchQuery<F,DB>> _searchQueryFactory;
	/**
	 * Transforms a db entity to a search result item
	 */
	@Getter(AccessLevel.PROTECTED) protected final TransformsDBEntityToSearchResultItem<DB,I> _transformsDBEntityToSearchResultItem;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected <S extends DBSearchQuery<F,DB>> DBSearcherBase(final EntityManager entityManager,
														   	 final Factory<S> searchQueryFactory,
														   	 final TransformsDBEntityToSearchResultItem<DB,I> transformsDBEntityToSearchResultItem) {
		_entityManager = entityManager;
		_searchQueryFactory = searchQueryFactory;
		_transformsDBEntityToSearchResultItem = transformsDBEntityToSearchResultItem;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public int countRecords(final SecurityContext securityContext,
							final F filter) {
		log.info("Filtering: {}",filter.getBooleanQuery().encodeAsString());
		
		// [0] - Get the entity manager
		// TODO needs some research... really must have to call clear??
		_entityManager.clear();	// see CorePersistenceServiceBase and http://stackoverflow.com/questions/9146239/auto-cleared-sessions-with-guice-persist
								// BEWARE that the EntityManagerProvider reuses EntityManager instances and those instances
								// could have cached entity instances... discard them all
		
		// [1] - Get the total number of results
		TypedQuery<Long> countQry = _searchQueryFactory.create()
													   .getCountQuery(filter);
		int totalItems = countQry.getSingleResult().intValue();
		log.debug("JPA COUNT Query: {}",countQry.toString());
		log.info("Total number of results: {}",totalItems);
		
		return totalItems;
	}
	@Override
	public SearchResults<F,I> filterRecords(final SecurityContext securityContext,
								 			final F filter,final Collection<SearchResultsOrdering> ordering,
								 			final int firstRowNum,final int numberOfRows) {
		log.info("Filtering: {}",filter.getBooleanQuery().encodeAsString());
		
		// [0] - Get the entity manager
		// TODO needs some research... really must have to call clear??
		_entityManager.clear();	// see CorePersistenceServiceBase and http://stackoverflow.com/questions/9146239/auto-cleared-sessions-with-guice-persist
								// BEWARE that the EntityManagerProvider reuses EntityManager instances and those instances
								// could have cached entity instances... discard them all
		
		// [1] - Get the total number of results
		TypedQuery<Long> countQry = _searchQueryFactory.create()
													   .getCountQuery(filter);
		int totalItems = countQry.getSingleResult().intValue();
		log.debug("JPA COUNT Query: {}",countQry.toString());
		log.debug("Total number of results: {}",totalItems);
		
		// [2] - Get the page results
		Collection<DB> pageEntities = null;
		if (totalItems > 0) {
			TypedQuery<DB> qry = _searchQueryFactory.create()
													.getResultsQuery(filter,
																	 ordering);
			qry.setFirstResult(firstRowNum);
			qry.setMaxResults(numberOfRows);
			pageEntities = qry.getResultList();
			log.debug("JPA RETRIEVE Query: {}",qry.toString());
			log.debug("{} Page results from {} to {}: {}",
					  numberOfRows,firstRowNum,(firstRowNum + numberOfRows),pageEntities.size());	
			if (totalItems > 0 && pageEntities.size() == 0) throw new IllegalStateException(Throwables.message("DBSearcher error: the count query returned {} items BUT the search query returned 0",totalItems));
		} else {
			pageEntities = Lists.newArrayList();
		}
		
		// [3] - Compose the search results
		final Language uiLang = filter.getUILanguage();
		SearchResults<F,I> outResults = new SearchResults<F,I>(filter,
					  										   totalItems,firstRowNum,
					  										   numberOfRows,
					  										   pageEntities, 
					  										   new Function<DB,I>() {
																		@Override
																		public I apply(final DB dbEntity) {
																			return _transformsDBEntityToSearchResultItem.dbEntityToSearchResultItem(securityContext,
																																					dbEntity,
																																					uiLang);
																		}
															   });
		return outResults;	
	}
}
