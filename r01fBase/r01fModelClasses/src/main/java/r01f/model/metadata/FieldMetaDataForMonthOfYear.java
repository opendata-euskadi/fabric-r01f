package r01f.model.metadata;


import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.MonthOfYear;

@MarshallType(as="metaDataConfigForMonthOfYearField")
@GwtIncompatible
public class FieldMetaDataForMonthOfYear
	 extends FieldMetaDataBase {


	private static final long serialVersionUID = 1871509028202773302L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForMonthOfYear() {
		super();
	}
	public FieldMetaDataForMonthOfYear(final FieldMetaDataForMonthOfYear other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());	
	}
	public FieldMetaDataForMonthOfYear(final FieldID fieldId,
									   final LanguageTexts name,final LanguageTexts description,
									   final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  MonthOfYear.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof MonthOfYear;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,MonthOfYear.class.getSimpleName(),value.getClass())); 
	}
}
