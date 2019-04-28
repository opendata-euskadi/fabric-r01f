package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="metaDataConfigForFloatField")
@GwtIncompatible
public class FieldMetaDataForFloat
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 2346548292276365808L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForFloat() {
		super();
	}
	public FieldMetaDataForFloat(final FieldMetaDataForFloat other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());
	}
	public FieldMetaDataForFloat(final FieldID fieldId,
								 final LanguageTexts name,final LanguageTexts description,
								 final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  Float.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof Float;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,Float.class.getSimpleName(),value.getClass())); 
	}
}
