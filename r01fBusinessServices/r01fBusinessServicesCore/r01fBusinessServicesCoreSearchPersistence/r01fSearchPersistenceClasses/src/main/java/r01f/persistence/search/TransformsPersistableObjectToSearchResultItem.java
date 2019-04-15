package r01f.persistence.search;

import r01f.locale.Language;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableObject;
import r01f.model.search.SearchResultItem;
import r01f.securitycontext.SecurityContext;

/**
 * Interface for types that transforms a {@link PersistableObject} into a {@link SearchResultItem}
 * @param <DB>
 * @param <M>
 */
public interface TransformsPersistableObjectToSearchResultItem<M extends IndexableModelObject,
												 	  		   I extends SearchResultItem> {
	/**
	 * Builds a {@link SearchResultItem} from this {@link PersistableObject} data
	 * @param securityContext
	 * @param modelObj
	 * @param lang
	 * @return a search result item
	 */
	public abstract I objToSearchResultItem(final SecurityContext securityContext,
										    final M modelObj,
										    final Language lang);
}
