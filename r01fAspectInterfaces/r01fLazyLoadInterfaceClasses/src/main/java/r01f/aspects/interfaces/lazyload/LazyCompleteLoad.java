package r01f.aspects.interfaces.lazyload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Annotation that complements @LazyLoadCapable in a way that if it's set to a {@link Map}
 * annotated with @LazyLoadCapable, it forces that all it's elements are fully loaded on first access 
 * <pre>
 * Usually items of a  @LazyLoadCapable annotated {@link java.util.Map} are loaded whenever are
 * needed... @LazyCompleteLoad forces all items being loaded on first access
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LazyCompleteLoad {
	/* just an interface */
}
