package r01f.persistence.lucene;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.enums.EnumWithCode;
import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.guids.OIDBase;
import r01f.guids.OIDs;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.model.IndexableModelObject;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.FieldMetaDataForCollection;
import r01f.model.metadata.FieldMetaDataForJavaType;
import r01f.model.metadata.FieldMetaDataForPolymorphicType;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.MetaDataDescribable;
import r01f.model.metadata.TypeFieldMetaData;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataForModelObjectBase;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.persistence.index.document.IndexDocumentBase;
import r01f.persistence.index.document.IndexDocumentFieldID;
import r01f.persistence.index.document.IndexDocumentFieldValue;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.IsPath;
import r01f.types.Range;
import r01f.types.summary.LangIndependentSummary;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryLanguageTextsBacked;
import r01f.types.summary.SummaryStringBacked;
import r01f.types.url.Url;
import r01f.util.enums.Enums.EnumWrapper;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;

@Slf4j
public class LuceneSearchResultDocument<M extends IndexableModelObject> 
     extends IndexDocumentBase<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Document _luceneDoc;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public LuceneSearchResultDocument(final Document luceneDoc) {
		super();
		_luceneDoc = luceneDoc;
	}
	public static <M extends IndexableModelObject> LuceneSearchResultDocumentBuilderTypeMetaDataInspectorStep<M> from(final Document doc) {
		return new LuceneSearchResultDocumentBuilderTypeMetaDataInspectorStep<M>(doc);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class LuceneSearchResultDocumentBuilderTypeMetaDataInspectorStep<M extends IndexableModelObject> {
		private final Document _doc;
		
		public LuceneSearchResultDocument<M> using(final HasTypesMetaData hasTypesMetaData) {
			LuceneSearchResultDocument<M> outDoc = new LuceneSearchResultDocument<M>(_doc);
			return outDoc;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public TypeMetaData<M> getModelObjectMetaData() {
		// [1] - Get the lucene's indexed field
		FieldID fieldId = FieldID.from(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE_CODE);
		IndexableField field = _luceneDoc.getField(fieldId.asString());
		if (field == null) throw new IllegalStateException(Throwables.message("The lucene document is NOT valid: it does NOT have the type field (id={})",
																			  fieldId));
		// [2] - Get the type code value
		long typeCode = field.numericValue()
							 .longValue();
		// [3] - Get the type metadata from the type code
		return TypeMetaDataInspector.singleton()
									.getTypeMetaDataFor(typeCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Map<FieldID,IndexDocumentFieldValue<?>> getFields() {
		// [0] - Get the model object metadata
		TypeMetaData<M> modelObjMetaData = this.getModelObjectMetaData();
		
		// [1] - Map the lucene document fields to metadata
		Map<FieldID,Collection<IndexableField>> luceneFieldsByMetaData = Maps.newHashMap();
		for (IndexableField luceneField : _luceneDoc.getFields()) {
			// 1.a - Find the metadata id from the stored indexFieldId
			IndexDocumentFieldID fieldId = IndexDocumentFieldID.forId(luceneField.name());	
			FieldID metaDataId = IndexDocumentFieldID.findMetaDataId(modelObjMetaData,
																			  fieldId);
			if (metaDataId == null) throw new IllegalStateException(Throwables.message("The index-stored metadata with id={} is NOT configured on model object with type={}",
																					   fieldId,modelObjMetaData.getRawType()));
			// 1.b - Store the indexed field indexed by the metaData id		
			Collection<IndexableField> indexedFields = luceneFieldsByMetaData.get(metaDataId);			
			if (indexedFields == null) {
				indexedFields = Lists.newLinkedList();
				luceneFieldsByMetaData.put(metaDataId,indexedFields);
			}	
			indexedFields.add(luceneField);
		}
		
		// [2] - Convert every metadata to a IndexDocumentFieldValue
		Map<FieldID,IndexDocumentFieldValue<?>> outFields = Maps.newLinkedHashMap();
		for (Map.Entry<FieldID,Collection<IndexableField>> me : luceneFieldsByMetaData.entrySet()) {
			FieldID metaDataId = me.getKey();
			TypeFieldMetaData typeFieldMetaData = modelObjMetaData.findFieldByIdOrThrow(metaDataId);
			FieldMetaData fieldMetaData = typeFieldMetaData.asFieldMetaData();
			Collection<IndexableField> luceneFields = me.getValue();
			
			// Security checks
			if (!(fieldMetaData.isCollectionField() || fieldMetaData.isSummaryField() || fieldMetaData.isLanguageTextsField()) 
			 && luceneFields.size() > 1) throw new IllegalStateException(Throwables.message("The field with id={} is NOT supposed to be multi-valued BUT multiple values are indexed",
																							fieldMetaData.getIndexableFieldId()));
			if (fieldMetaData.isBooleanField() && luceneFields.size() > 1) throw new IllegalStateException(Throwables.message("The field with id={} is a boolean field and this type of fields cannot be multi-valued",
																						 							     	  fieldMetaData.getIndexableFieldId()));
			// transform lucene fields to an IndexDocumentFieldValue
			IndexDocumentFieldValue<?> indexDocumentFieldValue = _toIndexDocumentField(modelObjMetaData,
																					   fieldMetaData,
																					   luceneFields);
			outFields.put(metaDataId,indexDocumentFieldValue);
		}
		return outFields;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private static <M extends IndexableModelObject,T> IndexDocumentFieldValue<T> _toIndexDocumentField(final TypeMetaData<M> modelObjMetaData,
																 									   final FieldMetaData fieldMetaData,
																 									   final Collection<IndexableField> luceneFields) {
		IndexDocumentFieldValue<T> outValue = null;
		
		// Collection metadata
		if (fieldMetaData.isCollectionField() && !fieldMetaData.isSummaryField()) {
			outValue = (IndexDocumentFieldValue<T>)IndexDocumentFieldValue.forMetaDataNotCheckingType(fieldMetaData)	// do not check indexed type (it's supposed to be the correct one)
																		  .andValues(FluentIterable.from(luceneFields)
																	  				    .transform(new Function<IndexableField,T>() {
																											@Override
																											public T apply(final IndexableField luceneField) {
																												return (T)_createValueFromLuceneIndexField(modelObjMetaData,
																																						   fieldMetaData,
																																				 	       luceneField);
																											}
																	  				    		   })
																	  				    .toList());
		} 
		// Normal metadata stored in a single lucene field 
		else if (luceneFields.size() == 1) {
			// Transform the single value
			IndexableField luceneField = CollectionUtils.pickOneAndOnlyElement(luceneFields);
			T value = LuceneSearchResultDocument.<M,T>_createValueFromLuceneIndexField(modelObjMetaData,
													   								   fieldMetaData,
													   								   luceneField);
			outValue = IndexDocumentFieldValue.forMetaDataNotCheckingType(fieldMetaData)	// do not check indexed type (it's supposed to be the correct one)
											  .andValue(value);
		} 
		// Multi-dimension metadata stored in multiple lucene fields (ie: language-dependent summaries)
		else if (luceneFields.size() > 1) {
			T value =  LuceneSearchResultDocument.<T>_createValueFromMultipleLuceneIndexFields(fieldMetaData,
													    		luceneFields);
			outValue = IndexDocumentFieldValue.forMetaDataNotCheckingType(fieldMetaData)	// do not check indexed type (it's supposed to be the correct one)
											  .andValue(value);
		}
		return outValue;
	}
	@SuppressWarnings("unchecked")
	private static <M extends IndexableModelObject,T> T _createValueFromLuceneIndexField(final TypeMetaData<M> modelObjMetaData,
																						 final FieldMetaData fieldMetaData,
																						 final IndexableField luceneField) {
		if (fieldMetaData.isJavaTypeField()) return (T)((FieldMetaDataForJavaType)fieldMetaData).getDataType();

		// Usually...
		T outValue = null;
				
		// [1]: Get the correct type to be created
		Class<T> type = null;
		if (fieldMetaData.isCollectionField() && !fieldMetaData.isSummaryField()) {
			type = (Class<T>)((FieldMetaDataForCollection)fieldMetaData).getComponentsType();
		} 
		else if (fieldMetaData.isPolymorphicField()) {
			// Get the model object type and from it guess the field type
			Class<? extends MetaDataDescribable> modelObjType = modelObjMetaData.getRawType();
			FieldMetaDataForPolymorphicType polyFieldMetaData = (FieldMetaDataForPolymorphicType)fieldMetaData;
			type = (Class<T>)polyFieldMetaData.getFieldTypeForModelObjType(modelObjType);
		} 
		else {
			type = (Class<T>)fieldMetaData.getDataType();
		}		
		if (type == null) throw new IllegalStateException(Throwables.message("Cannot guess the {} field type from the field meta data config",luceneField.name()));
		
		// [2]: Create the instance knowing the field type
		if (type == OID.class) {
			outValue = (T)new OIDBase<String>() {
								private static final long serialVersionUID = -5827815981557645538L;
								@Override
								public String getId() {
									return luceneField.stringValue();
								}
					      };
		}
		else if (ReflectionUtils.isImplementing(type,OID.class)) {
			outValue = (T)_createOidFromLuceneIndexedField((Class<? extends OID>)type,
										 		           luceneField);
		} 
		else if (ReflectionUtils.isImplementing(type,Boolean.class)) {
			outValue = (T)_createBooleanFromLuceneIndexedField(luceneField);
		} 
		else if (ReflectionUtils.isImplementing(type,Integer.class)) {
			outValue = (T)_createIntegerFromLuceneIndexedField(luceneField);
		} 
		else if (ReflectionUtils.isImplementing(type,Long.class)) { 
			outValue = (T)_createLongFromLuceneIndexedField(luceneField);
		} 
		else if (ReflectionUtils.isImplementing(type,Double.class)) { 
			outValue = (T)_createDoubleFromLuceneIndexedField(luceneField);
		} 
		else if (ReflectionUtils.isImplementing(type,Float.class)) { 
			outValue = (T)_createFloatFromLuceneIndexedField(luceneField);
		}  
		else if (ReflectionUtils.isImplementing(type,Date.class)) { 
			outValue = (T)_createDateFromLuceneIndexedField(luceneField);
		} 
		else if (ReflectionUtils.isImplementing(type,LangIndependentSummary.class)) {
			outValue = (T)_createLangIndependentSummaryFromLuceneIndexedField(luceneField);
		} 
		else if (ReflectionUtils.isImplementing(type,Url.class)) {
			outValue = (T)_createURLFromLuceneIndexedField(luceneField);
		}
		else if (ReflectionUtils.isImplementing(type,Language.class)) {
			outValue = (T)_createLanguageFromLuceneIndexedField(luceneField);
		}
		else if (ReflectionUtils.isImplementing(type,Enum.class)) {
			Class<? extends Enum<?>> castedType =(Class<? extends Enum<?>>)type;
			 outValue = (T) _createEnumFromLuceneIndexedField(castedType,
						luceneField);
			
		}
		else if (ReflectionUtils.isImplementing(type,IsPath.class)) {
			outValue = (T)_createPathFromLuceneIndexedField((Class<? extends IsPath>)type,
															luceneField);
		}
		else if (ReflectionUtils.isImplementing(type,String.class)) {
			outValue = (T)_createStringFromLuceneIndexedField(luceneField);
		}
		else if (ReflectionUtils.isImplementing(type,CanBeRepresentedAsString.class)) {		// DO NOT MOVE!!!
			outValue = (T)_createCanBeStringFromLuceneIndexedField((Class<? extends CanBeRepresentedAsString>)type,
																   luceneField);
		} 
		log.debug("\t-{}={} ({})",fieldMetaData.getFieldId(),outValue,type);
		return outValue;
	}
	@SuppressWarnings("unchecked")
	private static <T> T _createValueFromMultipleLuceneIndexFields(final FieldMetaData metaData,
												     	   		   final Collection<IndexableField> luceneFields) {
		Object outValue = null;		
		// Get the correct type to be created
		Class<T> type = null; 
		if (metaData.isCollectionField()) {
			type = (Class<T>)((FieldMetaDataForCollection)metaData).getComponentsType();
		} else {
			type = (Class<T>)metaData.getDataType();
		}
		// Create the instance
		if (ReflectionUtils.isImplementing(type,Summary.class)) {
			outValue = _createLangDependentSummaryFromLuceneIndexedField(luceneFields);
		}
		else if (ReflectionUtils.isImplementing(type,LanguageTexts.class)) {
			outValue = _createLanguageTextsFromLuceneIndexedField(luceneFields);
		}
		else if (ReflectionUtils.isImplementing(type,Range.class)) {				
			return (T) _createRangeFromLuceneIndexedField((Class<? extends Comparable<?>>) type,luceneFields);
		}
		return (T)outValue;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static OID _createOidFromLuceneIndexedField(final Class<? extends OID> oidType,
												 	    final IndexableField luceneField) {
		String oidVal = luceneField.stringValue();
		return OIDs.createOIDFromString(oidType,
										oidVal);
	}
	private static Boolean _createBooleanFromLuceneIndexedField(final IndexableField luceneField) {
		int boolVal = luceneField.numericValue().intValue();
		return boolVal == 1;
	}
	private static Integer _createIntegerFromLuceneIndexedField(final IndexableField luceneField) {
		return luceneField.numericValue().intValue();
	}
	private static Long _createLongFromLuceneIndexedField(final IndexableField luceneField) {
		return luceneField.numericValue().longValue();
	}
	private static Double _createDoubleFromLuceneIndexedField(final IndexableField luceneField) {
		return luceneField.numericValue().doubleValue();
	}
	private static Float _createFloatFromLuceneIndexedField(final IndexableField luceneField) {
		return luceneField.numericValue().floatValue();
	}
	private static Date _createDateFromLuceneIndexedField(final IndexableField luceneField) {
		long milis = luceneField.numericValue().longValue();
		return Dates.fromMillis(milis);
	}
	private static Language _createLanguageFromLuceneIndexedField(final IndexableField luceneField) {
		String langValue = luceneField.stringValue();
		return Languages.fromName(langValue);
	}
	@SuppressWarnings("unchecked")
	/*private static <C extends Comparable<C> > Range<C> _createRangeFromLuceneIndexedField(final Class<? extends Comparable<?>> rangeDataType,
																					     final Collection<IndexableField> luceneFields) {*/
	private static <T> T _createRangeFromLuceneIndexedField(final Class<? extends Comparable<?>> rangeDataType,
															final Collection<IndexableField> luceneFields) {
		IndexableField lowerBoundField = null;
		IndexableField upperBoundField = null;
		for (IndexableField luceneField : luceneFields) {
			if (luceneField.name().endsWith(".lower")) {
				lowerBoundField = luceneField;
			} else if (luceneField.name().endsWith(".upper")) {
				upperBoundField = luceneField;
			} else {
				throw new IllegalStateException(Throwables.message("{} is NOT a valid indexed field id for a Range field",luceneField.name()));
			}
		}
		if (lowerBoundField == null || upperBoundField == null) throw new IllegalStateException("Range type values are stored in two fields named [metaDataId].lower and [metaDataId].upper; any of these fields were NOT found");
		
		Object outRange = null;
		if (rangeDataType == Date.class) {
			long lowerBound = lowerBoundField.numericValue().longValue();
			long upperBound = upperBoundField.numericValue().longValue();
			outRange = Range.closed(Dates.fromMillis(lowerBound),Dates.fromMillis(upperBound));
		} 
		else if (rangeDataType == Integer.class) {
			int lowerBound = lowerBoundField.numericValue().intValue();
			int upperBound = upperBoundField.numericValue().intValue();
			outRange = Range.closed(lowerBound,upperBound);
		}
		else if (rangeDataType == Long.class) {
			long lowerBound = lowerBoundField.numericValue().intValue();
			long upperBound = upperBoundField.numericValue().intValue();
			outRange = Range.closed(lowerBound,upperBound);
		}
		else if (rangeDataType == Double.class) {
			double lowerBound = lowerBoundField.numericValue().doubleValue();
			double upperBound = upperBoundField.numericValue().doubleValue();
			outRange = Range.closed(lowerBound,upperBound);
		}
		else if (rangeDataType == Float.class) {
			float lowerBound = lowerBoundField.numericValue().floatValue();
			float upperBound = upperBoundField.numericValue().floatValue();
			outRange = Range.closed(lowerBound,upperBound);
		}
		return (T) outRange;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	/*private static <E extends Enum<E>> E _createEnumFromLuceneIndexedField(final Class<?> enumType,
															  			   final IndexableField luceneField) {*/
	private static <T> T _createEnumFromLuceneIndexedField(final Class<?> enumType,
														   final IndexableField luceneField) {
		Object outEnum = null;
		String enumValue = luceneField.stringValue();		
		Class<?> type = ReflectionUtils.typeFromClassName(enumType.getName());
		EnumWrapper enumWrapper = new EnumWrapper((Enum[]) type.getEnumConstants(),true);
	    ///Don't use here  Enums.of(enumType.getName())).fromCode...!! Does not compile for generic <T>.USE DIRECTLY ENUMWRAPPER and asign to an Object!!!!!
		 // The error was: 
		//   [javac] /softbase_ejie/aplic/r01fb/tmp/compileLib/r01fbPersistenceClasses/src/r01f/persistence/lucene/LuceneSearchResultDocument.java:369: incompatible types; inferred type argument(s) java.lang.Object do not conform to bounds of type variable(s) E
	    /*   [javac] found   : <E>r01f.enums.Enums.EnumWrapper<E>
	          [javac] required: java.lang.Object
	          [javac] 		Enums.of(theEnumTypeName); */	     
		if (enumType == Language.class) {
			Language outEnumLocale  = Language.fromName(enumValue);				
			outEnum = enumWrapper.fromCode(outEnumLocale.getCode()); 			
		} else if (ReflectionUtils.isImplementing(enumType,EnumWithCode.class)) {
			outEnum = enumWrapper.fromCode(enumValue);
		} else {
			outEnum = enumWrapper.fromName(enumValue);
		}
		return (T)outEnum;
	}
	
	private static Summary _createLangIndependentSummaryFromLuceneIndexedField(final IndexableField luceneField) {		
		String summaryStr = luceneField.stringValue();
		if (Strings.isNullOrEmpty(summaryStr)) summaryStr = "--NO SUMMARY--";
		SummaryStringBacked summary = SummaryStringBacked.of(summaryStr);
		return summary;
	}
	private static Summary _createLangDependentSummaryFromLuceneIndexedField(final Collection<IndexableField> luceneFields) {
		SummaryLanguageTextsBacked summary = SummaryLanguageTextsBacked.create();
		// lang dependent summaries are stored using a field named [metaDataID].[lang] for each language-summary
		for (IndexableField luceneField : luceneFields) {
			Language lang = Language.fromName(IndexDocumentFieldID.dynamicDimensionPointFromFieldId(IndexDocumentFieldID.forId(luceneField.name())));
			summary.addForLang(lang,
				 			   luceneField.stringValue());
		}
		return summary;
	}
	private static LanguageTexts _createLanguageTextsFromLuceneIndexedField(final Collection<IndexableField> luceneFields) {
		LanguageTexts langTexts = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL);
		// lang texts are stored using a field named [metaDataID].[lang] for each language-text
		for (IndexableField luceneField : luceneFields) {
			Language lang = Language.fromName(IndexDocumentFieldID.dynamicDimensionPointFromFieldId(IndexDocumentFieldID.forId(luceneField.name())));
			langTexts.add(lang,
				 		  luceneField.stringValue());
		}
		return langTexts;
		
	}
	private static Url _createURLFromLuceneIndexedField(final IndexableField luceneField) {
		String urlValue = luceneField.stringValue();
		return Url.from(urlValue);
	}
	private static IsPath _createPathFromLuceneIndexedField(final Class<? extends IsPath> pathType,
															final IndexableField luceneField) {
		String pathValue = luceneField.stringValue();
		return ReflectionUtils.createInstanceFromString(pathType,
														pathValue);
	}
	private static String _createStringFromLuceneIndexedField(final IndexableField luceneField) {
		String strValue = luceneField.stringValue();
		return ReflectionUtils.createInstanceFromString(String.class,
														strValue);
	}
	private static IsPath _createCanBeStringFromLuceneIndexedField(final Class<? extends CanBeRepresentedAsString> strType,
																   final IndexableField luceneField) {
		String strValue = luceneField.stringValue();
		return ReflectionUtils.createInstanceFromString(strType,
														strValue);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the document's fields whose name is the provided one 
	 * note that if the field is multi-valued many {@link IndexableField}s with the same name
	 * might be returned
	 * @param name the name
	 * @return
	 */
	@SuppressWarnings("unused")
	private Collection<IndexableField> _fieldsWithName(final IndexDocumentFieldID name) {
		List<IndexableField> fields = _luceneDoc.getFields();
		Collection<IndexableField> matchingFields = Collections2.filter(fields,
																  		new Predicate<IndexableField>() {
																				@Override
																				public boolean apply(final IndexableField field) {
																					return field.name().equals(name);
																				}
																  		});
		return matchingFields;
	}
	/**
	 * Filters the document's fields whose name matches the provided pattern
	 * This method is needed for {@link FieldID} that are stored in multiple document fields
	 * with different {@link IndexDocumentFieldID} because the field have multiple values depending
	 * on a dimension (ie language)
	 * For example, a multi-language {@link Summary} field with {@link FieldID}=r01.summary
	 * is stored in multiple lucene fields, one for each document: r01.summary.es, r01.summary.eu, etc
	 * @param pattern the pattern to match against the field name
	 * @return the fields that matches
	 */
	@SuppressWarnings("unused")
	private Collection<IndexableField> _fieldsMatching(final Pattern pattern) {
		List<IndexableField> fields = _luceneDoc.getFields();
		Collection<IndexableField> matchingFields = Collections2.filter(fields,
																  		new Predicate<IndexableField>() {
																				@Override
																				public boolean apply(final IndexableField field) {
																					Matcher m = pattern.matcher(field.name());
																					return m.matches();
																				}
																  		});
		return matchingFields;
	}

}
