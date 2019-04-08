package r01f.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import com.google.common.base.Preconditions;

import lombok.experimental.Accessors;
import r01f.types.IsPath;

@Accessors(prefix="_")
abstract class ResourcesLoaderBase 
    implements ResourcesLoader {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private ResourcesLoaderDef _def;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourcesLoaderBase() {
		// nothing
	}
	public ResourcesLoaderBase(final ResourcesLoaderDef def) {
		Preconditions.checkArgument(def != null,"The definition MUST NOT be null");
		if (!_checkProperties(def.getLoaderProps())) throw new IllegalArgumentException("The loader definition has invalid properties!");
		_def = def;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	abstract boolean _checkProperties(final Map<String,String> props);
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ResourcesLoaderDef getConfig() {
		return _def;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public InputStream getInputStream(final IsPath resourcePath) throws IOException {
		return _doGetInputStream(resourcePath,
							     false);
	}
	@Override
	public InputStream getInputStream(final IsPath resourcePath,
									  final boolean reload) throws IOException {
		return _doGetInputStream(resourcePath,
							     reload);
	}
	protected abstract InputStream _doGetInputStream(final IsPath resourcePath,
									               	 final boolean reload) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Reader getReader(final IsPath resourcePath) throws IOException {
		return _getReader(resourcePath,
						  false);
	}
	@Override
	public Reader getReader(final IsPath resourcePath,
							final boolean reload) throws IOException {
		return _getReader(resourcePath,
						  reload);
	}
    private Reader _getReader(final IsPath resourcePath,
    						  final boolean reload) throws IOException {
    	return new InputStreamReader(_doGetInputStream(resourcePath,reload),
    								 _def.getCharset());
    }
}
 