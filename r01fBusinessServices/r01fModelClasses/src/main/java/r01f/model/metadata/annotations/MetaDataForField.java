package r01f.model.metadata.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.model.ModelObject;

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
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface MetaDataForField {

//	String id();
	DescInLang[] description();
	DescInLang[] alias() default {};
	Storage storage() default @Storage;	
	PolymorphicFieldTypeResolve[] polymorphicResolution() default {};
}
