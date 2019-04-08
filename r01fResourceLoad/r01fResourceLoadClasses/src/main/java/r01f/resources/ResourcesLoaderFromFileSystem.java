package r01f.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import r01f.types.IsPath;

/**
 * Loads a file from the file system
 */
public class ResourcesLoaderFromFileSystem 
     extends ResourcesLoaderBase {
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	ResourcesLoaderFromFileSystem(ResourcesLoaderDef def) {
		super(def);
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		return true;	// no properties are needed
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    protected InputStream _doGetInputStream(final IsPath resourceName,
    								  	    final boolean reload) throws IOException {
        InputStream fileIS = new FileInputStream(resourceName.asAbsoluteString());
        return fileIS;
    }
}
