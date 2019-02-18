package r01f.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import r01f.internal.R01F;
import r01f.types.Path;
import r01f.util.types.Strings;


/**
 * XML utilities.
 */
public final class XMLUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
//////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * ISO-8859-1 xml header.
	 */
    public static final String HEADER_ENCODING_ISO_8859_1 = "<?xml version='1.0' encoding='" + R01F.ENCODING_ISO_8859_1 + "'?>";
    /**
     * UTF-8 xml header.
     */
    public static final String HEADER_ENCODING_ISO_UTF8 = "<?xml version='1.0' encoding='" + R01F.ENCODING_UTF_8 + "'?>";
    /**
     * Regexp to linarize xml string.
     */
    private static final String XML_LINARIZATION_REGEX = "(>|&gt;) {1,1}(\\t)*(\\n|\\r)+(\\s)*(<|&lt;) {1,1}";
    /**
     * Linarize xml string replacement.
     */
    private static final String XML_LINARIZATION_REPLACEMENT = "$1$5";

///////////////////////////////////////////////////////////////////////////////
//  SERIALIZATIONS
///////////////////////////////////////////////////////////////////////////////
    /**
     * Prints a XML {@link Document}
     * @param doc
     * @return a String
     */
    public static String asString(final Document doc) {
    	return XMLUtils.asString(doc,Charset.defaultCharset());
    }
    /**
     * Prints a XML {@link Document} linearized without whitespace within tags
     * @param doc
     * @return
     */
    public static String asStringLinearized(final Document doc) {
    	return XMLUtils.asStringLinearized(doc,Charset.defaultCharset());
    }
    /**
     * Prints a XML {@link Document} encoded as provided
     * @param doc
     * @param outEncoding
     * @return
     */
    public static String asString(final Document doc,final Charset outEncoding) {
        if (doc == null) return null;
        return XMLUtils.asString(doc.getDocumentElement(),outEncoding);
    }
    /**
     * Prints a XML {@link Document} encoded as provided and linearized without
     * whitespace within tags
     * @param doc
     * @param outEncoding
     * @return
     */
    public static String asStringLinearized(final Document doc,
    									    final Charset outEncoding) {
        if (doc == null) return null;
        return XMLUtils.asStringLinearized(doc.getDocumentElement(),outEncoding);
    }
    /**
     * Prints a XML {@link Node}
     * @param beginNode
     * @return
     */
    public static String asString(final Node beginNode) {
    	return XMLUtils.asString(beginNode,Charset.defaultCharset());
    }
    /**
     * Prints a XML {@link Node} linearized without whitespace within tags
     * @param beginNode
     * @return
     */
    public static String asStringLinearized(final Node beginNode) {
    	return XMLUtils.asStringLinearized(beginNode,Charset.defaultCharset());
    }
    /**
     * Prints a XML {@link Node} encoded as provided
     * @param beginNode
     * @param outEncoding
     * @return
     */
     public static String asString(final Node beginNode,
    							   final Charset outEncoding) {
        if (beginNode == null) return null;
        try {
            return XMLStringSerializer.writeOuterXML(beginNode,outEncoding);
        } catch (TransformerException tEx) {
            return ( "Error while serializing a DOM node as a String: " + tEx.toString() );
        }
     }
     /**
     * Prints a XML {@link Node} encoded as provided and linearized
     * without whitespace within tags
     * @param beginNode
     * @param outEncoding
     * @return
     */
    public static String asStringLinearized(final Node beginNode,
    										final Charset outEncoding) {
        if (beginNode == null) return null;
        try {
        	return _linarizeXml(XMLStringSerializer.writeOuterXML(beginNode,outEncoding));
        } catch (TransformerException tEx) {
            return ( "Error while serializing a DOM node as a String: " + tEx.toString() );
        }
    }
///////////////////////////////////////////////////////////////////////////////
// 	PARSE
///////////////////////////////////////////////////////////////////////////////
    /**
     * Loads a XML File, parses it and returns a DOM {@link Document}
     * @param filePath
     * @return
     * @throws SAXException
     */
    public static Document parse(final Path filePath,
    							 final String... ignoredEntities) throws IOException,
    																	 SAXException {
    	return XMLUtils.parse(new FileInputStream(filePath.asAbsoluteString()),
    						  ignoredEntities);
    }
    /**
     * Loads a XML File, parses it and returns a DOM {@link Document}
     * @param file
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public static Document parse(final File file,
    							 final String... ignoredEntities) throws IOException,
    													 SAXException {
        return XMLUtils.parse(new FileInputStream(file),
        					  ignoredEntities);
    }
    /**
     * Gets a DOM {@link Document} from a XML
     * @param is
     * @param ignoredEntities EXTRENAL entities to be ignored
     *          - Internal entities: <!ENTITY entityname "replacement text">
     *          - External entities: <!ENTITY entityname [PUBLIC "public-identifier"] SYSTEM "system-identifier">
     *        (this is used for example to avid DTD validation set at DOCTYPE entity
     *         <!DOCTYPE record SYSTEM "dcr4.5.dtd">)
     *        The IGNORED external entities are provided in an array like publicId:systemId
     * @return
     * @throws SAXException
     */
    public static Document parse(final InputStream is,
    							 final String... ignoredEntities) throws SAXException {
        // Instance a parser (builder)
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);      // Pasar de los comentarios
        factory.setValidating(false);           // NO Validar los documentos
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(false);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(
                    // Create an EntityResolver instance used to ignore external entities
                    new EntityResolver() {
                    	@Override
                        public InputSource resolveEntity(final String publicId,final String systemId) {
                    		return new InputSource(new ByteArrayInputStream("".getBytes()));
                            //String key = publicId != null ? publicId + "." + systemId : systemId;
                            //if  (_isIgnoredExternalEntity(key)) {
                            //    return new InputSource(new ByteArrayInputStream("".getBytes()));
                            //}
                            //return null;
                        }
                        @SuppressWarnings("unused")
						private boolean _isIgnoredExternalEntity(final String key) {
                            if (ignoredEntities != null) {
                                for (int i=0; i<ignoredEntities.length; i++) {
                                    if (ignoredEntities[i].equals(key)) return true;
                                }
                            }
                            return false;
                        }
                    }
            );
            InputSource ins = new InputSource(is);
            return builder.parse(ins);    // parsear el xml y devolver el documento xml (DOM)
        } catch (ParserConfigurationException pcEx) {
            throw new SAXException(pcEx);
        } catch (IOException ioEx) {
            throw new SAXException(ioEx);
        } finally {
        	try {
        		is.close();
        	} catch(IOException ioEx) {
        		ioEx.printStackTrace();
        	}
        }
    }
///////////////////////////////////////////////////////////////////////////////
//	XPath
///////////////////////////////////////////////////////////////////////////////
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
	/**
	 * Gets the {@link Node} at the provided XPath
	 * @param xml
	 * @param theXPath
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Node nodeByXPath(final Node xml,
    				        	   final String theXPath) throws XPathExpressionException {
		return (Node)_xPath(xml,theXPath,XPathConstants.NODE);
	}
	/**
	 * Gets the {@link NodeList} at the provided XPath
	 * @param xml
	 * @param theXPath
	 * @return
	 * @throws XPathExpressionException
	 */
	public static NodeList nodeListByXPath(final Node xml,
    				                	   final String theXPath) throws XPathExpressionException {
		return (NodeList)_xPath(xml,theXPath,XPathConstants.NODESET);
	}
	/**
	 * Gets a {@link String} at the provided XPath
	 * @param xml
	 * @param theXPath
	 * @return
	 * @throws XPathExpressionException
	 */
	public static String stringByXPath(final Node xml,
									   final String theXPath) throws XPathExpressionException {
		return (String)_xPath(xml,theXPath,XPathConstants.STRING);
	}
	/**
	 * Gets the {@link Number} at the provided XPath
	 * @param xml
	 * @param theXPath
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Number numberByXPath(final Node xml,
									   final String theXPath) throws XPathExpressionException {
		return (Number)_xPath(xml,theXPath,XPathConstants.NUMBER);
	}
	/**
	 * Gets the {@link Boolean} at the provided XPath
	 * @param xml
	 * @param theXPath
	 * @return
	 * @throws XPathExpressionException
	 */
	public static boolean booleanByXPath(final Node xml,
								  		 final String theXPath) throws XPathExpressionException {
		return (Boolean)_xPath(xml,theXPath,XPathConstants.BOOLEAN);
	}
///////////////////////////////////////////////////////////////////////////////
//  UTILITY
///////////////////////////////////////////////////////////////////////////////
    /**
     * Returns true if the node is a text nod
     * @param textNode
     * @return true if it's a text node
     */
    public static boolean isTextNode(final Node textNode) {
        if (textNode == null) return false;
        final short nodeType = textNode.getNodeType();
        return nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE;
    }
    /**
     * Returns the text content of a node
     * For example, in:
     * <pre class='brush:xml'>
     * 		<node>text</node>
     * </pre>
     * returns 'text'
     * @param node
     * @return
     */
    public static String nodeTextContent(final Node node) {
    	String outStr = null;
    	if (node != null) {
    		final Node firstChild = node.getFirstChild();	// the text is on the first son
			if (XMLUtils.isTextNode(firstChild)) {
				outStr = node.getTextContent();
			} else {
				outStr = XMLStringSerializer.writeNode(firstChild,null);
			}
    	}
    	return outStr;
    }
    /**
     * Returns the value of a node's attribute
     * @param node the node
     * @param attrName the attribute name
     * @return
     */
    public static String nodeAttributeValue(final Node node,final String attrName) {
    	if (node.getAttributes() == null || node.getAttributes().getLength() == 0) return null;
    	return XMLUtils.nodeTextContent(node.getAttributes().getNamedItem(attrName));
    }
    /**
     * Returns true if the node is an element node
     * @param textNode the node
     * @return true if it's a normal node
     */
    public static boolean isElementNode(final Node textNode) {
    	final short nodeType = textNode.getNodeType();
    	return nodeType == Node.ELEMENT_NODE;
    }
    /**
     * Checks if the {@link NodeList} is null or empty
     * @param nodeList the list
     * @return true if the {@link NodeList} is null or empty
     */
    public static boolean isNullOrEmpty(final NodeList nodeList) {
    	return (nodeList == null || nodeList.getLength() == 0);
    }
    /**
     * Checks if the {@link NodeList} has data.
     * @param nodeList the list.
     * @return true if the {@link NodeList} has data.
     */
    public static boolean hasData(final NodeList nodeList) {
    	return !isNullOrEmpty(nodeList);
    }
    /**
     * Creates an iterator from a {@link NodeList}
     * @param nodeList
     * @return
     */
    public Iterator<Node> nodeListIteratorFrom(final NodeList nodeList) {
    	return new Iterator<Node>() {
    					private int _currPos = 0;

						@Override
						public boolean hasNext() {
							return nodeList != null ? _currPos < nodeList.getLength() - 1
													: false;
						}
						@Override
						public Node next() {
							return nodeList != null ? nodeList.item(_currPos)
													: null;
						}
						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
    		   };
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Linearize xml, strip whitespaces and newlines from xml.
     * @param xml source xml
     * @return xml linarized, if xml is null or empty returns empty string
     */
    private static String _linarizeXml(final String xml) {
        return (Strings.isNOTNullOrEmpty(xml)) ? xml.trim().replaceAll(XMLUtils.XML_LINARIZATION_REGEX, XMLUtils.XML_LINARIZATION_REPLACEMENT) : "";
    }
 }
