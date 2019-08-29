package r01f.xmlproperties.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import r01f.xmlproperties.XMLPropertiesForAppComponent;


/**
 * Annotation used with {@link XMLPropertiesForAppComponent} type to inject
 * a component's properties
 */
@Qualifier // @BindingAnnotation	// this tells guice that this is an annotation used to know where to inject
@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
public @interface XMLPropertiesComponent {
	String value();
}
