package r01f.persistence.search.db;

import r01f.locale.Language;
import r01f.model.search.SearchResultItem;
import r01f.persistence.db.DBEntity;
import r01f.securitycontext.SecurityContext;

/**
 * Interface for types that transforms a {@link DBEntity} into a {@link SearchResultItem}
 * @param <DB>
 * @param <M>
 */
public interface TransformsDBEntityToSearchResultItem<DB extends DBEntity,
												 	  I extends SearchResultItem> {
	/**
	 * Builds a {@link SearchResultItem} from this {@link DBEntity} data
	 * @param securityContext
	 * @param dbEntity
	 * @param lang
	 * @return a search result item
	 */
	public abstract I dbEntityToSearchResultItem(final SecurityContext securityContext,
										    	 final DB dbEntity,
										    	 final Language lang);
}
