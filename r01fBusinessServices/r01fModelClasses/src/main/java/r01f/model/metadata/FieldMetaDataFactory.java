package r01f.model.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.LanguageTexts;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.IsPath;
import r01f.types.summary.Summary;

@GwtIncompatible
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class FieldMetaDataFactory {

	private final FieldID _fieldId;
	private final LanguageTexts _name;
	private final Field _field;
	private final LanguageTexts _description;
	private final MetaDataForField _metaData;

	public static FieldMetaData getInstance(final FieldID fieldId,
											final LanguageTexts name,
											final Field field,
											final LanguageTexts description,
											final MetaDataForField metaData) {
		return new FieldMetaDataFactory(fieldId, name, field, description, metaData).getFieldMetaData();
	}


		public FieldMetaData getFieldMetaData() {
			if (ReflectionUtils.isImplementing(_field.getType(), OID.class))
				return getOIDField();
			if (ReflectionUtils.isImplementing(_field.getType(), Integer.class))
				return getIntegerField();
			if (ReflectionUtils.isImplementing(_field.getType(), Long.class))
				return getLongField();
			if (ReflectionUtils.isImplementing(_field.getType(), Double.class))
				return getDoubleField();
			if (ReflectionUtils.isImplementing(_field.getType(), Float.class))
				return getFloatField();
			if (ReflectionUtils.isImplementing(_field.getType(), Date.class))
				return getDateField();
			if (ReflectionUtils.isImplementing(_field.getType(), CanBeRepresentedAsString.class))
				return getStringField();
			if (ReflectionUtils.isImplementing(_field.getType(), Enum.class))
				return getEnumField();
			if (ReflectionUtils.isImplementing(_field.getType(), LanguageTexts.class))
				return getLanguageTextsField();
			if (ReflectionUtils.isImplementing(_field.getType(), IsPath.class))
				return getPathField();
			if (ReflectionUtils.isImplementing(_field.getType(), URL.class))
				return getURLField();
			if (ReflectionUtils.isImplementing(_field.getType(), Collection.class))
				return getCollectionField();
			if (ReflectionUtils.isImplementing(_field.getType(), Map.class))
				return getMapField();
			if (ReflectionUtils.isImplementing(_field.getType(), Summary.class))
				return getSummaryField();
			if (ReflectionUtils.isImplementing(_field.getType(), Field.class))
				return getFieldField();
			return getStringField();
		}

		public FieldMetaData getFieldField() {
			MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForJavaType> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forJavaTypeField(_field.getType()).searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forJavaTypeField( _field.getType()).searchEngine().notStored();
			if (!_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.indexed().withBoosting(_metaData.storage().boosting());
		}

		public FieldMetaData getSummaryField() {
			MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForSummary> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forSummaryField((Class<? extends Summary>) _field.getType()).searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forSummaryField((Class<? extends Summary>) _field.getType()).searchEngine().notStored();
			if (!_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.indexed().withBoosting(_metaData.storage().boosting());
		}

		public FieldMetaData getStringField() {
			MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forStringField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forStringField().searchEngine().notStored();
			if (!_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
			if (_metaData.storage().tokenized())
				return  metaDataConfigBuilderIndexingCfgStoreStep.indexed().tokenized().withBoosting(_metaData.storage().boosting());
			return metaDataConfigBuilderIndexingCfgStoreStep.indexed().notTokenized();
		}

		public FieldMetaData getMapField() {
			MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForMap> metaDataConfigBuilderIndexingCfgStoreStep;
			final Class<?> keyClass =  (Class<?>) ((ParameterizedType)_field.getGenericType()).getActualTypeArguments()[0];
			final Class<?> valueClass =  (Class<?>) ((ParameterizedType)_field.getGenericType()).getActualTypeArguments()[1];
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forMapField(keyClass, valueClass).searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forMapField(keyClass, valueClass).searchEngine().notStored();
			if (!_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
			if (_metaData.storage().tokenized())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed().tokenized().withBoosting(_metaData.storage().boosting());
			return metaDataConfigBuilderIndexingCfgStoreStep.indexed().notTokenized();
		}

		public FieldMetaData getCollectionField() {
			MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForCollection> metaDataConfigBuilderIndexingCfgStoreStep;
			 final Class<?> clazz = (Class<?>) ((ParameterizedType)_field.getGenericType()).getActualTypeArguments()[0];
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forCollectionField(clazz).searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forCollectionField(clazz).searchEngine().notStored();
			if (!_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
			if (_metaData.storage().tokenized())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed().tokenized().withBoosting(_metaData.storage().boosting());
			return metaDataConfigBuilderIndexingCfgStoreStep.indexed().notTokenized();
		}

		public FieldMetaData getURLField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForUrl> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forURLField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forURLField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getPathField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForPath> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forPathField((Class<? extends IsPath>) _field.getType()).searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forPathField((Class<? extends IsPath>) _field.getType()).searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getLanguageTextsField() {
			MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForLanguageTexts> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forLanguageTextsField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forLanguageTextsField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed().withBoosting(_metaData.storage().boosting());
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getEnumField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForEnum> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forEnumField((Class<? extends Enum<?>>) _field.getType()).searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forEnumField((Class<? extends Enum<?>>) _field.getType()).searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getBooleanField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForBoolean> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forBooleanField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forBooleanField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getDateField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDate> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forDateField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forDateField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getFloatField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForFloat> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forFloatField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forFloatField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}


		public FieldMetaData getDoubleField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDouble> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forDoubleField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forDoubleField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getLongField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLong> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forLongField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forLongField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getIntegerField() {
			MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForInteger> metaDataConfigBuilderIndexingCfgStoreStep;
			if (_metaData.storage().stored())
				metaDataConfigBuilderIndexingCfgStoreStep = forIntegerField().searchEngine().stored();
			else
				metaDataConfigBuilderIndexingCfgStoreStep = forIntegerField().searchEngine().notStored();
			if (_metaData.storage().indexed())
				return metaDataConfigBuilderIndexingCfgStoreStep.indexed();
			return metaDataConfigBuilderIndexingCfgStoreStep.notIndexed();
		}

		public FieldMetaData getOIDField() {
			if (_metaData.storage().indexed())
				return forOIDField((Class<? extends OID>) _field.getType()).searchEngine().indexed();
			return forOIDField((Class<? extends OID>) _field.getType()).searchEngine().notIndexed();
		}


		public PolimorphicFieldMetaDataConfigBuilderTypeStep1 forPolymorphicField(final Class<?> baseType) {
			return new PolimorphicFieldMetaDataConfigBuilderTypeStep1(new FieldMetaDataForPolymorphicType(_fieldId,
										 	   													  			_name,_description,
										 	   													  			new FieldMetaDataSearchEngineIndexingConfig(),
										 	   													  			baseType));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
											    MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>> forStringField() {
			final FieldMetaDataForString fieldMetaData = new FieldMetaDataForString(_fieldId,
											  								  _name,_description,
											  								  new FieldMetaDataSearchEngineIndexingConfig());
			// strings can be indexed tokenized or not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
													    MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>>(fieldMetaData,
																					 											 new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
											    MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>> forStringField(final Class<? extends CanBeRepresentedAsString> type) {
			final FieldMetaDataForString fieldMetaData = new FieldMetaDataForString(_fieldId,
														   					  _name,_description,
														   					  new FieldMetaDataSearchEngineIndexingConfig(),
														   					  type);
			// strings can be indexed tokenized or not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
														MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>>(fieldMetaData,
																					 											 new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingAlwaysStoredCfg<MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForOID>> forOIDField(final Class<? extends OID> oidType) {
			final FieldMetaDataForOID fieldMetaData = new FieldMetaDataForOID(_fieldId,
																	    _name,_description,
																	    new FieldMetaDataSearchEngineIndexingConfig(),
																	    oidType);
			// oids are always stored and can be indexed not tokenized
			fieldMetaData.getSearchEngineIndexingConfig()
						 .setStored(true);
			return new MetaDataConfigBuilderIndexingAlwaysStoredCfg<MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForOID>>(new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForOID>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForInteger,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForInteger>> forIntegerField() {
			final FieldMetaDataForInteger fieldMetaData = new FieldMetaDataForInteger(_fieldId,
																			    _name,_description,
																			    new FieldMetaDataSearchEngineIndexingConfig());
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForInteger,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForInteger>>(fieldMetaData,
																	 														     	 new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForInteger>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLong,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLong>> forLongField() {
			final FieldMetaDataForLong fieldMetaData = new FieldMetaDataForLong(_fieldId,
																		  _name,_description,
																		  new FieldMetaDataSearchEngineIndexingConfig());
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLong,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLong>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLong>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDouble,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDouble>> forDoubleField() {
			final FieldMetaDataForDouble fieldMetaData = new FieldMetaDataForDouble(_fieldId,
																		  	  _name,_description,
																		  	  new FieldMetaDataSearchEngineIndexingConfig());
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDouble,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDouble>>(fieldMetaData,
																	 														        new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDouble>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForFloat,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForFloat>> forFloatField() {
			final FieldMetaDataForFloat fieldMetaData = new FieldMetaDataForFloat(_fieldId,
																			_name,_description,
																			new FieldMetaDataSearchEngineIndexingConfig());
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForFloat,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForFloat>>(fieldMetaData,
																	 														       new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForFloat>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDate,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDate>> forDateField() {
			final FieldMetaDataForDate fieldMetaData = new FieldMetaDataForDate(_fieldId,
																	      _name,_description,
																	      new FieldMetaDataSearchEngineIndexingConfig());
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDate,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDate>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDate>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForYear,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForYear>> forYearField() {
			final FieldMetaDataForYear fieldMetaData = new FieldMetaDataForYear(_fieldId,
																	      _name,_description,
																	      new FieldMetaDataSearchEngineIndexingConfig());
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForYear,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForYear>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForYear>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMonthOfYear,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForMonthOfYear>> forMonthOfYearField() {
			final FieldMetaDataForMonthOfYear fieldMetaData = new FieldMetaDataForMonthOfYear(_fieldId,
																	      		 		_name,_description,
																	      		 		new FieldMetaDataSearchEngineIndexingConfig());
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMonthOfYear,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForMonthOfYear>>(fieldMetaData,
																	 														      		 new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForMonthOfYear>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDayOfMonth,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDayOfMonth>> forDayOfMonthField() {
			final FieldMetaDataForDayOfMonth fieldMetaData = new FieldMetaDataForDayOfMonth(_fieldId,
																	      		 	  _name,_description,
																	      		 	  new FieldMetaDataSearchEngineIndexingConfig());
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDayOfMonth,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDayOfMonth>>(fieldMetaData,
																	 														      		 new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDayOfMonth>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForBoolean,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForBoolean>> forBooleanField() {
			final FieldMetaDataForBoolean fieldMetaData = new FieldMetaDataForBoolean(_fieldId,
																			    _name,_description,
																			    new FieldMetaDataSearchEngineIndexingConfig());
			// booleans can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForBoolean,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForBoolean>>(fieldMetaData,
																	 														         new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForBoolean>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForEnum,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForEnum>> forEnumField(final Class<? extends Enum<?>> enumType) {
			final FieldMetaDataForEnum fieldMetaData = new FieldMetaDataForEnum(_fieldId,
																		  _name,_description,
																		  new FieldMetaDataSearchEngineIndexingConfig(),
																		  enumType);
			// enums can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForEnum,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForEnum>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForEnum>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguage,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLanguage>> forLanguageField() {
			final FieldMetaDataForLanguage fieldMetaData = new FieldMetaDataForLanguage(_fieldId,
																				  _name,_description,
																				  new FieldMetaDataSearchEngineIndexingConfig());
			// language can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguage,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLanguage>>(fieldMetaData,
																	 														      	  new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLanguage>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForPath,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForPath>> forPathField(final Class<? extends IsPath> pathType) {
			final FieldMetaDataForPath fieldMetaData = new FieldMetaDataForPath(_fieldId,
																		  _name,_description,
																		  new FieldMetaDataSearchEngineIndexingConfig(),
																		  pathType);
			// paths can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForPath,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForPath>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForPath>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForUrl,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForUrl>> forURLField() {
			final FieldMetaDataForUrl fieldMetaData = new FieldMetaDataForUrl(_fieldId,
																	    _name,_description,
																	    new FieldMetaDataSearchEngineIndexingConfig());
			// urls can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForUrl,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForUrl>>(fieldMetaData,
																	 														     new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForUrl>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForJavaType,
											    MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForJavaType>> forJavaTypeField(final Class<?> type) {
			final FieldMetaDataForJavaType fieldMetaData = new FieldMetaDataForJavaType(_fieldId,
																				  _name,_description,
																				  new FieldMetaDataSearchEngineIndexingConfig(),
																				  type);
			// java types can be indexed always tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForJavaType,
														MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForJavaType>>(fieldMetaData,
																	 														      	   new MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForJavaType>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForSummary,
												MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForSummary>> forSummaryField(final Class<? extends Summary> summaryType) {
			final FieldMetaDataForSummary fieldMetaData = new FieldMetaDataForSummary(_fieldId,
																			    _name,_description,
																			    new FieldMetaDataSearchEngineIndexingConfig(),
																			    summaryType);
			// summaries can be indexed always tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForSummary,
														MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForSummary>>(fieldMetaData,
																	 														      	  new MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForSummary>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguageTexts,
												MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForLanguageTexts>> forLanguageTextsField() {
			final FieldMetaDataForLanguageTexts fieldMetaData = new FieldMetaDataForLanguageTexts(_fieldId,
																						    _name,_description,
																						    new FieldMetaDataSearchEngineIndexingConfig());
			// language texts can be indexed always tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguageTexts,
														MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForLanguageTexts>>(fieldMetaData,
																	 														      	  		new MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForLanguageTexts>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForCollection,
												MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForCollection>> forCollectionField(final Class<?> componentType) {
			final FieldMetaDataForCollection fieldMetaData = new FieldMetaDataForCollection(_fieldId,
																					  _name,_description,
																					  new FieldMetaDataSearchEngineIndexingConfig(),
																					  componentType);
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForCollection,
														MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForCollection>>(fieldMetaData,
																	 														      	 new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForCollection>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMap,
												MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForMap>> forMapField(final Class<?> keyType,final Class<?> valueType) {
			final FieldMetaDataForMap fieldMetaData = new FieldMetaDataForMap(_fieldId,
																	    _name,_description,
																	    new FieldMetaDataSearchEngineIndexingConfig(),
																	    keyType,valueType);
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMap,
														MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForMap>>(fieldMetaData,
																	 														  new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForMap>(fieldMetaData));

		}
		public FieldMetaDataForDependentObject forDependantObject(final Class<?> objType,
																   final Set<FieldMetaData> childMetaData) {
			return new FieldMetaDataForDependentObject(_fieldId,
											   		   _name,_description,
											   		   new FieldMetaDataSearchEngineIndexingConfig(),
											   		   objType,
											   		   childMetaData);
		}
		public FieldMetaDataForDependentObject forDependantObject(final Class<?> objType,
																  final FieldMetaData... childMetaData) {
			return new FieldMetaDataForDependentObject(_fieldId,
											   		   _name,_description,
											   		   new FieldMetaDataSearchEngineIndexingConfig(),
											   		   objType,
											   		   Sets.newHashSet(childMetaData));
		}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class PolimorphicFieldMetaDataConfigBuilderTypeStep1 {
		@Getter(AccessLevel.PRIVATE) private final FieldMetaDataForPolymorphicType _fieldMetaData;

		public PolimorphicFieldMetaDataConfigBuilderTypeStep2 forModelObjectType(final Class<? extends MetaDataDescribable> modelObjType) {
			return new PolimorphicFieldMetaDataConfigBuilderTypeStep2(modelObjType,
																	  this);
		}
		public MetaDataConfigBuilderIndexingCfgStoreStep<FieldMetaDataForPolymorphicType,
														 MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForPolymorphicType>> searchEngine() {
			return new MetaDataConfigBuilderIndexingCfgStoreStep<FieldMetaDataForPolymorphicType,
																 MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForPolymorphicType>>(_fieldMetaData,
																																  				   new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForPolymorphicType>(_fieldMetaData));
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class PolimorphicFieldMetaDataConfigBuilderTypeStep2 {
		private final Class<? extends MetaDataDescribable> _modelObjType;
		private final PolimorphicFieldMetaDataConfigBuilderTypeStep1 _step1;

		public PolimorphicFieldMetaDataConfigBuilderTypeStep1 use(final Class<?> type) {
			_step1.getFieldMetaData().getFieldDataTypeMap()
						  			 .put(_modelObjType,type);
			return _step1;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingCfg<F extends FieldMetaData,
											      BUILDER_NEXT_STEP> {
		private final F _fieldMetaDataCfg;
		private final BUILDER_NEXT_STEP _builderNextStep;

		public MetaDataConfigBuilderIndexingCfgStoreStep<F,BUILDER_NEXT_STEP> searchEngine() {
			return new MetaDataConfigBuilderIndexingCfgStoreStep<F,BUILDER_NEXT_STEP>(_fieldMetaDataCfg,
																					  _builderNextStep);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingAlwaysStoredCfg<BUILDER_NEXT_STEP> {
		private final BUILDER_NEXT_STEP _builderNextStep;

		public BUILDER_NEXT_STEP searchEngine() {
			return _builderNextStep;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingCfgStoreStep<F extends FieldMetaData,
														   BUILDER_NEXT_STEP> {
		private final F _fieldMetaDataCfg;
		private final BUILDER_NEXT_STEP _builderNextStep;

		public BUILDER_NEXT_STEP stored() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setStored(true);
			return _builderNextStep;
		}
		public BUILDER_NEXT_STEP notStored() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setStored(false);
			return _builderNextStep;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<F extends FieldMetaData> {
		private final F _fieldMetaDataCfg;

		public F notIndexed() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setIndexed(false);
			return _fieldMetaDataCfg;
		}
		public MetaDataConfigBuilderIndexingCfgBoostingStep<F> indexed() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setIndexed(true);
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setTokenized(true);	// Tokenized
			return new MetaDataConfigBuilderIndexingCfgBoostingStep<F>(_fieldMetaDataCfg);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingCfgNotTokenizableStep<F extends FieldMetaData> {
		private final F _fieldMetaDataCfg;

		public F notIndexed() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setIndexed(false);
			return _fieldMetaDataCfg;
		}
		public F indexed() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setIndexed(true);
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setTokenized(false);	// NOT tokenized!!
			return _fieldMetaDataCfg;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingCfgTokenizableStep<F extends FieldMetaData> {
		private final F _fieldMetaDataCfg;

		public F notIndexed() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setIndexed(false);
			return _fieldMetaDataCfg;
		}
		public MetaDataConfigBuilderIndexingCfgTokenizeStep<F> indexed() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setIndexed(true);
			return new MetaDataConfigBuilderIndexingCfgTokenizeStep<F>(_fieldMetaDataCfg);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingCfgTokenizeStep<F extends FieldMetaData> {
		private final F _fieldMetaDataCfg;

		public F notTokenized() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setTokenized(false);
			return _fieldMetaDataCfg;
		}
		public MetaDataConfigBuilderIndexingCfgBoostingStep<F> tokenized() {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setTokenized(true);
			return new MetaDataConfigBuilderIndexingCfgBoostingStep<F>(_fieldMetaDataCfg);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class MetaDataConfigBuilderIndexingCfgBoostingStep<F extends FieldMetaData> {
		private final F _fieldMetaDataCfg;

		public F withDefaultBoosting() {
			return _fieldMetaDataCfg;
		}
		public F withBoosting(final float boosting) {
			_fieldMetaDataCfg.getSearchEngineIndexingConfig()
							 .setBoost(boosting);
			return _fieldMetaDataCfg;
		}
	}
}
