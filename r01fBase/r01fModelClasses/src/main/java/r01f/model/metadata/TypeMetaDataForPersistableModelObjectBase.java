package r01f.model.metadata;

import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.ModelObject;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.ModelObjectData;


/**
 * Base type for the metaData that describes a {@link ModelObject} a
 * This type is set using the {@link ModelObjectData} annotation set at model objects as:
 * <pre class='brush:java'>
 * 		@ModelObjectMetaData(MyModelObjectMetaData.class)
 * 		public class MyModelObject
 * 		  implements ModelObject {
 * 			...
 * 		}
 * </pre>
 */
@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.PERSISTABLE_MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Objeto persistente"),
						@DescInLang(language=Language.BASQUE, value="[eu] Persistent model object"),
						@DescInLang(language=Language.ENGLISH, value="Persistent model object")
				 })
@Accessors(prefix="_")
public abstract class TypeMetaDataForPersistableModelObjectBase<O extends OID>
    	      extends TypeMetaDataForModelObjectBase
    	   implements HasMetaDataForPersistableModelObject<O> {
	// nothing
}
