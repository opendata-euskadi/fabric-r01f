package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Range;

@MarshallType(as="metaDataConfigForRangeField")
@GwtIncompatible
@Accessors(prefix="_")
public class FieldMetaDataForRange
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = -8019257197049447852L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="rangeDataType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<? extends Comparable<?>> _rangeDataType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForRange() {
		super();
	}
	@SuppressWarnings("unchecked")
	public FieldMetaDataForRange(final FieldMetaDataForRange other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 (Class<? extends Comparable<?>>)other.getDataType());
	}
	public FieldMetaDataForRange(final FieldID fieldId,
								 final LanguageTexts name,final LanguageTexts description,
								 final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
								 final Class<? extends Comparable<?>> rangeDataType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  Range.class);
		_rangeDataType = rangeDataType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof Range;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,Range.class.getSimpleName(),value.getClass().getSimpleName()));
		Range<?> rangeValue = (Range<?>)value;
		acceptable = rangeValue.getDataType().equals(_rangeDataType);
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {}<{}> FIELD (the provided value it's a {}<{}> type)",
																			   _fieldId,Range.class.getSimpleName(),_rangeDataType,value.getClass().getSimpleName(),rangeValue.getDataType())); 
	}
}
