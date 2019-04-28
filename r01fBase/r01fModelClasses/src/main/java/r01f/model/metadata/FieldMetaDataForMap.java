package r01f.model.metadata;

import java.util.Map;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="metaDataConfigForMapField")
@GwtIncompatible
@Accessors(prefix="_")
public class FieldMetaDataForMap
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 1253152050874032334L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="keyComponentsType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<?> _keyComponentsType;
	
	@MarshallField(as="valueComponentsType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<?> _valueComponentsType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForMap() {
		super();
	}
	public FieldMetaDataForMap(final FieldMetaDataForMap other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 other.getKeyComponentsType(),other.getValueComponentsType());
	}
	public FieldMetaDataForMap(final FieldID fieldId,
							   final LanguageTexts name,final LanguageTexts description,
							   final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
							   final Class<?> keyComponentsType,final Class<?> valueComponentsType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  Map.class);
		_keyComponentsType = keyComponentsType;
		_valueComponentsType = valueComponentsType;	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(Object value) {
		boolean acceptable = value instanceof Map;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,Map.class.getSimpleName(),value.getClass())); 
	}
}
