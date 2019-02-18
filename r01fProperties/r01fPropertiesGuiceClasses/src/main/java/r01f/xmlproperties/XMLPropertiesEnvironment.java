package r01f.xmlproperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/** 
 * Annotation used to inject the type {@link XMLPropertiesForAppCacheImpl} customized for the required environment
 * This environment usually is a JVM property (r01Env) and is loaded at {@link XMLPropertiesGuiceModule}
 */
@BindingAnnotation 	// this tells guice that this is an annotation used to know where to inject
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@interface XMLPropertiesEnvironment {
	/* marker interface */
}
