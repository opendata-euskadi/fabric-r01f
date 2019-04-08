package r01f.xmlproperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * Properties cache for a given app code<br/>
 * There are TWO cache types:
 * <ul>
 * 	<li><b>Properties cache</b>: It's being filled as properties are being accessed: when a property is used, it's cached so the 
 * 								 xml file does not have to be queried (xpath) again and again
 * 								 This cache is managed at {@link XMLPropertiesForAppCache} and used at {@link XMLPropertiesForApp}.</li>
 *
 * 	<li><b>Component xml</b>: When a property of an {appCode}.component is accessed for the first time, it's xml is loaded from where it's stored
 * 							  (filesystem, db, ect) and the xml is cached to avoid loading it again and again
 * 							  This cache is managed at {@link XMLPropertiesForAppComponentsContainer}</li>
 * </ul>
 */
@Slf4j
@Accessors(prefix="_")
public  class XMLPropertiesForAppCache 
   implements XMLPropertiesComponentLoadedListener {
/////////////////////////////////////////////////////////////////////////////////////////
// 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Factory of {@link XMLPropertiesForAppCache} instances 
	 * This factory is binded at {@link XMLPropertiesGuiceModule} as an assisted inject factory
	 * needed to create {@link XMLPropertiesForAppCache} objects cannot be created by guice since
	 * the appCode and prop number estimation are needed at creation time and both are only known
	 * at RUNTIME
	 */
	static interface XMLPropertiesForAppCacheFactory {
		/**
		 * Creates a {@link XMLPropertiesForAppCache} instance from the appCode and prop number estimation
		 * @param appCode 
		 * @param componentsNumberEstimation 
		 * @return
		 */
		public XMLPropertiesForAppCache createFor(final AppCode appCode,
												  final int componentsNumberEstimation);
		/**
		 * Creates a {@link XMLPropertiesForAppCache} instance from the appCode and prop number estimation
		 * @param appCode
		 * @param environment 
		 * @param componentsNumberEstimation 
		 * @param useCache true if cache is used, false otherwise
		 * @return
		 */
		public XMLPropertiesForAppCache createFor(final Environment env,final AppCode appCode,
												  final int componentsNumberEstimation);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static int DEFAULT_COMPONENTS_NUMBER = 30;
	static int DEFAULT_PROPERTIES_PER_COMPONENT = 100;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Environment _systemSetEnvironment;	// the environment set at a system property
	@Getter private final AppCode _appCode;	
	
/////////////////////////////////////////////////////////////////////////////////////////
//  PROPERTIES CACHE: relates a xpath expression with the xml's DOM node containing the prop
/////////////////////////////////////////////////////////////////////////////////////////
	private final XMLPropertiesForAppComponentsContainer _componentXMLManager;
	
	private final boolean _useCache;
	private Map<CacheKey,CacheValue> _cache;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesForAppCache(final Environment env,		// see XMLPropertiesGuiceModule
							      	final AppCode appCode,
							      	final int componentsNumberEstimation,
							      	final boolean useCache) {
		_appCode = appCode;
		_systemSetEnvironment = env;
		_useCache = useCache;	
		_componentXMLManager = new XMLPropertiesForAppComponentsContainer(this,									// component loading listener
																	      env,									// environment
																	      _appCode,componentsNumberEstimation);	// appCode / props estimation
	}
	public XMLPropertiesForAppCache(final AppCode appCode,final int componentsNumberEstimation) {
    	this(null,
    		 appCode,componentsNumberEstimation,
    		 true);		// use cache by default
	}
	public XMLPropertiesForAppCache(final AppCode appCode,final int componentsNumberEstimation,
							      	final boolean useCache) {
    	this(null,
    		 appCode,componentsNumberEstimation,
    		 useCache);
	}
	public XMLPropertiesForAppCache(final AppCode appCode) {
		this(null,
			 appCode,DEFAULT_PROPERTIES_PER_COMPONENT,
			 false);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFACE XMLPropertiesComponentLoadedListener
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void newComponentLoaded(final XMLPropertiesComponentDef def) {
		// Ensure there's enough space at the props cache
		_ensureCapacity(def.getNumberOfPropertiesEstimation());
	}
	/**
	 * Ensures a certain cache capacity<br>
	 * This function is called from {@link XMLPropertiesForAppComponentsContainer} when a new XML properties file is loaded
	 * <ul>
	 *      <li>The component definition sets the estimated props number</li>
	 * 		<li>When the component definition is loaded, this method is called to make space for the properties at the cache.</li>
	 * </ul>
	 * @param propertiesPerComponentEstimation estimated number of properties
	 */
	private void _ensureCapacity(final int propertiesPerComponentEstimation) {
		final float cacheMapLoadFactor = 0.5F;	// A Map's loadFactor is an indication of how full the underlying table can be BEFORE it's capacity is increased
												//		when numEntries > loadFactor * actualCapacity the underlying table is rebuilt and the bucket number (capacity) is doubled
												// NOTE that:
												//		- A HashMap stores it's entries indexed by a hash of their keys in the underlying table's positions (buckets)
												//		  It's possible that at a certain position (bucket) there's MORE THAN A SINGLE entry by two means:
												//			1.- When an new entry is inserted, it has the SAME hashCode than an existent one
												//					In this case, the entry's key's equals() method is calle to know if it's the SAME entry -in which case the 
												//					existing entry is REPLACED by the new one-, or it's a DIFFERENT entry -in which case, the bucket will store two
												//					entries-.
												// 			2.- The table's capacity has been exceed and obviously at every table's position (bucket) more than a single entry
												//				MUST be stored; in this situation, when a new entry is inserted, it's hashCode MUST be checked against the bucket 
												//			    existing ones:
												//					- if new entry's key's hashCode == any bucket entry's key's hash code the equals() method must be called
												//					  and replace the entry if both key's hashcodes are equal
												//					- if new entry's key's hashCode != any bucket entry's key's hash code a new entry is added to the bucket
												// The SECOND collision type is affected by the table's capacity (bucket number) that ultimatelly is affected by the loadFactor
												// (a value between 0 and 1)
												//		The lower the loadFactor is, the LESS collision probability (the number of buckets in the table will sooner be doubled and 
												//	 	the probability of table's space starvation is LESS probable)
		if (_cache == null) {
			// a new cache is created
			int cacheSize = propertiesPerComponentEstimation + 100;
			log.trace("Creating a {} positions cache for every component of {}",Integer.toString(cacheSize),_appCode);
			_cache = new HashMap<CacheKey,CacheValue>(cacheSize,cacheMapLoadFactor);	// the second parameter is the load factor
																						// ... the lower it's... the less collision probability
		} else {
			// the cache is re-built
			int cacheSize = _cache.size() + propertiesPerComponentEstimation + 100;
			log.trace("Resizing cache to add {} positions; final size: {}",Integer.toString(propertiesPerComponentEstimation),Integer.toString(cacheSize));
			Map<CacheKey,CacheValue> tempCache = new HashMap<CacheKey,CacheValue>(cacheSize,cacheMapLoadFactor);		// the second parameter is the load factor
																														// ... the lower it's... the less collision probability
			tempCache.putAll(_cache);
			_cache = tempCache;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Reloads the cache for all components of all appcodes
	 * @return the number of cleared components
	 */
	public int clear() {
		return this.clear(null);
	}
    /**
     * Reloads properties of a component
     * @param component
     * <ul>
     * 		<li> component != null only the given component is reloaded</li>
     * 		<li> component == null all components are reloaded</li>
     * </ul>
     * @return number of reloaded components
     */
	public int clear(final AppComponent component) {
		log.trace("clearing cached properties for component {} of {}",component,_appCode);
		if (_cache == null) return 0;
		int numMatches = 0;
		if (component == null) {
			// all cache is reset
			numMatches = _cache.size();
			_cache.clear();
		} else {
			// only a given app cache is reset
			List<CacheKey> keysToRemove = new ArrayList<CacheKey>();
			for (CacheKey key : _cache.keySet()) {
				if (key.isSameAs(component)) {
					keysToRemove.add(key);	// add the key to the collection of keys to be removed
					numMatches++;
				}
			}
			if (!keysToRemove.isEmpty()) {
				for (CacheKey key : keysToRemove) {
					Object removedProperty = _cache.remove(key);	// Remove the key
					if (removedProperty != null) numMatches++;
				}
			}
		}
		return numMatches;
	}
	/**
	 * Gets the usage statistics
	 * @return 
	 */
	public CacheStatistics usageStats() {
		CacheStatistics outStats = new CacheStatistics();
		for (CacheValue val : _cache.values()) {
			if (val.getPropValue() != null) {
				if (val.isDefaultValue()) {
					outStats.setDefaultCount( outStats.getDefaultCount() + val.getAccessCount() );
				} else {
					outStats.setHitCount( outStats.getHitCount() + val.getAccessCount() - 1);	// el primer acceso NO es por la cache
					outStats.setNonHitCount( outStats.getNonHitCount() + 1 );
				}
			} else {
				outStats.setInvalidCount( outStats.getInvalidCount() + val.getAccessCount());
			}
		}
		return outStats;
	}
///////////////////////////////////////////////////////////////////////////////
//	METHODS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if there exists a component's properties file 
	 * @param component
	 * @return
	 */
	public boolean existsComponentPropertiesFile(final AppComponent component) {
		return _componentXMLManager.existsComponentPropertiesFile(component);
	}
    /**
     * Checks if a property exists
     * @param component 
     * @param propXPath 
     * @return 
     */
    public boolean existProperty(final AppComponent component,final Path propXPath) {
        Object propValue = _retrieve(component,
        							 propXPath,
        							 null,	// type=null: just check the cache; do NOT try to load the property from the xml
        							 null);	// marshaller=null
        if (propValue != null) return true;
        // if false, the property MIGHT NOT EXIST or it MIGHT NOT BE ALREADY LOADED so in order to confirm
        // it really does NOT exist, try to find the xml node
        Node node = _componentXMLManager.getPropertyNode(component,propXPath);
        return node != null;
    }
	/**
	 * Returns a property
	 * @param component the app component
	 * @param propXPath property xPath
	 * @param defaultValue the property default value
	 * @param type the property data type
	 * @return The property or <code>null</code> if the property does NOT exists
	 */
    public <T> T getProperty(final AppComponent component,final Path propXPath,
    						 final T defaultValue,
    						 final Class<T> type) {
    	T outObj = this.getProperty(component,propXPath,
    								defaultValue,
    								type,
    								null);		// marshaller=null
    	return outObj;
    }
	/**
	 * Returns a property
	 * @param component the app component
	 * @param propXPath property xPath
	 * @param defaultValue the property default value
	 * @param typeRef the property data type
	 * @return The property or <code>null</code> if the property does NOT exists 
	 */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(final AppComponent component,final Path propXPath,
    						 final T defaultValue,
    						 final TypeToken<T> typeRef) {
    	T outObj = this.getProperty(component,propXPath,
    								defaultValue,
    								(Class<T>)typeRef.getRawType());
    	return outObj;
    }
	/**
	 * Returns a property
	 * @param component the app component
	 * @param env the environment
	 * @param propXPath property xPath
	 * @param the property value by environment to be used if the property value is not found (defined)
	 * @param typeRef the property data type
	 * @return The property or <code>null</code> if the property does NOT exists 
	 */
    public <T> T getProperty(final AppComponent component,
    						 final Environment env,final Path propXPath,
    						 final XMLPropertyDefaultValueByEnv<T> valByEnv,
    						 final Class<T> type) {
    	T outObj = this.getProperty(component,
    								env,propXPath,
    								valByEnv,
    								type,
    								null);		// marshaller = null
    	return outObj;
    }
	/**
	 * Returns a property
	 * @param component the app component
	 * @param env the environment
	 * @param propXPath property xPath
	 * @param the property value by environment to be used if the property value is not found (defined)
	 * @param typeRef the property data type
	 * @return The property or <code>null</code> if the property does NOT exists 
	 */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(final AppComponent component,
    						 final Environment env,final Path propXPath,
    						 final XMLPropertyDefaultValueByEnv<T> valByEnv,
    						 final TypeToken<T> typeRef) {
    	T outObj = this.getProperty(component,
    								env,propXPath,
    								valByEnv,
    								(Class<T>)typeRef.getRawType());
    	return outObj;
    }
	/**
	 * Returns a property
	 * @param component the app component
	 * @param propXPath property xPath
	 * @param defaultValue the property default value
	 * @param type the property data type
	 * @param marshaller the marshaller used to transform the property xml into a java object
	 * @return The property or <code>null</code> if the property does NOT exists
	 */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(final AppComponent component,final Path propXPath,
    						 final T defaultValue,
    						 final Class<T> type,
    						 final Function<Node,T> transformFuncion) {
    	CacheValue cachedValue = _retrieve(component,propXPath,
    								 	   type,transformFuncion);
    	T outObj = (T)cachedValue.getPropValue();
    	if (outObj == null && defaultValue != null) {
    		// The property does NOT exist but a default value was given... store the default value
    		outObj = defaultValue;
    		cachedValue.setValue(defaultValue,true);
    	} else if (outObj == null) {
    		// the property does NOT exist and NO default value was given
    	}
    	return outObj;
    }
	/**
	 * Returns a property
	 * @param component the app component
	 * @param env the environment
	 * @param propXPath property xPath
	 * @param the property value by environment to be used if the property value is not found (defined)
	 * @param type the property data type
	 * @param marshaller the marshaller used to transform the property xml into a java object
	 * @return The property or <code>null</code> if the property does NOT exists
	 */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(final AppComponent component,
    						 final Environment env,final Path propXPath,
    						 final XMLPropertyDefaultValueByEnv<T> valByEnv,
    						 final Class<T> type,
    						 final Function<Node,T> transformFuncion) {
    	CacheValue cachedValue = _retrieve(component,propXPath,
    								 	   type,transformFuncion);
    	T outObj = (T)cachedValue.getPropValue();
    	if (outObj == null && valByEnv != null) {
    		// The property does NOT exist but a default value was given... store the default value
    		outObj = valByEnv.getFor(env);
    		cachedValue.setValue(outObj,true);
    	} else if (outObj == null) {
    		// the property does NOT exist and NO default value was given
    	}
    	return outObj;
    }
	/**
	 * Returns the node containing the property
	 * @param component
	 * @param propXPath 
	 * @return 
	 */
	public Node getPropertyNode(final AppComponent component,final Path propXPath) {
		Node node = _componentXMLManager.getPropertyNode(component,propXPath);
		return node;
	}
	/**
	 * Returns the node list result of applying the xPath expression
	 * @param component 
	 * @param propXPath 
	 * @return 
	 */
	public NodeList getPropertyNodeList(final AppComponent component,final Path propXPath) {
		NodeList nodeList = _componentXMLManager.getPropertyNodeList(component,propXPath);
		return nodeList;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS TO STORE AND RETRIVE FROM THE CACHE
// 	Objects at cache are indexed by a key composed by appCode, component and the XPath expression
// 		appCode1 / component1A --> xPath_prop1 - obj1
// 								   xPath_prop2 - obj2
// 								   ...
// 		appCode2 / Component2A --> xPath_prop1 - obj1
//								   ...
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a cached value or null if the property is NOT cached
     * @param component
     * @param propXPath
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T> T getCachedPropertyOrNull(final AppComponent component,final Path propXPath) {
    	T outValue = null;
    	
		// Try to retrieve the cached value
		CacheKey key = new CacheKey(component,propXPath);
		CacheValue value = null;
		value = _cache.get(key);
		if (value != null) {
			value.anotherHit();
			outValue = (T)value.getPropValue();
		}
		return outValue;
    }
	/**
	 * Retrieves a property value given it's XPath
	 * TWO caches are in use:
	 * - LEVEL1: a cache of values indexed by their XPath expression
	 * - LEVEL2: a cache of components' XML documents
	 * <b>BEWARE:</b><p>If type==null, just check the cache (do NOT try to load the property value from the xml).
	 * @param component
	 * @param xPath 
	 * @param type 
	 * @param marshaller
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> CacheValue _retrieve(final AppComponent component,final Path xPath,
									 final Class<T> type,final Function<Node,T> transformFuncion) {
		// [0] Check if a property reloading is needed
		boolean hasToClear = _componentXMLManager.reloadIfNecessary(component);
		if (hasToClear) this.clear(component);		// Remove all cache entries

		// [1] LEVEL 1 cache: Load the property from the values cache
		CacheKey key = new CacheKey(component,xPath);
		CacheValue outValue = null;
		if (_useCache) {
			// Try to retrieve the cached value
			outValue = _cache.get(key);
			if (outValue != null) outValue.anotherHit();
		}
		
		// If type==null, just check the cache (do NOT try to load the property value from the xml)
		if (type == null) return outValue;
		
		// [2] LEVEL 2 cache: If the property is NOT at the values cache, load the component's XML
		//	   				  and get the property
		if (outValue == null) {
			// It's the first time the property is accessed so the cache is empty: go to the xml 
			T obj = null;
    		if (type.equals(String.class)) {
    			// String
    			obj = (T)_componentXMLManager.getStringProperty(component,xPath);

    		} else if (type.equals(Number.class)) {
    			// number (int, long, double, etc)
    			String numStr = _componentXMLManager.getStringProperty(component,xPath);
		    	try {
		    		Number num = NumberUtils.createNumber(numStr);
		    		obj = (T)num;
		    	} catch (NumberFormatException nfEx) {
		    		log.warn("Property {} from appCode/component={}/{}: {} cannot be converted to a Number!",
		    			     xPath,_appCode.asString(),component,numStr);
		    	}

    		} else if (type.equals(Boolean.class)) {
    			// Boolean
    			String bolStr = _componentXMLManager.getStringProperty(component,xPath);
		    	Boolean bool = BooleanUtils.toBooleanObject(bolStr);
		    	if (bolStr != null && bool == null) {
		    		log.debug("Property {} from appCode/component={}/{}: {} cannot be converted to a boolean!",
		    				  xPath,_appCode.asString(),component,bolStr);
		    	}
		    	obj = (T)bool;

    		} else if (CollectionUtils.isMap(type)) {
    			// Map
    			obj = (T)_componentXMLManager.getMapOfStringsProperty(component,xPath);

    		} else if (transformFuncion != null) {
    			obj = (T)_componentXMLManager.getBeanPropertyUsingTransformFunction(component,xPath,
    														  				 		type,
    														  				 		transformFuncion);
    		} else {
    			log.warn("{} type is not a supported for a property. Property {} from appCode/component={}/{}: cannot be converted",
    					 type.getName(),xPath,_appCode.asString(),component);
    		}
	    	// Store the created object at the cache
    		outValue = _store(component,xPath,obj,false);	// BEWARE!! obj can be NULL if the property DOES NOT EXISTS!!!!
		}
		// return
		return outValue;
	}
	/**
	 * Caches a property value
	 * @param component 
	 * @param xPath
	 * @param obj 
	 * @param isDefaultVal 
	 * @return the previously stored value
	 */
	private CacheValue _store(final AppComponent component,final Path xPath,
							  final Object obj,final boolean isDefaultVal) {
		CacheKey key = new CacheKey(component,xPath);
		CacheValue value = new CacheValue(1,System.currentTimeMillis(),obj,isDefaultVal);
		_cache.put(key,value);
		return value;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE KEY
/////////////////////////////////////////////////////////////////////////////////////////
  	@Accessors(prefix="_")
  	@EqualsAndHashCode @ToString
  	@AllArgsConstructor
  	private class CacheKey {
  		@Getter private AppComponent _component;
  		@Getter private Path _propXPath;

		boolean isSameAs(final AppComponent... keyComponent) {
			boolean isSame = false;
			if (keyComponent.length == 2) {	// component / xPath
				isSame = this.composeKey(_component).equals(this.composeKey(keyComponent[0],keyComponent[1]));
			} else if (keyComponent.length == 1) {	// appCode
				isSame = this.composeKey(_component).equals(this.composeKey(keyComponent[0]));
			}
			return isSame;
		}
		AppComponent composeKey(final AppComponent... keyComponent) {
			AppComponent outKey = null;
			if (keyComponent.length == 2) {	// component / xPath
				outKey = AppComponent.forId(keyComponent[0].asString() + "." + keyComponent[1].asString());
			} else if (keyComponent.length == 1) {	// component
				outKey = keyComponent[0];
			}
			return outKey;
		}
  	}
    /**
     * Value stoed at the cache
     */
  	@Accessors(prefix="_")
    @NoArgsConstructor @AllArgsConstructor
    private class CacheValue {
    	@Getter private long _accessCount;
    	@Getter private long _lastAcessTimeStamp;
    	@Getter private Object _propValue;
    	@Getter private boolean _defaultValue;
    	public void anotherHit() {
    		_accessCount++;
    		_lastAcessTimeStamp = System.currentTimeMillis();
    	}
    	public void setValue(final Object val,final boolean defaultVal) {
    		_propValue = val;
    		_defaultValue = defaultVal;
    	}
    }
///////////////////////////////////////////////////////////////////////////////
//	Cache statistics
///////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
	@NoArgsConstructor
         class CacheStatistics 
    implements Debuggable {
    	@Getter @Setter private long _hitCount;		// total cache hits 
    	@Getter @Setter private long _nonHitCount;	// total non hits (non-existents values)
    	@Getter @Setter private long _invalidCount;	// total invalid
    	@Getter @Setter private long _defaultCount;	// total default values
    	
    	public String debugInfo() {
    		StringBuilder dbg = new StringBuilder("");
    		dbg.append("XMLProperties cache stats:")
			   .append("\r\n     Hits: ").append(Long.toString(_hitCount))
			   .append("\r\n  NO-Hits: ").append(Long.toString(_nonHitCount))
			   .append("\r\n Defaults: ").append(Long.toString(_defaultCount))
			   .append("\r\n Invalids: ").append(Long.toString(_invalidCount))
			   .append("\r\n");
    		return dbg.toString();
	    }
    }
}
