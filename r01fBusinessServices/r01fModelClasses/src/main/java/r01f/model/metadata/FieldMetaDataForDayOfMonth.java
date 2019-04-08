package r01f.model.metadata;


import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.DayOfMonth;

@MarshallType(as="metaDataConfigForDayOfMonthField")
@GwtIncompatible
public class FieldMetaDataForDayOfMonth
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = -2790839169331622479L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForDayOfMonth() {
		super();
	}
	public FieldMetaDataForDayOfMonth(final FieldMetaDataForDayOfMonth other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig());	
	}
	public FieldMetaDataForDayOfMonth(final FieldID fieldId,
									  final LanguageTexts name,final LanguageTexts description,
									  final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  DayOfMonth.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof DayOfMonth;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,DayOfMonth.class.getSimpleName(),value.getClass())); 
	}
}
