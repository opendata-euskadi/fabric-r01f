package r01f.bundles;

import java.util.ResourceBundle;


/**
 * Behavior at a {@link ResourceBundle} (ie: {@link r01f.locale.I18NService} o {@link r01f.configproperties.ConfigProperties}) 
 * when a requested key is NOT found
 */
public enum ResourceBundleMissingKeyBehavior {
    THROW_EXCEPTION,	// An exception is thrown
    RETURN_NULL,		// Null is returned
    RETURN_KEY			// The requested key is returned
}
