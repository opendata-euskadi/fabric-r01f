package r01f.aspects.interfaces.dirtytrack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Forces a {@link ConvertToDirtyStateTrackable}-annotated type's transient field NOT to be taken
 * into account when checking if the status has changed
 * <pre>
 * NOTE: Usually transient fields are NOT taken into account when checking the status
 * 		 ... so this annotation is NOT necessary
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NotDirtyStateTrackable {
	/* just an interface */
}
