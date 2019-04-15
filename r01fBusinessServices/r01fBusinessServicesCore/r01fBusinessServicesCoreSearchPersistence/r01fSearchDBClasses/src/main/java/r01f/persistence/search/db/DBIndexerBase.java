package r01f.persistence.search.db;

import javax.inject.Provider;

import lombok.experimental.Accessors;
import r01f.model.IndexableModelObject;
import r01f.model.metadata.TypeMetaData;
import r01f.persistence.index.IndexableFieldValuesExtractor;
import r01f.persistence.index.IndexerBase;

/**
 * Base type for DB indexers
 * @param <P>
 */
@Accessors(prefix="_")
public abstract class DBIndexerBase<P extends IndexableModelObject>
              extends IndexerBase<P> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public DBIndexerBase(final Class<P> modelObjType,final TypeMetaData<P> modelObjectTypeMetaData,
						 final Provider<IndexableFieldValuesExtractor<P>> indexableFieldsValuesExtractorProvider) {
		super(modelObjType,modelObjectTypeMetaData,
			  indexableFieldsValuesExtractorProvider);
	}
}
