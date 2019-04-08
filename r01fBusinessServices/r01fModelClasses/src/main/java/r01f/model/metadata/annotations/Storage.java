package r01f.model.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Storage {

	boolean stored() default false;
	float boosting() default 5;
	boolean indexed() default true;
	boolean tokenized() default false;


}
