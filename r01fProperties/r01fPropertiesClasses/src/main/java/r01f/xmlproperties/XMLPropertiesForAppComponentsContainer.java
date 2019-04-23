package r01f.xmlproperties;
/**
 * Manages XML properties files access
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.resources.ResourcesReloadControl;
import r01f.resources.ResourcesReloadControlBuilder;
import r01f.resources.ResourcesReloadControlDef;
import r01f.types.Path;
import r01f.xml.XMLDocumentBuilder;
import r01f.xml.XMLStringSerializer;
import r01f.xml.XMLUtils;
import r01f.xml.merge.XMLMerger;


/**
 * Manages an app code's component's properties
 * This type has a cache of the component's XML documents so the XML file does NOT have to be loaded and parsed
 * again and again
 * <p>
 * The component's properties can be loaded from many sources as set at the component definition (see {@link XMLPropertiesComponentDef}).
 * </p>
 * <ul>
 * 		<li>A folder in the file system</li>
 * 		<li>A folder in the app classpath</li>
 * 		<li>A database table's row</li>
 * 		<li>...</li>
 * </ul>
 * It also sets how the properties are LOADED AND RELOADED (see {@link r01f.resources.ResourcesLoaderDef})
 * ie:
 * <ul>
 * 		<li>Reload periodically</li>
 * 		<li>Reload when a file is touched (modified)</li>
 * 		<li> etc.</li>
 * </ul>
 * see {@link r01f.resources.ResourcesReloadControlDef}.
 */
@Slf4j
class XMLPropertiesForAppComponentsContainer {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Environment
	 */
	private final Environment _systemSetEnvironment;
	/**
	 * App code
	 */
	private final AppCode _appCode;
    /**
     * Listener of component loaded events
     */
    private final XMLPropertiesComponentLoadedListener _componentLoadedListener;
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPONENT CACHE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Cache that stores the component's XML DOM that stores the properties
     */
    private Map<ComponentCacheKey,ComponentCacheXML> _componentsXMLCache;

    @Accessors(prefix="_")
  	@EqualsAndHashCode @ToString
  	@AllArgsConstructor
  	private class ComponentCacheKey {
  		@Getter private final AppComponent _component;

		boolean isSameAs(final AppComponent... keyComponent) {
			boolean isSame = false;
			if (keyComponent.length == 1) {
				isSame = this.composeKey(_component).equals(this.composeKey(keyComponent[0]));
			}
			return isSame;
		}
		AppComponent composeKey(AppComponent... keyComponent) {
			AppComponent outKey = null;
			if (keyComponent.length == 1) {
				outKey = keyComponent[0];
			}
			return outKey;
		}
  	}
  	@Accessors(prefix="_")
  	@AllArgsConstructor
  	private class ComponentCacheXML {
  		@Getter private XMLPropertiesComponentDef _compDef;			// component's definition
  		@Getter private long _loadTimeStamp;						// load timeStamp
  		@Getter private ResourcesReloadControl _reloadControlImpl;	// property reload control
  		@Getter private Document _xml;								// xml document
  	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Cache size and debug mode based constructor
     * @param componentLoadedListener listener of component loaded events
     * @param systemSetEnvironment environment
     * @param appCode app code
     * @param componentsNumberEstimation number of properties estimation
     */
    XMLPropertiesForAppComponentsContainer(final XMLPropertiesComponentLoadedListener componentLoadedListener,
    								   	   final Environment systemSetEnvironment,
    								   	   final AppCode appCode,
    								   	   final int componentsNumberEstimation) {
    	_systemSetEnvironment = systemSetEnvironment;
    	_appCode = appCode;
    	_componentLoadedListener = componentLoadedListener;
    	_componentsXMLCache = new HashMap<ComponentCacheKey,ComponentCacheXML>(componentsNumberEstimation,0.5F);
    }
    /**
     * Removes the the cached properties forcing it's reloading
     * 	<ul>
     * 		<li>if <code>component != null</code> the given component is reloaded</li>
     * 		<li>if <code>component == null</code> ALL appcode's components are reloaded</li>
     * 	</ul>
     * @param appCode
     * @param component
     * @return the number of removed property entries
     */
    int clear(final AppComponent component) {
    	log.trace("Clearing XML documents cache for {}/{}",_appCode,component);
    	int numMatches = 0;
        if (component == null) {
        	numMatches = _componentsXMLCache.size();
        	_componentsXMLCache.clear();
        } else {
        	List<ComponentCacheKey> keysToRemove = new ArrayList<ComponentCacheKey>();
        	for (ComponentCacheKey key : _componentsXMLCache.keySet()) {
        		if (key.isSameAs(component)) {
        			keysToRemove.add(key);
        			numMatches++;
        		}
        	}
        	if (!keysToRemove.isEmpty()) {
        		for (ComponentCacheKey key : keysToRemove) {
        			ComponentCacheXML removedComp = _componentsXMLCache.remove(key);	// Eliminar la clave del cache de DOMs por componente
        			if (removedComp != null) numMatches++;
        		}
        	}
        }
        return numMatches;
    }
    /**
     * Reloads the config if necessary; to do so it checks the last reload time-stamp with the properties source modification time-stamp
     * (this properties source modification time-stamp is handed by the type set at the component definition)
     * ie:
     * <pre class='xml'>
	 *		<?xml version="1.0" encoding="UTF-8"?>
	 *		<componentDef>
	 *			<numberOfPropertiesEstimation>10</numberOfPropertiesEstimation>
	 *			<resourcesLoader type='CLASSPATH'/>
	 *			<propertiesFileURI>...</propertiesFileURI>	<!-- BEWARE with ClassPathLoader: USE relative paths -->
	 *		</componentDef>
     * </pre>
     * @param component .
     * @return <code>true</code> if the component properties must be reloaded
     */
    boolean reloadIfNecessary(final AppComponent component) {
    	boolean outReload = false;

    	ComponentCacheXML comp = _retrieveComponent(component);
    	if (comp == null) return false;

    	ResourcesReloadControl reloadControlImpl = comp.getReloadControlImpl();
    	if (reloadControlImpl == null) return false;

    	// time between component reload checking
    	long checkInterval = comp.getCompDef().getLoaderDef().getReloadControlDef()
    										  				 .getCheckIntervalMilis();
    	if (checkInterval > 0) {
	    	long timeElapsed = System.currentTimeMillis() - comp.getLoadTimeStamp();
	    	if (timeElapsed > checkInterval) {
		    	outReload = reloadControlImpl.needsReload(component.asString());
		    	if (outReload) {
		    		log.debug("***** RELOAD component {}/{} ******",
		    				  _appCode,component);
		    		this.clear(component);		// If a reload is needed, delete the component's definition
		    	}
	    	}
    	}
    	return outReload;
    }
    /**
     * Checks if a component's properties file exists
     * @param component
     * @return
     */
    public boolean existsComponentPropertiesFile(final AppComponent component) {
    	ComponentCacheXML comp = _retrieveComponent(component);		// just try to retrieve the component xml
    	return comp != null;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the xml {@link Document} that backs a component's properties
     * @param component
     * @return
     */
    Document getXMLDocumentFor(final AppComponent component) {
        // [1]- Load the document's DOM
        ComponentCacheXML comp = _retrieveComponent(component);
		if (comp == null) return null;		// the component could NOT be loaded... return null
		
		// [2] - Return the xml
		return comp.getXml();
    }
    /**
     * Returns the XML's DOM's {@link Node} obtained applying the given xpath expression to the app/component properties XML.
     * @param component
     * @param xPath
     * @return The DOM {@link Node} or null if the node is NOT found
     */
    Node getPropertyNode(final AppComponent component,final Path xPath) {
    	return (Node)this.getPropertyNode(component,xPath,XPathConstants.NODE);
    }
    NodeList getPropertyNodeList(AppComponent component,Path xPath) {
    	String xPathStr = xPath.asString();
    	String effXPath = !xPathStr.endsWith("/child::*") ? xPathStr.concat("/child::*")
    													  : xPathStr;
    	return (NodeList)this.getPropertyNode(component,Path.from(effXPath),XPathConstants.NODESET);
    }
    /**
     * Returns the XML's DOM's {@link Node} obtained applying the given xpath expression to the app/component properties XML.
     * @param component
     * @param propXPath
     * @param returnType the {@link XPath} returned type (boolean, number, string, node o nodeSet).
     * @return The DOM {@link Node} or null if the node is NOT found
     */
    Object getPropertyNode(final AppComponent component,final Path propXPath,
    					   final QName returnType) {
        // [1]- Load the document's DOM
        ComponentCacheXML comp = _retrieveComponent(component);
		if (comp == null) return null;		// the component could NOT be loaded... return null

        // [2]- Exec the XPath expression
		String thePropXPath = null;
        try {
            Object outObj = null;
            thePropXPath = propXPath.asString().trim();
            if (thePropXPath.startsWith("/")) thePropXPath = thePropXPath.substring(1);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression xPathExpr = xPath.compile(thePropXPath);
            if (returnType == XPathConstants.BOOLEAN) {
            	outObj = xPathExpr.evaluate(comp.getXml(),XPathConstants.BOOLEAN);
            } else if (returnType == XPathConstants.NUMBER) {
            	outObj = xPathExpr.evaluate(comp.getXml(),XPathConstants.NUMBER);
            } else if (returnType == XPathConstants.STRING) {
            	outObj = xPathExpr.evaluate(comp.getXml(),XPathConstants.STRING);
            } else if (returnType == XPathConstants.NODE) {
            	outObj = xPathExpr.evaluate(comp.getXml(),XPathConstants.NODE);
            } else if (returnType == XPathConstants.NODESET) {
            	outObj = xPathExpr.evaluate(comp.getXml(),XPathConstants.NODESET);
            }
            return outObj;
        } catch (XPathExpressionException xPathEx) {
        	log.warn("Error retrieving property at {} for {}/{}",
        			 thePropXPath,_appCode.asString(),component);
        	xPathEx.printStackTrace(System.out);
        }
        return null;    // the property could NOT be loaded
    }
///////////////////////////////////////////////////////////////////////////////
// 	PRIVATE RETRIEVE FROM COMPONENT XML METHODS
///////////////////////////////////////////////////////////////////////////////
	public String getStringProperty(final AppComponent component,final Path propXPath) {
		Node node = this.getPropertyNode(component,propXPath);
    	return node != null ? XMLUtils.nodeTextContent(node)
    						: null;
	}
  	public Map<String,List<String>> getMapOfStringsProperty(final AppComponent component,final Path propXPath) {
    	Map<String,List<String>> outMap = null;
		NodeList nodeList = this.getPropertyNodeList(component,propXPath);
		if (nodeList != null && nodeList.getLength() > 0) {
			outMap = new HashMap<String,List<String>>(nodeList.getLength());
			for (int i=0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				
				if (XMLUtils.isTextNode(node)) continue;
				
				// Get the item name
				String nodeName = node.getLocalName() != null ? node.getLocalName() : node.getNodeName();
				
				// Get the item value
				String nodeStrValue = null;
				Node contentNode = node.getFirstChild();
    			if (XMLUtils.isTextNode(contentNode)) {
    				nodeStrValue = contentNode.getTextContent();
    			} else {
    				nodeStrValue = XMLStringSerializer.writeNode(contentNode,null);
    			}
    			// Put the item at the Map
				if (nodeStrValue != null) {
					List<String> currItemValue = outMap.get(nodeName);
					if (currItemValue == null) {
						currItemValue = new ArrayList<String>();
						outMap.put(nodeName,currItemValue);
					}
					currItemValue.add(nodeStrValue);
					outMap.put(nodeName,currItemValue);
				}
			}
		}
    	return outMap;
	}
	/**
	 * @param component
	 * @param propXPath
	 * @param type
	 * @param marshaller
	 * @return
	 */
	public <T> T getBeanPropertyUsingTransformFunction(final AppComponent component,final Path propXPath,
						 	       					   final Class<?> type,final Function<Node,T> transformFuncion) {
		T outObj = null;
		if (transformFuncion == null) {
			log.warn("Error transforming property {} from {}/{} to an object using a xml node to java object instance transform funcion: the provided function is null",
					 propXPath,_appCode.asString(),component);
		} else {
			Node node = this.getPropertyNode(component,propXPath);
			if (node != null) {
		        try {
			        outObj = transformFuncion.apply(node);
		        } catch (Throwable th) {
		        	String err = String.format("Error transforming property %s from %s/%s to an object using a transform function: %s",
		        						       propXPath,_appCode,component,th.getMessage());
		        	log.error(err,th);
		        }
			}
		}
		return outObj;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a cached property or it gets it from the underlying properties XML if it was NOT loaded (execs the associated xpath expression)
     * @param component
     * @return
     */
    private ComponentCacheXML _retrieveComponent(final AppComponent component) {
    	ComponentCacheXML outComp = null;
    	try {
        	ComponentCacheKey key = new ComponentCacheKey(component);
        	outComp = _componentsXMLCache.get(key);		// Get the component from the cache if present
        	if (outComp == null) {
        		// Load the component definition
        		XMLPropertiesComponentDef compDef = XMLPropertiesComponentDefLoader.loadOrDefault(_systemSetEnvironment,
	        													   								  _appCode,component);
    			log.trace("Loading properties for {}/{} with component definition:{}",
    					 _appCode.asString(),component,compDef.debugInfo().toString());

    			// [0] -- Tell the cache that a new properties component has been loaded
    			//		  (at this point the cache is re-built to accommodate the new estimated property number)
    			_componentLoadedListener.newComponentLoaded(compDef);

    			// [1] -- Load the XML file
    			Document xmlDoc = _loadComponentXML(compDef);

        		// [2] -- Load the reload control policy
    			ResourcesReloadControl reloadControlImpl = _loadReloadControlImpl(compDef);

        		// [3] -- Cache
        		outComp = new ComponentCacheXML(compDef,System.currentTimeMillis(),reloadControlImpl,
        									    xmlDoc);
        		_componentsXMLCache.put(key,outComp);
        	}
        } catch (XMLPropertiesException xmlPropsEx) {
        	xmlPropsEx.printStackTrace(System.out);
        }
    	return outComp;
    }
    /**
     * Loads a properties XML file for appCode/component as stated at the component definition
     * @param component
     * @param compDef component definition
     * @return the xml {@link Document}
     * @throws XMLPropertiesException if the XML file cannot be loaded or it's malformed
     */
    private Document _loadComponentXML(final XMLPropertiesComponentDef compDef) throws XMLPropertiesException {
    	// [1] Get a resources loader
    	ResourcesLoader resLoader = ResourcesLoaderBuilder.createResourcesLoaderFor(compDef.getLoaderDef());

		XMLDocumentBuilder domBuilder = new XMLDocumentBuilder(resLoader);
    	
    	// [2] Load the XML file using the configured resourcesLoader and parse it
		Path defPropsFileUri = compDef.getPropertiesFileURI();
		Document defXmlDoc = null;
		try {
			defXmlDoc = domBuilder.buildXMLDOM(defPropsFileUri);
		} catch (SAXException saxEx) {
			if (Throwables.getRootCause(saxEx) instanceof FileNotFoundException) {
				log.error("Could NOT load xml properties file at {} using {} loader",
						  defPropsFileUri,
						  compDef.getLoaderDef().getLoader());
				throw XMLPropertiesException.propertiesLoadError(_systemSetEnvironment,_appCode,compDef.getName());
			} 
			throw XMLPropertiesException.propertiesXMLError(_systemSetEnvironment,_appCode,compDef.getName());
		}
		
		// [3] Try to find an env-dependent XML Properties file
		Document envXmlDoc = null;
		Path envDepPropsFileUri = Path.from(_systemSetEnvironment)
									  .joinedWith(compDef.getPropertiesFileURI());
		if (_systemSetEnvironment != null) {
			log.warn("...trying to find env-dependent properties file at {}",envDepPropsFileUri);
			InputStream envDepPropsFileIS = null;
			try {
				envDepPropsFileIS = resLoader.getInputStream(envDepPropsFileUri);
			} catch (IOException ioEx) {
				log.warn("...NO env-dependent properties file found at {}",envDepPropsFileUri);
			}
			if (envDepPropsFileIS != null) {
				try {
					log.warn("...loading env-dependent properties file at {}",
							 envDepPropsFileUri);
					envXmlDoc = domBuilder.buildXMLDOM(envDepPropsFileUri);
				} catch (SAXException saxEx) {
					saxEx.printStackTrace();
				}
			}
		} else {
			log.warn("...no env set with -DR01ENV={env}: NO env-dependent properties file is used!");
		}
		// [4] Merge all files if necessary
		Document outXml = null;
		if (envXmlDoc != null) {
			try {
				log.warn("... merge xml properties file at {} with env-dependent at {}",
						 defPropsFileUri,envDepPropsFileUri);
				XMLMerger merger = new XMLMerger();
				merger.merge(defXmlDoc);		// recessive
				merger.merge(envXmlDoc);	// dominant
						
				outXml = merger.buildDocument();
			} catch (ParserConfigurationException cfgEx) {
				log.error("Error while merging properties xml doc at {} with the env-dependent at {}: {}",
						  defPropsFileUri,envDepPropsFileUri,
						  cfgEx.getMessage(),cfgEx);
				outXml = defXmlDoc;
			}
    	} else {
    		outXml = defXmlDoc;
    	}
		if (log.isDebugEnabled()) log.debug("Effective xml properties at {}\n{}",
											compDef.getPropertiesFileURI(),
											XMLUtils.asString(outXml));	
		return outXml;
    }
    private static ResourcesReloadControl _loadReloadControlImpl(final XMLPropertiesComponentDef compDef) {
		ResourcesReloadControlDef reloadControlDef = compDef.getLoaderDef()
															.getReloadControlDef();
		if (reloadControlDef == null) return null;

		ResourcesReloadControl outReloadControl = ResourcesReloadControlBuilder.createFor(reloadControlDef);
		return outReloadControl;
    }

}
