package r01f.locale.services;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.common.annotations.GwtIncompatible;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bundles.ResourceBundleControl;
import r01f.bundles.ResourceBundleControlBuilder;
import r01f.bundles.ResourceBundleMissingKeyBehavior;
import r01f.bundles.ResourceBundleMissingKeyException;
import r01f.locale.Language;
import r01f.resources.ResourcesLoader;
import r01f.util.types.locale.Languages;
import r01f.xmlproperties.XMLProperties;

/**
 * Provides access to a service of ·localized texts in an hierchical way:
 * <pre>
 * 		Service {@link I18NService}
 * 			|----- Bundle {@link I18NBundle}
 * 					  |------ Mensaje (el texto)
 * </pre>
 * In order to get a text three steps must be acomplished:
 * <pre>
 * 		1.- Get a service {@link I18NService} representing the different {@link Locale}s of a "bundle", that's to say, a {@link I18NService} 
 * 		    provides access to every bundle/locale pair
 * 				In order to get a {@link I18NService} the name of the bundle is needed. This bundle name is an identifier like 'myApp.components.myBundle'
 * 				The {@link I18NService} will search for the bundle at /myApp/components/myBundle using a {@link r01f.resources.ResourcesLoader}, 
 * 				for example (ej {@link r01f.resources.ResourcesLoaderFromClassPath} (loads the resources from the classPath)
 * 
 * 		2.- Get the bundle {@link I18NBundle} of a {@link Locale}
 * 				
 * 		3.- Get the message from the bundle
 * </pre>
 * The access to the messages is done through the {@link I18NService} that provides a {@link I18NBundle} of a {@link Locale}
 * <pre class='brush:java'>
 * 		myI18nService.forLocale(new Locale("es","ES"))
 * 					 .message("msgKey"));
 * </pre>
 * 
 * CHAIN OF BUNDLES TO SEARCH FOR A MESSAGE
 * -------------------------------------------
 * {@link I18NService} allows a chain of bundles to be specified in order to find a message
 * When a message is required by it's key, every link in the chain is checked to see if the key is present; when the key is found, the search stops.
 * Example:
 * <pre>
 * 		- If the bundle-chain is {"myApp.myComponent.myChildBundle","myApp.myComponent.myParentBundle"}
 * 		  ... and a key like 'textKey' is required
 * 		  ... the key would be searched starting at the "myApp.myComponent.myChildBundle" bundle 
 * 		  ... if the key is not found there, the next link in the chain of boundles would be checked: "myApp.myComponent.myParentBundle"
 * </pre>
 * SIDE NOTE: In order to optimize the processing, it's better to put the bundles where the keys are likely to be found in the first places of the list
 * 
 * <h2>OPTIONS TO GET AN INSTANCE OF {@link I18NService}</h2>
 * <pre>---------------------------------------------------------</pre>
 * 
 * <h3>
 * [OPTION 1]: When the instance where the {@link I18NService} is to be used is injected by guice 
 * </h3>
 * <pre>
 * 		Ej: If a type MyType, needs to use messages of a resource boundle called components.myResourceBundle
 * 		    the type will have a {@link I18NService} member that is going to be injected:
 * </pre>
 * <pre class='brush:java'>
 * 		public class MyType {
 * 			@Inject
 * 			private I18NService _i18NService;
 * 			...
 * 		}
 * </pre>
 * If GUICE is used to get an instance of MyType, GUICE will inject the {@link I18NService} instance, BUT this requires a {@link ResourceBundleControl} 
 * instance that controls how the resources are going to be loaded/reloaded
 * There are two options:
 * <h4>Use Annotations</h4>
 * 		<pre>
 * 		1.- Annotate the types where the {@link I18NService} is going to be injected with @I18NLocalized 
 * 		2.- Annotate the {@link I18NService} members with
 * 				a.- @I18NMessageBundleService that specifies the bundle search specs (if the bundle name is not specified, the type name is used instead)
 * 				b.- @ResourcesLoaderDefLocation that specifies the location of the definition for the resources loading/reloading</pre>
 * 			<pre class='brush:java'>
 * 				@I18NLocalized		// [1]
 * 				public class MyType {
 * 					// [2] injected member
 * 					@I18NMessageBundleService(chain={"components.myParentBundle","components.myChildBundle"},	// bundles to search the keys
 *				  							  missingKeyBehaviour=MissingKeyBehaviour.RETURN_NULL)				// if the key is not found in any bundle, return null
 *				  	@ResourcesLoaderDefLocation(appCode="r01fb",
 *									  		    component="test",
 *									  		    xPath="properties/resourcesLoader[@name='myClassPathResourcesLoader']")	// use the ResourcesLoader
 * 					private I18NService _i18NService;
 * 					...
 * 				}
 * 			</pre><pre>
 * 		3.- Use GUICE to create an instance of MyType, for example:</pre>
 * 			<pre class='brush:java'>
 * 				Injector injector = Guice.createInjector(new BootstrapGuiceModule());
 *				MyType myTypeInstance = injector.getInstance(MyType.class);
 *			</pre><pre>
 *			... now on, the myTypeInstance instance of MyType type is injected with the {@link I18NService} instance that allows access to message bundles
 *		</pre>
 *
 * <h4>Another way (without having to annotate MyType)</h4>
 * 		<pre>
 * 		1.- Create a GUICE {@link com.google.inject.Module} including a {@link com.google.inject.Provider} for EVERY BUNDLE annotated with <code>@Provides</code>
 * 	  	    and <code>@Named("myBundle")</code> (or a custom annotation)</pre>
 * 			<pre class='brush:java'>
 *				public class TestI18NModule
 *        		  implements Module {
 *						@Override
 *						public void configure(Binder binder) {		
 *						}
 *						@Provides @Named("myBundle")
 *						public I18NService newMyBundle(final XMLProperties xmlProperties) {
 *							// The XMLProperties is injected to the provider
 *							ResourceBundleControl resBundleControl = ResourceBundleControlFactory.create(xmlProperties)
 *																				 				 .forLoadingDefinitionAt(AppCode.forId("r01fb"),
 *																						 				 				 AppComponent.forId("test"),
 *																						 				 				 Path.of("/properties/resourcesLoader[@id='myClassPathResourcesLoader']")); 
 *							return I18NServiceFactory.create(resBundleControl)
 *													 .forBundleChain(new String[] {"components.myParentBundle","components.myChildBundle"})
 *									 				 .withMissingKeyBehaviour(ResourceBundleMissingKeyBehaviour.RETURN_NULL);
 *						}
 *					}
 * 			</pre><pre>
 * 		2.- In the type where the bundle is going to be used, the bundle must be injected using the annotation @Named("myBundle")
 * 	   		(the @Named identifier must be the same as the one in the {@link com.google.inject.Provider} of the step1</pre>
 * 			<pre class='brush:java'>
 * 				public class MyType {
 * 					@Inject @Named("myBundle")
 *					private I18NService _i18NService;
 * 					...
 * 				}
 * 			</pre><pre>
 * 			If the type is NOT created by GUICE, the {@link I18NService} can be created as:</pre>
 * 			<pre class='brush:java'>
 * 				public class MyType {
 * 					public void someMethod() {
 * 						...
 * 						I18NService i18n = Guice.createInjector(new BootstrapGuiceModule(),
 * 																new TestI18NModule())
 *   		 									.getInstance(Key.get(I18NService.class,
 *   																 Names.named("myBundle")));
 * 						}
 * 					}
 * 				}
 * 			</pre>
 * 
 * 
 * <h3>
 * 
 * [OPTION 2]: Create the {@link I18NService} by hand (not using GUICE at all)
 * </h3>
 * <pre>
 * 		1.- Create a {@link ResourceBundleControlBuilder} that provides {@link ResourceBundleControl} object instances
 * 		    The factory needs an instance of an {@link XMLProperties} file where the resources loading/reloading is defined</pre>
 * 			<pre class='brush:java'>
 * 					XMLProperties props = XMLProperties.create(); 	// Normally you would put this XMLProperties in an static instance since it maintains a cache of properties
 * 					ResourceBundleControlBuilder resBundleControlFactory = ResourceBundleControlBuilder.create(props);
 * 			</pre><pre>
 * 		2.- Create a {@link ResourceBundle} instance for a definition located at an xml properties
 * 			<pre class='brush:java'>
 * 				ResourceBundle resBundle = resBundleControlFactory.forLoadingDefinitionAt(appCode,component,xPath);
 * 			</pre>
 * 		3.- Create the {@link I18NService}</pre>
 * 			<pre class='brush:java'>
 *	    		I18NService i18n = I18NServiceBuilder.create(resBundle)
 *												 	 .forBundleChain("properties/myProject");
 * 			</pre>
 * <pre> 
 * 
 * If the resources loading/reloading is NOT defined in an XMLProperties file, the ResourceBundleControlFactory can be
 * created as:
 * 		1.- Create a ResourceBundleControlFactory that provides ResourceBundleControl object instances</pre>
 * 			<pre class='brush:java'>
 * 				ResourceBundleControlFactory resBundleControlFactory = ResourceBundleControlFactory.create();
 * 			</pre><pre>
 * 		2.- Create the {@link I18NServiceBuilder}</pre>
 * 			<pre class='brush:java'>
 * 				I18NServiceFactory i18nServiceFactory = I18NServiceFactory.create(resBundleControlFactory);
 * 			</pre><pre>
 * 		3.- Create the {@link I18NService}</pre>
 * 			<pre class='brush:java'>
 *	    	I18NService i18n = i18nServiceFactory.forBundle("properties/myProject")
 *	    										 .loadedUsingDefinition(ResourceLoaderDef.DEFAULT);	// use the default resources loading/reloading definition
 *																									// obviously a custom one could be used
 * 			</pre>
 */
@GwtIncompatible
@Accessors(prefix="_")
@Slf4j
public class I18NService {
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS
/////////////////////////////////////////////////////////////////////////////////////////
    // ResourceBundle reload check control responsible
    // It's important that the ResourceBundle.Control object is CACHED because it may contain some status about the reload process (ej: timeStamp of the last reloading)
    private final ResourceBundleControl _control;
    
    private final ClassLoader _classLoader;
    
    private final String[] _bundleChain;
    
    private       ResourceBundleMissingKeyBehavior _missingKeyBehaviour = ResourceBundleMissingKeyBehavior.THROW_EXCEPTION;
    private		  boolean _devMode;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public I18NService(final ResourceBundleControl resControl,
    				   final String... bundleChain) {
    	this(resControl,
    		 null,
    		 bundleChain);
    }
    public I18NService(final ResourceBundleControl resControl,
    				   final ClassLoader classLoader,
    				   final String... bundleChain) {
    	_control = resControl;
    	_classLoader = classLoader;
    	_bundleChain = bundleChain;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFAZ 
/////////////////////////////////////////////////////////////////////////////////////////
    public final I18NBundle forLocale(final Locale locale) {
    	I18NBundle outBundle = new I18NBundle(locale);
//        I18NBundle outBundle = _bundleCache.get(locale);
//        if (outBundle == null) { 	
//        	// Crear el bundle para el locale que se suministra
//        	outBundle = new I18NBundle(locale);
//            _bundleCache.putIfAbsent(locale,outBundle);
//            outBundle = _bundleCache.get(locale);
//        }
        return outBundle;
    }
    public final I18NBundle forLanguage(final Language lang) {
    	return this.forLocale(Languages.getLocale(lang));
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDES
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public final String toString() {
        return Arrays.toString(_bundleChain);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Sets the behaviour when a requested key is not found (ie: throw an exception, return null)
     * @param behaviour the behaviour
     */
    public I18NService withMissingKeyBehaviour(final ResourceBundleMissingKeyBehavior behaviour) {
    	_missingKeyBehaviour = behaviour;
    	return this;
    }
    /**
     * Sets the bundle in debug mode
     */
    public I18NService inDevMode() {
    	log.warn("The I18NService is in DEVELOPER MODE which forces the ResourceBundles to load EVERY TIME a message key is requested. This is useful in develop time but NEVER left it on in PRODUCTION mode!!!");
    	_devMode = true;
    	return this;
    }
    
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////
//	I18NBundle
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encapsula el acceso al objeto {@link ResourceBundle} que es donde est�n realmente los mensajes y la
	 * cach� de mensajes.
	 * Esta clase es manejada desde la clase {@link I18NService} quien le pasa:
	 * 		- El {@link ResourcesLoader} encargado de cargar los ficheros de recursos 
	 * 		- El nombre del bundle a cargar
	 * 		- El {@link Locale}
	 */
	@Accessors(prefix="_")
	public class I18NBundle {
	    private final Locale _locale;
	    
	
	    I18NBundle(final Locale locale) {
	        _locale = locale;
	    }
	    /**
	     * Devuelve todas las claves de todos los bundles de mensajes
	     * @return todas las claves del bundle
	     */
	    public List<String> keys() {
	        List<String> outKeys = new LinkedList<String>();
	        for (String bundle : _bundleChain) {
		        Enumeration<String> en = _retrieveBundle(bundle).getKeys();
		        while (en.hasMoreElements()) outKeys.add(en.nextElement());
	        }
	        return outKeys;
	    }
	    /**
	     * Comprueba si existe un mensaje con una clave en la cadena de Bundles
	     * @param key la clave
	     * @return el mensaje
	     */
	    public boolean hasKey(final String key) {
	        boolean containsKey = false;
	        for (String bundle : _bundleChain) {
	        	containsKey = _retrieveBundle(bundle).containsKey(key);
	        	if (containsKey) break;
	        } 
	        return containsKey;
	    }
	    /**
	     * Devuelve un mensaje del bundle a partir de su clave
	     * @param key la clave
	     * @return el mensaje tal cual aparece en el bundle (sin customizar)
	     * @throws I18NMissingMessageException si NO se encuentra la clave
	     */
	    public final String message(final String key) throws ResourceBundleMissingKeyException {
	        return this.message(key,(Object[])null);
	    }
	    /**
	     * Devuelve un mensaje del bundle a partir de su clave
	     * @param key la clave
	     * @param params los parametros para customizar la clave
	     * @return el mensaje customizado
	     * @throws I18NMissingMessageException si NO se encuentra la clave
	     */
	    public final String message(final String key,final Object... params) throws ResourceBundleMissingKeyException {
	        if (key == null) throw new IllegalArgumentException("Cannot load bundle key: Missing key!");
	        
	        String outValue = _retrieveMessage(key);
	        if (outValue == null || outValue.length() == 0) {
	            switch (_missingKeyBehaviour) {
	                case RETURN_KEY: {
	                    outValue = "[" + key + "]";
	                    break;
	                }
	                case RETURN_NULL:
	                    outValue = null;
	                    break;
	                case THROW_EXCEPTION:
	                    throw new ResourceBundleMissingKeyException(key,_locale,_bundleChain);
	                default:
	            }
	        }
	        return outValue == null || params == null || params.length == 0 ? outValue 
	        																: MessageFormat.format(outValue,params);
	    }
	    /**
	     * Devuelve todos los mensajes cuyas claves empiezan por un prefijo dado
	     * Ej: Si en el bundle hay las siguientes claves:
	     * 			my.one = One
	     * 			my.two = Two
	     * 			yours.one = Your One
	     * 	   y se llama a:
	     * 		<code>
	     * 			messagesWithKeysStartingWith("my")
	     *		</code>
	     *	   se van a devolver los mensajes siguientes como un mapa
	     * 			my.one = One
	     * 			my.two = Two
	     * @param keyPrefix el prefijo
	     * @return
	     */
	    public final Map<String,String> messagesWithKeysStartingWith(final String keyPrefix) {
	        if (keyPrefix == null) throw new IllegalArgumentException("Cannot load bundle key: Missing key!");  
	        Map<String,String> outMessages = new HashMap<String,String>();
	        try {
	        	for (int i = 0; i < _bundleChain.length; i++) {
	        		String thisBundle = _bundleChain[i];
	        		
			        // Cargar el resourceBundle e iterar por todos los keys para encontrar las que empiezan
			        // por el valor dado
			    	ResourceBundle bundle = _retrieveBundle(thisBundle);
		    		Enumeration<String> keys = bundle.getKeys();
		    		if (keys != null && keys.hasMoreElements()) {
		    			do {
		    				String key = keys.nextElement();
		    				String msg = key.startsWith(keyPrefix) ? bundle.getString(key)
		    													   : null;
		    				if (msg != null) outMessages.put(key,msg);
		    			} while(keys.hasMoreElements());
		    		}
	        	}
	        } catch (MissingResourceException mrEx) {
	        	log.error("No se ha podido cargar el Resource bundle de la cadena {}",_bundleChain,mrEx);
	        }
	        return outMessages;
	    }
	    /**
	     * Returns a Map with all bundles messages
	     * @return
	     */
	    public final Map<String,String> messagesMap() {
	        Map<String,String> outMessages = new HashMap<String,String>();
	        try {
	        	for (int i = 0; i < _bundleChain.length; i++) {
	        		String thisBundle = _bundleChain[i];
	        		
			        // Cargar el resourceBundle e iterar por todos los keys para encontrar las que empiezan
			        // por el valor dado
			    	ResourceBundle bundle = _retrieveBundle(thisBundle);
		    		Enumeration<String> keys = bundle.getKeys();
		    		if (keys != null && keys.hasMoreElements()) {
		    			do {
		    				String key = keys.nextElement();
		    				String msg = bundle.getString(key);
		    				if (msg != null) outMessages.put(key,msg);
		    			} while(keys.hasMoreElements());
		    		}
	        	}
	        } catch (MissingResourceException mrEx) {
	        	log.error("No se ha podido cargar el Resource bundle de la cadena {}",_bundleChain,mrEx);
	        }
	        return outMessages;
	    }
	    @Override
	    public final String toString() {
	        return Arrays.toString(_bundleChain) + " (" + _locale + ")";
	    }
	/////////////////////////////////////////////////////////////////////////////////////////
	//	METODOS PRIVADOS
	/////////////////////////////////////////////////////////////////////////////////////////
	    /**
	     * Obtiene una clave buscando en todos los Bundles de la cadena en orden
	     * @param key la clave a buscar
	     * @return
	     * @throws I18NMissingMessageException
	     */
	    private final String _retrieveMessage(final String key) throws ResourceBundleMissingKeyException {
	    	String outKey = null;
	        try {
	        	for (int i = 0; i < _bundleChain.length; i++) {
	        		String thisBundle = _bundleChain[i];
		        	ResourceBundle bundle = _retrieveBundle(thisBundle);
		        	try {
		        		outKey = bundle.getString(key);
		        	} catch(MissingResourceException mkEx) {
		        		if (i == _bundleChain.length-1) {
		        			log.warn("NO se encuentra la clave {} en NINGUNO de los bundles de la cadena {}",
		        					 key,_bundleChain);
		        		} else {
		        			log.warn("NO se encuentra la clave {} en el bundle {}; se mira en el siguiente bundle de la cadena {}",
		        					 key,_bundleChain[i],_bundleChain[i+1]);
		        		}
		        	}
		        	if (outKey != null) break;
	        	}
	        } catch (MissingResourceException mrEx) {
	        	log.error("No se ha podido cargar el Resource bundle de la cadena {}",_bundleChain,mrEx);
	        }
	        return outKey;
	    }
	    private ResourceBundle _retrieveBundle(final String bundleName) {
	        if (_devMode) ResourceBundle.clearCache();	// OJO!!! se borra la cache en modo DEBUG de forma que SIEMPRE se vuelve a cargar
	        											//		  el bundle.... NO es eficiente!!! NO dejar NUNCA EN PROD
		    // Crear o cargar el bundle que para el Locale que se pasa en el constructor
		    // (es el que realmente permite el acceso a los mensajes)
	        // IMPORTANTE!!
	        //		- NO CACHEAR el ResourceBoundle ya que si ResourceBundle YA IMPLEMENTA UNA CACHE y adem�s si se cachea se inactiva la opci�n de recargar en caliente
	        //		- Utilizar un ResourceBundle.Control para personalizar la recarga en caliente en base a la politica de control definida en el ResourcesLoaderDef
	        //		- CACHEAR el ResourceBundle.Control ya que puede mantener estado sobre la recarga (ej: timeStamp de la �ltima recarga)
	    	ResourceBundle outBundle = _classLoader != null ? ResourceBundle.getBundle(bundleName,
	    																			   _locale,
	    																			   _classLoader,
	    																			   _control)
	    													: ResourceBundle.getBundle(bundleName,
	    																			   _locale,
	    																			   _control);
	        return outBundle;
	    }
	}
}
