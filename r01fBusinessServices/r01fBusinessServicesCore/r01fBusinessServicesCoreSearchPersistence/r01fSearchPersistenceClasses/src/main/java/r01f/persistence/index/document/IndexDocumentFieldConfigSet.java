	package r01f.persistence.index.document;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.model.IndexableModelObject;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.TypeMetaData;
import r01f.persistence.index.document.IndexDocumentFieldConfigFactories.IndexDocumentFieldConfigFactory;
import r01f.persistence.index.document.IndexDocumentFieldConfigFactories.IndexDocumentFieldConfigFactoryMatchingFieldNameByEquality;
import r01f.persistence.index.document.IndexDocumentFieldConfigFactories.IndexDocumentFieldConfigFactoryMatchingFieldNameByPattern;
import r01f.util.types.collections.CollectionUtils;


/**
 * Holds the field's config for document's indexed fields 
 */
@Slf4j
@Accessors(prefix="_")
public class IndexDocumentFieldConfigSet<M extends IndexableModelObject> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The type of the model object to be indexed
	 */
	@Getter protected final Class<M> _modelObjType;
	/**
	 * MetaData about the model object
	 */
	@Getter protected final transient TypeMetaData<M> _modelObjectTypeMetaData;
	/**
	 * Cache of {@link IndexDocumentFieldConfig} by it's id
	 * The {@link IndexDocumentFieldConfig} objects are created using the {@link IndexDocumentFieldConfigFactory}ies
	 */
	@Getter protected final IndexDocumentFieldConfigById<M> _fieldConfigById;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public IndexDocumentFieldConfigSet(final Class<M> modelObjType,final TypeMetaData<M> modelObjTypeMetaData) {
		this(modelObjType,modelObjTypeMetaData,
			 null);			// index document's fields use default config (are NOT customized)
	}
	public IndexDocumentFieldConfigSet(final Class<M> modelObjType,final TypeMetaData<M> modelObjTypeMetaData,
									   final IndexDocumentFieldConfigCustomizer indexDocFieldCustomizer) {
		// ** do not move **
		_modelObjType = modelObjType;
		_modelObjectTypeMetaData = modelObjTypeMetaData;
		if (_modelObjectTypeMetaData == null) throw new IllegalArgumentException(String.format("Model object metadata cannot be null: check that model object type '%s' has a reference to it's meta-data",
															 								   modelObjType));
		// ** do not move **
		
		// [1] - Register index document factories for every model object field
		// 		 (the ones defined at model object's metadata)
		// 		 Field factories creates a config object for each index document's field
		List<IndexDocumentFieldConfigFactory> fieldFactories = null;
		fieldFactories = CollectionUtils.hasData(_modelObjectTypeMetaData.getFieldsMetaDataMap())
								? FluentIterable.from(_modelObjectTypeMetaData.getFieldsMetaDataMap()
																			  .values())
										// filter fields with no indexing config
										.filter(new Predicate<FieldMetaData>() {
														@Override
														public boolean apply(final FieldMetaData fieldMetaData) {
															if (fieldMetaData.getSearchEngineIndexingConfig() == null) {
																log.error("The metadata config for {} does NOT have search engine indexing config! This metadata will NOT be indexed",
																		  fieldMetaData.getFieldId());
																return false;
															}
															return true;
														}
												})
										// transform to IndexDocumentFieldConfigFactory
										.transform(new Function<FieldMetaData,IndexDocumentFieldConfigFactory>() {
															@Override
															public IndexDocumentFieldConfigFactory apply(final FieldMetaData fieldMetaData) {
																log.debug("Registering index document's fieldFactory for metaData {} as {}",
																		  fieldMetaData.getFieldId(),fieldMetaData.getIndexableFieldId());
																return _fieldFactoryFor(fieldMetaData,
																						indexDocFieldCustomizer);
															}
											
												   })
										.toList()
								: null;
		// [2] - Create a cache of IndexDocumentFieldConfig objects created using the IndexDocumentFieldConfigFactories
		// 		 as needed
		_fieldConfigById = fieldFactories != null
								? new IndexDocumentFieldConfigById<M>(fieldFactories)
								: null;
	}
	/**
	 * Creates a field factory for a given field
	 * @param fieldMetaData
	 * @param indexDocFieldCustomizer sustomizer that gives chance to an app to customize the {@link IndexDocumentFieldConfig} generated from a {@link FieldMetaData} 
	 * @return
	 */
	private static IndexDocumentFieldConfigFactory _fieldFactoryFor(final FieldMetaData fieldMetaData,
																	final IndexDocumentFieldConfigCustomizer indexDocFieldCustomizer) {
		// NOT multi-dimensional fields
		// they're stored as indexableFieldId = value
		if (!fieldMetaData.hasMultipleDimensions()) {
			return new IndexDocumentFieldConfigFactoryMatchingFieldNameByEquality(fieldMetaData.getIndexableFieldId()) {
							@Override
							public IndexDocumentFieldConfig<?> createFieldConfigFor(final IndexDocumentFieldID fieldId) {
								IndexDocumentFieldConfig<?> outIdxDocFieldCfg = IndexDocumentFieldConfig.createStandardFor(fieldId,
																				  										   fieldMetaData);
								if (indexDocFieldCustomizer != null) indexDocFieldCustomizer.customize(outIdxDocFieldCfg);
								return outIdxDocFieldCfg;
							}
				   };
		}
		// multi-dimensional fields (ie language texts)
		// these fields are stored as: indexableFieldId.[dimension]  = value
		// for example, if the language is the dimension the indexed fields will be named as:
		//			myField.es = value_es
		//			myField.en = value_en
		//			...
		else {
			return new IndexDocumentFieldConfigFactoryMatchingFieldNameByPattern(IndexDocumentFieldID.dynamicDimensionDependantFieldNamePattern(fieldMetaData.getIndexableFieldId())) {
							@Override
							public IndexDocumentFieldConfig<?> createFieldConfigFor(final IndexDocumentFieldID fieldId) {
								IndexDocumentFieldConfig<?> outIdxDocFieldCfg = IndexDocumentFieldConfig.createStandardFor(fieldId,
																				  										   fieldMetaData);
								if (indexDocFieldCustomizer != null) indexDocFieldCustomizer.customize(outIdxDocFieldCfg);
								return outIdxDocFieldCfg;
							}
				   };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONFIG GET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param id
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigOrThrowFor(final IndexDocumentFieldID fieldId) {
		return _fieldConfigById.getConfigFor(fieldId);
	}
	/**
	 * Returns the index document's field config for the field which id is provided
	 * @param id
	 * @return
	 */
	public IndexDocumentFieldConfig<?> getConfigOrNullFor(final IndexDocumentFieldID fieldId) {
		return _fieldConfigById.getConfigOrNullFor(fieldId);
	}
}
