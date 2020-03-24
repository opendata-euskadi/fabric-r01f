/*
 * Created on 27/09/2006
 * 
 * @author co01556e - Alex Lara
 * (c) 2006 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Utility to exec XSLT transformations; once the template is initialized, it can be
 * used to transform any XML
 */
public class XSLTransformer {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private Source _xslSrc;
	private Properties _transformerParams;
	private Transformer _transformer;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Default no-args constructor
     */
    public XSLTransformer() {
        super();
    }
    /**
     * Constructor from a XML InputStream and the transformer params
     * @param xslIS
     * @param transformerParams
     */
    public XSLTransformer(final InputStream xslIS,
    					  final Properties transformerParams) throws TransformerConfigurationException {
    	_xslSrc = new StreamSource(xslIS);
    	_transformerParams = transformerParams;
    	_initTransformer();    	
    }
    /**
     * Constructor from a XML Reader and the transformer params
     * @param xslIS
     * @param transformerParams
     */
    public XSLTransformer(final Reader xslReader,
    					  final Properties transformerParams) throws TransformerConfigurationException {
    	_xslSrc = new StreamSource(xslReader);
    	_transformerParams = transformerParams;
    	_initTransformer();    	
    }    
    /**
     * Constructor from a SAX-style InputSource and the transformer params
     * @param saxInputSrc 
     * @param transformerParams
     */
    public XSLTransformer(final InputSource saxInputSrc,
    					  final Properties transformerParams) throws TransformerConfigurationException {
    	_xslSrc = new SAXSource(saxInputSrc);
    	_transformerParams = transformerParams;
    	_initTransformer();    	
    }
    /**
     * Contructor from a SAX-style InputSource
     * @param saxXMLReader
     * @param saxInputSrc 
     * @param transformerParams 
     */    
    public XSLTransformer(final XMLReader saxXMLReader,
    					  final InputSource saxInputSrc,
    					  final Properties transformerParams) throws TransformerConfigurationException {
    	_xslSrc = new SAXSource(saxXMLReader,
    							saxInputSrc);
    	_transformerParams = transformerParams;
    	_initTransformer();    	
    }
    /**
     * Constructor from a DOM's Node
     * @param node
     * @param transformerParams 
     */
    public XSLTransformer(final Node node,
    					  final Properties transformerParams) throws TransformerConfigurationException {
    	_xslSrc = new DOMSource(node);
    	_transformerParams = transformerParams;
    	_initTransformer();
    }
    /**
     * Inits the transformer
     */
    private void _initTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        _transformer = transformerFactory.newTransformer(_xslSrc);  
        if (_transformerParams != null) {
            for (Map.Entry<Object,Object> me  : _transformerParams.entrySet()) {
                _transformer.setParameter((String)me.getKey(),me.getValue());
            }
        }    
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////        
    public void applyToXMLGeneratingStream(final InputStream xmlIS,
    									   final OutputStream xmlOS) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlIS);
	    	Result res = new StreamResult(xmlOS);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlIS != null) xmlIS.close();
    			if (xmlOS != null) {
    				xmlOS.flush();
    				xmlOS.close();
    			}
    		} catch (IOException ioEx) { /* ignore */ }
    	}
    }
    public void applyToXMLGeneratingStream(final InputStream xmlIS,
    									   final Writer xmlWriter) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlIS);
	    	Result res = new StreamResult(xmlWriter);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlIS != null) xmlIS.close();
    			if (xmlWriter != null) {
    				xmlWriter.flush();
    				xmlWriter.close();
    			}
    		} catch (IOException ioEx) { /* ignore */ }
    	}    	
    }    
    public void applyToXMLGeneratingSAXEvents(final InputStream xmlIS,
    										  final ContentHandler saxContentHandler) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlIS);
	    	Result res = new SAXResult(saxContentHandler);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlIS != null) xmlIS.close();
    		} catch (IOException ioEx) { /* ignore */ }
    	} 
    }
    public void applyToXMLGeneratingDOMNode(final InputStream xmlIS,
    										final Node outNode) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlIS);
	    	Result res = new DOMResult(outNode);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlIS != null) xmlIS.close();
    		} catch (IOException ioEx) { /* ignore */ }
    	} 
    }
    public void applyToXMLGeneratingStream(final Reader xmlReader,
    									   final OutputStream xmlOS) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlReader);
	    	Result res = new StreamResult(xmlOS);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlReader != null) xmlReader.close();
    			if (xmlOS != null) {
    				xmlOS.flush();
    				xmlOS.close();
    			}
    		} catch (IOException ioEx) { /* ignore */ }
    	}
    }
    public void applyToXMLGeneratingStream(final Reader xmlReader,
    									   final Writer xmlWriter) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlReader);
	    	Result res = new StreamResult(xmlWriter);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlReader != null) xmlReader.close();
    			if (xmlWriter != null) {
    				xmlWriter.flush();
    				xmlWriter.close();
    			}
    		} catch (IOException ioEx) { /* ignore */ }
    	}    	
    }    
    public void applyToXMLGeneratingSAXEvents(final Reader xmlReader,
    										  final ContentHandler saxContentHandler) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlReader);
	    	Result res = new SAXResult(saxContentHandler);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlReader != null) xmlReader.close();
    		} catch (IOException ioEx) { /* ignore */ }
    	} 
    }
    public void applyToXMLGeneratingDOMNode(final Reader xmlReader,
    										final Node outNode) throws TransformerException {
    	try {
	    	Source xmlSrc = new StreamSource(xmlReader);
	    	Result res = new DOMResult(outNode);
	    	_doTransform(xmlSrc,res);
    	} finally {
    		try {
    			if (xmlReader != null) xmlReader.close();
    		} catch (IOException ioEx) { /* ignore */ }
    	} 
    }
    /**
     * Do the transformation 
     * @param xmlSrc 
     * @param res 
     * @throws TransformerException
     */
    public void _doTransform(final Source xmlSrc,
    						 final Result res) throws TransformerException {       
    	if (_transformer == null) throw new TransformerException("The transformer is NOT initialized: please check the constructor call");
    	if (xmlSrc == null) throw new TransformerException("The source cannot be transformed: the source XML is null, please check the applyToXMLGeneratingStream() method call");
    	if (res == null) throw new TransformerException("The destination is null: please check the applyToXMLGeneratingStream() method call");
        // Do transform
        _transformer.transform(xmlSrc,res);
    }

}
