package r01f.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import r01f.types.IsPath;
/**
 * Interface for the resources loader types
 * 
 * To create a {@link ResourcesLoader} some things are needed:
 * <ul>
 * 		<li>The XPath of a {@link XMLProperties} file where the {@link ResourcesLoader} definition can be found.<br />
 * 			This definition is an object of {@link ResourcesLoaderDef} type</li>
 * 		<li>An instance of a type in charge of creating the {@link ResourcesLoader} instance (a {@link ResourcesLoader} factory)<br />
 * 			This factory object is an object of {@link ResourcesLoaderBuilder} type</li>
 * </ul>
 * 
 * OPTION 1: (normal) -> Use GUICE to inject the ResourcesLoader<br/>
 * -------------------------------------------------------------
 * <pre class='brush:java'>
 * 		public class MyType {
 * 			@Inject ResourcesLoaderFactory _resLoaderFactory;
 * 
 * 			public ResourcesLoader getResourcesLoader() {
 * 				// [1] - Load the resourcesLoader definition
 * 				Path resLoaderDefXPath = Path.of("/resourcesLoaders/myResourcesLoader");
 * 				ResourcesLoaderDef resLoaderDef = ResourcesLoaderDef.forDefinition(appCode,component,resLoaderDefXPath);
 * 
 * 				// [2] - Create the resourcesLoader 
 * 				ResourcesLoader resLoader = _resLoaderFactory.createResourcesLoaderFor(resLoaderDef);
 * 				
 * 				// [3] - Return 
 * 				return resLoader;
 * 			}
 * 		}
 * </pre>
 * OPTION 2: -> Manual creation of the resources loader using a definition in an XMLProperties file<br/>
 * -----------------------------------------------------------------------------------------------------
 * The {@link ResourcesLoaderDef} needs a {@link XMLProperties} instance. If guice is not in charge of injecting the {@link XMLProperties} object
 * to the {@link ResourcesLoaderDefLoader} instance, this must be done by hand:
 * <pre class='brush:java'>
 * 		public class MyType {
 * 			public ResourcesLoader getResourcesLoader() {
 * 				// [1] - Get an XMLProperties instance
 * 				XMLProperties props = new XMLProperties(resourcesFactory,
 * 														AppCode.forId("xx"),1000,
 * 														true);
 * 				
 * 				// [2] - Load the resourcesLoader definition
 * 				Path resLoaderDefXPath = Path.of("/resourcesLoaders/myResourcesLoader");
 * 				ResourcesLoaderDefLoader resDefLoader = new ResourcesLoaderDefLoader(props);	// the xmlProperties must be provided to the ResourcesLoaderDefLoader
 * 				ResourcesLoaderDef resLoaderDef = resLoaderDef.loadForDefLocation(resLoaderDefXPath);
 * 
 * 				// [2] - Create the resourcesLoader 
 * 				ResourcesLoader resLoader = resourcesLoaderFactory.createResourcesLoaderFor(resLoaderDef);
 * 				
 * 				// [3] - Return 
 * 				return resLoader;
 * 			}
 * 		}
 * </pre>
 * OPTION 3: -> Full Manual creation of the resources loader<br/>
 * -----------------------------------------------------------------------------------------------------
 * The {@link ResourcesLoaderDefLoader} needs the definition of how to load the Resources (a {@link ResourcesLoaderDef} instance),
 * this must be done by hand:
 * <pre class='brush:java'>
 * 		public class MyType {
 * 			public ResourcesLoader getResourcesLoader() {
 *				// [0] - Create the ResourcesLoaderFactory by hand
 * 				ResourcesLoaderFactory resourcesLoaderFactory = ResourcesLoaderFactoryImpl.create();
 *		
 * 				// [1] - Definition of the resourcesLoader (the default classPathLoader)
 *				ResourcesLoaderDef resDefLoader = ResourcesLoaderDef.DEFAULT;
 * 
 * 				// [2] - Create the resourcesLoader 
 *				ResourcesLoader resLoader = resourcesLoaderFactory.createResourcesLoaderFor(resDefLoader);
 * 				
 * 				// [3] - Return 
 * 				return resLoader;
 * 			}
 * 		}
 * </pre>
 */
public interface ResourcesLoader {
///////////////////////////////////////////////////////////////////////////////
// CONFIG
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link ResourcesLoader} config as a {@link ResourcesLoaderDef} object
	 * @return
	 */
	public ResourcesLoaderDef getConfig();
///////////////////////////////////////////////////////////////////////////////
// 	INPUTSTREAM
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a {@link InputStream} to a resource. If the resource exists in any cache, it's taken from the cache;
	 * if it doesn't exists there the resource is loaded
	 * @param resourcePath path to the resource
	 * @return The {@link InputStream} to the resource
	 * @throws IOException if the resource could not be retrieved
	 */
	public InputStream getInputStream(final IsPath resourcePath) throws IOException;
	/**
	 * Gets a {@link InputStream} to a resource
	 * @param resourcePath path to the resource
	 * @param reload it will be <code>true</code> if the resource is to be reloaded not taking cached data (if it exists)
	 * @return The {@link InputStream} to the resource
	 * @throws IOException if the resource could not be retrieved
	 */
	public InputStream getInputStream(final IsPath resourcePath,
									  final boolean reload) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  READER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a {@link Reader} to a resource. If the resource exists in any cache, it's taken from the cache;
	 * if it doesn't exists there the resource is loaded
	 * @param resourcePath path to the resource
	 * @return The {@link InputStream} to the resource
	 * @throws IOException if the resource could not be retrieved
	 */
	public Reader getReader(final IsPath resourcePath) throws IOException;
	/**
	 * Gets a {@link Reader} to a resource
	 * @param resourcePath path to the resource
	 * @param reload it will be <code>true</code> if the resource is to be reloaded not taking cached data (if it exists)
	 * @return The {@link InputStream} to the resource
	 * @throws IOException if the resource could not be retrieved
	 */
	public Reader getReader(final IsPath resourcePath,
							final boolean reload) throws IOException;
}
