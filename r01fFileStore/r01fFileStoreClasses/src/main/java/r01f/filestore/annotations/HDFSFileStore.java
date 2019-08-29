package r01f.filestore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier // @BindingAnnotation 
@Target({ ElementType.FIELD,ElementType.PARAMETER }) 
@Retention(RetentionPolicy.RUNTIME)
public @interface HDFSFileStore {
	/* empty */
}
