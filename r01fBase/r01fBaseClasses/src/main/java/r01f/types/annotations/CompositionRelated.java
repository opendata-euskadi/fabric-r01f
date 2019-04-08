package r01f.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Annotation stating that an object is related with other by means of a COMPOSITION relation
 * (see http://design-antony.blogspot.com.es/2007/07/aggregation-vs-composition.html)
 * An object is related by means of a COMPOSITION relation with other if the first cannot exists without the other
 * In the real world for example, a room cannot exists without the containing house
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CompositionRelated {
	/* marker interface */
}
