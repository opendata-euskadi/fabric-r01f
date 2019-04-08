package r01f.configproperties;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bundles.ResourceBundleControl;
import r01f.bundles.ResourceBundleControl.ResourceBundleControlResourceNameResolver;
import r01f.bundles.ResourceBundleControlBuilder;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.patterns.IsBuilder;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderDef;
import r01f.resources.ResourcesLoaderDefBuilder;
import r01f.resources.ResourcesReloadControl;
import r01f.types.Path;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertyLocation;

/**
 * A factory of {@link ConfigProperties} objects
 * It can be used in two ways:
 * <ul>
 * 		<li>If the resource loading/reloading is defined in a XMLProperties file, then a XMLProperties instance shoud be provided
 * 			and the method loadedUsingDefinitionAt(..) should be used to get the {@link ConfigProperties} instance</li>
 * 		<li>It the resource loading/reloading is NOT defined in a XMLProperties file and a {@link ResourcesLoaderDef} config object is used
 * 			instead, then no XMLProperties instance will be provided BUT the method  loadedUsingDefinition({@link ResourcesLoaderDef}) shoud
 * 			be used to get an instance of {@link ConfigProperties} type</li>
 * </ul>
 * @See {@link ConfigProperties}
 */
@Accessors(prefix="_")
public class ConfigPropertiesBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED STATUS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * {@link ResourceBundleControl} type instance factory that maintains a cache of {@link ResourceBundleControl}
     * instances
     * The {@link ResourceBundleControl} type manages the loading/reloading of resources
     */
    private transient final XMLProperties _xmlProperties;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
    @Inject
    private ConfigPropertiesBuilder(final XMLProperties xmlProps) {
    	_xmlProperties = xmlProps;
    }
    /**
     * Creates a {@link ConfigPropertiesBuilder} instance 
     * This builder is the one to use when not using injection (guice)
     * <pre class='brush:java'>
     * 		ConfigPropertiesFactory factory = ConfigPropertiesFactory.create(xmlProperties)
     * 																 .forBundle("myBundle")
     * 																 .loadedUsingDefinitionAt("props/resLoaderdef")
     * </pre>
     * @param resControlFactory
     * @return
     */
    public static ConfigPropertiesBuilder create(final XMLProperties xmlProperties) {
    	ConfigPropertiesBuilder outFactory = new ConfigPropertiesBuilder(xmlProperties);
    	return outFactory;
    }
    /**
     * Creates a {@link ConfigPropertiesBuilder} instance 
     * This builder is the one to use when not using injection (guice)
     * <pre class='brush:java'>
     * 		ConfigPropertiesFactory factory = ConfigPropertiesFactory.create()
     * 																 .forBundle("myBundle")
     * 																 .loadedUsingDefinition(ResourcesLoaderDef.DEFAULT)
     * </pre>
     * @return
     */
    public static ConfigPropertiesBuilder create() {
    	ConfigPropertiesBuilder outFactory = new ConfigPropertiesBuilder(null);		// no xml properties
    	return outFactory;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////    
    public ConfigPropertiesBundleBuilderBundleStep forBundle(final String bundleSpec) {
    	return new ConfigPropertiesBundleBuilderBundleStep(bundleSpec);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
    @RequiredArgsConstructor
    public final class ConfigPropertiesBundleBuilderBundleStep {
    	private final String _bundleSpec;
    	
	    /**
	     * Constructor from {@link ResourcesLoader} and the resources load and re-load control {@link ResourcesReloadControl}
	     * IMPORTANT!! This constructor is ONLY called from the I18NServiceFactory
	     * @param resourceBundleControlFactory factory of {@link ResourceBundleControl} instances 
	     * @param resLoaderDef 
	     * @param bundleSpec 
	     * @param missingKeyBehaviour 
	     */
	    public ConfigProperties loadedUsingDefinition(final ResourcesLoaderDef resLoaderDef) {
	    	ResourceBundleControl bundleControl = ResourceBundleControlBuilder.forLoadingDefinition(resLoaderDef);
	    	bundleControl.setNameResolver(new NameResolver());		// avoid loading properties for diferent locales
	    	ConfigProperties outCfgProps = new ConfigProperties(_bundleSpec,
	    														bundleControl);
	    	return outCfgProps;
	    }
	    public ConfigProperties loadedUsingDefinitionAt(final AppCode appCode,final AppComponent component,final Path xPath) {
	    	return this.loadedUsingDefinitionAt(XMLPropertyLocation.createFor(appCode,component,xPath));
	    }
	    public ConfigProperties loadedUsingDefinitionAt(final XMLPropertyLocation resLoaderDefLocation) {
	    	if (_xmlProperties == null) throw new IllegalStateException("No XMLProperties set; maybe #loadedUsingDefinition(ResourcesLoaderDef) method might be used instead"); 
	    	// Get the loader definition from the XMLProperties file
	    	ResourcesLoaderDef resLoaderDef = ResourcesLoaderDefBuilder.forDefinitionAt(_xmlProperties,
	    																		 		resLoaderDefLocation);
	    	return this.loadedUsingDefinition(resLoaderDef);
	    }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  ResourceBundleControl wrapper
/////////////////////////////////////////////////////////////////////////////////////////
     class NameResolver
implements ResourceBundleControlResourceNameResolver {
		@Override
		public String toBundleName(final String baseName,final Locale locale) {
			// Do NOT include the locale 
			return baseName;
		}
    	@Override
    	public List<Locale> getCandidateLocales(final String baseName,final Locale locale) {
    		// Just to avoid ResourceBundle.Control tring to load the Properties file sometimes (one for each locale),
    		return Arrays.asList(Locale.getDefault());	
    	}
		@Override
		public Locale getFallbackLocale(String baseName, Locale locale) {
			return Locale.getDefault();
		}
    }
}
