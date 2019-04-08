package r01f.objectstreamer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

/**
 * Sets ths annotated property / type to be ignored when marshalling/unmarshalling
 * it's equivalent to Jackson's:
 * 		- @JsonIgnoreType when annotating a type
 * 		- @JsonIgnore when annotating a field
 */
@Target({
			ElementType.FIELD
		})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation @MarshallAnnotation
public @interface MarshallIgnoredField {
	// just a marker interface
}
