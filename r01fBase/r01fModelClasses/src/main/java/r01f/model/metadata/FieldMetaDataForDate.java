package r01f.model.metadata;


import java.util.Date;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="metaDataConfigForDateField")
@GwtIncompatible
public class FieldMetaDataForDate
	 extends FieldMetaDataBase {


	private static final long serialVersionUID = -7664178115984850510L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForDate() {
		super();
	}
	public FieldMetaDataForDate(final FieldMetaDataForDate other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());	
	}
	public FieldMetaDataForDate(final FieldID fieldId,
								final LanguageTexts name,final LanguageTexts description,
								final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  Date.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof Date;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,Date.class.getSimpleName(),value.getClass())); 
	}
}
