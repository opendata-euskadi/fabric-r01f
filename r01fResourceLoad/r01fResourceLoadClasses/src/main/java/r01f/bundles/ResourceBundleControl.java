package r01f.bundles;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesReloadControl;
import r01f.resources.ResourcesReloadControlVoid;
import r01f.types.Path;

/**
 * Controls the loading & reloading of a resource based on a definition in an xml properties file
 * This encapsulates an instance of:
 * <ul>
 * 		<li>The resources loader: {@link ResourcesLoader}</li>
 * 		<li>The resources loader control: {@link ResourcesReloadControl}</li>
 * </ul>
 * 
 * Usually an instance of {@link ResourceBundleControl} is created using the {@link ResourceBundleControlBuilder} factory
 * that internally maintains a CACHE of created {@link ResourceBundleControl} objects
 * 
 * @see ResourceBundleControlBuilder
 */
@Accessors(prefix="_")
@Slf4j
public class ResourceBundleControl 
     extends ResourceBundle.Control {

/////////////////////////////////////////////////////////////////////////////////////////
// FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private ResourcesLoader _resourcesLoader;
	@Getter private ResourcesReloadControl _reloadControl;
	@Setter private ResourceBundleControlResourceNameResolver _nameResolver;

/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected ResourceBundleControl() {
		// nothing
	}
    /**
     * Constructor from a {@link ResourcesLoader} and a {@link ResourcesReloadControl}
     * @param resLoader the resources loader
     * @param resReloadControl how resources are reloaded
     */
    public ResourceBundleControl(final ResourcesLoader resLoader,
    						     final ResourcesReloadControl resReloadControl) {
    	_resourcesLoader = resLoader;
    	_reloadControl = resReloadControl;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
    public ResourceBundleControl resolveBundleNameWith(final ResourceBundleControlResourceNameResolver resolver) {
    	_nameResolver = resolver;
    	return this;
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	OVERRIDE PARA INDICAR QUE SOLO "BUSQUE" FICHEROS PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public List<String> getFormats(final String baseName) {
		if (baseName == null) throw new NullPointerException();
		return FORMAT_PROPERTIES;		// Forzar que solo busque ficheros PROPERTIES (no busque ficheros java)
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	OVERRIDEN METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ResourceBundle newBundle(final String baseName,final Locale locale,
									final String format, 
									final ClassLoader loader,
									final boolean reload) throws IllegalAccessException,
																 InstantiationException, 
																 IOException {
		if (baseName == null || locale == null || format == null || loader == null) throw new NullPointerException("ResourceBoundle.Control baseName, locale, format and loader must NOT be null");
		
		ResourceBundle outBundle = null;
		if (format.equals("java.properties")) {
			String resourceExtension = format.substring(5);		// remove "java." string
		    String resourceName = this.toResourceName(this.toBundleName(baseName,locale),
		    										  resourceExtension);
		    
    		log.warn("...load {}: reload={}",resourceName,
    							   			 reload);
		    
		    // Load file
			InputStream is = _resourcesLoader.getInputStream(Path.from(resourceName),
															 reload);
		    if (is != null) outBundle = new PropertyResourceBundle(is);
        } else {
            throw new IllegalArgumentException("ResourceBundle: unknown format: " + format);
        }
	    return outBundle;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// OVERRIDE TO ALLOW HOT-RELOADING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public long getTimeToLive(final String baseName,final Locale locale) {
        if (baseName == null || locale == null) throw new NullPointerException("ResourceBoundle.Control baseName, and locale must NOT be null");

        long outTTL = TTL_NO_EXPIRATION_CONTROL;		// by default it does NOT expires
        if (_reloadControl != null) {
        	outTTL = _resourcesLoader.getConfig()
        							 .getReloadControlDef()
        							 .getCheckIntervalMilis();
        	log.debug("\t\t...checking if a reload is needed every {} millis",_resourcesLoader.getConfig()
        							 													      .getReloadControlDef()
        							 													      .getCheckIntervalMilis());
        }
        return outTTL;
    }
	@Override
    public boolean needsReload(final String baseName,final Locale locale,
                               final String format,final ClassLoader loader,
                               final ResourceBundle bundle,final long loadTime) {
		// If ReloadControlVoid is used, it NEVER expires
		if (_reloadControl == null || _reloadControl instanceof ResourcesReloadControlVoid) return false;
		
        boolean outReload = false;
		try {
	        if (bundle == null) throw new NullPointerException("ResourceBoundle.Control bundle must NOT be null");
	        
	        // Resource name
	        String resourceName = null; 
	        if (format.equals("java.properties")) {
				String resourceExtension = format.substring(5);	// eliminar la cadena "java."
			    resourceName = this.toResourceName(this.toBundleName(baseName,locale),
			    								   resourceExtension);
	        } else {
	        	resourceName = this.toResourceName(this.toBundleName(baseName,locale),
			    								   format);
	        }
			// Delegate...    
		    outReload = _reloadControl.needsReload(resourceName);	
		    
		    // log
		    if (outReload) {
		    	log.warn("{} NEEDS reloading!",resourceName);
		    } else {
		    	log.debug("{} DOES NOT NEED reloading!",resourceName);
		    }
		} catch(Throwable th) {
			log.error("Unknown error when trying to guess if a {} should be reloaded",ResourceBundle.class,th);
		}
        return outReload;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE BUNDLE FILE NAME METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String toBundleName(final String baseName,final Locale locale) {
		return _nameResolver != null ? _nameResolver.toBundleName(baseName,locale) 
									 : super.toBundleName(baseName, locale);
	}
	@Override
	public List<Locale> getCandidateLocales(final String baseName,final Locale locale) {
		return _nameResolver != null ? _nameResolver.getCandidateLocales(baseName,locale)
									 : super.getCandidateLocales(baseName, locale);
	}
	@Override
	public Locale getFallbackLocale(final String baseName,final Locale locale) {
		return _nameResolver != null ? _nameResolver.getFallbackLocale(baseName,locale)
									 : super.getFallbackLocale(baseName, locale);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * By any reason if ResourceBundle.Control.toBundleName(final String baseName,final Locale locale)
	 * method is overriden at a subclass of {@link ResourceBundleControl}, this overriden method is 
	 * ignored... 
	 */
	public static interface ResourceBundleControlResourceNameResolver {
		
		public String toBundleName(final String baseName,final Locale locale);
		
		public List<Locale> getCandidateLocales(final String baseName,final Locale locale);
		
		public Locale getFallbackLocale(final String baseName,final Locale locale);
	}
}
