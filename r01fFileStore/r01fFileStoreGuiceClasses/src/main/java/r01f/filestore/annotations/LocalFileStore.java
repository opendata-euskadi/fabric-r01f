package r01f.filestore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@BindingAnnotation 
@Target({ ElementType.FIELD,ElementType.PARAMETER }) 
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalFileStore {
	/* empty */
}
