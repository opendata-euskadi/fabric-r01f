package r01f.model.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.model.ModelObject;
import r01f.model.metadata.HasMetaDataForModelObject;

/**
 * Annotation used to set metadata about a {@link ModelObject} see {@link ModelObjectTypeMetaData} and {@link ModelObjectTypeMetaDataBase}
 * <pre class='brush:java'>
 * 		@ModelObjectMetaData(MyModelObjectMetaData.class)
 * 		public class MyModelObject
 * 		  implements ModelObject {
 * 			...
 * 		}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModelObjectData {
	/**
	 * The {@link FieldsMetaDataForModelObject} type that contains the fields metaData config
	 */
	Class<? extends HasMetaDataForModelObject> value() ;// default ModelObjectMetaData.class;
}
