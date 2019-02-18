package r01f.bundles;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.patterns.IsBuilder;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.resources.ResourcesLoaderDef;
import r01f.resources.ResourcesLoaderDefBuilder;
import r01f.resources.ResourcesReloadControl;
import r01f.resources.ResourcesReloadControlBuilder;
import r01f.resources.ResourcesReloadControlDef;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertyLocation;

/**
 * Builder of {@link ResourceBundleControl} instances
 * It maintains an internal cache of created {@link ResourceBundleControl} objects by {@link ResourcesLoaderDef} id
 * <pre>
 * SIDE NOTE:	This is NOT a RESOURCES cache, it's simply a {@link ResourceBundleControl} instances cache that  
 * 				is used to access the {@link ResourcesLoader} and {@link ResourcesReloadControl} 
 * </pre>
 * 
 * Usually the {@link ResourceBundleControlBuilder} is injected using GUICE:
 * <pre class='brush:java'>
 * 		public myGuiceManagedType {
 * 			@Inject
 * 			private ResourceBundleControlBuilder _resBundleControlBuilder;
 * 
 * 			public void myMethod(..) {
 * 				ResourceBundleControl resBundleControl = _resBundleControlBuilder.loadingDefinitionAt(appCode,component,xPath);
 * 				ResourceBundle bundle = resBundleControl.newBundle(...);
 * 			}
 * 		}
 * </pre>
 * 
 * When a {@link ResourceBundleControl} is needed and GUICE is NOT used, the {@link XMLProperties} object is NOT injected to the
 * {@link ResourceBundleControlBuilder} and it has to be provided by hand:
 * <pre class='brush:java'>
 * 		// 1-Create a XMLProperties instance
 * 		XMLProperties props = XMLProperties.create();
 * 		// 2-Create the ResourceBundleControl using the factory		
 * 		ResourceBundleControl control = ResourceBundleControlBuilder.createUsing(props)
 * 																	.loadingDefinitionAt(appCode,component,xPath); 
 * </pre>
 * 
 * It's also possible to create the resources loading definition by hand (not loaded from a xml properties file) and
 * create a {@link ResourceBundle}
 * <pre class='brush:java'>
 * 		ResourceBundleControl control = ResourceBundleControlFactory.forLoadingDefinition(ResourcesLoaderDefBuilder.create("myLoaderId")		// id for caching the loader
 * 														 														   .usingLoader(ResourcesLoaderType.CLASSPATH)
 * 														 														   .reloadingAs(ResourcesReloadControlDef.periodicReloading("5s"));
 * </pre>
 */
@Accessors(prefix="_")
public class ResourceBundleControlBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Cache of {@link ResourceBundleControl} instances indexes by its definition location
     * at the xml properties file
     */
    static final ConcurrentHashMap<String,ResourceBundleControl> _resourceBundleControlCache = new ConcurrentHashMap<String,ResourceBundleControl>();
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED STATUS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter private final XMLProperties _xmlProperties;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor to be used when an instance is NOT managed by GUICE
     * @param props
     */
    public ResourceBundleControlBuilder(final XMLProperties props) {
    	_xmlProperties = props;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a {@link ResourceBundleControlBuilder} from a {@link XMLProperties} instance
     * which is used to load the definition of the loading/reloading of the resources (an instance of {@link ResourcesLoaderDef})
     * This is the builder to use when not injected (in a not guice injected context)
     * <pre class='brush:java'>
     * 		ResourceBundleControl resBundleControl = ResourceBundleControlBuilder.createUsing(xmlProperties)
     * 																			 .loadingDefinitionAt(appCode,component,xPath);
     * </pre> 
     * @param props the {@link XMLProperties} instance
     * @return the {@link ResourceBundleControlBuilder}
     */
    public static ResourceBundleControlBuilder createUsing(final XMLProperties props) {
    	return new ResourceBundleControlBuilder(props);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
    public ResourceBundleControl loadingDefinitionAt(final XMLPropertyLocation loc) {
    	return ResourceBundleControlBuilder.forLoadingDefinitionAt(_xmlProperties,
    															   loc);
    }
    public ResourceBundleControl loadingDefinitionAt(final AppCode appCode,final AppComponent component,
    												 final Path definitionPath) {
    	ResourceBundleControl outControl = ResourceBundleControlBuilder.forLoadingDefinitionAt(_xmlProperties, 
    															   							   appCode,component,definitionPath);
    	return outControl;
    }
    private static ResourceBundleControl _createResourceBundleControl(final ResourcesLoaderDef resLoaderDef) {		
		// [1] - Create the ResourcesLoader and the ResourcesReloadControl using the injected factory
		ResourcesLoader resourcesLoader = ResourcesLoaderBuilder.createResourcesLoaderFor(resLoaderDef);
		ResourcesReloadControl resourcesReloadControl = null;
		if (resLoaderDef.getReloadControlDef() != null) {
			resourcesReloadControl = ResourcesReloadControlBuilder.createFor(resLoaderDef.getReloadControlDef());
		} else {
			resourcesReloadControl = ResourcesReloadControlBuilder.createFor(ResourcesReloadControlDef.DEFAULT);
		}
		// [2] - Create the ResourceBundleControl
		ResourceBundleControl resourceBundleControl = new ResourceBundleControl(resourcesLoader,resourcesReloadControl);
		
		// Return
		return resourceBundleControl;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER STATIC COUNTERPARTS
/////////////////////////////////////////////////////////////////////////////////////////
    public static ResourceBundleControl defaultResourceBundleControl() {
    	ResourcesLoaderDef resLoaderDef = ResourcesLoaderDefBuilder.getDefault();
    	return ResourceBundleControlBuilder.forLoadingDefinition(resLoaderDef);
    }
	public static ResourceBundleControl forLoadingDefinition(final ResourcesLoaderDef resLoadDef) {
    	String id = resLoadDef.getId();
    	if (Strings.isNullOrEmpty(id)) throw new IllegalArgumentException("The ResouresLoaderDef must have an id in order to cache the ResourcesLoader");
    	ResourceBundleControl outCtrl = _resourceBundleControlCache.get(id);
    	if (outCtrl == null) {
			// [1] - Create the ResourceBundleControl
			ResourceBundleControl resourceBundleControl = _createResourceBundleControl(resLoadDef);
			
			// [2] - Cache
			outCtrl = _cache(id,resourceBundleControl);
    	}
		return outCtrl;
    }
    public static ResourceBundleControl forLoadingDefinitionAt(final XMLProperties xmlProperties,
    														   final XMLPropertyLocation loc) {
    	return ResourceBundleControlBuilder.forLoadingDefinitionAt(xmlProperties,
    															   loc.getAppCode(),loc.getComponent(),loc.getXPath());
    }
    public static ResourceBundleControl forLoadingDefinitionAt(final XMLProperties xmlProperties,
    														   final AppCode appCode,final AppComponent component,final Path definitionPath) {
    	XMLPropertyLocation propLoc = new XMLPropertyLocation(appCode,component,definitionPath);
    	String id = propLoc.composeId();
    	ResourceBundleControl outCtrl = _resourceBundleControlCache.get(id);
    	if (outCtrl == null) {
			// [1]-Get the ResourcesLoader definition
    		ResourcesLoaderDef resLoaderDef = ResourcesLoaderDefBuilder.forDefinitionAt(xmlProperties,
    																			 		appCode,component,definitionPath);
    		if (resLoaderDef == null) throw new IllegalStateException(Throwables.message("The resources loader definition was NOT found at {} in {} component of {} app",
    																					 definitionPath,component,appCode));
			// [2] - Create the ResourceBundleControl
			ResourceBundleControl resourceBundleControl = _createResourceBundleControl(resLoaderDef);
			
			// [3] - Cache
			outCtrl = _cache(id,resourceBundleControl);
    	}
		return outCtrl;
    }
    public static ResourceBundleControl forLoadingDefinitionAt(final XMLPropertiesForApp xmlPropertiesForApp,
    														   final AppComponent component,final Path definitionPath) {
    	XMLPropertyLocation propLoc = new XMLPropertyLocation(xmlPropertiesForApp.getAppCode(),component,definitionPath);
    	String id = propLoc.composeId();
    	ResourceBundleControl outCtrl = _resourceBundleControlCache.get(id);
    	if (outCtrl == null) {
			// [1]-Get the ResourcesLoader definition
    		ResourcesLoaderDef resLoaderDef = ResourcesLoaderDefBuilder.forDefinitionAt(xmlPropertiesForApp,
    																			 		component,definitionPath);
			// [2] - Create the ResourceBundleControl
			ResourceBundleControl resourceBundleControl = _createResourceBundleControl(resLoaderDef);
			
			// [3] - Cache
			outCtrl = _cache(id,resourceBundleControl);
    	}
		return outCtrl;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    private static ResourceBundleControl _cache(final String id,final ResourceBundleControl ctrl) {
    	ResourceBundleControl outCtrl = null;
		ResourceBundleControl existing = _resourceBundleControlCache.putIfAbsent(id,ctrl);
		if (existing != null) {
			outCtrl = existing;					// discard the new 
		} else {	
			outCtrl = ctrl;						// the new one
		}
		return outCtrl;
    }

}
