package r01f.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import r01f.types.IsPath;

/**
 * Loads a file from the ClassPath
 * <h2>IMPORTANT!</h2>
 * ClassLoader and Class apply resource names differently
 * see: http://www.thinkplexx.com/learn/howto/java/system/java-resource-loading-explained-absolute-and-relative-names-difference-between-classloader-and-class-resource-loading
 * the ClassLoader methods needs RELATIVE paths, but the Class methods needs ABSOLUTE paths
 * <ul>
 * 		<li>
 * 		ResourcesLoaderFromClassPath.class.getResource(name) and ResourcesLoaderFromClassPath.class.getResourceAsStream(name)
 * 		needs absolute paths
 * 		</li>
 * 		<li>
 * 		ResourcesLoaderFromClassPath.class.getClassLoader().getResource(name) and ResourcesLoaderFromClassPath.class.getClassLoader().getResourceAsStream(name)
 * 		needs relativePaths.
 * 		</li>
 * </ul>
 * This class uses ResourcesLoaderFromClassPath.class.getClassLoader() so it needs RELATIVE paths. Normally this class ensures that the path
 * is RELATIVE and converts an absolute path into a relative one
 */
public class ResourcesLoaderFromClassPath
     extends ResourcesLoaderBase {
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	ResourcesLoaderFromClassPath(final ResourcesLoaderDef def) {
		super(def);
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		return true;	// no properties are needed
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    protected InputStream _doGetInputStream(final IsPath resourcePath,
    								        final boolean reload) throws IOException {
		// IMPORTANT: ClassLoader and Class apply resource names differently
		// see: http://www.thinkplexx.com/learn/howto/java/system/java-resource-loading-explained-absolute-and-relative-names-difference-between-classloader-and-class-resource-loading
		// The ClassLoader methods, NEEDS absolute paths while Class methods NEEDS relative paths
		//		- ResourcesLoaderFromClassPath.class.getResource(name) y ResourcesLoaderFromClassPath.class.getResourceAsStream(name)
		//	  	  use ABSOLUTE paths
		//	  	- BUT ResourcesLoaderFromClassPath.class.getClassLoader().getResource(name) y ResourcesLoaderFromClassPath.class.getClassLoader().getResourceAsStream(name)
		//		  use relative paths

		String theResourcePath = resourcePath.asRelativeString();
        InputStream outResourceIS = null;
        ClassLoader loader = ResourcesLoaderFromClassPath.class.getClassLoader();

        if (reload) {
        	// ... ensures NOT to use the loader's cache
        	URL url = loader.getResource(theResourcePath);
            if (url != null) {
            	URLConnection connection = url.openConnection();
                if (connection != null) {
                	// Disable caches to get fresh data for reloading.
                    connection.setUseCaches(false);
                    outResourceIS = connection.getInputStream();
                }
            }
        } else {
        	// ... can use the loader's cache
        	outResourceIS = loader.getResourceAsStream(theResourcePath);
        }
        return outResourceIS;
    }
}
