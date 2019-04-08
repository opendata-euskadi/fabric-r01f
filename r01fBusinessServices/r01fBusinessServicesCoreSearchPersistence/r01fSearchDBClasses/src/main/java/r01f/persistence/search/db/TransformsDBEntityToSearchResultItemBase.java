package r01f.persistence.search.db;

import com.google.common.base.Function;

import r01f.locale.Language;
import r01f.model.search.SearchResultItem;
import r01f.persistence.db.DBEntity;
import r01f.securitycontext.SecurityContext;

/**
 * Base impl of {@link TransformsDBEntityToSearchResultItem}
 * @param <DB>
 * @param <I>
 */
public abstract class TransformsDBEntityToSearchResultItemBase<DB extends DBEntity,I extends SearchResultItem> 
		   implements TransformsDBEntityToSearchResultItem<DB,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns this transformer as a {@link Function}
	 * @param securityContext
	 * @return
	 */
	public Function<DB,I> asTransformFuncion(final SecurityContext securityContext,
											 final Language lang) {
		return new Function<DB,I>() {			
						@Override
						public I apply(final DB dbEntity) {
							return TransformsDBEntityToSearchResultItemBase.this.dbEntityToSearchResultItem(securityContext,
																		  									dbEntity,
																		  									lang);
						}
			   };
	}
}
