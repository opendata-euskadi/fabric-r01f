package r01f.bundles;

import java.util.Arrays;
import java.util.Locale;

/**
 * Exception thrown in a ResourceBundle ( {@link r01f.locale.I18NService} or {@link r01f.configproperties.ConfigProperties} ) 
 * when a searched key is not found and the {@link ResourceBundleMissingKeyBehavior} is setted to THROWEXCEPTION
 */
public class ResourceBundleMissingKeyException 
     extends RuntimeException {
	
	private static final long serialVersionUID = 8569200760575353323L;
/////////////////////////////////////////////////////////////////////////////////////////
//		
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourceBundleMissingKeyException(final String key,final Locale locale,final String... bundleChain) {
        super( String.format("Missing key '%s' for locale '%s' in bundle '%s'",
        			  		 key,locale.toString(),Arrays.asList(bundleChain)) );
    }
	public ResourceBundleMissingKeyException(final String key,final String... bundleChain) {
        super( String.format("Missing key '%s' in bundle '%s'",
        			  		 key,Arrays.asList(bundleChain)) );
    }

}
