package r01f.configproperties;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bundles.ResourceBundleControl;
import r01f.bundles.ResourceBundleMissingKeyBehavior;
import r01f.bundles.ResourceBundleMissingKeyException;
import r01f.util.types.StringConverterWrapper;
import r01f.util.types.Strings;

/**
 * Provides access to configuration properties
 * This type is a simple alternative to the more powerfull Apache Commons Config (http://commons.apache.org/proper/commons-configuration/index.html) project
 * 
 * <h2>OPTIONS TO GET AN INSTANCE OF {@link ConfigProperties}</h2>
 * <pre>---------------------------------------------------------</pre>
 * 
 * <h3>
 * [OPTION 1]: When the instance where the {@link ConfigProperties} is to be used is injected by guice 
 * </h3>
 * <pre>
 * 		Ej: If a type MyType, needs to use property of a properties file called myProject.properties
 * 		    the type will have a {@link ConfigProperties} member that is going to be injected:
 * </pre>
 * <pre class='brush:java'>
 * 		public class MyType {
 * 			@Inject
 * 			private ConfigProperties _config;
 * 			...
 * 		}
 * </pre>
 * If GUICE is used to get an instance of MyType, GUICE will also inject the config properties. 
 * <pre>
 * 1.- Create a GUICE {@link com.google.inject.Module} including a {@link com.google.inject.Provider} for EVERY BUNDLE annotated with <code>@Provides</code>
 *     and <code>@Named("myBundle")</code> (or a custom annotation)</pre>
 * 	<pre class='brush:java'>
 *		public class TestConfigModule
 *   		  implements Module {
 *				@Override
 *				public void configure(Binder binder) {		
 *				}
 *				@Provides @Named("myProjectProperties")
 *				public ConfigProperties provideMyConfigProperties(final ConfigPropertiesBuilder ConfigPropertiesBuilder) {
 *					// The ConfigPropertiesBuilder instance is injected to the provider
 *					return ConfigPropertiesBuilder.forBundle("myProject.properties")
 *				  		 	 			 		  .loadedUsingDefinitionAt(AppCode.forId("r01fb"),AppComponent.forId("test"),
 *										  								   Path.of("/properties/resourcesLoader[@id='myClassPathResourcesLoader']"));
 *				}
 *			}
 * 	</pre><pre>
 * 2.- In the type where the properties are going to be used, the properties must be injected using the annotation @Named("myProjectProperties") -or a custom annotation-
 * 	(the @Named identifier must be the same as the one in the {@link com.google.inject.Provider} of the step1</pre>
 * 	<pre class='brush:java'>
 * 		public class MyType {
 * 			@Inject @Named("myProjectProperties")
 *			private ConfigProperties _config;
 * 			...
 * 		}
 * 	</pre><pre>
 * 	If the type MyType is NOT created by GUICE so no injection in MyType will be done, the {@link ConfigProperties} can be created as:</pre>
 * 	<pre class='brush:java'>
 * 		public class MyType {
 * 			public void someMethod() {
 * 				...
 * 				ConfigProperties properties = Guice.createInjector(new BootstrapGuiceModule(),
 * 																   new TestConfigPropertiesModule())
 *  	 											   .getInstance(Key.get(ConfigProperties.class,
 *  	 											   						Names.named("myProjectProperties")));
 * 				}
 * 			}
 * 		}
 * 	</pre>
 * 
 * 
 * <h3>
 * [OPTION 2]: Create the {@link ConfigProperties} by hand (not using GUICE at all)
 * </h3>
 * <pre>
 *	    ConfigProperties props = ConfigPropertiesBuilder.create(new XMLProperties())
 *														.forBundle("properties/myProject")
 *	    												.loadedUsingDefinitionAt(AppCode.forId("r01fb"),AppComponent.forId("test"),
 *	    															     		 Path.of("/properties/resourcesLoader[@id='myClassPathResourcesLoader']"));
 * <pre> 
 * If the resources loading/reloading is NOT defined in an XMLProperties file, the ResourceBundleControlBuilder can be
 * created as:
 * 	<pre class='brush:java'>
 *	   	ConfigProperties props = ConfigPropertiesBuilder.create()
 *														.forBundle("properties/myProject")
 *	   													.loadedUsingDefinition(ResourceLoaderDef.DEFAULT);	// use the default resources loading/reloading definition
 *																											// obviously a custom one could be used
 * 	</pre>
 */
@Slf4j
@Accessors(prefix="_")
@RequiredArgsConstructor
public class ConfigProperties {
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _bundleSpec;				// Bundle Name
	private final ResourceBundleControl _control;	// Bundle load & reload control
    private ResourceBundleMissingKeyBehavior _missingKeyBehaviour = ResourceBundleMissingKeyBehavior.THROW_EXCEPTION;
    private boolean _devMode;

/////////////////////////////////////////////////////////////////////////////////////////
//	FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
    public ConfigProperties withMissingKeyBehaviour(final ResourceBundleMissingKeyBehavior behaviour) {
    	_missingKeyBehaviour = behaviour;
    	return this;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFAZ I18NBundle
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns every key from the properties file
     * @return 
     */
    public List<String> keys() {
        List<String> outKeys = new LinkedList<String>();
        Enumeration<String> en = _retrievePropertiesResource(_bundleSpec).getKeys();
        while (en.hasMoreElements()) outKeys.add(en.nextElement());
        return outKeys;
    }
    /**
     * Checks if exists a property for a given key 
     * @param key the key
     * @return the property
     */
    public boolean hasKey(final String key) {
        boolean containsKey = _retrievePropertiesResource(_bundleSpec).containsKey(key);
        return containsKey;
    }
    /**
     * Returns a wrapper of the property that offers some usefull methods to get the value in different formats 
     * @param key the key
     * @return the wrapper
     */
    public StringConverterWrapper get(final String key) {
    	String keyValue = _retrieveProperty(key);
    	return new StringConverterWrapper(keyValue);
    }
    /**
     * Returns every property whose keys starts with a given prefix
     * IE: If the properties file contains;
     * 			my.one = One
     * 			my.two = Two
     * 			yours.one = Your One
     * 		<pre class='brush:java'>
     * 			propertiesWithKeysStartingWith("my")
     *		</pre>
     *	   these messages would be returned as a Map
     * 			my.one = One
     * 			my.two = Two
     * @param keyPrefix 
     * @return
     */
    public final Map<String,StringConverterWrapper> propertiesWithKeysStartingWith(final String keyPrefix) {
        if (keyPrefix == null) throw new IllegalArgumentException("Cannot load bundle key: Missing key!");  
        Map<String,StringConverterWrapper> outMessages = new HashMap<String,StringConverterWrapper>();
        try {
	        // Load the resourceBundle and iterate all the keys to find the ones that starts with the given prefix
	    	ResourceBundle bundle = _retrievePropertiesResource(_bundleSpec);
    		Enumeration<String> keys = bundle.getKeys();
    		if (keys != null && keys.hasMoreElements()) {
    			do {
    				String key = keys.nextElement();
    				String keyValue = key.startsWith(keyPrefix) ? bundle.getString(key)
    													   		: null;
    				if (keyValue != null) outMessages.put(key,new StringConverterWrapper(keyValue));
    			} while (keys.hasMoreElements());
    		}
        } catch (MissingResourceException mrEx) {
        	log.error("The properties file could not be retrieved {}",_bundleSpec,mrEx);
        }
        return outMessages;
    }
    @Override
    public final String toString() {
        return _bundleSpec;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets the given key value 
     * @param key the key to search for
     * @return
     * @throws MissingConfigPropertyException
     */
    private final String _retrieveProperty(final String key) throws ResourceBundleMissingKeyException {
        if (key == null) throw new IllegalArgumentException("Cannot load bundle key: Missing key!");
    	String outKeyValue = null;
        try {
        	ResourceBundle bundle = _retrievePropertiesResource(_bundleSpec);
        	try {
        		outKeyValue = bundle.getString(key);
        	} catch(MissingResourceException mkEx) {
    			log.warn("the requested key {} could not be found int the bundle {}",
    					 key,_bundleSpec);
        	}
        } catch (MissingResourceException mrEx) {
        	outKeyValue = String.format("The config file %s could NOT be loaded: %s",_bundleSpec,mrEx.getMessage());
        	log.error(outKeyValue,mrEx);
        }
        if (Strings.isNullOrEmpty(outKeyValue)) {
            switch (_missingKeyBehaviour) {
                case RETURN_KEY: {
                    outKeyValue = "[" + key + "]";
                    break;
                }
                case RETURN_NULL:
                    outKeyValue = null;
                    break;
                case THROW_EXCEPTION:
                    throw new ResourceBundleMissingKeyException(key,_bundleSpec);
                default:
            }
        }
        return outKeyValue;
    }
    private ResourceBundle _retrievePropertiesResource(final String bundleName) {
        if (_devMode) ResourceBundle.clearCache();	// IMPORTANT!! 	the cache is deleted in DEBUG mode so the bundle is reloaded everytime it's requested
        											//		 		This is NOT efficient; do not leave this way in production
        // Load the Properties file
        // IMPORTANT!!
        //		- DO NOT CACHE the ResourceBoundle because ResourceBundle itself implements a cache; also if ResourceBundel is cached the hot reload function is deactivated
        //		- Use ResourceBundle.Control to customize the hot reload 
        //		- CACHEE the ResourceBundle.Control instance because it could maintain some status about the hot reload (ie: timeStamp of the las reload)
    	ResourceBundle outBundle = ResourceBundle.getBundle(bundleName,
    									   	 				_control);
        return outBundle;
    }
}
