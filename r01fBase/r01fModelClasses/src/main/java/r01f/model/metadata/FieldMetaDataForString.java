package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;

@MarshallType(as="metaDataConfigForStringField")
@GwtIncompatible
public class FieldMetaDataForString
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 6480065284988093130L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForString() {
		super();
	}
	public FieldMetaDataForString(final FieldMetaDataForString other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());
		if (other.getDataType() != String.class) _dataType = other.getDataType();
	}
	public FieldMetaDataForString(final FieldID fieldId,
								  final LanguageTexts name,final LanguageTexts description,
								  final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  String.class);
	}
	public FieldMetaDataForString(final FieldID fieldId,
								  final LanguageTexts name,final LanguageTexts description,
								  final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
								  final Class<? extends CanBeRepresentedAsString> stringType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  stringType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof String 
						  || CanBeRepresentedAsString.class.isAssignableFrom(value.getClass());
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} or {} FIELD (the provided value it's a {} type)",
																			   _fieldId,String.class.getSimpleName(),CanBeRepresentedAsString.class.getSimpleName(),value.getClass())); 
	}
}
