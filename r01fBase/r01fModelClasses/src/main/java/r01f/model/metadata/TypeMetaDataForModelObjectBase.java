package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import lombok.experimental.Accessors;
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
@MetaDataForType(modelObjTypeCode = HasFieldsMetaData.MODEL_OBJECT_TYPE_CODE,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Objeto del modelo"),
						@DescInLang(language=Language.BASQUE, value="[eu] Model object"),
						@DescInLang(language=Language.ENGLISH, value="Model object")
				 })
@GwtIncompatible
@Accessors(prefix="_")
public abstract class TypeMetaDataForModelObjectBase
    	   implements HasMetaDataForModelObject {
	// nothing
}
