package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="metaDataConfigForLanguageTextsField")
@GwtIncompatible
public class FieldMetaDataForLanguageTexts
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = -250940472843050277L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForLanguageTexts() {
		super();
	}
	public FieldMetaDataForLanguageTexts(final FieldMetaDataForLanguageTexts other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());
	}
	public FieldMetaDataForLanguageTexts(final FieldID fieldId,
								   		 final LanguageTexts name,final LanguageTexts description,
								   		 final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  LanguageTexts.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof LanguageTexts;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,_dataType.getSimpleName(),value.getClass())); 
	}
}
