package r01f.model.metadata;


import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.Year;

@MarshallType(as="metaDataConfigForYearField")
@GwtIncompatible
public class FieldMetaDataForYear
	 extends FieldMetaDataBase {


	private static final long serialVersionUID = 2611029676811826731L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForYear() {
		super();
	}
	public FieldMetaDataForYear(final FieldMetaDataForYear other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());	
	}
	public FieldMetaDataForYear(final FieldID fieldId,
								final LanguageTexts name,final LanguageTexts description,
								final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  Year.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof Year;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,Year.class.getSimpleName(),value.getClass())); 
	}
}
