package r01f.locale.services;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.bundles.ResourceBundleMissingKeyBehavior;

/**
 * Annotation used to inject a {@link I18NService} 
 * see {@link I18NService}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18NMessageBundleService {
    /**
     * Bundles where to look after a key
     * When a message is requested by it's key, bundles are searched in order until the key is found
     */
    String[] chain() default {"default"};	// if no bundle chain is set, a bundle called "default" is returned
    /**
     * How to handle not found keys
     */
    ResourceBundleMissingKeyBehavior missingKeyBehavior() ;//default ResourceBundleMissingKeyBehaviour.THROW_EXCEPTION;
}
