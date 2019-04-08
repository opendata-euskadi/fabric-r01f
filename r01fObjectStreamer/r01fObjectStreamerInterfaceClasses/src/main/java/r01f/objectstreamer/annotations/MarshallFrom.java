package r01f.objectstreamer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

@Target({
			ElementType.PARAMETER
		})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation @MarshallAnnotation
public @interface MarshallFrom {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public final static String MARKER_FOR_DEFAULT = "";
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
    /**
     * Sets the field name
     */
    public String value();
}
