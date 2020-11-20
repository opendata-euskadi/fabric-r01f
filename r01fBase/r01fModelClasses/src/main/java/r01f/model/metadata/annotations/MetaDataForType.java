package r01f.model.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.model.ModelObject;


/**
 * Annotation used to set metadata about a {@link ModelObject}
 * The model object type (or one of the types in it's hierarchy) MUST be annotated with @ModelObjectData
 * setting the type that holds the meta-data about the type
 * <pre class='brush:java'>
 * 		@ModelObjectData(MyBusinessObjecTypeMetaData.class)
 * 		@Accessors(prefix="_")
 * 		public class MyBusinessObjectType
 * 		  implements ModelObject {		// extends MetaDataDescribable
 * 			@Getter @Setter private MyOID _oid;
 * 			@Getter @Setter private MyOtherModelObject _myOtherField;
 * 		}
 * </pre>
 * The type that holds the metadata is roughly (not mandatory) similar to the model object
 * BUT with meta-data information
 * <pre class='brush:java'>
 * 		@MetaDataForType(modelObjTypeCode = 100,
 *					     description = {
 *							@DescInLang(language=Language.SPANISH,value="My model object type"),
 *							@DescInLang(language=Language.BASQUE,value="[eu] my model object type"),
 *							@DescInLang(language=Language.ENGLISH,value="My model object type")
 *						 })
 * 		public class MyBusinessObjecTypeMetaData
 * 		  implements HasModelObjectMetaData {
 *				@MetaDataForField(id = "oid",
 *			   				      description = {
 *									@DescInLang(language=Language.SPANISH,value="Identificador único"),
 *									@DescInLang(language=Language.BASQUE,value="[eu] Identificador único"),
 *									@DescInLang(language=Language.ENGLISH,value="Unique identifier")
 *							     },
 *			   				     storage = @Storage(indexed=false))
 * 				private MyOID _oid;
 *
 *				@MetaDataForField(id = "otherField",
 *			   				      description = {
 *									@DescInLang(language=Language.SPANISH,value="Other field"),
 *									@DescInLang(language=Language.BASQUE,value="[eu] Other field"),
 *									@DescInLang(language=Language.ENGLISH,value="Other field")
 *							     },
 *			   				     storage = @Storage(indexed=false))
 * 				private MyOtherModelObject _myOtherField;
 * 		}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MetaDataForType {
	long modelObjTypeCode();
	DescInLang[] description() default {};
}
