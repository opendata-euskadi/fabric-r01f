package r01f.model.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.model.metadata.MetaDataDescribable;
import r01f.model.metadata.TypeMetaData;

/**
 * Use this annotation to give hits about how to resolve a type's field type
 * Given an interface type that has a field defined using another interface type:
 * <code class='brush:java'>
 * 		public interface MyFieldType {
 * 			...
 * 		}
 * 		public abstract class MyType<F extends MyFieldType> {
 * 			@Getter F _field;
 * 		}
 * </code>
 * The implementation could be:
 * <code class='brush:java'>
 * 		public class MyFieldTypeImpl1 
 * 		  implements MyFieldType {
 * 			...
 * 		}
 * 		public class MyTypeImpl1
 * 		     extends MyType<MyFieldTypeImpl1> {
 * 		}
 * </code>
 * and 
 * <code class='brush:java'>
 * 		public class MyFieldTypeImpl2 
 * 		  implements MyFieldType {
 * 			...
 * 		}
 * 		public class MyTypeImpl2
 * 		     extends MyType<MyFieldTypeImpl2> {
 * 		}
 * </code> 
 * The {@link TypeMetaData} for MyType cannot set statically the field type since it depends on
 * the concrete MyType implementation
 * ... so the metadata for the field is defined as:
 * <code class='brush:java'>
 * 		public abstract class MyTypeMetaData {
 *			@MetaDataForField(description = {
 *							@DescInLang(language=Language.ENGLISH, value="a polymorphic field"),
 *					  },
 *					  polymorphicResolution = {
 *							  					@PolymorphicFieldTypeResolve(whenContainerType=MyTypeImpl1.class,resolveTo=MyFieldTypeImpl1.class),
 *							  					@PolymorphicFieldTypeResolve(whenContainerType=MyTypeImpl2.class,resolveTo=MyFieldTypeImpl2.class),
 *					  						  })
 *			private MyFieldType _field;
 * 		}
 * </code>
 * This way using meta-info about MyType the concrete field type can be guess at run-time given a MyType concrete instance
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface PolymorphicFieldTypeResolve {
	Class<? extends MetaDataDescribable> whenContainerType();
	Class<?> resolveTo();
}
