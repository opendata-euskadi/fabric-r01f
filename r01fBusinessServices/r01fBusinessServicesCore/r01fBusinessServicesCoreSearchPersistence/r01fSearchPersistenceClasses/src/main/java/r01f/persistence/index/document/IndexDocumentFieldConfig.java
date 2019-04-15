package r01f.persistence.index.document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.annotations.OidField;
import r01f.model.metadata.FieldMetaData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * The config of an index document's field 
 * @see IndexDocumentConfig
 * 
 * There are two "flavors" of an index document's fields:
 * <ul>
 * 		<li>Standard fields (see {@link IndexDocumentStandardFieldType}): normal text, numeric, enum, etc fields</li>
 * 		<li>Value fields (see {@link IndexDocumentValueFieldType}): numeric field used in custom ordering / boosting</li>
 * </ul>
 * An index document's field can be built like:
 * <pre class='brush:java'>
 * 		// A standard field
 *	    IndexDocumentConfig<IndexDocumentStandardFieldType> outFieldCfg = LuceneFieldConfig.createStandard("standardFieldType")
 *	    																			  	   .ofKind(IndexDocumentStandardFieldType.String)
 *	    																			  	   .overridenWith(IndexDocumentFieldTypeOptionsOverride.create()
 *	    																					  									   		   	   .indexed());
 * 
 * 		// A document value field (to be used for custom ordering / boosting)
 *	    IndexDocumentConfig<IndexDocumentStandardFieldType> outFieldCfg = IndexDocumentConfig.createDocValue("docValueField")
 *	    																		  	  		 .ofKind(IndexDocumentStandardFieldType.Double);
 * </pre>
 * @param <FIELD_TYPE>
 */
@MarshallType(as="fieldType")
@Accessors(prefix="_")
@Slf4j
public class IndexDocumentFieldConfig<FIELD_TYPE extends IndexDocumentFieldType> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Field Name
	 */
	@MarshallField(as="name",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@OidField
	@Getter @Setter private IndexDocumentFieldID _id;
	/**
	 * Field Type
	 * <ul>
	 * 		<li>{@link IndexDocumentValueFieldType} if the field ONLY stores a SINGLE term per document to be used to sort, boost, filter, etc</li>
	 * 		<li>{@link IndexDocumentStandardFieldType} it the field is a "normal" index field</li>
	 * </ul>
	 */
	@MarshallField(as="type")
	@Getter @Setter private FIELD_TYPE _type; 
	/**
	 * If the field's value is true the value is stored "as-is"
	 * This field's value could be used to be shown in the search results
	 */
	@MarshallField(as="store",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _stored;
	/**
	 * If the field's value is depends on some dimension (ie language), in which case, the field name will be _name.{lang}
	 */
	@MarshallField(as="multiDimensional",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _multiDimensional;
	/**
	 * If the field's value depends on the language (it's multidimensional by the language)
	 */
	@MarshallField(as="languageDependent",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _languageDependent;
	/**
	 * Custom index options
	 */
	@MarshallField(as="indexOptions")
	@Getter @Setter private IndexDocumentFieldIndexOptions _indexOptions;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public IndexDocumentFieldConfig() {
		
	}
	public IndexDocumentFieldConfig(final IndexDocumentFieldID id) {
		_id = id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static IndexDocumentFieldConfig<IndexDocumentValueFieldType> createDocValue(final IndexDocumentFieldID id) {
		return new IndexDocumentFieldConfig<IndexDocumentValueFieldType>(id);
	}
	public static IndexDocumentFieldConfig<IndexDocumentStandardFieldType> createStandard(final IndexDocumentFieldID id) {
		return new IndexDocumentFieldConfig<IndexDocumentStandardFieldType>(id);
	}
	public static IndexDocumentFieldConfig<IndexDocumentStandardFieldType> createStandardFor(final IndexDocumentFieldID id,
																							 final FieldMetaData fieldMetaData) {
		// set the id and kind
		IndexDocumentFieldConfig<IndexDocumentStandardFieldType> outIndexFieldCfg = IndexDocumentFieldConfig.createStandard(id);
		outIndexFieldCfg.setType(IndexDocumentStandardFieldType.fromFieldMetaDataConfig(fieldMetaData));
		
		// is stored?
		boolean stored = fieldMetaData.getSearchEngineIndexingConfig().isStored();
		outIndexFieldCfg.setStored(stored);
		
		// is indexed?
		boolean indexed = fieldMetaData.getSearchEngineIndexingConfig().isIndexed();
		if (indexed) {
			IndexDocumentFieldIndexOptions indexOptions = new IndexDocumentFieldIndexOptions();
			if (fieldMetaData.getSearchEngineIndexingConfig().getBoost() != 0F) indexOptions.setBoost(fieldMetaData.getSearchEngineIndexingConfig().getBoost());
			indexOptions.setTokenized(fieldMetaData.getSearchEngineIndexingConfig().isTokenized());
			
			outIndexFieldCfg.setIndexedAs(indexOptions);
		}
		log.warn("MetaData {} will be {}/{} at a document field with name={}",
				 fieldMetaData.getFieldId(),
				 stored ? "stored" : "not stored",
				 indexed ? "indexed" : "not indexed",
				 id);
		
		// is multi-dimensional or language dependent
		outIndexFieldCfg.setLanguageDependent(fieldMetaData.isLanguageDependent());
		outIndexFieldCfg.setMultiDimensional(fieldMetaData.hasMultipleDimensions());
		
		return outIndexFieldCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the field is indexed
	 */
	public boolean isIndexed() {
		return _indexOptions != null;
	}
	public void setIndexedAs(final IndexDocumentFieldIndexOptions options) {
		if (this.getType().getClass() == IndexDocumentValueFieldType.class) throw new IllegalStateException("custom index store options cannot be specified for DocValueField");
		if (this.getIndexOptions() != null) {
			this.getIndexOptions()
				.merge(options);
		} else {
			this.setIndexOptions(options);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> ofKind(final FIELD_TYPE type) {
		_type = type;
		return new IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE>(this) {/* nothing */};
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static abstract class IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE extends IndexDocumentFieldType> {
		private final IndexDocumentFieldConfig<FIELD_TYPE> _fieldConfig;

		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> stored() {
			if (_fieldConfig.getType().getClass() == IndexDocumentValueFieldType.class) throw new IllegalStateException("DocValueField field types cannot store values"); 
			_fieldConfig.setStored(true);
			return this;
		}
		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> notStored() {
			_fieldConfig.setStored(false);
			return this;
		}
		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> notIndexed() {
			_fieldConfig.setIndexOptions(null);
			return this;
		}
		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> indexedAs(final IndexDocumentFieldIndexOptions options) {
			_fieldConfig.setIndexOptions(options);
			return this;
		}
		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> multiDimensional() {
			_fieldConfig.setMultiDimensional(true);
			return this;
		}
		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> notMultiDimensional() {
			_fieldConfig.setMultiDimensional(false);
			return this;
		}
		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> languageDependent() {
			_fieldConfig.setMultiDimensional(true);
			_fieldConfig.setLanguageDependent(true);
			return this;
		}
		public IndexDocumentFieldConfigBuilderStep1<FIELD_TYPE> languageIndependent() {
			_fieldConfig.setMultiDimensional(false);
			_fieldConfig.setLanguageDependent(false);
			return this;
		}
		public IndexDocumentFieldConfig<FIELD_TYPE> build() {
			return _fieldConfig;
		}
	}
}
