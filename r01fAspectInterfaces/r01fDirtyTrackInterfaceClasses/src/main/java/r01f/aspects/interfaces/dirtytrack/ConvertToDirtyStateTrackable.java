package r01f.aspects.interfaces.dirtytrack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Annotation that enables dirty checking in a type: it monitorizes changes in type's fields so when a state change occurs, the 
 * instance is marked as dirty
 * 
 * IMPORTANT! See DirtyStateAspect
 * 		It's based ASPECT-J that instruments types in order to "intercept" all type's fields changes
 * 		and set a "dirty" flag when a field is changed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface ConvertToDirtyStateTrackable {
	/* just an interface */
}
