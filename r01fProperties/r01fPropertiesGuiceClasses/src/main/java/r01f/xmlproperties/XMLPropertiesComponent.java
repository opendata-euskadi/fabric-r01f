package r01f.xmlproperties;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;


/**
 * Annotation used with {@link XMLPropertiesForAppComponent} type to inject
 * a component's properties
 */
@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@BindingAnnotation	// this tells guice that this is an annotation used to know where to inject
public @interface XMLPropertiesComponent {
	String value();
}
