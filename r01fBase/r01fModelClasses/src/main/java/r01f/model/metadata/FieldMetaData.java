package r01f.model.metadata;

import java.io.Serializable;

import r01f.locale.LanguageTexts;


/**
 * Models a MetaData configuration
 * A MetaData can be:
 * <ul>
 * 		<li>A final {@link FieldID} of certain data types
 * 			<ul>
 * 				<li>{@link FieldMetaDataForDate}</li>
 * 				<li>{@link FieldMetaDataForFloat}</li>
 * 				<li>{@link FieldMetaDataForInteger}</li>
 * 				<li>{@link FieldMetaDataForLanguage}</li>
 * 				<li>{@link FieldMetaDataForLong}</li>
 * 				<li>{@link FieldMetaDataForOID}</li>
 * 				<li>{@link FieldMetaDataForString}</li>
 * 				<li>{@link FieldMetaDataForSummary}</li>
 * 			</ul>
 * 		</li>
 * 		<li>An object composed by child {@link FieldMetaData} that in turn might be final IndexableFields or complex objects</li>
 * </ul>
 */
public interface FieldMetaData 
	     extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldID getFieldId();
	public void setFieldId(final FieldID metadataId);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public Class<?> getDataType();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldID getIndexableFieldId();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageTexts getName();
	public void setName(final LanguageTexts names);
	
	public LanguageTexts getDescription();
	public void setDescription(final LanguageTexts description);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataSearchEngineIndexingConfig getSearchEngineIndexingConfig();
	public void setSearchEngineIndexingConfig(final FieldMetaDataSearchEngineIndexingConfig cfg);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void checkIfIsAcceptableValueOrThrow(Object value);
	
	public <F extends FieldMetaData> F as(Class<F> fieldMetaDataType);
	
	public boolean isStringField();
	public boolean isBooleanField();
	public boolean isNumberField();
	public boolean isIntegerField();
	public boolean isLongField();
	public boolean isDoubleField();
	public boolean isFloatField();
	public boolean isDateField();
	public boolean isOIDField();
	public boolean isEnumField();
	public boolean isLanguageField();
	public boolean isPathField();
	public boolean isUrlField();
	public boolean isLanguageTextsField();
	public boolean isCollectionField();
	public boolean isMapField();
	public boolean isSummaryField();
	public boolean isPolymorphicField();
	public boolean isJavaTypeField();
	public boolean isDependentObjectField();
	
	public boolean hasMultipleDimensions();
	public boolean isLanguageDependent();
	
	public boolean hasMultipleValues();
}
