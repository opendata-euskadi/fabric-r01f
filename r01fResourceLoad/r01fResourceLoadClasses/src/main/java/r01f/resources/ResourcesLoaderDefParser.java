package r01f.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.resources.ResourcesReloadControlDef.ResourcesReloadPolicy;
import r01f.types.TimeLapse;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Parses the resources loader definition xml
 */
@Slf4j
class ResourcesLoaderDefParser {
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////	
	public static ResourcesLoaderDef parse(final InputStream defXmlIs) {
		ResourcesLoaderDef outDef = null;
		try {
			Document doc = _parse(defXmlIs);
			outDef = ResourcesLoaderDefParser.parse(doc.getDocumentElement());
		} catch (SAXException saxEx) {
			log.error("Error at the resources loader definition xml: {}",
				      saxEx.getMessage(),
				      saxEx);
		}
		return outDef;
	}
	/**
	 * <pre class='brush:xml'>
	 * 		<resourcesLoader id='myResourcesLoader' type='CLASSPATH' charset='ISO-8859-1'>
	 * 			<reloadControl impl='FILE_LAST_MODIF_TIMESTAMP' checkInterval='3000s' enabled='true'>
	 * 				<props>
	 * 					<prop1>value1</prop1>
	 * 					<prop2>value2</prop2>
	 * 				</props>
	 * 			</reloadControl>
	 * 		</resourcesLoader>
	 * </pre>
	 * @param defXmlNode
	 * @return
	 */
	public static ResourcesLoaderDef parse(final Node defXmlNode) {
		ResourcesLoaderDef outDef = null;
		try {
			String id = (String)_xPath(defXmlNode,"@id",
							   		   XPathConstants.STRING);
			String type = (String)_xPath(defXmlNode,"@type",
							   		   	 XPathConstants.STRING);
			String charset = (String)_xPath(defXmlNode,"@charset",
											XPathConstants.STRING);
			Node reloadControlNode = (Node)_xPath(defXmlNode,"reloadControl",
												  XPathConstants.NODE);
			
			ResourcesReloadControlDef reloadControlDef = reloadControlNode != null ? _parseReloadControlDef(reloadControlNode)
																				   : null;
			outDef = new ResourcesLoaderDef();
			if (Strings.isNOTNullOrEmpty(id)) outDef.setId(id);
			if (Strings.isNOTNullOrEmpty(type)) outDef.setLoader(ResourcesLoaderType.valueOf(type));
			if (Strings.isNOTNullOrEmpty(charset)) outDef.setCharsetName(charset);
			outDef.setReloadControlDef(reloadControlDef);
			
		} catch (XPathExpressionException xpathEx) {
			log.error("Error at the resources loader definition xml: {}",
				      xpathEx.getMessage(),
				      xpathEx);
		}
		return outDef;
	}
	private static ResourcesReloadControlDef _parseReloadControlDef(final Node reloadControlDefXmlNode) throws XPathExpressionException {
		String impl = (String)_xPath(reloadControlDefXmlNode,"@impl",
							   		 XPathConstants.STRING);
		String checkInterval = (String)_xPath(reloadControlDefXmlNode,"@checkInterval",
							   		 		  XPathConstants.STRING);
		String enabled = (String)_xPath(reloadControlDefXmlNode,"@enabled",
							   		 	XPathConstants.STRING);
		NodeList propsNodes = (NodeList)_xPath(reloadControlDefXmlNode,"props/child::node()",
											   XPathConstants.NODESET);
		
		Map<String,String> controlProps = null;
		if (propsNodes != null
		 && propsNodes.getLength() > 0) {
			controlProps = Maps.newHashMap();
			for (int i=0; i < propsNodes.getLength(); i++) {
				Node propNode = propsNodes.item(i);
				String propName = propNode.getNodeName();
				String propValue = propNode.getFirstChild().getNodeValue();
				controlProps.put(propName,propValue);
			}
		}
		ResourcesReloadControlDef outReloadControlDef = new ResourcesReloadControlDef();
		if (Strings.isNOTNullOrEmpty(impl)) outReloadControlDef.setImpl(ResourcesReloadPolicy.valueOf(impl));
		if (Strings.isNOTNullOrEmpty(checkInterval)) outReloadControlDef.setCheckInterval(TimeLapse.of(checkInterval));
		if (Strings.isNOTNullOrEmpty(enabled)) outReloadControlDef.setEnabled(Boolean.parseBoolean(enabled));
		if (CollectionUtils.hasData(controlProps)) outReloadControlDef.setControlProps(controlProps);
		
		return outReloadControlDef; 
	}
///////////////////////////////////////////////////////////////////////////////
// 	
///////////////////////////////////////////////////////////////////////////////
    private static Document _parse(final InputStream is) throws SAXException {
        // Instance a parser (builder) 
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);      // Pasar de los comentarios
        factory.setValidating(false);           // NO Validar los documentos
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(false);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            InputSource ins = new InputSource(is);
            return builder.parse(ins);    // parsear el xml y devolver el documento xml (DOM)
        } catch (ParserConfigurationException pcEx) {
            throw new SAXException(pcEx);
        } catch (IOException ioEx) {
            throw new SAXException(ioEx);
        } finally {
        	try {
        		is.close();
        	} catch (IOException ioEx) {
        		ioEx.printStackTrace();
        	}
        }
    }
    /**
     * Gets the {@link Node} at the provided XPath 
     * @param xml
     * @param theXPath
     * @param returnType The java type returned (boolean, number, string, node o nodeSet).
     * @return 
     */
	private static Object _xPath(final Node xml,
    				      		 final String theXPath,
    				      		 final QName returnType) throws XPathExpressionException {

        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xPath = xPathFactory.newXPath();
        final XPathExpression xPathExpr = xPath.compile(theXPath.trim());

        Object outObj = null;
        if (returnType == XPathConstants.BOOLEAN) {
        	outObj = xPathExpr.evaluate(xml,XPathConstants.BOOLEAN);
        } else if (returnType == XPathConstants.NUMBER) {
        	outObj = xPathExpr.evaluate(xml,XPathConstants.NUMBER);
        } else if (returnType == XPathConstants.STRING) {
        	outObj = xPathExpr.evaluate(xml,XPathConstants.STRING);
        } else if (returnType == XPathConstants.NODE) {
        	outObj = xPathExpr.evaluate(xml,XPathConstants.NODE);
        } else if (returnType == XPathConstants.NODESET) {
        	outObj = xPathExpr.evaluate(xml,XPathConstants.NODESET);
        }
        return outObj;
    }
}
