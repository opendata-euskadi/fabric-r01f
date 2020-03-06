package r01f.resources;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.patterns.IsBuilder;



/**
 * Factory of {@link ResourcesLoader} instances
 */
public class ResourcesLoaderBuilder
  implements IsBuilder {
///////////////////////////////////////////////////////////////////////////////
// 	Default ResourcesLoader
///////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	private enum DEFAULT {
		CLASSPATH();

		@Getter private ResourcesLoader _resourcesLoader;
		private DEFAULT() {
			_resourcesLoader = new ResourcesLoaderFromClassPath(ResourcesLoaderDef.DEFAULT);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * default {@link ResourcesLoader} (uses classPath loader and NO reloading).
	 */
	public static final ResourcesLoader DEFAULT_RESOURCES_LOADER = DEFAULT.CLASSPATH
															 			  .getResourcesLoader();
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the default classpath no reloading {@link ResourcesLoader}
	 */
	public static ResourcesLoader createDefaultResourcesLoader() {
		return DEFAULT_RESOURCES_LOADER;
	}
	/**
	 * Creates a {@link ResourcesLoader} for a given definition
	 * <pre class='brush:java'>
	 * 		ResourcesLoader loader = ResourcesLoaderBuilder.createResourcesLoaderFor(ResourcesLoaderDefBuilder.create("myResLoader")
     * 														  												  .usingClassPathResourcesLoader()
     * 														  												  .reloadingAsDefinedAt(ResourcesReloadControlDefBuilder.createForVoidReloading()))
	 * </pre>
	 * @param def
	 * @return
	 */
	public static ResourcesLoader createResourcesLoaderFor(final ResourcesLoaderDef def) {
		ResourcesLoader outResLoader = null;
		if (def.getLoader() == null) throw new IllegalStateException("NO loader was set");
		switch (def.getLoader()) {
		case BBDD:
			outResLoader = new ResourcesLoaderFromBBDD(def);
			break;
		case CONTENT_SERVER:
			break;
		case URL:
			outResLoader = new ResourcesLoaderFromURL(def);
			break;
		case FILESYSTEM:
			outResLoader = new ResourcesLoaderFromFileSystem(def);
			break;
		case CLASSPATH:
		default:
			outResLoader = new ResourcesLoaderFromClassPath(def);
		}
		return outResLoader;
	}
}
