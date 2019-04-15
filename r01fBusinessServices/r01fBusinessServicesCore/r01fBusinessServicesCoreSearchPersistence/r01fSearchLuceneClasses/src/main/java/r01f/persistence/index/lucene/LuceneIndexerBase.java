package r01f.persistence.index.lucene;

import java.io.Reader;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.inject.Provider;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.enums.EnumWithCode;
import r01f.exceptions.Throwables;
import r01f.facets.HasOID;
import r01f.facets.util.Facetables;
import r01f.guids.OID;
import r01f.guids.OIDBase;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.IndexableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataForPersistableModelObjectBase;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.persistence.index.IndexableFieldValuesExtractor;
import r01f.persistence.index.IndexerBase;
import r01f.persistence.index.document.IndexDocumentFieldConfigSet;
import r01f.persistence.index.document.IndexDocumentFieldID;
import r01f.persistence.index.document.IndexDocumentFieldValue;
import r01f.persistence.index.document.IndexDocumentFieldValueSet;
import r01f.persistence.index.document.IndexDocumentStandardFieldType;
import r01f.persistence.lucene.LuceneIndex;
import r01f.securitycontext.SecurityContext;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.IsPath;
import r01f.types.Range;
import r01f.types.summary.LangDependentSummary;
import r01f.types.summary.LangIndependentSummary;
import r01f.types.summary.Summary;
import r01f.types.url.Url;
import r01f.util.types.Dates;
import r01f.util.types.StringConverter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;

/**
 * Base Lucene indexer that encapsulates the logic described at {@link LuceneDocumentFactoryForIndexableObject} to create a lucene's {@link Document}
 * from a model object
 * @param <P>
 */
@Slf4j
@Accessors(prefix="_")
public abstract class LuceneIndexerBase<P extends IndexableModelObject>
		      extends IndexerBase<P> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FINAL STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Fields config
	 */
	@Getter(AccessLevel.PROTECTED) private final IndexDocumentFieldConfigSet<P> _fieldsConfigSet;
	/**
	 * Lucene index to search against
	 */
	@Getter(AccessLevel.PROTECTED) private final LuceneIndex _luceneIndex;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 * @param modelObjectType
	 * @pram modelObjectTypeMetaData
	 * @param fieldsConfigSet
	 * @param luceneIndex
	 * @param indexableFieldValuesExtractorProvider the IndexableFieldValuesExtractor is NOT thread safe BUT this indexer is a singleton
	 * 											    ... so a new instance of the extractor MUST be created for every indexing operation
	 * 												... so a Provider is provided
	 */
	public LuceneIndexerBase(final Class<P> modelObjectType,final TypeMetaData<P> modelObjectTypeMetaData,
							 final IndexDocumentFieldConfigSet<P> fieldsConfigSet,
						 	 final LuceneIndex luceneIndex,
							 final Provider<IndexableFieldValuesExtractor<P>> indexableFieldValuesExtractorProvider) {	// IndexableFieldValuesExtractor are NOT thread safe!
		super(modelObjectType,modelObjectTypeMetaData,
			  indexableFieldValuesExtractorProvider);
		_fieldsConfigSet = fieldsConfigSet;
		_luceneIndex = luceneIndex;																						// indexes documents with fields
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INDEX METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void index(final SecurityContext securityContext,
					  final P modelObject) {
		// Get the document from the record
		Document doc = _createLuceneDocumentFor(securityContext,
												modelObject,
												PersistenceRequestedOperation.CREATE);	// new record
		// Index in Lucene
		_luceneIndex.index(doc);
	}
	@Override
	public void updateIndex(final SecurityContext securityContext,
							final P modelObject) {
		// Get the document from the record
		Document doc = _createLuceneDocumentFor(securityContext,
												modelObject,
												PersistenceRequestedOperation.UPDATE);	// update an existing record

		// update lucene index (it deletes the reg and inserts it again)
		FieldID docIdFieldId = FieldID.from(TypeMetaDataForPersistableModelObjectBase.SEARCHABLE_METADATA.DOCID);
		_luceneIndex.reIndex(new Term(docIdFieldId.asString(),
								  	  _luceneDOCIDFieldValueFrom(modelObject)),
							 doc);
	}
	@Override
	public void removeFromIndex(final SecurityContext securityContext,
								final OID oid) {
		if (this.getModelObjectMetaData().hasFacet(HasVersionableFacet.class)) {
			if (!(oid instanceof OIDForVersionableModelObject)) throw new UnsupportedOperationException(Throwables.message("The model object {} is a versionable object, BUT it's oid type does NOT implements {}",
																		   												   this.getModelObjectType(),OIDForVersionableModelObject.class));
			OIDForVersionableModelObject versionableOid = (OIDForVersionableModelObject)oid;
			_removeFromIndex(securityContext,
							 versionableOid.getOid(),versionableOid.getVersion());
		} else {
			_removeFromIndex(securityContext,
							 oid);
		}
	}
	@SuppressWarnings("unused")
	private void _removeFromIndex(final SecurityContext securityContext,
								  final OID oid) {
		FieldID docIdFieldId = FieldID.from(TypeMetaDataForPersistableModelObjectBase.SEARCHABLE_METADATA.DOCID);
		Term idTerm = new Term(docIdFieldId.asString(),
							   _idFor(oid));
		// UN-index
		_luceneIndex.unIndex(idTerm);
	}
	private void _removeFromIndex(final SecurityContext securityContext,
								  final VersionIndependentOID oid,final VersionOID version) {
		if (!this.getModelObjectMetaData().hasFacet(HasVersionableFacet.class)) throw new UnsupportedOperationException(Throwables.message("The model object {} is NOT a versionable object, so removeFromIndex(oid) MUST be used",
																																		  this.getModelObjectType()));
		FieldID docIdFieldId = FieldID.from(TypeMetaDataForPersistableModelObjectBase.SEARCHABLE_METADATA.DOCID);
		Term idTerm = new Term(docIdFieldId.asString(),
							   _idFor(oid,version));
		// UN-index
		_luceneIndex.unIndex(idTerm);
	}
	private Document _createLuceneDocumentFor(final SecurityContext securityContext,
											  final P modelObj,
											  final PersistenceRequestedOperation reqOp) {
		// [1] - Extract the field values from the model object
		IndexDocumentFieldValueSet fieldsValues = _extractIndexableFields(securityContext,
																	  	  modelObj,
																	  	  reqOp);
		// [2] - Add the id field (every document has an id field)
		FieldMetaData docIdField = this.getModelObjectMetaData()
											.findFieldByIdOrThrow(TypeMetaDataForPersistableModelObjectBase.SEARCHABLE_METADATA.DOCID)
												.asFieldMetaData();
		fieldsValues.add(IndexDocumentFieldValue.forMetaData(docIdField)
									  			.andValue(// anonymous oid type to avoid creating a new oid type just for this
									  					  new OIDBase<String>() {
									  							private static final long serialVersionUID = -5184629874103543060L;
																@Override
									  							public String getId() {
									  								return _luceneDOCIDFieldValueFrom(modelObj);
									  							}
									  					  }));

		// [3] - Create a fields factory that uses the fields config to create the lucene fields
		LuceneDocumentFactoryForIndexableObject docFactory = LuceneDocumentFactoryForIndexableObject.of(_fieldsConfigSet);	// creates fields using the fieldsConfigSet

		// [4] - Create the fields using the factory
		if (fieldsValues.hasData()) {
			log.debug("Creating a Lucene document for {} with fields: {}",
					  fieldsValues.getFieldIdsAsString(),_fieldsConfigSet.getClass().getSimpleName());

			for (IndexDocumentFieldValue<?> fieldValue : fieldsValues) {
				// If a field's creation fails... continue: create the document with the other fields
				try {
					log.debug("- Document field {}.{} of type {}",
							  _fieldsConfigSet.getClass().getSimpleName(),fieldValue.getIndexableFieldId(),
							  fieldValue.getValue() != null ? fieldValue.getValue().getClass() : "[NO VALUE]");
					_createField(docFactory,
								 fieldValue);
				} catch (Throwable th) {
					log.error("Error {} while creating the field {} > {} of type {}",
							  th.getMessage(),
							  _fieldsConfigSet.getClass().getSimpleName(),fieldValue.getIndexableFieldId(),
							  fieldValue.getValue() != null ? fieldValue.getValue().getClass() : "[NO VALUE]",
							  th);
				}
			}
		}
		// [5] - Create the Document using a factory that uses a
		//		 LuceneFieldConfig template
		return docFactory.createDocument();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ID FIELD VALUE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Every lucene document has an id field whose value is obtained in this method
	 * Usually, the id field value is the model object's oid field value, but this is
	 * NOT always the situation: for example when the model object is a versionable
	 * model object, the id field is the composition of the model object's oid and version
	 * @param modelObj
	 * @return
	 */
	private String _luceneDOCIDFieldValueFrom(final P modelObj) {
		OID oid = Facetables.asFacet(modelObj,HasOID.class)
							.getOid();

		String luceneIdStr = null;
		if (oid instanceof OIDForVersionableModelObject) {
			OIDForVersionableModelObject vOid = (OIDForVersionableModelObject)oid;
			VersionIndependentOID versionIndependentOid = vOid.getOid();
			VersionOID versionOid = vOid.getVersion();
			if (versionOid != null) {
				luceneIdStr = _idFor(versionIndependentOid,versionOid);
			} else {
				throw new IllegalStateException(Throwables.message("NO version info in an oid instance of type {}",oid.getClass()));
			}
		} else {
			luceneIdStr = _idFor(oid);
		}
		return luceneIdStr;
	}
	private static String _idFor(final OID oid) {
		return oid.asString();
	}
	private static String _idFor(final VersionIndependentOID oid,final VersionOID versionOid) {
		return Strings.customized("{}_{}",
					  			  oid,versionOid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELD CREATION
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private <T> void _createField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  final IndexDocumentFieldValue<T> indexableFieldValue) {
		FieldMetaData fieldMetaDataConfig = indexableFieldValue.getMetaDataConfig();
		FieldID fieldId = indexableFieldValue.getIndexableFieldId();
		T fieldValue = indexableFieldValue.getValue();

		if (fieldValue == null) return;	// do not waste time

		if (fieldValue instanceof OID) {
			log.debug("\t-OID field: {}={}",fieldId,fieldValue);
			_createOidField(fieldsFactory,
							(IndexDocumentFieldValue<OID>)indexableFieldValue);
		}
		else if (fieldValue instanceof Class) {
			log.debug("\t-java type field: {}={}",fieldId,fieldValue);
			_createJavaTypeField(fieldsFactory,
								 (IndexDocumentFieldValue<Class<?>>)indexableFieldValue);
		}
		else if (fieldValue instanceof String) {
			log.debug("\t-String field: {}={}",fieldId,fieldValue);
			// Sometimes a String field is indexed as FULL-TEXT...
			if (!indexableFieldValue.dependsOnDynamicDimension()
			 && _fieldsConfigSet.getConfigOrThrowFor(IndexDocumentFieldID.fieldIdOf(fieldId)).getType() == IndexDocumentStandardFieldType.Text) {
				// Full-text field
				_createReaderField(fieldsFactory,
								   IndexDocumentFieldID.fieldIdOf(fieldId),
								   StringConverter.asReader((String)fieldValue));
			} else {
				// Normal String field
				_createStringField(fieldsFactory,
								   (IndexDocumentFieldValue<String>)indexableFieldValue);
			}
		}
		else if (fieldValue instanceof Boolean) {
			log.debug("\t-boolean field: {}={}",fieldId,fieldValue);
			_createBooleanField(fieldsFactory,
								(IndexDocumentFieldValue<Boolean>)indexableFieldValue);
		}
		else if (fieldValue instanceof Integer) {
			log.debug("\t-int field: {}={}",fieldId,fieldValue);
			_createIntegerField(fieldsFactory,
								(IndexDocumentFieldValue<Integer>)indexableFieldValue);
		}
		else if (fieldValue instanceof Long) {
			log.debug("\t-long field: {}={}",fieldId,fieldValue);
			_createLongField(fieldsFactory,
							 (IndexDocumentFieldValue<Long>)indexableFieldValue);
		}
		else if (fieldValue instanceof Float) {
			log.debug("\t-float field: {}={}",fieldId,fieldValue);
			_createFloatField(fieldsFactory,
							  (IndexDocumentFieldValue<Float>)indexableFieldValue);
		}
		else if (fieldValue instanceof Double) {
			log.debug("\t-double field: {}={}",fieldId,fieldValue);
			_createDoubleField(fieldsFactory,
							  (IndexDocumentFieldValue<Double>)indexableFieldValue);
		}
		else if (fieldValue instanceof Date) {
			log.debug("\t-Date field: {}={}",fieldId,fieldValue);
			_createDateField(fieldsFactory,
							 (IndexDocumentFieldValue<Date>)indexableFieldValue);
		}
		else if (fieldValue instanceof Enum) {
			log.debug("\t-Enum field: {}={}",fieldId,((Enum<?>)fieldValue).name());
			_createEnumField(fieldsFactory,
							 (IndexDocumentFieldValue<Enum<? extends Enum<?>>>)indexableFieldValue);
		}
		else if (fieldValue instanceof Range) {
			log.debug("\t-Range field: {}={}",fieldId,((Range<? extends Comparable<?>>)fieldValue).asString());
			_createRangeField(fieldsFactory,
							 (IndexDocumentFieldValue<Range<? extends Comparable<?>>>)indexableFieldValue);
		}
		else if (fieldValue instanceof LanguageTexts) {
			log.debug("\t-lang texts field: {}",fieldId);
			_createLanguageTextsFields(fieldsFactory,
									   (IndexDocumentFieldValue<LanguageTexts>)indexableFieldValue);			
		}
		else if (fieldValue instanceof Summary) {
			log.debug("\t-summary field: {}",fieldId);
			_createSummaryField(fieldsFactory,
								(IndexDocumentFieldValue<Summary>)indexableFieldValue);
		}
		else if (fieldValue instanceof IsPath) {
			log.debug("\t-path field: {}",fieldId);
			_createPathField(fieldsFactory,
							 (IndexDocumentFieldValue<IsPath>)indexableFieldValue);
		}
		else if (fieldValue instanceof Url) {
			log.debug("\t-serializedURL: {}",fieldId);
			_createSerializedUrlField(fieldsFactory,
									  (IndexDocumentFieldValue<Url>)indexableFieldValue);
		}
		else if (fieldValue instanceof Collection) {
			log.debug("\t-Multi valued field: {}",fieldId);
			// Transform in multiple _createField(...) calls
			Collection<?> col = (Collection<?>)indexableFieldValue.getValue();
			for (Object colVal : col) {
				if (colVal == null) continue;
				// A recursive call with the same metadata id (multiple value)
				// BUT do not check the type when creating the field value since it's NOT a collection
				_createField(fieldsFactory,
							 IndexDocumentFieldValue.forMetaDataNotCheckingType(fieldMetaDataConfig)
							 						.andValue(colVal));		// recursive call
			}
		}
		else if (fieldValue instanceof CanBeRepresentedAsString) {		// DO NOT MOVE!!!!
			log.debug("\t-CanBeRepresentedAsString: {}",fieldId);
			_createCanBeRepresentedAsStringField(fieldsFactory,
												 (IndexDocumentFieldValue<CanBeRepresentedAsString>)indexableFieldValue);
		}
		else if (fieldValue instanceof Map) {
			log.debug("\t-Dimension dependent field: {}",fieldId);
			// Transform in multiple _createField(...) calls
			Map<?,?> fieldValues = (Map<?,?>)indexableFieldValue.getValue();
			for (Map.Entry<?,?> me : fieldValues.entrySet()) {
				IndexDocumentFieldID luceneFieldId = IndexDocumentFieldID.fieldIdOf(indexableFieldValue.getIndexableFieldId(),
																   					me.getKey());		// the point
				Object pointValue = me.getValue();
				_createField(fieldsFactory,
							 luceneFieldId,
							 pointValue);
			}
		}
		else {
			log.error("The field {} of type {} is NOT indexable in Lucene",
					  fieldId,fieldValue.getClass());
		}
	}
	@SuppressWarnings("unchecked")
	private static <T> void _createField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldID luceneFieldId,
								  		 final T value) {
		if (value instanceof OID) {
			_createOidField(fieldsFactory,luceneFieldId,
						    (OID)value);
		} else if (value instanceof Boolean) {
			_createBooleanField(fieldsFactory,luceneFieldId,
								(Boolean)value);
		} else if (value instanceof Integer) {
			_createIntegerField(fieldsFactory,luceneFieldId,
						    	(Integer)value);
		} else if (value instanceof Long) {
			_createLongField(fieldsFactory,luceneFieldId,
						    (Long)value);
		} else if (value instanceof Double) {
			_createDoubleField(fieldsFactory,luceneFieldId,
						       (Double)value);
		} else if (value instanceof Float) {
			_createFloatField(fieldsFactory,luceneFieldId,
						    (Float)value);
		} else if (value instanceof Date) {
			_createDateField(fieldsFactory,luceneFieldId,
						    (Date)value);
		} else if (value instanceof Enum) {
			_createEnumField(fieldsFactory,luceneFieldId,
						     (Enum<?>)value);
		} else if (value instanceof Range) {
			_createRangeField(fieldsFactory,luceneFieldId,
							  (Range<? extends Comparable<?>>)value);
		} else if (value instanceof Summary) {
			_createSummaryField(fieldsFactory,luceneFieldId,
								(Summary)value);
		} else if (value instanceof IsPath) {
			_createPathField(fieldsFactory,luceneFieldId,
							 (IsPath)value);
		} else if (value instanceof Url) {
			_createSerializedUrlField(fieldsFactory,luceneFieldId,
									  (Url)value);
		} else if (value instanceof CanBeRepresentedAsString) {		// DO NOT MOVE!!!
			_createCanBeRepresentedAsStringField(fieldsFactory,luceneFieldId,
												 (CanBeRepresentedAsString)value);
		} else if (value instanceof Collection) {
			// Transform in multiple _createField(...) calls
			Collection<?> col = (Collection<?>)value;
			for (final Object colVal : col) {
				_createField(fieldsFactory,
							 luceneFieldId,
							 colVal);		// recursive call
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OID
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createOidField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								 		final IndexDocumentFieldValue<OID> indexableField) {
		_process(indexableField,
				 new FieldFactory<OID>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final OID value) {
								_createOidField(fieldsFactory,
												luceneFieldId,
											   	indexableField.getValue());
							}
				 });
	}
	private static void _createOidField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								 		final IndexDocumentFieldID luceneFieldId,
								 		final OID oid) {
		fieldsFactory.createField(luceneFieldId)
			  		 .setStringValue(oid.asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  String
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createJavaTypeField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										   	 final IndexDocumentFieldValue<Class<?>> indexableField) {
		_process(indexableField,
				 new FieldFactory<Class<?>>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Class<?> value) {
								_createJavatypeField(fieldsFactory,
												  	 luceneFieldId,
												  	 indexableField.getValue());
							}
				 });
	}
	private static void _createJavatypeField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										     final IndexDocumentFieldID luceneFieldId,
										     final Class<?> value) {
		fieldsFactory.createField(luceneFieldId)
					 .setStringValue(value.getName());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  String
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createStringField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										   final IndexDocumentFieldValue<String> indexableField) {
		_process(indexableField,
				 new FieldFactory<String>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final String value) {
								_createStringField(fieldsFactory,
												   luceneFieldId,
											       indexableField.getValue());
							}
				 });
	}
	private static void _createStringField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										   final IndexDocumentFieldID luceneFieldId,
										   final String value) {
		fieldsFactory.createField(luceneFieldId)
					 .setStringValue(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CanBeRepresentedAsString
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createCanBeRepresentedAsStringField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 		  			 final IndexDocumentFieldValue<CanBeRepresentedAsString> indexableField) {
		_process(indexableField,
				 new FieldFactory<CanBeRepresentedAsString>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final CanBeRepresentedAsString value) {
								_createCanBeRepresentedAsStringField(fieldsFactory,
												 		  			 luceneFieldId,
												 		  			 indexableField.getValue());
							}
				 });
	}
	private static void _createCanBeRepresentedAsStringField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										 		  			 final IndexDocumentFieldID luceneFieldId,
										 		  			 final CanBeRepresentedAsString str) {
		fieldsFactory.createField(luceneFieldId)
					 .setStringValue(str.asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Reader
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private static void _createReaderField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										   final IndexDocumentFieldValue<Reader> indexableField) {
		_process(indexableField,
				 new FieldFactory<Reader>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Reader value) {
								_createReaderField(fieldsFactory,
												   luceneFieldId,
											       indexableField.getValue());
							}
				 });
	}
	private static void _createReaderField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										   final IndexDocumentFieldID luceneFieldId,
										   final Reader value) {
		fieldsFactory.createField(luceneFieldId)
					 .setReaderValue(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Numeric
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createBooleanField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
									 		final IndexDocumentFieldValue<Boolean> indexableField) {
		_process(indexableField,
				 new FieldFactory<Boolean>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Boolean value) {
								_createBooleanField(fieldsFactory,
													luceneFieldId,
											   		indexableField.getValue());
							}
				 });
	}
	private static void _createBooleanField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										    final IndexDocumentFieldID luceneFieldId,
										    final boolean bool) {
		int boolValue = bool ? 1 : 0;
		fieldsFactory.createField(luceneFieldId)
					 .setIntValue(boolValue);
	}
	private static void _createIntegerField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
									 		final IndexDocumentFieldValue<Integer> indexableField) {
		_process(indexableField,
				 new FieldFactory<Integer>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Integer value) {
								_createIntegerField(fieldsFactory,
													luceneFieldId,
											   		indexableField.getValue());
							}
				 });
	}
	private static void _createIntegerField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										    final IndexDocumentFieldID luceneFieldId,
										    final int number) {
		fieldsFactory.createField(luceneFieldId)
					 .setIntValue(number);
	}
	private static void _createLongField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldValue<Long> indexableField) {
		_process(indexableField,
				 new FieldFactory<Long>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Long value) {
								_createLongField(fieldsFactory,
												 luceneFieldId,
											   	 indexableField.getValue());
							}
				 });
	}
	private static void _createLongField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldID luceneFieldId,
								  		 final long number) {
		fieldsFactory.createField(luceneFieldId)
					 .setLongValue(number);
	}
	private static void _createDoubleField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		   final IndexDocumentFieldValue<Double> indexableField) {
		_process(indexableField,
				 new FieldFactory<Double>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Double value) {
								_createDoubleField(fieldsFactory,
												   luceneFieldId,
											   	   indexableField.getValue());
							}
				 });
	}
	private static void _createDoubleField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		   final IndexDocumentFieldID luceneFieldId,
								  		   final double number) {
		fieldsFactory.createField(luceneFieldId)
					 .setDoubleValue(number);
	}
	private static void _createFloatField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		  final IndexDocumentFieldValue<Float> indexableField) {
		_process(indexableField,
				 new FieldFactory<Float>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Float value) {
								_createFloatField(fieldsFactory,
												 luceneFieldId,
											   	 indexableField.getValue());
							}
				 });
	}
	private static void _createFloatField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldID luceneFieldId,
								  		 final float number) {
		fieldsFactory.createField(luceneFieldId)
					 .setFloatValue(number);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Date
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createDateField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldValue<Date> indexableField) {
		_process(indexableField,
				 new FieldFactory<Date>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Date value) {
								_createDateField(fieldsFactory,
												 luceneFieldId,
											   	 indexableField.getValue());
							}
				 });
	}
	private static void _createDateField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										 final IndexDocumentFieldID luceneFieldId,
										 final Date date) {
		fieldsFactory.createField(luceneFieldId)
					 .setLongValue(Dates.asMillis(date));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createRangeField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		  final IndexDocumentFieldValue<Range<? extends Comparable<?>>> indexableField) {
		_process(indexableField,
				 new FieldFactory<Range<? extends Comparable<?>>>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Range<? extends Comparable<?>> value) {
								_createRangeField(fieldsFactory,
												  luceneFieldId,
											   	  indexableField.getValue());
							}
				 });
	}



	private static <T extends Comparable<? super T>> void _createRangeField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										 									final IndexDocumentFieldID luceneFieldId,
										 									final Range<T> range) {

		Class<?> java_util_Date_class = java.util.Date.class;
		Class<?> java_lang_Integer_class = Integer.class;
		Class<?> java_lang_Long_class = Long.class;
		Class<?> java_lang_Double_class = Double.class;
		Class<?> java_lang_Float_class = Float.class;
		/*
		 *   [javac] /softbase_ejie/aplic/r01fb/tmp/compileLib/r01fbPersistenceClasses/src/r01f/persistence/index/lucene/LuceneIndexerBase.java:606: inconvertible types
		 *   [javac] found   : r01f.types.Range<T>
		 *   [javac] required: r01f.types.Range<java.util.Date>
		 *   [javac] 			Range<Date> typedRange = (Range<Date>)range;
		 *   [javac] 			                                      ^
		 */
		Object lowerBoundRaw = range.getLowerBound();
		Object upperBoundRaw =	range.getUpperBound();

		if (range.getDataType() == java_util_Date_class ) {
			Date lowerBound = (Date)lowerBoundRaw;
			Date upperBound = (Date)upperBoundRaw;
			if (lowerBound != null) _createDateField(fieldsFactory,
													 IndexDocumentFieldID.forId(luceneFieldId.asString() + ".lower"),
													 lowerBound);
			if (upperBound != null) _createDateField(fieldsFactory,
													 IndexDocumentFieldID.forId(luceneFieldId.asString() + ".upper"),
													 upperBound);
		} else if (range.getDataType() == java_lang_Integer_class) {
			Integer lowerBound = (Integer) lowerBoundRaw;
			Integer upperBound = (Integer) upperBoundRaw;
			if (lowerBound != null) _createIntegerField(fieldsFactory,
														IndexDocumentFieldID.forId(luceneFieldId.asString() + ".lower"),
														lowerBound);
			if (upperBound != null) _createIntegerField(fieldsFactory,
														IndexDocumentFieldID.forId(luceneFieldId.asString() + ".upper"),
														upperBound);
		} else if (range.getDataType() == java_lang_Long_class) {

			Long lowerBound = (Long)lowerBoundRaw;
			Long upperBound = (Long)upperBoundRaw;

			if (lowerBound!= null) _createLongField(fieldsFactory,
													IndexDocumentFieldID.forId(luceneFieldId.asString() + ".lower"),
													lowerBound);
			if (upperBound != null) _createLongField(fieldsFactory,
													 IndexDocumentFieldID.forId(luceneFieldId.asString() + ".upper"),
													 upperBound);
		} else if (range.getDataType() == java_lang_Double_class) {

			Double lowerBound = (Double)lowerBoundRaw;
			Double upperBound = (Double)upperBoundRaw;

			if (lowerBound!= null) _createDoubleField(fieldsFactory,
													  IndexDocumentFieldID.forId(luceneFieldId.asString() + ".lower"),
													  lowerBound);
			if (upperBound != null) _createDoubleField(fieldsFactory,
													   IndexDocumentFieldID.forId(luceneFieldId.asString() + ".upper"),
													   upperBound);
		} else if (range.getDataType() == java_lang_Float_class) {
			Float lowerBound = (Float)lowerBoundRaw;
			Float upperBound = (Float)upperBoundRaw;
			if (lowerBound != null) _createFloatField(fieldsFactory,
													  IndexDocumentFieldID.forId(luceneFieldId.asString() + ".lower"),
													  lowerBound);
			if (upperBound!= null) _createFloatField(fieldsFactory,
													 IndexDocumentFieldID.forId(luceneFieldId.asString() + ".upper"),
													 upperBound);
		} else {
			throw new IllegalArgumentException("Type " + range.getDataType() + " is NOT supported in lucene");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Path
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createPathField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldValue<IsPath> indexableField) {
		_process(indexableField,
				 new FieldFactory<IsPath>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final IsPath value) {
								_createPathField(fieldsFactory,
												 luceneFieldId,
											   	 indexableField.getValue());
							}
				 });
	}
	private static void _createPathField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										 final IndexDocumentFieldID luceneFieldId,
										 final IsPath path) {
		fieldsFactory.createField(luceneFieldId)
					 .setStringValue(path.asAbsoluteString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Path
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createSerializedUrlField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 		  final IndexDocumentFieldValue<Url> indexableField) {
		_process(indexableField,
				 new FieldFactory<Url>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Url value) {
								_createSerializedUrlField(fieldsFactory,
												 		  luceneFieldId,
												 		  indexableField.getValue());
							}
				 });
	}
	private static void _createSerializedUrlField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
										 		  final IndexDocumentFieldID luceneFieldId,
										 		  final Url url) {
		fieldsFactory.createField(luceneFieldId)
					 .setStringValue(url.asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Enum
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createEnumField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldValue<Enum<? extends Enum<?>>> indexableField) {
		_process(indexableField,
				 new FieldFactory<Enum<? extends Enum<?>>>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Enum<? extends Enum<?>> value) {
								_createEnumField(fieldsFactory,
												 luceneFieldId,
											   	 indexableField.getValue());
							}
				 });
	}
	private static void _createEnumField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
								  		 final IndexDocumentFieldID luceneFieldId,
								  		 final Enum<? extends Enum<?>> en) {
		if (en instanceof Language) {
			_createStringField(fieldsFactory,
							   luceneFieldId,en.name());
		} else if (en instanceof EnumWithCode) {
			Object code = ((EnumWithCode<?,?>)en).getCode();
			if (code instanceof Integer) {
				_createIntegerField(fieldsFactory,
									luceneFieldId,(Integer)code);
			}
			else if (code instanceof Long) {
				_createLongField(fieldsFactory,
								 luceneFieldId,(Long)code);
			}
			else if (code instanceof Character) {
				_createStringField(fieldsFactory,
								   luceneFieldId,Character.toString((Character)code));
			}
			else if (code instanceof String) {
				_createStringField(fieldsFactory,
								   luceneFieldId,(String)code);
			}
			else {
				throw new IllegalStateException(Strings.customized("{} with a {} type code is not supported!!",
																   EnumWithCode.class,code.getClass()));
			}
		} else {
			fieldsFactory.createField(luceneFieldId)
						 .setStringValue(en.name());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LanguageTexts
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createLanguageTextsFields(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
												   final IndexDocumentFieldValue<LanguageTexts> indexableField) {
		_process(indexableField,
				 new FieldFactory<LanguageTexts>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final LanguageTexts value) {
								_createLanguageTextsField(fieldsFactory,
												 		  luceneFieldId,
												 		  indexableField.getValue());
							}
				 });
	}
	private static void _createLanguageTextsField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
									 			  final IndexDocumentFieldID luceneFieldId,
									 			  final LanguageTexts langTexts) {
		// Guess the language from the id
		Language lang = Languages.fromNameOrThrow(IndexDocumentFieldID.dynamicDimensionPointFromFieldId(luceneFieldId));

		_createStringField(fieldsFactory,
						   luceneFieldId,
						   langTexts.get(lang));		// dynamic dimension (language) dependent
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Summary
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _createSummaryField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
									 		final IndexDocumentFieldValue<Summary> indexableField) {
		_process(indexableField,
				 new FieldFactory<Summary>() {
							@Override
							public void create(final IndexDocumentFieldID luceneFieldId,
											   final Summary value) {
								_createSummaryField(fieldsFactory,
													luceneFieldId,
											   	 	indexableField.getValue());
							}
				 });
	}
	private static void _createSummaryField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
									 		final IndexDocumentFieldID luceneFieldId,
									 		final Summary summary) {
		if (summary != null) {
			if (summary.isLangDependent()) {
				LangDependentSummary langDepSummary = summary.asLangDependent();
				log.debug("\t-Lang Dependent Summary field: {} = {}",
						  luceneFieldId,Strings.customized("--Lang dependant summary in {} languages",CollectionUtils.of(langDepSummary.getAvailableLanguages())
																																	   .toStringCommaSeparated()));
				_createLangDependentSummaryFields(fieldsFactory,
												  luceneFieldId,
												  langDepSummary);

			} else if (summary.isLangIndependent()) {
				LangIndependentSummary langIndSummary = summary.asLangIndependent();
				log.debug("\t-Lang Independent Summary field: {} = {}",
						  luceneFieldId,langIndSummary.asString());

				_createLangIndependentSummaryField(fieldsFactory,
												   luceneFieldId,
												   langIndSummary);
			}
		}
	}
	private static void _createLangDependentSummaryFields(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
												   		  final IndexDocumentFieldID luceneFieldId,
												   		  final LangDependentSummary summary) {
		// Guess the language from the id
		Language lang = Languages.fromNameOrThrow(IndexDocumentFieldID.dynamicDimensionPointFromFieldId(luceneFieldId));

		// Field value: note that summaries are only stored but full-text summaries are indexed and not stored
		if (summary.isFullTextSummary()) {
			_createReaderField(fieldsFactory,
							   luceneFieldId,
							   summary.asReader(lang));		// dynamic dimension (language) dependent
		} else {
			_createStringField(fieldsFactory,
							   luceneFieldId,
							   summary.asString(lang));		// dynamic dimension (language) dependent
		}
	}
	private static void _createLangIndependentSummaryField(final LuceneDocumentFactoryForIndexableObject fieldsFactory,
														   final IndexDocumentFieldID luceneFieldId,
														   final LangIndependentSummary summary) {
		// Field value: note that summaries are only stored but full-text summaries are indexed and not stored
		if (summary.isFullTextSummary()) {
			_createReaderField(fieldsFactory,
							   luceneFieldId,
							   summary.asReader());			// NOT dynamic dimension (language) dependent
		} else {
			_createStringField(fieldsFactory,
							   luceneFieldId,
							   summary.asString());			// NOT dynamic dimension (language) dependent
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Process a field value
	 * - If the field value is language dependent creates N fields, one per lang
	 * - If the field value is language independent creates only ONE field
	 * @param indexableField
	 * @param fieldFactory
	 */
	private static <T> void _process(final IndexDocumentFieldValue<T> indexableField,
							  		 final FieldFactory<T> fieldFactory) {
		if (indexableField.dependsOnDynamicDimension()) {
			// Create a field for each dynamic dimension point (ie language) with the id = [fieldId].[lang]
			for (Object point : indexableField.getDynamicDimensionPoints()) {
				IndexDocumentFieldID luceneFieldId = IndexDocumentFieldID.fieldIdOf(indexableField.getIndexableFieldId(),point);
				fieldFactory.create(luceneFieldId,
									indexableField.getValue());
			}
		} else {
			// Create ONLY one field with the id = [fieldId]
			IndexDocumentFieldID luceneFieldId = IndexDocumentFieldID.fieldIdOf(indexableField.getIndexableFieldId());
			fieldFactory.create(luceneFieldId,
								indexableField.getValue());
		}
	}
	private static abstract class FieldFactory<T> {
		public abstract void create(final IndexDocumentFieldID luceneFieldId,
									final T value);
	}
}
