package r01f.persistence.search.db;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.search.SearcherProvider;

@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class DBSearcherProviderBase<F extends SearchFilter,I extends SearchResultItem>
		   implements SearcherProvider<F,I> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final DBModuleConfig _dbModuleConfig;
	protected final Provider<EntityManager> _entityManagerProvider;
	protected final Marshaller _marshaller;
}
