package r01f.objectstreamer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

@Target({
			ElementType.TYPE
		})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation @MarshallAnnotation
public @interface MarshallPolymorphicTypeInfo {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * The property name for the type identifier
     */
    public String typeIdPropertyName() default "typeId";
    /**
     * Is the type identifier property visible for the deserializer?
     */
    public boolean typeInfoAvailableWhenDeserializing() default false;
    /**
     * Always include the typeId info
     */
    public MarshallTypeInfoInclude includeTypeInfo() default @MarshallTypeInfoInclude();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    public @interface MarshallTypeInfoInclude {
    	public MarshalTypeInfoIncludeCase type() default MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE;
    	public MarshalTypeInfoIncludeCase property() default MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE;
    }
    public enum MarshalTypeInfoIncludeCase {
    	NEVER,
    	ALWAYS,
    	WHEN_ABSTRACT_OR_INTERFACE;
    }
}
