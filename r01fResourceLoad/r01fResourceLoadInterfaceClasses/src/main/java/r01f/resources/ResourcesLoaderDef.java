package r01f.resources;

import java.nio.charset.Charset;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * ResourcesLoader definition<br/>
 * @see ResourcesLoaderDefBuilder
 */
@MarshallType(as="resourcesLoader")
@Accessors(prefix="_")
public class ResourcesLoaderDef 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
// 	RESOURCES LOADERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static enum ResourcesLoaderType {
		CLASSPATH,
		FILESYSTEM,
		URL,
		CONTENT_SERVER,
		BBDD;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	DEFAULT DEFINITION
/////////////////////////////////////////////////////////////////////////////////////////
	public static final ResourcesLoaderDef DEFAULT = new ResourcesLoaderDef("DefaultClassPathLoaderDef",
																	  		ResourcesLoaderType.CLASSPATH,
																	  		ResourcesReloadControlDef.DEFAULT);
///////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter	@Setter private String _id;
	
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ResourcesLoaderType _loader;
	
	@MarshallField(as="reloadControl")
	@Getter @Setter private ResourcesReloadControlDef _reloadControlDef;
	
	@MarshallField(as="props")
	@Getter @Setter private Map<String,String> _loaderProps;
	
	@MarshallField(as="charset",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _charsetName;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourcesLoaderDef() {
		// no args constructor
	}
	public ResourcesLoaderDef(final String id,final ResourcesLoaderType type,final ResourcesReloadControlDef reloadControlDef) {
		_id = id;
		_loader = type;
		_reloadControlDef = reloadControlDef;
		_charsetName = Charset.defaultCharset().name();
	}
	public ResourcesLoaderDef(final String id,
							  final ResourcesLoaderType type,
							  final ResourcesReloadControlDef reloadControlDef,
							  final Charset charset) {
		_id = id;
		_loader = type;
		_reloadControlDef = reloadControlDef;
		_charsetName = charset != null ? charset.name() : Charset.defaultCharset().name();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Charset getCharset() {
		return Charset.forName(_charsetName);
	}
	/**
	 * Returns a property using it's key (name)
	 * @param propName the property key
	 * @return the property value
	 */
	public String getProperty(final String propName) {
		String outProp = this.getLoaderProps() != null ? this.getLoaderProps().get(propName)
													   : null;
		return outProp;
	}
	@Override
	public CharSequence debugInfo() {
		StringBuilder sw = new StringBuilder(100);
		if (_id != null) 			sw.append(String.format("\r\n\t\t      id: %s",_id));
		if (_loader != null)		sw.append(String.format("\r\n\t\t    name: %s",_loader.name()));
		if (_loaderProps != null)	sw.append(String.format("\r\n\t\t   props: (%s)",Integer.toString(_loaderProps.size())));
		if (_loaderProps != null) {
			for (Map.Entry<String,String> prop : _loaderProps.entrySet()) {
				sw.append(String.format("\r\n\t\t\t-%s:%s",prop.getKey(),prop.getValue()));
			}
		}
		if (_reloadControlDef != null) sw.append(String.format("\r\n\t\treloadControl:%s",_reloadControlDef.debugInfo()));
		return sw;
	}
}
