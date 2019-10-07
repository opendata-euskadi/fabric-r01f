package r01f.aspects.interfaces.freezable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Signs an object as freezable
 */ 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConvertToFreezable {
	/* just an interface */
}
