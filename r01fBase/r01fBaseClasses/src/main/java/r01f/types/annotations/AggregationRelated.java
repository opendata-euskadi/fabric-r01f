package r01f.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Annotation stating that an object is related with other by means of a AGGREGATION relation
 * (see http://design-antony.blogspot.com.es/2007/07/aggregation-vs-composition.html)
 * An object is related by means of a AGGREGATION relation with other if the first can exists without the other
 * In the real world for example, table can exists whithout the enclosing house, they both have independent lives
 * 
 * The aggregation relation is often refered to as a "has-a" relation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AggregationRelated {
	/* marker interface */
}
