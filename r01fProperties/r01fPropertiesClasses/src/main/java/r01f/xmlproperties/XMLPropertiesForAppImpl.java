package r01f.xmlproperties;
/**

 * @author  Alex Lara
 * @version
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.objectstreamer.Marshaller;
import r01f.resources.ResourcesLoaderDef;
import r01f.resources.ResourcesLoaderDefBuilder;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xml.XMLUtils;

/**
 * Manages properties for an app code<br/>
 * 
 * This type SHOULD NOT be used directly BUT using {@link XMLProperties} instead
 * (see {@link XMLProperties})
 */
public final class XMLPropertiesForAppImpl
		implements XMLPropertiesForApp {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private final XMLPropertiesForAppCache _cache;  // Properties cache (built at XMLPropertiesCache factory)
    
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public XMLPropertiesForAppImpl(final XMLPropertiesForAppCache cache) {
    	_cache = cache;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AppCode getAppCode() {
		return _cache.getAppCode();
	}
	@Override
	public Environment getSystemSetEnvironment() {
		return _cache.getSystemSetEnvironment();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String cacheStatsDebugInfo() {
    	return _cache.usageStats().debugInfo();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ComponentProperties of(final AppComponent component) {
    	return new ComponentPropertiesImpl(this.getSystemSetEnvironment(),
    									   this.getAppCode(),component);
    }
    @Override
    public ComponentProperties of(final String component) {
    	return new ComponentPropertiesImpl(this.getSystemSetEnvironment(),
    									   this.getAppCode(),AppComponent.forId(component));
    }
    @Override
    public XMLPropertiesForAppComponent forComponent(final AppComponent component) {
    	return new XMLPropertiesForAppComponent(this,component);
    }
    @Override
    public XMLPropertiesForAppComponent forComponent(final String component) {
    	return new XMLPropertiesForAppComponent(this,AppComponent.forId(component));
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPONENT PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Wraps an app component in a type that offers convenient property access methods
     */
    public class ComponentPropertiesImpl 
      implements ComponentProperties {
    	
    	private final Environment _systemSetEnv;
    	
    	@SuppressWarnings("unused")
		private final AppCode _appCode;
    	private final AppComponent _component;

    	ComponentPropertiesImpl(final Environment systemEnv,
    							final AppCode appCode,final AppComponent component) {
    		_systemSetEnv = systemEnv;
    		_appCode = appCode;
    		_component = component;
    	}
    	@Override
    	public Environment getEnvironment() {
    		// [1] - If there's a system wide env var use it
    		if (_systemSetEnv != null) return _systemSetEnv;
    		// [2] - Try to find an env attribute at the xml's root node
    		return XMLPropertiesEnv.guessEnvironmentFromXMLProperties(this);
    	}
    	@Override
    	public boolean existsComponentPropertiesFile() {
    		return _cache.existsComponentPropertiesFile(_component);
    	}
    	@Override
    	public Node node(final Path propXPath) {
    		return _cache.getPropertyNode(_component,Path.from(propXPath));
    	}
    	@Override
    	public NodeList nodeList(final Path propXPath) {
    		return _cache.getPropertyNodeList(_component,Path.from(propXPath));
    	}
    	@Override
	    public boolean existProperty(final Path propXPath) {
	    	boolean outExists = _cache.existProperty(_component,Path.from(propXPath));
	    	return outExists;
	    }
    	@Override
		public <T> Collection<T> getObjectList(final Path propXPath,
											   final Function<Node,T> transformFunction) {
    		return this.<T>getObjectList(propXPath,
    									 transformFunction,
    									 null);
    	}
    	@Override
		public <T> Collection<T> getObjectList(final Path propXPath,
											   final Function<Node,T> transformFunction,
											   final Collection<T> defaultVal) {
			Collection<T> outObjs = null;
			NodeList nodes = this.nodeList(propXPath);
			if (nodes != null && nodes.getLength() > 0) {
				outObjs = Lists.newArrayListWithExpectedSize(nodes.getLength());
				for (int i=0; i < nodes.getLength(); i++) {
					outObjs.add(transformFunction.apply(nodes.item(i)));	// transform the node
				}
			} else {
				outObjs = defaultVal;
			}
			return outObjs;
		}
    	@Override
	    public <T> T get(final Path propXPath) {
	    	return this.<T>get(propXPath,null);
	    }
    	@Override @SuppressWarnings("serial")
	    public <T> T get(final Path propXPath,final T defaultValue) {
	    	return _cache.getProperty(_component,Path.from(propXPath),
	    							  defaultValue,
	    							  new TypeToken<T>() {/* empty */});
	    }
    	@Override @SuppressWarnings("serial")
    	public <T> T get(final Path propXPath,final XMLPropertyDefaultValueByEnv<T> valByEnv) {
    		return _cache.getProperty(_component,this.getEnvironment(),Path.from(propXPath),
    								  valByEnv,
    								  new TypeToken<T>() {/* empty */});    				
    	}
    	@Override
	    public String getString(final Path propXPath) {
	    	return this.getString(propXPath,(String)null);
	    }
    	@Override
	    public String getString(final Path propXPath,final String defaultValue) {
	        return _cache.getProperty(_component,Path.from(propXPath),
	        						  defaultValue,
	        						  String.class);
	    }
    	@Override
    	public String getString(final Path propXPath,final XMLPropertyDefaultValueByEnv<String> valByEnv) {
    		return _cache.getProperty(_component,this.getEnvironment(),propXPath,
    								  valByEnv,
    								  String.class);
    	}
    	@Override
	    public Number getNumber(final Path propXPath) {
	    	return this.getNumber(propXPath,(Number)null);
	    }
    	@Override
	    public Number getNumber(final Path propXPath,final Number defaultValue) {
	    	return _cache.getProperty(_component,Path.from(propXPath),
	    							  defaultValue,
	    							  Number.class);
	    }
    	@Override
    	public Number getNumber(final Path propXPath,final XMLPropertyDefaultValueByEnv<Number> valByEnv) {
	    	return _cache.getProperty(_component,this.getEnvironment(),Path.from(propXPath),
	    							  valByEnv,
	    							  Number.class);
    	}
    	@Override
	    public int getInteger(final Path propXPath) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.intValue() : Integer.MIN_VALUE;
	    }
    	@Override
	    public int getInteger(final Path propXPath,final int defaultValue) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.intValue() : defaultValue;
	    }
    	@Override
    	public int getInteger(final Path propXPath,final XMLPropertyDefaultValueByEnv<Integer> valByEnv) {
    		return _cache.getProperty(_component,this.getEnvironment(),Path.from(propXPath),
	    							  valByEnv,
	    							  Integer.class);
    	}
    	@Override
	    public long getLong(final Path propXPath) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.longValue() : Long.MIN_VALUE;
	    }
    	@Override
	    public long getLong(final Path propXPath,final long defaultValue) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.longValue() : defaultValue;
	    }
    	@Override
    	public long getLong(final Path propXPath,final XMLPropertyDefaultValueByEnv<Long> valByEnv) {
    		return _cache.getProperty(_component,this.getEnvironment(),Path.from(propXPath),
	    							  valByEnv,
	    							  Long.class);
    	}
    	@Override
	    public double getDouble(final Path propXPath) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.doubleValue() : Double.MIN_VALUE;
	    }
	    @Override
	    public double getDouble(final Path propXPath,final double defaultValue) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.doubleValue() : defaultValue;
	    }
	    @Override
	    public double getDouble(final Path propXPath,final XMLPropertyDefaultValueByEnv<Double> valByEnv) {
    		return _cache.getProperty(_component,this.getEnvironment(),Path.from(propXPath),
	    							  valByEnv,
	    							  Double.class);
	    }
    	@Override
	    public float getFloat(final Path propXPath) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.floatValue() : Float.MIN_VALUE;
	    }
    	@Override
	    public float getFloat(final Path propXPath,final float defaultValue) {
	    	Number num = this.getNumber(propXPath);
	    	return num != null ? num.floatValue() : defaultValue;
	    }
    	@Override
    	public float getFloat(final Path propXPath,final XMLPropertyDefaultValueByEnv<Float> valByEnv) {
    		return _cache.getProperty(_component,this.getEnvironment(),Path.from(propXPath),
	    							  valByEnv,
	    							  Float.class);
    	}
    	@Override
	    public boolean getBoolean(final Path propXPath) {
	    	return this.getBoolean(propXPath,false);
	    }
    	@Override
	    public boolean getBoolean(final Path propXPath,final boolean defaultValue) {
	    	Boolean bool = _cache.getProperty(_component,Path.from(propXPath),
	    									  defaultValue,
	    									  Boolean.class);
	        return bool != null ? bool : false;
	    }
    	@Override
    	public boolean getBoolean(final Path propXPath,final XMLPropertyDefaultValueByEnv<Boolean> valByEnv) {
    		Boolean bool =  _cache.getProperty(_component,this.getEnvironment(),Path.from(propXPath),
	    							  		   valByEnv,
	    							  		   Boolean.class);
    		return bool != null ? bool : false;
    	}
    	@Override @SuppressWarnings("unchecked")
	    public Properties getProperties(final Path propXPath) {
	    	Map<String,List<String>> map = _cache.getProperty(_component,Path.from(propXPath),
	    													  null,
	    													  Map.class);
    		Properties outProps = null;
	    	if (map != null) {
	    		outProps = new Properties();
		    	for (Map.Entry<String,List<String>> me : map.entrySet()) {
		    		if (me.getValue() != null && me.getValue().size() == 1) {
		    			outProps.put(me.getKey(),me.getValue().get(0));
		    		} else {
		    			StringBuilder sb = new StringBuilder();
		    			for (Iterator<String> it=me.getValue().iterator(); it.hasNext(); ) {
		    				sb.append('[');
		    				sb.append(it.next());
		    				sb.append(']');
		    				if (it.hasNext()) {
		    					sb.append(',');
		    				}
		    			}
		    			outProps.put(me.getKey(),sb.toString());
		    		}
		    	}
	    	}
	    	return outProps;
	    }
    	@Override
	    public Properties getProperties(final Path propXPath,final Properties defaultValue) {
	    	Properties outProps = this.getProperties(propXPath);
	    	if (outProps == null) outProps = defaultValue;
	    	return outProps;
	    }
    	@Override
    	public Properties getProperties(final Path propXPath,final XMLPropertyDefaultValueByEnv<Properties> valByEnv) {
    		Properties outProps = this.getProperties(propXPath);
	    	if (outProps == null) outProps = valByEnv.getFor(this.getEnvironment());
	    	return outProps;
    	}
	    @Override @SuppressWarnings("unchecked")
	    public List<String> getListOfStrings(final Path propXPath) {
	    	String effXPath = (propXPath != null 
	    					&& !propXPath.asString().endsWith("/child::*")) 
	    								? propXPath.asString().concat("/child::*")
	    								: propXPath != null ? propXPath.asString() : "";
	    	
	    	List<String> outList = null;	    	
	    	if (Strings.isNOTNullOrEmpty(effXPath)) {
		    	Map<String,List<String>> map = _cache.getProperty(_component,Path.from(effXPath),
		    													  null,
		    													  Map.class);
		    	
		    	if (map != null) {
		    		outList = new ArrayList<String>(map.size());
			    	for (Map.Entry<String,List<String>> me : map.entrySet()) {
			    		if (me.getValue() != null && me.getValue().size() == 1) {
			    			outList.add(me.getValue().get(0));
			    		} else {
			    			for (Iterator<String> it=me.getValue().iterator(); it.hasNext(); ) {
			    				outList.add(it.next());
			    			}
			    		}
			    	}
		    	}
	    	}
	    	
	    	return outList;
	    }
	    @Override
	    public List<String> getListOfStrings(final Path propXPath,final List<String> defaultValue) {
	    	List<String> outList = this.getListOfStrings(propXPath);
	    	if (outList == null) outList = defaultValue;
	    	return outList;
	    }
	    @Override
	    public List<String> getListOfStrings(final Path propXPath,final XMLPropertyDefaultValueByEnv<List<String>> valByEnv) {
	    	List<String> outList = this.getListOfStrings(propXPath);
	    	if (outList == null) outList = valByEnv.getFor(this.getEnvironment());
	    	return outList;
	    }
	    @Override
	    public <T> T getObject(final Path propXPath,
	    					   final Marshaller marshaller,
	    					   final Class<T> type) {
	    	return this.getObject(propXPath,
	    						  new Function<Node,T>() {
											@Override
											public T apply(final Node node) {
												return marshaller.forReading().fromXml(XMLUtils.asString(node),
																					   type);
											}
	    						  });
	    }
	    @Override
        public <T> T getObject(final Path propXPath,
					   		   final Marshaller marshaller,
					   		   final TypeToken<T> typeToken) {
	    	return this.getObject(propXPath,
	    						  new Function<Node,T>() {
											@Override
											public T apply(final Node node) {
												return marshaller.forReading().fromXml(XMLUtils.asString(node),
																					   typeToken);
											}
	    						  });
        }
	    @Override
		public <T> T getObject(final Path propXPath,
							   final Function<Node,T> transformFuncion) {
	    	T outObj = _cache.getProperty(_component,Path.from(propXPath),
	    								  null,		// default value
	    								  transformFuncion);	
			return outObj;
		}
		@Override
		public Document getXMLDocument() {
			return _cache.getXMLDocumentFor(_component);
		}
	    @Override
	    public ResourcesLoaderDef getResourcesLoaderDef(final Path propXPath) {
	    	return this.getObject(propXPath,
	    						  new Function<Node,ResourcesLoaderDef>() {
											@Override
											public ResourcesLoaderDef apply(final Node node) {
												return ResourcesLoaderDefBuilder.from(node);
											}
	    						  });

	    }
    }
}
