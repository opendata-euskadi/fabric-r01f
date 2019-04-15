package r01f.persistence.index.document;

import java.sql.Date;

import lombok.NoArgsConstructor;
import r01f.enums.EnumWithCode;
import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.FieldMetaDataForCollection;
import r01f.model.metadata.FieldMetaDataForDependentObject;
import r01f.model.metadata.FieldMetaDataForEnum;
import r01f.model.metadata.FieldMetaDataForMap;
import r01f.model.metadata.FieldMetaDataForPolymorphicType;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.IsPath;
import r01f.types.url.Url;
import r01f.util.enums.Enums;
import r01f.util.types.Strings;

/**
 * A standard index document field
 */
@MarshallType(as="indexFieldType")
@NoArgsConstructor
public enum IndexDocumentStandardFieldType  
 implements IndexDocumentFieldType {
	Double,
	Float,
	Int,
	Long,
	String,
	Text;
	
	/**
	 * Guess the {@link IndexDocumentStandardFieldType} from a java type
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static IndexDocumentStandardFieldType fromType(final Class<?> type) {
		IndexDocumentStandardFieldType outType = null;
		if (type == String.class || ReflectionUtils.isImplementing(type,CanBeRepresentedAsString.class)) {
			outType = String;
		} else if (type == Boolean.class) {
			outType = Int;
		} else if (type == Integer.class) {
			outType = Int;
		} else if (type == Long.class) {
			outType = Long;
		} else if (type == Double.class) {
			outType = Double;
		} else if (type == Float.class) {
			outType = Float;
		} else if (type == Date.class) {
			outType = Long;
		} else if (ReflectionUtils.isImplementing(type,OID.class)) {
			outType = String;
		} else if (ReflectionUtils.isImplementing(type,Language.class)) {
			outType = String;
		} else if (ReflectionUtils.isImplementing(type,Enum.class)) {
			outType = _fieldTypeForEnum((Class<? extends Enum<?>>)type);
		} else if (ReflectionUtils.isImplementing(type,Url.class)) {
			outType = String;
		} else if (ReflectionUtils.isImplementing(type,IsPath.class)) {
			outType = String;
		} else { 
			throw new IllegalArgumentException(Throwables.message("The provided type ({}) is NOT suitable for an index document's field; available options are {} enum values",
																  type,IndexDocumentStandardFieldType.class));
		}
		return outType;
	}
	/**
	 * Guess the {@link IndexDocumentStandardFieldType} from a {@link FieldMetaData} 
	 * @param fieldMetaData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static IndexDocumentStandardFieldType fromFieldMetaDataConfig(final FieldMetaData fieldMetaData) {
		IndexDocumentStandardFieldType outType = null;
		if (fieldMetaData.isStringField()) {
			outType = String;
		} else if (fieldMetaData.isBooleanField()) {
			outType = Int;
		} else if (fieldMetaData.isIntegerField()) {
			outType = Int;
		} else if (fieldMetaData.isLongField()) {
			outType = Long;
		} else if (fieldMetaData.isDoubleField()) {
			outType = Double;
	    } else if (fieldMetaData.isFloatField()) {
			outType = Float;
	    } else if (fieldMetaData.isDateField()) {
	    	outType = Long;
	    } else if (fieldMetaData.isOIDField()) {
	    	outType = String;
	    } else if (fieldMetaData.isLanguageField()) {
	    	outType = String;
	    } else if (fieldMetaData.isEnumField()) {
	    	FieldMetaDataForEnum enumField = (FieldMetaDataForEnum)fieldMetaData;
	    	outType = _fieldTypeForEnum((Class<? extends Enum<?>>)enumField.getDataType());
	    } else if (fieldMetaData.isPathField()) {
	    	outType = String;
	    } else if (fieldMetaData.isUrlField()) {
	    	outType = String;
	    } else if (fieldMetaData.isJavaTypeField()) {
	    	outType = String;
	    } else if (fieldMetaData.isSummaryField() || fieldMetaData.isLanguageTextsField()) {
	    	if (fieldMetaData.getSearchEngineIndexingConfig().isStored()) {
	    		outType = String;
	    	} else {
	    		outType = Text;
	    	}
	    } else if (fieldMetaData.isDependentObjectField()) {
	    	// the type is the one for the Dependent type object
	    	FieldMetaDataForDependentObject objField = (FieldMetaDataForDependentObject)fieldMetaData;
	    	outType = IndexDocumentStandardFieldType.fromType(objField.getDataType());
	    } else if (fieldMetaData.isCollectionField()) {
	    	// the type is the one for the collection elements
	    	FieldMetaDataForCollection colField = (FieldMetaDataForCollection)fieldMetaData;
	    	outType = IndexDocumentStandardFieldType.fromType(colField.getComponentsType());
	    } else if (fieldMetaData.isMapField()) {
	    	// the type is the one for the value types (the key will be the field dimension)
	    	FieldMetaDataForMap mapField = (FieldMetaDataForMap)fieldMetaData;
	    	outType = IndexDocumentStandardFieldType.fromType(mapField.getValueComponentsType());
	    } else if (fieldMetaData.isPolymorphicField()) {
	    	// the type is the one for the base type
	    	FieldMetaDataForPolymorphicType polyField = (FieldMetaDataForPolymorphicType)fieldMetaData;
	    	outType = IndexDocumentStandardFieldType.fromType(polyField.getDataType());
	    } else { 
			throw new IllegalArgumentException(Throwables.message("The provided field metadata {} of type {} is NOT suitable for an index document's field; available options are {} enum values",
																  fieldMetaData.getFieldId(),fieldMetaData.getClass(),IndexDocumentStandardFieldType.class));
		}	
		return outType; 
	}
	/**
	 * Guess the {@link IndexDocumentStandardFieldType} from the enum type
	 * If it's a {@link EnumWithCode} type, the {@link IndexDocumentStandardFieldType} will be the one for the enum code
	 * otherwise the {@link IndexDocumentStandardFieldType} will be a String
	 * @param enumType
	 * @return
	 */
	private static IndexDocumentStandardFieldType _fieldTypeForEnum(final Class<? extends Enum<?>> enumType) {
		IndexDocumentStandardFieldType outType = null;
		if (ReflectionUtils.isImplementing(enumType,Language.class)) {
			outType = String;
		} else if (ReflectionUtils.isImplementing(enumType,EnumWithCode.class)) {
			Class<?> codeType = Enums.guessEnumWithCodeCodeType(enumType);
			if (codeType == Integer.class) {
				outType = Int;
			} 
			else if (codeType == Long.class) {
				outType = Long;
			}
			else if (codeType == Character.class) {
				outType = String;
			}
			else if (codeType == String.class) {
				outType = String;
			}
			else {
				throw new IllegalStateException(Strings.customized("{} with a {} type code is not supported!!",
																   EnumWithCode.class,codeType));
			}
		} else {
			outType = String;
		}
		return outType;
	}
}
