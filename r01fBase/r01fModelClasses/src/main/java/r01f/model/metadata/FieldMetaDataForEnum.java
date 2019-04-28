package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="metaDataConfigForEnumField")
@GwtIncompatible
public class FieldMetaDataForEnum
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = -9030568267462067246L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForEnum() {
		super();
	}
	@SuppressWarnings("unchecked")
	public FieldMetaDataForEnum(final FieldMetaDataForEnum other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 (Class<? extends Enum<?>>)other.getDataType());
	}
	public FieldMetaDataForEnum(final FieldID fieldId,
							    final LanguageTexts name,final LanguageTexts description,
							    final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
								final Class<? extends Enum<?>> enumType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  enumType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof Enum 
				          && value.getClass().equals(_dataType);
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,_dataType.getSimpleName(),value.getClass())); 
	}
}
