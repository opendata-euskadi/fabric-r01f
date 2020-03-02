package r01f.xmlproperties;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.AppComponentBase;
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
class XMLPropertiesComponentDefLoader {
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
												 final AppCode appCode,final AppComponent component) throws XMLPropertiesException {
		XMLPropertiesComponentDef outDef = null;
		// Load the component definition from /config/appCode/components/appCode.component.xml
		ResourcesLoader resourcesLoader = ResourcesLoaderBuilder.DEFAULT_RESOURCES_LOADER;

		// Get an IS to the xml definition
		// This file is ALWAYS loaded from the CLASSPATH at components/[appCode].[component].xml
		// BEWARE!! It's a RELATIVE path since the classloader is used (see ClassPathResourcesLoader)

		// Load the component definition file
		InputStream defXmlIS = null;
		try {
			// a) - Try the env-independent file
			Path compDefEnvIndepFilePath = XMLPropertiesComponentDefLoader.componentDefFilePath(appCode,component);

			log.debug("Loading ENV-INDEP xml properties component DEFINITION for appCode/component={}.{} (env={}) from CLASSPATH at {} (BEWARE that the file name DOES NOT ends with properties.xml!)",
					 appCode,component,
					 env,
					 compDefEnvIndepFilePath);

			defXmlIS = resourcesLoader.getInputStream(Path.from(compDefEnvIndepFilePath),
													  true);	// true: use cache
		} catch (IOException ioEx) {
			// b) - Try the env-dependent file
			if (ioEx instanceof FileNotFoundException
			 && env != null) {
				try {
					Path compDefEnvDepFilePath = XMLPropertiesComponentDefLoader.componentDefFilePath(env,
																   									  appCode,component);

					log.debug("Loading ENV-DEP xml properties component DEFINITION for appCode/component={}.{} (env={}) from CLASSPATH at {}",
							 appCode,component,
							 env,
							 compDefEnvDepFilePath);

					defXmlIS = resourcesLoader.getInputStream(Path.from(compDefEnvDepFilePath),
															  true);
				} catch (IOException ioEx2) {
					// nothing...
				}
			}
		}

		// c) - If not found throw
		if (defXmlIS == null)  throw XMLPropertiesException.componentDefLoadError(env,appCode,component);

		// d) - Return
		try {
			outDef = XMLPropertiesComponentDefLoader.load(defXmlIS);
			outDef.setName(component);		// Add the name  (it's not at the xml)
		} catch (IOException ioEx) {
			throw XMLPropertiesException.componentDefLoadError(env,appCode,component,
															   ioEx);
		} finally {
			if (defXmlIS != null) {
				try {
					defXmlIS.close();
				} catch (IOException ioEx) {
					/* ignored */
				}
			}
		}
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
	@SuppressWarnings("static-access")
	public static XMLPropertiesComponentDef loadOrDefault(final Environment env,
												 		  final AppCode appCode,final AppComponent component) throws XMLPropertiesException {
		// Load the component definition
		XMLPropertiesComponentDef compDef = null;
		try {
			compDef = XMLPropertiesComponentDefLoader.load(env,
														   appCode,component);
		} catch (XMLPropertiesException xmlPropsEx) {
			// If the component definition was NOT found, try a default one
			if (xmlPropsEx.is(XMLPropertiesErrorType.COMPONENTDEF_NOT_FOUND)) {
				// warn
				if (env != null) {
					log.debug("\t... Could NOT find the xml properties component definition for appCode/component={}.{} for env={} at {} or {}",
							 appCode,component,env,
							 XMLPropertiesComponentDefLoader.componentDefFilePath(appCode,component),
							 XMLPropertiesComponentDefLoader.componentDefFilePath(env,
									 				   							  appCode,component));
				} else {
					log.debug("\t... Could NOT find the xml properties component definition for appCode/component={}.{} for env={} at {}",
							 appCode,component,env,
							 XMLPropertiesComponentDefLoader.componentDefFilePath(appCode,component));
				}
				// build a default component definition
				compDef = new XMLPropertiesComponentDef();
				compDef.setName(component != null ? component : AppComponent.NO_COMPONENT);
				compDef.setNumberOfPropertiesEstimation(50);
				if (component != null && component.isNOT(AppComponent.NO_COMPONENT)) {
					// component set (the most usual case)
					compDef.setPropertiesFileURI(Path.from(String.format("%s/%s.%s.properties.xml",
																		 appCode,appCode,component)));
				} else {
					// no component
					compDef.setPropertiesFileURI(Path.from(String.format("%s/%s.properties.xml",
																		 appCode,appCode)));
				}
				compDef.setLoaderDef(ResourcesLoaderDef.DEFAULT);
				log.debug("\t... The properties file will be loaded using {} loader from path {}",
						 compDef.getLoaderDef().getLoader(),compDef.getPropertiesFileURI());
			} else {
				throw xmlPropsEx;
			}
		}
		log.debug("xml properties component loader definition for appCode/component={}/{} (env={}):\n{}",
				  appCode,component,
				  env,
				  component,compDef.debugInfo());
		return compDef;
	}
	@SuppressWarnings("static-access")
	static Path componentDefFilePath(final Environment env,
									 final AppCode appCode,final AppComponent component) {
		Path filePath = null;
		if (env == null) {
			filePath = XMLPropertiesComponentDefLoader.componentDefFilePath(appCode,component);
		} else if (component != null && component.isNOT(AppComponent.NO_COMPONENT)) {
			// component set (the most usual case)
			filePath = Path.from(Strings.customized("{}/{}/components/{}.{}.xml",			// ie: /components/loc/r01.default.xml
							   			  			env,appCode,appCode,component));
		} else {
			// no component
			filePath = Path.from(Strings.customized("{}/{}/components/{}.xml",			// ie: /components/loc/r01.xml
							   			  			env,appCode,appCode));
		}
		return filePath;
	}
	static Path componentDefFilePath(final AppCode appCode,final AppComponent component) {
		String filePath = null;
		if (component != null && component.isNOT(AppComponentBase.NO_COMPONENT)) {
			// ... this is the most common case
			filePath = Strings.customized("{}/components/{}.{}.xml",		// ie: /components/r01.default.xml
							  			  appCode,appCode,component);
		} else {
			// no component
			filePath = Strings.customized("{}/components/{}.xml",			// ie: /components/r01.xml
							  			  appCode,appCode);
		}
		return Path.from(filePath);
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
	 *						"</componentDef>";
	 * </pre>
	 * @param defXmlIS
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("null")
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
