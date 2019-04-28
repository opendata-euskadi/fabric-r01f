package r01f.model.metadata;

import java.util.Collection;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="metaDataConfigForCollectionField")
@GwtIncompatible
@Accessors(prefix="_")
public class FieldMetaDataForCollection
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 1253152050874032334L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="componentsType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<?> _componentsType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForCollection() {
		super();
	}
	public FieldMetaDataForCollection(final FieldMetaDataForCollection other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 other.getComponentsType());
	}
	public FieldMetaDataForCollection(final FieldID fieldId,
									  final LanguageTexts name,final LanguageTexts description,
									  final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
									  final Class<?> collectionComponentsType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  Collection.class);
		_componentsType = collectionComponentsType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		boolean acceptable = value instanceof Collection;
		if (!acceptable) throw new IllegalArgumentException(Throwables.message("The metaData {} is NOT defined as a {} FIELD (the provided value it's a {} type)",
																			   _fieldId,Collection.class.getSimpleName(),value.getClass())); 
	}
}
