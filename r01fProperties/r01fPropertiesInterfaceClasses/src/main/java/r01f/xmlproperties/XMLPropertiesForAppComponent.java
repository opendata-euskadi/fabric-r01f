package r01f.xmlproperties;

import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * Encapsulates properties for an app's component
 * 
 * It can be injected like:
 * <pre class='brush:java'>
 * 		@Inject @XMLPropertiesComponent("default") 
 * 		XMLPropertiesForAppComponent _componentProps;
 * 		
 * 		public someMethod() {
 * 			_componentProps.propertyAt(xPath);
 * 		}
 * </pre>
 * ... in order to do so, just create a guice Provider like:
 * <pre class='brush:java'>
 *		@Override
 *		public void configure(Binder binder) {
 *			// ... what ever
 *		}
 *		@Provides @XMLPropertiesComponent("myComponent")
 *		XMLPropertiesForAppComponent provideXMLPropertiesForAppComponent(final XMLProperties props) {
 *			XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AppCode.forId("r01m")),
 *																								 "myComponent");
 *			return outPropsForComponent;
 *		}
 * </pre>
 */
@RequiredArgsConstructor
public class XMLPropertiesForAppComponent {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final XMLPropertiesForApp _propertiesForAppManager;
	private final AppComponent _component;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public AppCode getAppCode() {
		return _propertiesForAppManager.getAppCode();
	}
	public AppComponent getAppComponent() {
		return _component;				
	}
	/**
	 * @return true if the component's properties file exists
	 */
	public boolean existsPropertiesFile() {
		return _propertiesForAppManager.of(_component)
									   .existsComponentPropertiesFile();
	}
	/**
	 * Retrieves a property from it's xPath in the XMLProperties file
	 * @param xPath the XPath
	 * @return a property wrapper that offers different type retrieving
	 */
	public XMLPropertyWrapper propertyAt(final String xPath) {
		return this.propertyAt(Path.from(xPath));
	}
	/**
	 * Retrieves a property from it's xPath in the XMLProperties file
	 * @param xPath the XPath
	 * @return a property wrapper that offers different type retrieving
	 */
	public XMLPropertyWrapper propertyAt(final Path xPath) {
		XMLPropertyWrapper prop = new XMLPropertyWrapper(_propertiesForAppManager.of(_component),
														 xPath);
		return prop;
	}
	/**
	 * Retrieves a property from it's xPath in the XMLProperties file
	 * @param xPathWithPlaceHolders a XPath sentence with placeholders that are replaced by vars
	 * @param vars the vars
	 * @return a property wrapper that ofers diferent type retrieving
	 */
	public XMLPropertyWrapper propertyAt(final String xPathWithPlaceHolders,final String... vars) {
		String theXPath = Strings.customized(xPathWithPlaceHolders,
											 vars);
		return this.propertyAt(theXPath);
	}
	/**
	 * Retrieves a property from it's xPath in the XMLProperties file
	 * @param xPathWithPlaceHolders a XPath sentence with placeholders that are replaced by vars
	 * @param vars the vars
	 * @return a property wrapper that ofers diferent type retrieving
	 */
	public XMLPropertyWrapper propertyAt(final Path xPathWithPlaceHolders,final String... vars) {
		return this.propertyAt(xPathWithPlaceHolders.asString(),vars);
	}
}
