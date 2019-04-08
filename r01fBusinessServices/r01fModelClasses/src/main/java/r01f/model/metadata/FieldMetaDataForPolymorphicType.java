package r01f.model.metadata;

import java.util.Map;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * This field metadata config is used when the field's type depends upon the model object's type
 * For example if there are two model objects that are almost the same except for the oid field
 * that depends on the model object type:
 * <pre class='brush:java'>
 * 		// A base metadata config
 * 		public class MetaDataConfigBase 
 * 			 extends R01MCommonMetaDataConfigForMyModelObjectBase {
 * 			...some common metadata config...
 * 
 * 			// type-dependent methods
 * 			public abstract MetaDataConfig getOidFieldMetaDataConfig();
 * 		}
 * 		public class MetaDataConfigForMyModelObjectX
 * 			 extends R01MCommonMetaDataConfigForMyModelObjectBase {
 * 			public MetaDataConfig getOidFieldMetaDataConfig() {
 *				return MetaDataConfigBuilder.forId(MetaDataConfigUtil.idFor("r01","myModelObject","oid"))
 *			 				  				.withName(...).finish())
 *							  				.withNODescription()
 *							  				.forOIDField(ModelObjectXOID.class);	// the oid type depends on the model object's type
 * 			}
 * 		}
 * 		public class MetaDataConfigForMyModelObjectY
 * 			 extends R01MCommonMetaDataConfigForMyModelObjectBase {
 * 			public MetaDataConfig getOidFieldMetaDataConfig() {
 *				return MetaDataConfigBuilder.forId(MetaDataConfigUtil.idFor("r01","myModelObject","oid"))
 *			 				  				.withName(...).finish())
 *							  				.withNODescription()
 *							  				.forOIDField(ModelObjectYOID.class);	// the oid type depends on the model object's type
 * 			}
 * 		}
 * </pre>
 * For a field like the oid field whose type is ModelObjectXOID when the model object is MyModelObjectX
 * and ModelObjectYOID when the model object is MyModelObjectY
 * <pre class='brush:java'>
 * 		public class MetaDataConfigForMyModelObject
 * 			 extends R01MCommonMetaDataConfigForMyModelObjectBase {
 * 			public MetaDataConfig getFieldXMetaDataConfig() {
 *				return MetaDataConfigBuilder.forId(MetaDataConfigUtil.idFor("r01","myModelObject","oid"))
 *			 				  				.withName(...).finish())
 *							  				.withNODescription()
 *							  				.forPolymorphicField()
 *							  						.forModelObjectType(MyModelObjectX.class).use(ModelObjectXOID.class)
 * 							  						.forModelObjectType(MyModelObjectY.class).use(ModelObjectYOID.class)
 *							  						.build();
 * 			}
 * 		} 
 * </pre>
 */
@MarshallType(as="metaDataConfigForPolymorphicField")
@GwtIncompatible
@Accessors(prefix="_")
public class FieldMetaDataForPolymorphicType
	 extends FieldMetaDataBase {

	private static final long serialVersionUID = 8528283170545110337L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Links a model object type with the type to be used for the metadata
	 * For example:
	 * 		When model object type is X, the meta-data's type is MDType1
	 * 		When model object type is Y, the meta-data's tyep is MDType2 
	 */
	@Getter @Setter private Map<Class<? extends MetaDataDescribable>,Class<?>> _fieldDataTypeMap = Maps.newHashMap();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FieldMetaDataForPolymorphicType() {
		super();
	}
	public FieldMetaDataForPolymorphicType(final FieldMetaDataForPolymorphicType other) {
		this(other.getFieldId(),
			 other.getName(),other.getDescription(),
			 other.getSearchEngineIndexingConfig(),
			 other.getDataType());
	}
	public FieldMetaDataForPolymorphicType(final FieldID fieldId,
									 	   final LanguageTexts name,final LanguageTexts description,
									 	   final FieldMetaDataSearchEngineIndexingConfig searchEngineIndexingConfig,
									 	   final Class<?> baseType) {
		super(fieldId,
			  name,description,
			  searchEngineIndexingConfig,
			  baseType);
	}
	public FieldMetaDataForPolymorphicType(final FieldID fieldId,
									 	   final LanguageTexts name,final LanguageTexts description,
									 	   final Class<?> baseType) {
		super(fieldId,
			  name,description,
			  null,
			  baseType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the field type for a certain model object type
	 * @param modelObjType
	 * @return
	 */
	public Class<?> getFieldTypeForModelObjType(Class<? extends MetaDataDescribable> modelObjType) {
		return _fieldDataTypeMap.get(modelObjType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void checkIfIsAcceptableValueOrThrow(final Object value) {
		throw new IllegalArgumentException(Throwables.message("The metaData {} is defined as an interface... it cannot accept values",
															  _fieldId)); 
	}
}
