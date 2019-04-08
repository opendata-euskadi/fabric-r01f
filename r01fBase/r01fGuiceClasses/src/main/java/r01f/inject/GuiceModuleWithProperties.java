package r01f.inject;

import com.google.inject.Module;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.types.Path;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.XMLPropertyWrapper;

/**
 * Base type for GUICE modules that provides access to {@link XMLProperties}
 * 
 * Sometimes during a module bootstraping, a module needs access to a property
 * BUT:
 * 	1.- {@link XMLProperties} uses the guice injector
 *  2.- ... which is being configured in that moment... so it's not available
 * ... so {@link XMLProperties} cannot be loaded using the guice injector... another way must be used
 * 
 * Usage: Let the guice module extend {@link GuiceModuleWithProperties} 
 * <pre class='brush:java'>
 *		public class MyDBPersistenceGuiceModule 
 *		     extends GuiceModuleWithProperties {
 *
 *			public MyDBPersistenceGuiceModule() {
 *				super(R01MInternalAppCode.CATALOG_CORE.code(),
 *					  "bbdd");
 *			}
 *			@Override
 *			public void configure(final Binder binder) {
 *				Properties props = _loadBBDDConnectionPropertiesForPool();
 *				...
 *			}
 *			private Properties _loadBBDDConnectionPropertiesForPool() {
 *				Properties props = this.propertyAt("bbdd/connection")
 *							   		   .asProperties();
 *			}
 *		}
 * </pre>
 */
@Slf4j
public abstract class GuiceModuleWithProperties 
		   implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppComponent
	 */
	protected final AppCode _appCode;
	/**
	 * Application module
	 */
	private AppComponent _appComponent;
	/**
	 * Application properties
	 */
	private final XMLPropertiesForApp _props;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GuiceModuleWithProperties(final AppCode appCode) {
		_appCode = appCode;
		// Create a XMLPropertiesManager for the app
		_props = XMLPropertiesBuilder.createForApp(appCode)
							  .notUsingCache();
	}
	public GuiceModuleWithProperties(final AppCode appCode,final AppComponent propsModule) {
		this(appCode);
		_appComponent = propsModule;  
	}
	public GuiceModuleWithProperties(final String appCode) {
		this(AppCode.forId(appCode));
	}
	public GuiceModuleWithProperties(final String appCode,final String propsModule) {
		this(appCode);
		_appComponent = AppComponent.forId(propsModule);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CARGA DE PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	public AppCode getAppCode() {
		return _appCode;
	}
	public AppComponent getAppComponent() {
		return _appComponent;
	}
	/**
	 * @return the properties for all components
	 */
	public XMLPropertiesForApp getProperties() {
		return _props;
	}
	/**
	 * @return the component properties
	 */
	public XMLPropertiesForAppComponent getComponentProperties() {
		return _props.forComponent(_appComponent);
	}
	/**
	 * Returns a property for an app's module
	 * @param module
	 * @param xPath
	 * @return a {@link XMLPropertyWrapper} object that encapsulates access to property's value
	 */
	public XMLPropertyWrapper getProperty(final String module,final String xPath) {
		return this.getProperty(AppComponent.forId(module),xPath);
	}
	/**
	 * Returns a property for an app's module
	 * @param module
	 * @param xPath
	 * @return a {@link XMLPropertyWrapper} object that encapsulates access to property's value
	 */
	public XMLPropertyWrapper getProperty(final AppComponent module,final String xPath) {
		XMLPropertyWrapper prop = new XMLPropertyWrapper(_props.of(module),Path.from(xPath));
		return prop;
	}
	/**
	 * Returns a property
	 * (if the module is not provided at the constructor, it uses "default" as the module name)
	 * @param xPath 
	 * @return a {@link XMLPropertyWrapper} that provides access to the property
	 */
	public XMLPropertyWrapper propertyAt(final String xPath) {
		if (_appComponent == null) log.warn("The properties module name was NOT given to the {} constructor; 'default' module is assumed",this.getClass().getName());
		AppComponent thePropsModule = _appComponent != null ? _appComponent
													 	    : AppComponent.forId("default");
		return this.getProperty(thePropsModule,xPath);
	}
}
