/*
 * Created on 11-ago-2004
 *
 * @author ie00165h
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.model;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.xml.XMLUtils;

@Slf4j
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class XLNetsObjectBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS DE UTILIDAD
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Ejecuta una sentencia XPath en un documento XML
     * @param xPathExpr La expresion xpath
     * @return El nodo encontrado o null si no se encuentra ninguno
     */
    static Node _executeXPathForNode(final Node node,
    								 final String xPathExpr) {
        if (xPathExpr == null || node == null) return null;
        Node outNode = null;
        try {
            outNode = XMLUtils.nodeByXPath(node,xPathExpr);		// outNode = XPathAPI.selectSingleNode(node,xPathExpr);
        } catch (XPathExpressionException transEx) {
            log.error("Error al ejecutar la sentencia XPath {}: {}",
            		  xPathExpr,transEx.getMessage(),transEx);
        }
        log.trace("-->XPATH: {} >>>> {}",
        		  xPathExpr,(outNode == null ? "Not found":"Found"));
        return outNode;
    }
    /**
     * Ejecuta una sentencia XPath en un documento XML
     * @param xPathExpr La expresion xpath
     * @return: El nodo encontrado o null si no se encuentra ninguno
     */
    static NodeList _executeXPathForNodeList(final Node node,
    								  		 final String xPathExpr) {
        if (xPathExpr == null || node == null) return null;
        NodeList nodeList = null;
        try {
            nodeList = XMLUtils.nodeListByXPath(node,xPathExpr);	// XMLUtils.selectNodeList(node,xPathExpr);
        } catch (XPathExpressionException transEx) {
            log.error("Error al ejecutar la sentencia XPath {}: {}",
            		  xPathExpr,transEx.getMessage(),transEx);
        }
        log.trace("-->XPATH: {} >>>> {}",
        		  xPathExpr,(XMLUtils.isNullOrEmpty(nodeList) ? "Not found" : "Found"));
        return nodeList;
    }
    /**
     * Obtiene un string con un valor obtenido de ejcutar una sentencia xpath en el xml
     * @param xPathExpr La sentencia xpath
     * @return: Un String con el valor
     */
    static String _extractValue(final Node node,
    							final String xPathExpr) {
        Node outNode = _executeXPathForNode(node,xPathExpr);
        return outNode != null ? outNode.getNodeValue()
        					   : null;
    }
    /**
     * Obtiene una lista de strings de una lista obtenida de ejecutar una sentencia xpath en el xml
     * @param xPathExpr La expresion xpath
     * @return: Un array de strings con los valores
     */
    static String[] _extractMultipleValue(final Node node,
    									  final String xPathExpr) {
        NodeList nodeList = _executeXPathForNodeList(node,xPathExpr);
        String[] outArray = null;
        if (nodeList != null) {
            outArray = new String[nodeList.getLength()];
            for (int i=0; i < nodeList.getLength(); i ++) {
                outArray[i] = nodeList.item(i).getFirstChild().getNodeValue();
            }
        }
        return outArray;
    }
    /**
     * Carga un fichero xml y devuelve el documento dom
     * @param fileName La ruta completa al fichero
     * @return El documento xml (DOM)
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    static Document _loadXMLFile(String fileName) throws ParserConfigurationException,
    													 IOException,
    													 SAXException {
        // Cargar el fichero
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        //factory.setNamespaceAware(true);
       DocumentBuilder builder = factory.newDocumentBuilder();
       return builder.parse( new File(fileName) );
    }

}
