package r01f.resources;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.xmlproperties.XMLProperties;

/**
 * Annotation to define a {@link ResourcesLoaderDef} location at a {@link XMLProperties} file 
 * where the load/reloading of resources is defined
 * (see I18NService)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourcesLoaderDefLocation {
    /**
     * Application code
     */
    String appCode() default "r01f";	
    /**
     * Component inside the application code
     */
    String component() default "default";
    /**
     * XPath inside the {@link XMLProperties} file
     */
    String xPath() default "/properties/resourcesLoader[@id='defaultClassPathLoader']";
}
