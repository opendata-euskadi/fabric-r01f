package r01f.model.metadata;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.PolymorphicFieldTypeResolve;
import r01f.patterns.IsBuilder;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.IsPath;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.types.summary.Summary;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

/**
 * {@link FieldMetaData} objects config builder
 * usage:
 * <pre class='brush:java'>
 * 		FieldMetaDataConfigBuilder.forId(DOCUMENT_ID_FIELD_ID)
 *		 		.withName(new LanguageTextsMapBacked()
 *			 					   .add(Language.SPANISH,"Identificador único del documento indexado")
 *			 					   .add(Language.BASQUE,"[eu] Identificador Único del documento indexado")
 * 			  					   .add(Language.ENGLISH,"Document unique identifier"))
 *				.withNODescription()
 *				.forStringField()
 *				.searchEngine()
 *					.stored()
 *					.indexed().notTokenized()
 * </pre>
 */
@GwtIncompatible
abstract class FieldMetaDataBuilder 
    implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  as FieldMetaData conversion
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link TypeFieldMetaData} field as a {@link FieldMetaData} object
	 * @return
	 */
	public static <F extends FieldMetaData> FieldMetaDataBuilderAsFieldUsingHasTypeMetaDataStep<F> asFieldMetaData(final TypeFieldMetaData field) {
		return new FieldMetaDataBuilderAsFieldUsingHasTypeMetaDataStep<F>(field);
	}
	@SuppressWarnings("unchecked")
	@RequiredArgsConstructor(access=AccessLevel.MODULE)
	static final class FieldMetaDataBuilderAsFieldUsingHasTypeMetaDataStep<F extends FieldMetaData> {
		private final TypeFieldMetaData _field;
		
		public F using(final HasTypesMetaData hasTypesMetaData) {			
			// [1] create the field metadata
			FieldMetaData outFieldMetaData = null;
			if (CollectionUtils.hasData(_field.getFieldMetaData().polymorphicResolution())) {
				// it's a polymorphic type
				FieldMetaDataForPolymorphicType md = new FieldMetaDataForPolymorphicType(_field.getId(),
																						 _laguageTextsFor(_field.getFieldMetaData().alias()),
																						 _laguageTextsFor(_field.getFieldMetaData().description()),
																						 _field.getRawFieldType());
				Map<Class<? extends MetaDataDescribable>,Class<?>> resolutionMap = Maps.newHashMapWithExpectedSize(_field.getFieldMetaData().polymorphicResolution().length);
				for (PolymorphicFieldTypeResolve res : _field.getFieldMetaData().polymorphicResolution()) {
					resolutionMap.put(res.whenContainerType(),res.resolveTo());
				}
				md.setFieldDataTypeMap(resolutionMap);
				outFieldMetaData = md; 
			}
			else {
				// it's NOT a polymorphic type (the usual case)
				outFieldMetaData = FieldMetaDataBuilder.forId(_field.getId())
										   .withName(_laguageTextsFor(_field.getFieldMetaData().alias()))
										   .withDescription(_laguageTextsFor(_field.getFieldMetaData().description()))
										   .using(hasTypesMetaData)	
										   .of(_field.getContainerType().getType(),
											   _field.getFieldType());
			}
			// [2] set the search engine config
			outFieldMetaData.setSearchEngineIndexingConfig(FieldMetaDataSearchEngineIndexingConfig.forFieldTypeWithConfig(_field.getRawFieldType(),
																														  _field.getFieldMetaData().storage()));
			return (F)outFieldMetaData;
		}
	}
	private static LanguageTexts _laguageTextsFor(final DescInLang[] descs) {
		if (CollectionUtils.isNullOrEmpty(descs)) return null;
		LanguageTexts outLangTexts = new LanguageTextsMapBacked();
		for (DescInLang desc : descs) {
			outLangTexts.add(desc.language(),desc.value());
		}
		return outLangTexts;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static MetaDataConfigBuilderNameStep forId(final FieldID fieldId) {
		return new FieldMetaDataBuilder() { /* nothing */ }
						.new MetaDataConfigBuilderNameStep(fieldId);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MetaDataConfigBuilderNameStep {
		private final FieldID _fieldId;
		
		public MetaDataConfigBuilderDescriptionStep withName(final LanguageTexts name) {
			return new MetaDataConfigBuilderDescriptionStep(_fieldId,
															name);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MetaDataConfigBuilderDescriptionStep {
		private final FieldID _fieldId;
		private final LanguageTexts _name;
		
		public MetaDataConfigBuilderInspectorStep withDescription(final LanguageTexts description) {
			return new MetaDataConfigBuilderInspectorStep(_fieldId,
													 	   _name,description);
		}
		public MetaDataConfigBuilderInspectorStep withNODescription() {
			return new MetaDataConfigBuilderInspectorStep(_fieldId,
													 	  _name,null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MetaDataConfigBuilderInspectorStep {
		private final FieldID _fieldId;
		private final LanguageTexts _name;
		private final LanguageTexts _description;
		
		public MetaDataConfigBuilderTypeStep using(final HasTypesMetaData hasTypesMetaData) {
			return new MetaDataConfigBuilderTypeStep(_fieldId,
												 	 _name,_description,
												 	 hasTypesMetaData);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MetaDataConfigBuilderTypeStep {
		private final FieldID _fieldId;
		private final LanguageTexts _name;
		private final LanguageTexts _description;
		private final HasTypesMetaData _hasTypesMetaData;

		public <F extends FieldMetaData> F of(final Type fieldType) {
			return this.<F>of(null,		// the container type is not needed
						      fieldType);
		}
		@SuppressWarnings({"unchecked","cast"})
		public <F extends FieldMetaData> F of(final Type containerType,
											  final Type fieldType) {
			FieldMetaData fieldMetaData = null;
			
			Type theFieldType = fieldType;
			
			// [1]: Parameterized collection or map
			if (theFieldType instanceof ParameterizedType) {
				// NOTE: when a field type is a Collection / Map with a TypeVariable component, an exception is thrown 
				// ... maybe the concrete component type could be guess using something like:
				//	@Accessors(prefix="_")
				//	public static abstract class ClazzBase<T> {
				//		@Getter @Setter private Collection<T> _pCol;
				//	}
				//	@Accessors(prefix="_")
				//	public static class Clazz
				//				extends ClazzBase<Integer> {
				//		@Getter @Setter private Collection<Clazz> _col;
				//		@Getter @Setter private Map<String,Clazz> _map;
				//	}
				//	Class<?> paramType = TypeToken.of(Clazz.class)		// use guava's type token
				//			 					  .resolveType(ClazzBase.class.getTypeParameters()[0])
				//			 					  .getRawType();
				
				
	        	ParameterizedType pFieldType = (ParameterizedType)theFieldType;
	            Class<?> fieldClass = (Class<?>)pFieldType.getRawType();
	            if (ReflectionUtils.isImplementing(fieldClass,Collection.class)) {
					Type componentType = ((ParameterizedType)pFieldType).getActualTypeArguments()[0];	// the collection parameter
					if (componentType instanceof TypeVariable) {	// see note above
						//		public class MyType<T> {
						//			@Getter @Setter private Collection<T> _field;	<-- the collection's element type is NOT a class
						//		}
						throw new IllegalArgumentException(String.format("Cannot create a %s with a %s component type (check field of type %s at %s)",
																		 FieldMetaDataForCollection.class,TypeVariable.class.getSimpleName(),
																		 theFieldType,containerType));
					}
	            	fieldMetaData = new FieldMetaDataForCollection(_fieldId,
																   _name,_description,
																   new FieldMetaDataSearchEngineIndexingConfig(),
																   (Class<?>)componentType);		// could throw an Illegal argument exception if
	            } 
	            else if (ReflectionUtils.isImplementing(fieldClass,Map.class)) {
				    Type keyType = ((ParameterizedType)pFieldType).getActualTypeArguments()[0];
				    Type valueType = ((ParameterizedType)pFieldType).getActualTypeArguments()[1];
					if (keyType instanceof TypeVariable
					 || valueType instanceof TypeVariable) {	// see note above
						//		public class MyType<K,V> {
						//			@Getter @Setter private Map<K,V> _field;	<-- either Map's key or value are NOT a class
						//		}
						throw new IllegalArgumentException(String.format("Cannot create a %s with a %s key or value type (check field of type %s at %s)",
																		 FieldMetaDataForMap.class,TypeVariable.class.getSimpleName(),
																		 theFieldType,containerType));
					}
				    fieldMetaData = new FieldMetaDataForMap(_fieldId,
															_name,_description,
															new FieldMetaDataSearchEngineIndexingConfig(),
															(Class<?>)keyType,(Class<?>)valueType);
	            } 
	            else {
	            	// if it's NOT a parameterized Map or Collection, the raw type is tried in the NEXT section (2)
	            	theFieldType = fieldClass;
	            }
			}
			if (fieldMetaData != null) return (F)fieldMetaData;
			
			
			
			// [2] - not parameterized types
			if (theFieldType instanceof Class) {
				Class<?> fieldClass = (Class<?>)theFieldType;
			
				if (fieldClass == String.class) {
					fieldMetaData = new FieldMetaDataForString(_fieldId,
												  			   _name,_description,
												  			   new FieldMetaDataSearchEngineIndexingConfig());
				}
				else if (fieldClass == Integer.class || fieldClass == int.class) {
					fieldMetaData = new FieldMetaDataForInteger(_fieldId,
																_name,_description,
																FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == Long.class || fieldClass == long.class) {
					fieldMetaData = new FieldMetaDataForLong(_fieldId,
															 _name,_description,
															 FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == Double.class || fieldClass == double.class) {
					fieldMetaData = new FieldMetaDataForDouble(_fieldId,
															   _name,_description,
															   FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == Float.class || fieldClass == float.class) {
					fieldMetaData = new FieldMetaDataForFloat(_fieldId,
															  _name,_description,
															  FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
					fieldMetaData = new FieldMetaDataForBoolean(_fieldId,
															    _name,_description,
															    FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == Date.class) {
					fieldMetaData = new FieldMetaDataForDate(_fieldId,
														     _name,_description,
														     FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == Year.class) {
					fieldMetaData = new FieldMetaDataForYear(_fieldId,
													      	 _name,_description,
															 FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == MonthOfYear.class) {
					fieldMetaData = new FieldMetaDataForMonthOfYear(_fieldId,
												      		 		_name,_description,
												      		 		FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == DayOfMonth.class) {
					fieldMetaData = new FieldMetaDataForDayOfMonth(_fieldId,
												      		 	   _name,_description,
																   FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == Language.class) {	// BEWARE!!! do not move AFTER isEnum since Language is an enum and it'll be detected as an enum metadata
					fieldMetaData = new FieldMetaDataForLanguage(_fieldId,
															  	 _name,_description,
																 FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (fieldClass == LanguageTexts.class) {
					fieldMetaData = new FieldMetaDataForLanguageTexts(_fieldId,
																      _name,_description,
																      new FieldMetaDataSearchEngineIndexingConfig());
				}
				else if (fieldClass == Url.class) {
					fieldMetaData = new FieldMetaDataForUrl(_fieldId,
															_name,_description,
															FieldMetaDataSearchEngineIndexingConfig.notTokenizable());
				}
				else if (ReflectionUtils.isImplementing(fieldClass,IsPath.class)) {
					fieldMetaData = new FieldMetaDataForPath(_fieldId,
														 	 _name,_description,
															 FieldMetaDataSearchEngineIndexingConfig.notTokenizable(),
															 (Class<? extends IsPath>)fieldClass);
				}
				else if (ReflectionUtils.isImplementing(fieldClass,Summary.class)) {
					fieldMetaData = new FieldMetaDataForSummary(_fieldId,
																_name,_description,
																new FieldMetaDataSearchEngineIndexingConfig(),
																(Class<? extends Summary>)fieldClass);
				}
				// DO NOT MOVE TYPES BELOW THIS POINT!!!
				else if (fieldClass.isEnum()) {											// DO NOT MOVE!!!
					fieldMetaData = new FieldMetaDataForEnum(_fieldId,
															 _name,_description,
															 FieldMetaDataSearchEngineIndexingConfig.notTokenizable(),
															 (Class<? extends Enum<?>>)fieldClass);
				}
				else if (ReflectionUtils.isImplementing(fieldClass,OID.class)) {		// DO NOT MOVE!!!
					fieldMetaData = new FieldMetaDataForOID(_fieldId,
															_name,_description,
															FieldMetaDataSearchEngineIndexingConfig.notTokenizable(),
															(Class<? extends OID>)fieldClass);
				}
				else if (ReflectionUtils.isImplementing(fieldClass,CanBeRepresentedAsString.class)) {
					fieldMetaData = new FieldMetaDataForString(_fieldId,
															   _name,_description,
															   new FieldMetaDataSearchEngineIndexingConfig(),
															   (Class<? extends CanBeRepresentedAsString>)fieldClass);
				}
				else if (ReflectionUtils.isImplementing(fieldClass,MetaDataDescribable.class)) {
					TypeMetaData<? extends MetaDataDescribable> typeMetaData = _hasTypesMetaData.getTypeMetaDataFor((Class<? extends MetaDataDescribable>)fieldClass);
					fieldMetaData = new FieldMetaDataForDependentObject(_fieldId,
											   		   					_name,_description,
											   		   					new FieldMetaDataSearchEngineIndexingConfig(),
											   		   					fieldClass,
											   		   					typeMetaData.getFieldsMetaDataMap().values());
				}
				else if (fieldClass == Class.class) {	// java type
					fieldMetaData = new FieldMetaDataForJavaType(_fieldId,
												 				 _name,_description,
												 				 FieldMetaDataSearchEngineIndexingConfig.notTokenizable(),
												 				 (Class<?>)containerType);	// BEWARE!!!
				}
			}
			
			// [3] - Sanity check!
			if (fieldMetaData == null) throw new IllegalArgumentException(String.format("Could NOT build a %s type from %s",
																						 FieldMetaData.class,theFieldType));
 			return (F)fieldMetaData;									
 		}
		public PolimorphicFieldMetaDataConfigBuilderTypeStep1 forPolymorphicField(final Class<?> baseType) {
			return new FieldMetaDataBuilder() {/* nothing */}
							.new PolimorphicFieldMetaDataConfigBuilderTypeStep1(new FieldMetaDataForPolymorphicType(_fieldId,
												 	   													  			_name,_description,
												 	   													  			new FieldMetaDataSearchEngineIndexingConfig(),
												 	   													  			baseType));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForCollection,
												MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForCollection>> forCollectionField(final Class<?> componentType) {
			FieldMetaDataForCollection fieldMetaData = this.of(componentType); 
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForCollection,
														MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForCollection>>(fieldMetaData,
																	 														      	 new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForCollection>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMap,
												MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForMap>> forMapField(final Class<?> keyType,final Class<?> valueType) {
			FieldMetaDataForMap fieldMetaData = new FieldMetaDataForMap(_fieldId,
																	    _name,_description,
																	    new FieldMetaDataSearchEngineIndexingConfig(),
																	    keyType,valueType);
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMap,
														MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForMap>>(fieldMetaData,
																	 														  new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForMap>(fieldMetaData));
			
		}
		public FieldMetaDataForDependentObject forDependentObject(final Class<?> objType,
																  final Set<FieldMetaData> childMetaData) {
			return new FieldMetaDataForDependentObject(_fieldId,
											   		   _name,_description,
											   		   new FieldMetaDataSearchEngineIndexingConfig(),
											   		   objType,
											   		   childMetaData);	
		}
		public FieldMetaDataForDependentObject forDependentObject(final Class<?> objType,
																  final FieldMetaData... childMetaData) {
			return new FieldMetaDataForDependentObject(_fieldId,
											   		   _name,_description,
											   		   new FieldMetaDataSearchEngineIndexingConfig(),
											   		   objType,
											   		   Sets.newHashSet(childMetaData));	
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForJavaType,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForJavaType>> forJavaTypeField(final Class<?> type) {
			FieldMetaDataForJavaType fieldMetaData = new FieldMetaDataForJavaType(_fieldId,
												 				 _name,_description,
												 				 FieldMetaDataSearchEngineIndexingConfig.notTokenizable(),
												 				 type);
			// java types can be indexed always NOT tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForJavaType,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForJavaType>>(fieldMetaData,
																	 														      	   new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForJavaType>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
											    MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>> forStringField() {
			FieldMetaDataForString fieldMetaData = this.of(String.class);
			// strings can be indexed tokenized or not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
													    MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>>(fieldMetaData,
																					 											 new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
											    MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>> forStringField(final Class<? extends CanBeRepresentedAsString> type) {
			FieldMetaDataForString fieldMetaData = this.of(type);
			// strings can be indexed tokenized or not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForString,
														MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>>(fieldMetaData,
																					 											 new MetaDataConfigBuilderIndexingCfgTokenizableStep<FieldMetaDataForString>(fieldMetaData));			
		}
		public MetaDataConfigBuilderIndexingAlwaysStoredCfg<MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForOID>> forOIDField(final Class<? extends OID> oidType) {
			FieldMetaDataForOID fieldMetaData = this.of(oidType);
			// oids are always stored and can be indexed not tokenized
			fieldMetaData.getSearchEngineIndexingConfig()
						 .setStored(true);
			return new MetaDataConfigBuilderIndexingAlwaysStoredCfg<MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForOID>>(new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForOID>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForInteger,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForInteger>> forIntegerField() {
			FieldMetaDataForInteger fieldMetaData = this.of(Integer.class);
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForInteger,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForInteger>>(fieldMetaData,
																	 														     	 new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForInteger>(fieldMetaData));			
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLong,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLong>> forLongField() {
			FieldMetaDataForLong fieldMetaData = this.of(Long.class);
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLong,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLong>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLong>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDouble,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDouble>> forDoubleField() {
			FieldMetaDataForDouble fieldMetaData = this.of(Double.class);
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDouble,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDouble>>(fieldMetaData,
																	 														        new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDouble>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForFloat,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForFloat>> forFloatField() {
			FieldMetaDataForFloat fieldMetaData = this.of(Float.class);
			// numbers can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForFloat,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForFloat>>(fieldMetaData,
																	 														       new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForFloat>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDate,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDate>> forDateField() {
			FieldMetaDataForDate fieldMetaData = this.of(Date.class);
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDate,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDate>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDate>(fieldMetaData));			
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForYear,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForYear>> forYearField() {
			FieldMetaDataForYear fieldMetaData = this.of(Year.class);
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForYear,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForYear>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForYear>(fieldMetaData));			
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMonthOfYear,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForMonthOfYear>> forMonthOfYearField() {
			FieldMetaDataForMonthOfYear fieldMetaData = this.of(MonthOfYear.class);
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForMonthOfYear,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForMonthOfYear>>(fieldMetaData,
																	 														      		 new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForMonthOfYear>(fieldMetaData));			
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDayOfMonth,
											    MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDayOfMonth>> forDayOfMonthField() {
			FieldMetaDataForDayOfMonth fieldMetaData = this.of(DayOfMonth.class);
			// dates can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForDayOfMonth,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDayOfMonth>>(fieldMetaData,
																	 														      		new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForDayOfMonth>(fieldMetaData));			
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForBoolean,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForBoolean>> forBooleanField() {
			FieldMetaDataForBoolean fieldMetaData = this.of(Boolean.class);
			// booleans can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForBoolean,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForBoolean>>(fieldMetaData,
																	 														         new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForBoolean>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForEnum,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForEnum>> forEnumField(final Class<? extends Enum<?>> enumType) {
			FieldMetaDataForEnum fieldMetaData = this.of(enumType);
			// enums can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForEnum,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForEnum>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForEnum>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguage,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLanguage>> forLanguageField() {
			FieldMetaDataForLanguage fieldMetaData = this.of(Language.class);
			// language can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguage,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLanguage>>(fieldMetaData,
																	 														      	  new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForLanguage>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForPath,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForPath>> forPathField(final Class<? extends IsPath> pathType) {
			FieldMetaDataForPath fieldMetaData = this.of(pathType);
			// paths can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForPath,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForPath>>(fieldMetaData,
																	 														      new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForPath>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForUrl,
												MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForUrl>> forURLField() {
			FieldMetaDataForUrl fieldMetaData = this.of(Url.class);
			// urls can be indexed not tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForUrl,
														MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForUrl>>(fieldMetaData,
																	 														     new MetaDataConfigBuilderIndexingCfgNotTokenizableStep<FieldMetaDataForUrl>(fieldMetaData));			
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForSummary,
												MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForSummary>> forSummaryField(final Class<? extends Summary> summaryType) {
			FieldMetaDataForSummary fieldMetaData = this.of(summaryType);
			// summaries can be indexed always tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForSummary,
														MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForSummary>>(fieldMetaData,
																	 														      	  new MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForSummary>(fieldMetaData));
		}
		public MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguageTexts,
												MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForLanguageTexts>> forLanguageTextsField() {
			FieldMetaDataForLanguageTexts fieldMetaData = this.of(LanguageTexts.class);
			// language texts can be indexed always tokenized
			return new MetaDataConfigBuilderIndexingCfg<FieldMetaDataForLanguageTexts,
														MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForLanguageTexts>>(fieldMetaData,
																	 														      	  		new MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<FieldMetaDataForLanguageTexts>(fieldMetaData));			
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class PolimorphicFieldMetaDataConfigBuilderTypeStep1 {
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
	public final class PolimorphicFieldMetaDataConfigBuilderTypeStep2 {
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
	public final class MetaDataConfigBuilderIndexingCfgAll<F extends FieldMetaData> {
		private final F _fieldMetaDataCfg;
		
		public F searchEngine(final FieldMetaDataSearchEngineIndexingConfig searchEngineCfg) {
			_fieldMetaDataCfg.setSearchEngineIndexingConfig(searchEngineCfg);
			return _fieldMetaDataCfg;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MetaDataConfigBuilderIndexingCfg<F extends FieldMetaData,
											            BUILDER_NEXT_STEP> {
		private final F _fieldMetaDataCfg;
		private final BUILDER_NEXT_STEP _builderNextStep;
		
		public MetaDataConfigBuilderIndexingCfgStoreStep<F,BUILDER_NEXT_STEP> searchEngine() {
			return new MetaDataConfigBuilderIndexingCfgStoreStep<F,BUILDER_NEXT_STEP>(_fieldMetaDataCfg,
																					  _builderNextStep);
		}
		public F searchEngine(final FieldMetaDataSearchEngineIndexingConfig cfg) {
			_fieldMetaDataCfg.setSearchEngineIndexingConfig(cfg);
			return _fieldMetaDataCfg;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MetaDataConfigBuilderIndexingAlwaysStoredCfg<BUILDER_NEXT_STEP> {
		private final BUILDER_NEXT_STEP _builderNextStep;
		
		public BUILDER_NEXT_STEP searchEngine() {
			return _builderNextStep;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MetaDataConfigBuilderIndexingCfgStoreStep<F extends FieldMetaData,
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
	public final class MetaDataConfigBuilderIndexingCfgAlwaysTokenizedStep<F extends FieldMetaData> {
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
	public final class MetaDataConfigBuilderIndexingCfgNotTokenizableStep<F extends FieldMetaData> {
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
	public final class MetaDataConfigBuilderIndexingCfgTokenizableStep<F extends FieldMetaData> {
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
	public final class MetaDataConfigBuilderIndexingCfgTokenizeStep<F extends FieldMetaData> {
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
	public final class MetaDataConfigBuilderIndexingCfgBoostingStep<F extends FieldMetaData> {
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
