package r01f.notifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Annotation that tells guice to inject the the eMail-based X47BEMailNotifier.java
 */
@BindingAnnotation 
@Target({ ElementType.FIELD,ElementType.PARAMETER}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface UseEMailNotifier {
	// nothing
}
