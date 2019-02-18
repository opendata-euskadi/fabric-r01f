package r01f.xmlproperties;

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;

/**
 * {@link XMLProperties} is the main component of the R01's properties systems
 * Properties have two levels
 * <ol>
 * 		<li>By application code</li>
 * 		<li>By component (module) within an application</li>
 * </ol>
 * Properties are defined in XML format by app code / module; this fact means that for a given
 * application code any number of modules may be defined
 * Example of an XML properties file:
 * <pre class="brush:xml">
 *          <misProps>
 *              <miProp>el valor</miProp>
 *              <miOtraProp value='a'></miOtraProp>
 *              <miOtraLista>
 *              	<prop1>value1</prop1>
 *              	<prop2>value2</prop2>
 *             	</miOtraLista>
 *          </misProps>
 * </pre>
 * 
 * Example of how application properties may be distributed in multiple XML files by module
 * <pre>
 * 		- 'main' 			-- general properties
 * 		- 'contentServer' 	-- content server related properties
 * 		- etc
 * </pre>
 * 
 * Each of the properties XML might be stored in a different place_
 * ie:  
 * <pre>
 * 		- 'main' 			-- stored in a file within the classpath 
 * 		- 'contentServer' 	-- stored in the BBDD
 * 		- etc
 * </pre>
 * 
 * <h2>COMPONENT LOADING</h2>
 * <hr/>
 * In order to a component to be loaded, the load method must be set before
 * (see {@link XMLPropertiesComponentDef})
 * <ol>
 * 	 	<li>The classpath MUST contain a folder named <b>{appCode}/components</b></li>
 * 		<li>Inside the <b>{appCode}/components</b> folder there MUST be a XML file for each component<br/>
 * 			<pre>
 * 				ie: If the appCode is xxx and it have two components (foo and bar) a xxx/components folder MUST exists in the classpath containing
 * 				    TWO xml files:
 * 						- xxx/components/xxx.foo.xml
 * 						- xxx/components/xxx.bar.xml
 * 			</pre>
 * 			Think about these files as an index or a definition of how / from where to load the real XML properties file
 * 			(ie: if the xml properties are real filesystem files, the component file contains the real path of the file
 * 				 if the xml properties are stored at a database table row, the component file contains how to connect to de bd and the table / row selection
 * 		</li>
 * </ol>
 * <h3>Environment dependent component loading</h3>
 * A system var named r01Env can be set (as a jvm argument -Dr01Env=loc or by means of System.setProperty("r01Env","loc"))
 * This var can set an environment where the components are loaded: it the r01Env var exists, the component loading will not be done from /{appCode}/components,
 * the components will be searched at: /{appCode}/components/{r01Env}/
 * 
 * <h2>XMLProperties usage</h2>
 * <hr/>
 * properties stored at XMLProperties files are retrieved using xpath sentences issued at a {@link XMLPropertiesForApp} object 
 * <pre class="brush:java">
 * 		XMLPropertiesForApp props = ...
 * 		props.of("componente").at("misProps/miProp").asString("defaultValue");
 * 		props.of("componente").at("miOtraProp/@value").asString();
 * </pre>
 * 
 * <h2>Property Caching</h2>
 * A high level vision of the cache system is:
 * <pre>
 * 		XMLProperties
 * 			|_Map<AppCode,XMLPropertiesForApp>  
 * 								|				
 * 								|
 * 								|_XMLPropertiesForAppCache  
 * 	</pre>
 * 
 * <h2>Property loading</h2>
 * In order to get a {@link XMLPropertiesForApp} object, there are many options:
 * <pre>
 * OPTION 1: Create the {@link XMLPropertiesForApp} by hand 
 * ------------------------------------------------------------------
 * </pre>
 * <pre class="brush:java">
 * 		// [1] Create an XMLProperties object
 * 		XMLProperties props = XMLProperties.create();
 * 		// [2] Get the properties for an application
 * 		XMLPropertiesForApp appProps = props.forApp(AppCode.forId("xx"),1000);
 * 		// [3] Access the properties of a component
 * 		String prop = props.of(component).propertyAt(xPath).asString()
 * 
 * 		// Or even simpler
 * 		String prop = XMLProperties.createForAppComponent(appCode,component)
 * 								   .notUsingCache()
 * 								   .propertyAt(xPath).asString();
 * </pre>
 * <b>IMPORTANTE</b>The {@link XMLPropertiesForApp} object maintains a cache of properties for each managed application code, so it's advisable to have 
 * 					a single instance of the {@link XMLProperties} object (ie: manage it as a guice singleton or make it static)
 * 
 * <pre>
 * OPCION 2: Use guice
 * ------------------------------------------------------------------
 * </pre>
 * It's advisable to have a SINGLE instance of {@link XMLPropertiesForApp} since it maintains a cache of properties<br />
 * A guice singleton can be used to achieve this pourpose:
 * <pre class="brush:java">
 * 		XMLPropertiesForApp appProps = Guice.createInjector(new XMLPropertiesGuiceModule())
 * 										  	.getInstance(XMLProperties.class).forApp(appCode);
 * 		String prop = props.of(component)
 * 						   .propertyAt(xPath).asString(defaultValue)
 * </pre>
 *
 * To let guice inject app component's properties, the @XmlPropertiesComponent annotation can be used: 
 * <pre class='brush:java'>
 * 		public class MyType {
 * 			@Inject @XMLPropertiesComponent("myComponent") 
 * 			XMLPropertiesForAppComponent _props;	<-- guice will inject an annotated XMLPropertiesForAppComponent instance
 * 			// ...
 * 			public void myMethod(..) {
 * 				String myProp = _props.propertyAt(xPath);
 * 			}
 * 		}
 * </pre>
 * ... to do so, there MUST exists a {@link XMLPropertiesForAppComponent} instance binded to a @XMLPropertiesComponent("myComponent") annotation
 * A guice's provider can be used: 
 * <pre class='brush:java'>
 *		@Override
 *		public void configure(final Binder binder) {
 *			// ... what ever other bindings
 *		}
 *		@Provides @XMLPropertiesComponent("myComponent")
 *		XMLPropertiesForAppComponent provideXMLPropertiesForAppComponent(final XMLProperties props) {
 *			// note that XMLProperties singleton will be injected to the provider by guice
 *			XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AppCode.forId("xx")),
 *																								 AppComponent.forId("myComponent"));
 *			return outPropsForComponent;
 *		}
 * </pre>
 * ... or directly using the binder:
 * <pre class='brush:java'>
 *	binder.bind(XMLPropertiesForAppComponent.class)
 *		  .annotatedWith(new XMLPropertiesComponent() {		// see [Binding annotations with attributes] at https://github.com/google/guice/wiki/BindingAnnotations
 *									@Override
 *									public Class<? extends Annotation> annotationType() {
 *										return XMLPropertiesComponent.class;
 *									}
 *									@Override
 *									public String value() {
 *										return "myComponent";
 *									}
 *		  				 })
 *		  .toProvider(new Provider<XMLPropertiesForAppComponent>() {
 *							@Override
 *							public XMLPropertiesForAppComponent get() {
 *								XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AppCode.forId("xx")),
 *																								 					 AppComponent.forId("myComponent"));
 *								return outPropsForComponent;
 *							}
 *		  			  });
 * </pre>
 * ... even simpler:
 *	binder.bind(XMLPropertiesForAppComponent.class)
 *		  .annotatedWith(new XMLPropertiesComponentImpl("myComponent"))
 *		  .toProvider(new Provider<XMLPropertiesForAppComponent>() {
 *							@Override
 *							public XMLPropertiesForAppComponent get() {
 *								XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AppCode.forId("xx")),
 *																								 					 AppComponent.forId("myComponent"));
 *								return outPropsForComponent;
 *							}
 *		  			  });
 */
public interface XMLProperties {
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets an app properties manager {@link XMLPropertiesForApp} 
	 * @param appCode app code
	 * @return the manager that provides access to components and from there to properties
	 */
	public <A extends AppCode> XMLPropertiesForApp forApp(final A appCode);
	/**
	 * Gets an app properties manager {@link XMLPropertiesForApp} 
	 * @param appCode app code
	 * @return the manager that provides access to components and from there to properties
	 */
	public XMLPropertiesForApp forApp(final String appCode);
	/**
	 * Gets an app properties manager (a {@link XMLPropertiesForApp} instance) and from it
	 * an manager of component properties (a {@link XMLPropertiesForAppComponent}) that provides
	 * access to properties
	 * @param appCode
	 * @param component
	 * @return
	 */
	public <A extends AppCode> XMLPropertiesForAppComponent forAppComponent(final A appCode,final AppComponent component);
	/**
	 * Gets an app properties manager (a {@link XMLPropertiesForApp} instance) and from it
	 * an manager of component properties (a {@link XMLPropertiesForAppComponent}) that provides
	 * access to properties
	 * @param appCode
	 * @param component
	 * @return
	 */
	public XMLPropertiesForAppComponent forAppComponent(final String appCode,final String component);
}
