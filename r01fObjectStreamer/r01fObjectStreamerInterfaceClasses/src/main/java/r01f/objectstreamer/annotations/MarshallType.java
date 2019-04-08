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
public @interface MarshallType {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public final static String MARKER_FOR_DEFAULT = "";

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Explicitly sets the name the field is mashalled to/from
     */
    public String as();
    /**
     * The namespace
     */
    public String namespace() default MARKER_FOR_DEFAULT;
    /**
     * When a type is an instance of a polymorphic type, sets the id to be used
     * at the typeId property
     */
    public String typeId() default MARKER_FOR_DEFAULT;
}
