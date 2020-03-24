package r01f.xml;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import r01f.resources.ResourcesLoader;
import r01f.types.IsPath;

/**
 * XML parsing utils
 */
public class XMLDocumentBuilder {
///////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////
	private ResourcesLoader _resourcesLoader;	// file loading
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	public XMLDocumentBuilder(final ResourcesLoader resLoader) {
		_resourcesLoader = resLoader;
	}
///////////////////////////////////////////////////////////////////////////////
//  METHODS
///////////////////////////////////////////////////////////////////////////////
    /**
     * Gets a DOM from an XML
     * @param xmlFilePath path to the xml file
     * @param ignoredEntities EXTERNAL entities to be ignored (not resolved)
     *          - Internal entities: <!ENTITY entityname "replacement text">
     *          - External entities: <!ENTITY entityname [PUBLIC "public-identifier"] SYSTEM "system-identifier"> 
     *        (ie: in order to avoid dtd validation the dcr4.5.dtd entity is ignored at <!DOCTYPE record SYSTEM "dcr4.5.dtd">)
     *        The ignored entities are given in an array as publicId:systemId
     * @return the dom object
     * @throws SAXException 
     */
    public Document buildXMLDOM(final IsPath xmlFilePath,
    							final String... ignoredEntities) throws SAXException {
    	Document outDoc = null;
    	try {
			InputStream xmlIS = _resourcesLoader.getInputStream(xmlFilePath,
																true);	// true=do not use resources cache
	    	if (xmlIS != null) {
	    		outDoc = XMLDocumentBuilder.buildXMLDOM(xmlIS,ignoredEntities);
	    	} else {
	    		throw new FileNotFoundException("The XML resource could not be loaded from " + xmlFilePath + ": resource does NOT exists!");
	    	}
	    	xmlIS.close();	// do not forget!
    	} catch (IOException ioEx) {
    		throw new SAXException(ioEx);
    	}
    	return outDoc;
    }	
    /**
     * Gets a DOM from an XML
     * @param xmlIs xml input stream
     * @param ignoredEntities EXTERNAL entities to be ignored (not resolved)
     *          - Internal entities: <!ENTITY entityname "replacement text">
     *          - External entities: <!ENTITY entityname [PUBLIC "public-identifier"] SYSTEM "system-identifier"> 
     *        (ie: in order to avoid dtd validation the dcr4.5.dtd entity is ignored at <!DOCTYPE record SYSTEM "dcr4.5.dtd">)
     *        The ignored entities are given in an array as publicId:systemId
     * @return the dom object
     * @throws SAXException 
     */
    public static Document buildXMLDOM(final InputStream xmlIs,final String... ignoredEntities) throws SAXException {
        // Instance a parser factory (builder)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);      
        factory.setValidating(false);           
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);            
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder(); 
            builder.setEntityResolver(
                    // An instance of EntityResolver used to ignore external entities
                    new EntityResolver() {
                        @Override
						public InputSource resolveEntity(String publicId, String systemId) {
                            String key = publicId != null ? publicId + "." + systemId : systemId;                            
                            if  (_isIgnoredExternalEntity(key)) {
                                return new InputSource(new ByteArrayInputStream("".getBytes()));
                            } 
                            return null;
                        }
                        private boolean _isIgnoredExternalEntity(String key) {
                            if (ignoredEntities != null) {
                                for (int i=0; i<ignoredEntities.length; i++) {
                                    if (ignoredEntities[i].equals(key)) return true;
                                }
                            }
                            return false;
                        }
                    }
            ); 
            return builder.parse(xmlIs);   	// parse and return
        } catch (ParserConfigurationException pcEx) {
            throw new SAXException(pcEx);
        } catch (IOException ioEx) {
            throw new SAXException(ioEx);
        }        
    }
}
