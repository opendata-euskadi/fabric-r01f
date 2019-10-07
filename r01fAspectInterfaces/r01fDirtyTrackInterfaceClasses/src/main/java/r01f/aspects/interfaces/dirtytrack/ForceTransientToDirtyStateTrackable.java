package r01f.aspects.interfaces.dirtytrack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Forces a {@link ConvertToDirtyStateTrackable}-annotated type's transient field to be taken
 * into account when checking if the status has changed
 * <pre>
 * NOTE: Usually, transient fields are NOT taken into account when checking the status
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ForceTransientToDirtyStateTrackable {
	/* just an interface */
}
