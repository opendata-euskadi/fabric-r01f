package r01f.xmlproperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.resources.ResourcesLoaderDef;
import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.resources.ResourcesReloadControlDef;
import r01f.resources.ResourcesReloadControlDef.ResourcesReloadPolicy;
import r01f.types.Path;
import r01f.types.TimeLapse;
import r01f.util.types.Strings;
import r01f.xml.XMLUtils;

@Slf4j
@Accessors(prefix="_")
@NoArgsConstructor
public class XMLPropertiesComponentDefLoader {
/////////////////////////////////////////////////////////////////////////////////////////
//  LOAD METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Loads a component definition xml
     * @param env environment
     * @param appCode 
     * @param component 
     * @return 
     * @throws XMLPropertiesException if the file cannot be loaded or it's malformed
     */
	public static XMLPropertiesComponentDef load(final Environment env,
    											 final AppCode appCode,
    											 final AppComponent component) throws XMLPropertiesException {
    	XMLPropertiesComponentDef outDef = null;
		// Load the component definition from /config/appCode/components/appCode.component.xml
        try {
        	// Get an IS to the xml definition
        	// This file is ALWAYS loaded from the CLASSPATH at components/[appCode].[component].xml
        	// BEWARE!! It's a RELATIVE path since the classloader is used (see ClassPathResourcesLoader)
        	String filePath = null;
        	if (env == null || env.equals(Environment.NO_ENV)) {
        		filePath = Strings.customized("{}/components/{}.{}.xml",			// ie: /components/r01.default.xml
        						  			  appCode,appCode,component);	
        	} else {
        		filePath = Strings.customized("{}/{}/components/{}.{}.xml",			// ie: /components/loc/r01.default.xml
        						   			  env,appCode,appCode,component);
        	}
        	
        	ResourcesLoader resourcesLoader = ResourcesLoaderBuilder.DEFAULT_RESOURCES_LOADER;					
			@Cleanup InputStream defXmlIS = resourcesLoader.getInputStream(Path.from(filePath),true);	// true: use cache
			outDef = XMLPropertiesComponentDefLoader.load(defXmlIS);
        } catch (IOException ioEx) {
    		throw XMLPropertiesException.componentDefLoadError(env,appCode,component,
    														   ioEx);
        }
        // Add the name  (it's not at the xml)
        outDef.setName(component);
    	return outDef;
    }
	/**
	 * Loads a component definition XML or a default one if it's NOT found
	 * @param env
	 * @param appCode
	 * @param component
	 * @return
	 * @throws XMLPropertiesException
	 */
	public static XMLPropertiesComponentDef loadOrDefault(final Environment env,
    											 		  final AppCode appCode,
    											 		  final AppComponent component) throws XMLPropertiesException {
		// Load the component definition
		XMLPropertiesComponentDef compDef = null;
		try {
    		compDef = XMLPropertiesComponentDefLoader.load(env,
    													   appCode,component);
		} catch(XMLPropertiesException xmlPropsEx) {
			// If the component definition was NOT found, try a default one
			if (xmlPropsEx.is(XMLPropertiesErrorType.COMPONENTDEF_NOT_FOUND) ) {
				// try the default component definition
				compDef = new XMLPropertiesComponentDef();
				compDef.setName(component);
				compDef.setNumberOfPropertiesEstimation(50);
				compDef.setPropertiesFileURI(Path.from(String.format("%s/%s.%s.properties.xml",
																	 appCode,appCode,component)));
				compDef.setLoaderDef(ResourcesLoaderDef.DEFAULT);
				log.warn("Could NOT found the xml properties component definition for appCode={} / component={}... trying to find it at {}",
						 appCode,component,
						 compDef.getPropertiesFileURI());
			} else {
				throw xmlPropsEx;
			}
		}
		return compDef;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////    
    public static XMLPropertiesComponentDef load(final String defXml) throws IOException {
    	XMLPropertiesComponentDef outDef = null;
    	if (Strings.isNOTNullOrEmpty(defXml)) {
    		outDef = XMLPropertiesComponentDefLoader.load(new ByteArrayInputStream(defXml.getBytes()));
    	} else {
    		throw new IOException("Cannot load the xml properties component definition!");
    	}
    	return outDef;
    }
    /**
     * Loads the xml properties component definition
     * <pre class="brush:java">
	 *		String defXml = "<componentDef name='myName'>" + 
	 *								"<propertiesFileURI>/config/r01fb.properties.xml</propertiesFileURI>" + 
	 *								"<numberOfPropertiesEstimation>10</numberOfPropertiesEstimation>" +
	 *								"<resourcesLoader id='myResLoader' type='CLASSPATH'>" + 
	 *									"<reloadControl impl='PERIODIC' enabled='true' checkInterval='2s'>" +
	 *										"<props>" +
	 *											"<a>a_value</a>" + 
	 *											"<b>b_value</b>" +
	 *										"</props>" +
	 *									"</reloadControl>" + 
	 *									"<props>" +
	 *										"<a>a_value</a>" + 
	 *										"<b>b_value</b>" +
	 *									"</props>" +
	 *								"</resourcesLoader>" + 
	 *					    "</componentDef>";
	 * </pre>
     * @param defXmlIS
     * @return
     * @throws IOException
     */
    public static XMLPropertiesComponentDef load(final InputStream defXmlIS) throws IOException {
    	XMLPropertiesComponentDef outDef = null;
    	if (defXmlIS != null) {
			try {
				// [0] - Parse the xml doc
				// 	<componentDef name="myName">
				// 		<propertiesFileURI>/config/r01fb.properties.xml</propertiesFileURI>
				// 		<resourcesLoader id="myResLoader" type='CLASSPATH'>
				// 			<reloadControl impl='PERIODIC' enabled='true' checkInterval='2s'/>
				// 		</resourcesLoader>
				//	</componentDef>
				Document xmlDoc = XMLUtils.parse(defXmlIS);
				
				String name = XMLUtils.stringByXPath(xmlDoc,"/componentDef/@name");
				String propsFileURI = XMLUtils.stringByXPath(xmlDoc,"/componentDef/propertiesFileURI");
				Number numberOfPropsEstimation = XMLUtils.numberByXPath(xmlDoc,"/componentDef/numberOfPropertiesEstimation");
								
				outDef = new XMLPropertiesComponentDef();
				if (Strings.isNOTNullOrEmpty(name)) 		outDef.setName(AppComponent.forId(name));
				if (Strings.isNOTNullOrEmpty(propsFileURI))	outDef.setPropertiesFileURI(Path.from(propsFileURI));
				if (numberOfPropsEstimation != null)		outDef.setNumberOfPropertiesEstimation(numberOfPropsEstimation.intValue());

				// ---> resources loader def
				ResourcesLoaderDef resLoaderDef = new ResourcesLoaderDef();
				
				String resLoaderId = XMLUtils.stringByXPath(xmlDoc,"/componentDef/resourcesLoader/@id");
				String resLoaderType = XMLUtils.stringByXPath(xmlDoc,"/componentDef/resourcesLoader/@type");
				String charSet = XMLUtils.stringByXPath(xmlDoc,"/componentDef/resourcesLoader/charset");
				NodeList resLoaderPropNodes = XMLUtils.nodeListByXPath(xmlDoc,"/componentDef/resourcesLoader/props/child::*");

				if (Strings.isNOTNullOrEmpty(resLoaderId)) 		resLoaderDef.setId(resLoaderId);
				if (Strings.isNOTNullOrEmpty(resLoaderType)) 	resLoaderDef.setLoader(ResourcesLoaderType.valueOf(resLoaderType));
				if (Strings.isNOTNullOrEmpty(charSet))			resLoaderDef.setCharsetName(charSet);
				if (resLoaderPropNodes != null && resLoaderPropNodes.getLength() > 0) 	resLoaderDef.setLoaderProps(_propertiesFrom(resLoaderPropNodes));
				
				// ---> resources loader control def
				ResourcesReloadControlDef resLoaderControlDef = new ResourcesReloadControlDef();
				String resLoaderCtrlPolicy = XMLUtils.stringByXPath(xmlDoc,"/componentDef/resourcesLoader/reloadControl/@impl");
				Boolean resLoaderCtrlEnabled = XMLUtils.booleanByXPath(xmlDoc,"/componentDef/resourcesLoader/reloadControl/@enabled");
				String resLoaderCtrlCheckInterval = XMLUtils.stringByXPath(xmlDoc,"/componentDef/resourcesLoader/reloadControl/@checkInterval");
				NodeList resLoaderCtrlPropNodes = XMLUtils.nodeListByXPath(xmlDoc,"/componentDef/resourcesLoader/reloadControl/props/child::*");
				if (Strings.isNOTNullOrEmpty(resLoaderCtrlPolicy)) 			resLoaderControlDef.setImpl(ResourcesReloadPolicy.valueOf(resLoaderCtrlPolicy));
				if (resLoaderCtrlEnabled != null)							resLoaderControlDef.setEnabled(resLoaderCtrlEnabled);
				if (Strings.isNOTNullOrEmpty(resLoaderCtrlCheckInterval))	resLoaderControlDef.setCheckInterval(TimeLapse.createFor(resLoaderCtrlCheckInterval));
				if (resLoaderCtrlPropNodes != null && resLoaderCtrlPropNodes.getLength() > 0)	resLoaderControlDef.setControlProps(_propertiesFrom(resLoaderCtrlPropNodes));
				resLoaderDef.setReloadControlDef(resLoaderControlDef);
				
				outDef.setLoaderDef(resLoaderDef);
				
			} catch (SAXException saxEx) {
				throw new IOException(saxEx);
			} catch (XPathExpressionException xpathEx) {
				throw new IOException(xpathEx);
			}
    	} else {
    		throw new IOException("Cannot load the xml properties component definition!");
    	}
    	return outDef;
    }
    private static Map<String,String> _propertiesFrom(final NodeList nodeList) {
    	Map<String,String> outProps = null;
    	if (nodeList.getLength() > 0) {
    		outProps = Maps.newLinkedHashMapWithExpectedSize(nodeList.getLength());
    		for (int i=0; i < nodeList.getLength(); i++) {
    			Node n = nodeList.item(i);
    			String key = n.getNodeName();
    			String value = n.getFirstChild() != null ? n.getFirstChild().getNodeValue()
    													 : null;
    			if (Strings.isNOTNullOrEmpty(value)) outProps.put(key,value);
    		}
    	}
    	return outProps;
    }
}
