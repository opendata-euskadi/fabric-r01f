package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.summary.LangDependentSummary;
import r01f.types.summary.LangIndependentSummary;
import r01f.types.summary.Summary;

@MarshallType(as="metaDataConfigForSummaryField")
@GwtIncompatible
public class FieldMetaDataForSummary
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = -7719936429450922842L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForSummary() {
		super();
	}
	@SuppressWarnings("unchecked")
	public FieldMetaDataForSummary(final FieldMetaDataForSummary other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 (Class<? extends Summary>)other.getDataType());
	}
	public FieldMetaDataForSummary(final FieldID fieldId,
								   final LanguageTexts name,final LanguageTexts description,
								   final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
								   final Class<? extends Summary> summaryType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  summaryType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isForLangDependentSummary() {
		return LangDependentSummary.class.isAssignableFrom(_dataType);
	}
	public boolean isForLangIndependentSummary() {
		return LangIndependentSummary.class.isAssignableFrom(_dataType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof Summary 
						  && _dataType.isAssignableFrom(value.getClass());
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,_dataType.getSimpleName(),value.getClass())); 
	}
}
