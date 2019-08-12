package r01f.model.metadata;

import java.lang.reflect.Type;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.reflect.TypeToken;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.patterns.Memoized;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible
@Accessors(prefix="_")
@RequiredArgsConstructor
public class TypeFieldMetaData 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final TypeMetaData<? extends MetaDataDescribable> _containerType;
	@Getter private final FieldID _id;
	@Getter private final MetaDataForField _fieldMetaData;
	@Getter private final Type _fieldType;
	@Getter private final TypeMetaData<? extends MetaDataDescribable> _fieldTypeMetaData; 		// if the type of this field is another type with metadata
	
	public Class<?> getRawFieldType() {
		return TypeToken.of(_fieldType)
						.getRawType();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  as FieldMetaData conversion
/////////////////////////////////////////////////////////////////////////////////////////
	private transient Memoized<? extends FieldMetaData> _asFieldMetaData = new Memoized<FieldMetaData>() {
																					@Override
																					public FieldMetaData supply() {
																						HasTypesMetaData hasTypesMetaData = TypeMetaDataInspector.singleton();
																						FieldMetaData outFieldMetaData = FieldMetaDataBuilder.asFieldMetaData(TypeFieldMetaData.this)
																													   			   .using(hasTypesMetaData);
																						return outFieldMetaData;
																					}
																		 	};
	/**
	 * Returns this field as a {@link FieldMetaData} object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <F extends FieldMetaData> F asFieldMetaData() {
		return (F)_asFieldMetaData.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the field is indexed
	 */ 
	public boolean isIndexed() {
		return _fieldMetaData != null
			&& _fieldMetaData.storage() != null
			&& _fieldMetaData.storage().indexed();
	}
	public FieldID getIndexableFieldId() {
		if (!this.isIndexed()) throw new UnsupportedOperationException(String.format("Field with id=%s of type %s is NOT indexable!",
																					 _id,_containerType.getType()));
		return _id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tField").append(" id=").append(_id)
							.append(" type=").append(_fieldType)
							.append(_fieldTypeMetaData != null ? " (has metadata)" : "")
							.append("\n");
		
		if (_fieldMetaData != null) {
			sb.append("\t\tMetaData").append("\n");
			if (CollectionUtils.hasData(_fieldMetaData.alias())) {
				sb.append("\t\tAlias:\n");
				for (DescInLang desc : _fieldMetaData.alias()) {
					sb.append("\t\t\t").append(desc.language()).append("=").append(desc.value()).append("\n");
				}
			}
			if (CollectionUtils.hasData(_fieldMetaData.description())) {
				sb.append("\t\tDescription:\n");
				for (DescInLang desc : _fieldMetaData.description()) {
					sb.append("\t\t\t").append(desc.language()).append("=").append(desc.value()).append("\n");
				}
			}
			if (_fieldMetaData.storage() != null) {
				sb.append("\t\tStorage:\n");
				sb.append("\t\t\t-  Indexed: ").append(_fieldMetaData.storage().indexed()).append("\n");
				sb.append("\t\t\t-   Stored: ").append(_fieldMetaData.storage().stored()).append("\n");
				sb.append("\t\t\t-Tokenized: ").append(_fieldMetaData.storage().tokenized()).append("\n");
				sb.append("\t\t\t- Boosting: ").append(_fieldMetaData.storage().boosting()).append("\n");
			}
		}
		return sb;
	}
}
