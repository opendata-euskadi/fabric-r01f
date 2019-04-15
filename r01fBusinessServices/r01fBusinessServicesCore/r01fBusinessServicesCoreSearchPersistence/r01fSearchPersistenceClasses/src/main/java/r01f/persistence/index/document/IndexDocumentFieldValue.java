package r01f.persistence.index.document;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.FieldID;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.IsPath;
import r01f.types.Range;
import r01f.types.summary.LangDependentSummary;
import r01f.types.summary.LangIndependentSummary;
import r01f.types.summary.Summary;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

import com.google.common.base.Preconditions;

@Accessors(prefix="_")
public class IndexDocumentFieldValue<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The metadata id
	 */
	@Getter private FieldMetaData _metaDataConfig;
	/**
	 * @return the metadata field id
	 */
	public FieldID getIndexableFieldId() {
		return _metaDataConfig.getIndexableFieldId();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The value
	 */
	@Getter private T _value;	
	/**
	 * Sometimes a metadata has a value in certain "points" of a dimension, for example,
	 * the summary metadata is available in certain languages (es|eu|en|fr|de)
	 * The metadata value for every language is indexed in the SAME document BUT in different
	 * fields
	 * 
	 * This field contains the set of point for which there's a value for this metaData
	 * for example:
	 * 		For the metaData "name" there's values for every language (es|eu|en|fr|de)
	 * 		so there exists multiple "name" metaData:
	 * 			name.es
	 * 			name.eu
	 * 			name.en
	 * 			name.fr
	 * 			name.de
	 */
	@Getter private Collection<?> _dynamicDimensionPoints;
	/**
	 * @return true if there's another dimension for the metadata values
	 */
	public boolean dependsOnDynamicDimension() {
		return CollectionUtils.hasData(_dynamicDimensionPoints);
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	private IndexDocumentFieldValue(final FieldMetaData fieldMetaDataConfig,
							        final T value,
							        final Collection<?> dynamicDimensionPoints) {
		if (!fieldMetaDataConfig.hasMultipleDimensions()) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a multi-dimensions FIELD as it's supposed to; please check the model object's document definition",fieldMetaDataConfig.getFieldId()));
		_metaDataConfig = fieldMetaDataConfig;
		_value = value;
		_dynamicDimensionPoints = dynamicDimensionPoints;
	}
	private IndexDocumentFieldValue(final FieldMetaData metaDataConfig,
							        final T value) {
		_metaDataConfig = metaDataConfig;
		_value = value;
		_dynamicDimensionPoints = null;
	}
	public static IndexableFieldBuilderStep1 forMetaData(final FieldMetaData metaDataCfg) {
		return new IndexableFieldBuilderStep1(true,		// check dataType
											  metaDataCfg);
	}
	public static IndexableFieldBuilderStep1 forMetaDataNotCheckingType(final FieldMetaData metaDataCfg) {
		return new IndexableFieldBuilderStep1(false,		// do not check dataType
											  metaDataCfg);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class IndexableFieldBuilderStep1 {
		private final boolean _checkType;
		private final FieldMetaData _metaDataCfg;

		public IndexDocumentFieldValue<String> andValue(final String strValue) {
			Preconditions.checkArgument(Strings.isNOTNullOrEmpty(strValue),"The input string must NOT be null or empty");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(strValue);		// check the data type
			return new IndexDocumentFieldValue<String>(_metaDataCfg,
											           strValue);			
		}
		public IndexDocumentFieldValue<OID> andValue(final OID oid) {
			Preconditions.checkArgument(oid != null,"The oid must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(oid);			// check the data type
			return new IndexDocumentFieldValue<OID>(_metaDataCfg,
											        oid);			
		}
		public IndexDocumentFieldValue<Language> andValue(final Language lang) {
			Preconditions.checkArgument(lang != null,"The language must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(lang);			// check the data type			
			return new IndexDocumentFieldValue<Language>(_metaDataCfg,
											  		     lang);
		}
		public <E extends Enum<E>> IndexDocumentFieldValue<E> andValue(final E en) {
			Preconditions.checkArgument(en != null,"The enum value must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(en);			// check the data type			
			return new IndexDocumentFieldValue<E>(_metaDataCfg,
											      en);
		}
		public <C extends Comparable<C>> IndexDocumentFieldValue<Range<C>> andValue(final Range<C> range) {
			Preconditions.checkArgument(range != null,"The range must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(range);
			return new IndexDocumentFieldValue<Range<C>>(_metaDataCfg,
													     range);
		}
		public IndexDocumentFieldValue<Boolean> andValue(final Boolean bool) {
			Preconditions.checkArgument(bool != null,"The boolean must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(bool);			// check the data type
			return new IndexDocumentFieldValue<Boolean>(_metaDataCfg,
											            bool);
		}
		public <P extends IsPath> IndexDocumentFieldValue<P> andValue(final P path) {
			Preconditions.checkArgument(path != null,"The path must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(path);			// check the data type
			return new IndexDocumentFieldValue<P>(_metaDataCfg,
											  path);
		}
		public <N extends Number> IndexDocumentFieldValue<N> andValue(final N num) {
			Preconditions.checkArgument(num != null,"The number must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(num);			// check the data type
			return new IndexDocumentFieldValue<N>(_metaDataCfg,
											      num);
		}
		public IndexDocumentFieldValue<Class<?>> andValue(final Class<?> type) {
			Preconditions.checkArgument(type != null,"The type must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(type);			// check the data type
			return new IndexDocumentFieldValue<Class<?>>(_metaDataCfg,
											  			 type);
		}
		public IndexDocumentFieldValue<Url> andValue(final Url url) {
			Preconditions.checkArgument(url != null,"The url must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(url);			// check the data type
			return new IndexDocumentFieldValue<Url>(_metaDataCfg,
											  			      url);
		}
		public IndexDocumentFieldValue<Date> andValue(final Date date) {
			Preconditions.checkArgument(date != null,"The date must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(date);			// check the data type
			return new IndexDocumentFieldValue<Date>(_metaDataCfg,
											         date);
		}
		public IndexDocumentFieldValue<LanguageTexts> andValue(final LanguageTexts langTexts) {
			Preconditions.checkArgument(langTexts != null,"The language texts must not be null");	
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(langTexts);			// check the data type
			return new IndexDocumentFieldValue<LanguageTexts>(_metaDataCfg,
											         		  langTexts,
											         		  langTexts.getDefinedLanguages());				// the value is dependent on language dimension
		}
		public IndexDocumentFieldValue<Summary> andValue(final Summary summary) {
			Preconditions.checkArgument(summary != null,"The summary must not be null");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(summary);		// check the data type
			
			IndexDocumentFieldValue<Summary> outSummaryField = null;
			if (summary instanceof LangDependentSummary) {
				LangDependentSummary langDepSum = summary.asLangDependent();
				if (CollectionUtils.isNullOrEmpty(langDepSum.getAvailableLanguages())) throw new IllegalArgumentException("The lang-dependent summary does NOT contain any summary in any language!");
				outSummaryField = new IndexDocumentFieldValue<Summary>(_metaDataCfg,
											  				   	       summary,
											  				   	       langDepSum.getAvailableLanguages());	// the value is dependent on language dimension
			} else {
				LangIndependentSummary langIndepSum = summary.asLangIndependent();
				outSummaryField = new IndexDocumentFieldValue<Summary>(_metaDataCfg,
											  				   	       langIndepSum);
			}
			return outSummaryField;
		}
		public IndexDocumentFieldValue<CanBeRepresentedAsString> andValue(final CanBeRepresentedAsString canBeString) {
			Preconditions.checkArgument(canBeString != null,"The object must not be null");
			return new IndexDocumentFieldValue<CanBeRepresentedAsString>(_metaDataCfg,
																	     canBeString);
		}
		public <T> IndexDocumentFieldValue<Collection<T>> andValues(final Collection<T> values) {
			Preconditions.checkArgument(CollectionUtils.hasData(values),"The values collection must not be null or empty");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(values);						// check the data type
			return new IndexDocumentFieldValue<Collection<T>>(_metaDataCfg,values);
		}
		public <D,T> IndexDocumentFieldValue<Map<D,T>> andValues(final Map<D,T> valuesByDimensionPoint) {
			Preconditions.checkArgument(CollectionUtils.hasData(valuesByDimensionPoint),"The values Map must not be null or empty");
			if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(valuesByDimensionPoint);		// check the data type
			return new IndexDocumentFieldValue<Map<D,T>>(_metaDataCfg,
													     valuesByDimensionPoint,
													     valuesByDimensionPoint.keySet());	// the dimension points are the keys of the map
		}
		@SuppressWarnings("unchecked")
		public <T> IndexDocumentFieldValue<T> andValue(final T value) {
			if (value instanceof String) {
				return (IndexDocumentFieldValue<T>)this.andValue((String)value);
			} else if (value instanceof Language) {
				return (IndexDocumentFieldValue<T>)this.andValue((Language)value);
			} else if (value instanceof OID) {
				return (IndexDocumentFieldValue<T>)this.andValue((OID)value);
			} else if (value instanceof Enum) {
				Preconditions.checkArgument(value != null,"The enum value must not be null");
				if (_checkType) _metaDataCfg.checkIfIsAcceptableValueOrThrow(value);			// check the data type			
				return new IndexDocumentFieldValue<T>(_metaDataCfg,
												      value);
			} else if (value instanceof Range) {
				return (IndexDocumentFieldValue<T>)this.andValue((Range<? extends Comparable<?>>)value);
			} else if (value instanceof IsPath) {
				return (IndexDocumentFieldValue<T>)this.andValue((IsPath)value);
			} else if (value instanceof Date) {
				return (IndexDocumentFieldValue<T>)this.andValue((Date)value);
			} else if (value instanceof Number) {
				return (IndexDocumentFieldValue<T>)this.andValue((Number)value);
			} else if (value instanceof Class) {
				return (IndexDocumentFieldValue<T>)this.andValue((Class<?>)value);
			} else if (value instanceof LanguageTexts) {
				return (IndexDocumentFieldValue<T>)this.andValue((LanguageTexts)value);
		    } else if (value instanceof Summary) {
				return (IndexDocumentFieldValue<T>)this.andValue((Summary)value);
			} else if (value instanceof Url) {
				return (IndexDocumentFieldValue<T>)this.andValue((Url)value);		
			} else if (value instanceof CanBeRepresentedAsString) {		// DO NOT MOVE
				// last resort
				return (IndexDocumentFieldValue<T>)this.andValue((CanBeRepresentedAsString)value);
			} else if (value instanceof Collection) {
				return (IndexDocumentFieldValue<T>)this.andValues((Collection<?>)value);
			} else if (value instanceof Map) {
				return (IndexDocumentFieldValue<T>)this.andValues((Map<?,?>)value);
			}
			else {
				throw new IllegalArgumentException(Throwables.message("The indexable field value of type {} is NOT a valid type",value.getClass()));
			}
		}
	}
}
