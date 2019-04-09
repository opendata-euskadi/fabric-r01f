package r01f.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.Cleanup;
import r01f.httpclient.HttpClientProxySettings;
import r01f.resources.UrlResourceLoader;
import r01f.types.Path;
import r01f.types.url.Url;


/**
 * XML utilities.
 */
public final class XMLParseUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  PARSING
/////////////////////////////////////////////////////////////////////////////////////////
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
    public static Document parse(final File file) throws IOException,
    													 SAXException {
        return XMLUtils.parse(new FileInputStream(file));
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param urlResourceLoader
     * @param url 
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
    public static Document parse(final UrlResourceLoader urlResourceLoader,
    							 final Url url) throws IOException,
    								   				   SAXException {
    	return XMLParseUtils.parse(urlResourceLoader,
    							   url,Charset.defaultCharset(),
    						  	   (String[])null);	// no cookies
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param urlResourceLoader
     * @param url 
     * @param charset response encoding
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
    public static Document parse(final UrlResourceLoader urlResourceLoader,
    							 final Url url,final Charset charset,
    					  		 final String[]... cookies) throws IOException,
    					  		 								   SAXException {
    	return XMLParseUtils.parse(urlResourceLoader,
    							   null,		// no proxy
    							   url,    							  
    					  	  	   charset,
    					  	  	   cookies);
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param urlResourceLoader
     * @param proxySettings proxy info
     * @param url 
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
    public static Document parse(final UrlResourceLoader urlResourceLoader,
    							 final HttpClientProxySettings proxySettings,
    							 final Url url,
    					  		 final String[]... cookies) throws IOException,
    														  	   SAXException {
    	return XMLParseUtils.parse(urlResourceLoader,
    							   proxySettings,
    							   url,
    					  	  	   Charset.defaultCharset(),
    					  	  	   cookies);
    }
    /**
     * Loads a XML from a remote url, parses it and returns a DOM {@link Document}
     * @param urlResourceLoader
     * @param proxySettings proxy info
     * @param url 
     * @param charset response encoding
     * @param cookies cookies to send in the remote http request (every element is a two-position array: 0=cookieName,1=cookieValue)
     * @return 
     * @throws SAXException 
     * @throws IOException 
     */
	public static Document parse(final UrlResourceLoader urlResourceLoader,
								 final HttpClientProxySettings proxySettings,
								 final Url url,
    					  		 final Charset charset,
    					  		 final String[]... cookies) throws IOException,
    														  	   SAXException {
        @Cleanup InputStream is = urlResourceLoader.load(proxySettings,
        												 url,charset,
        												 cookies);
        Document outDoc = XMLUtils.parse(is);
        return outDoc;
    }
    /**
     * Gets a DOM {@link Document} from a XML
     * @param xml 
     * @param ignoredEntities EXTRENAL entities to be ignored
     *          - Internal entities: <!ENTITY entityname "replacement text">
     *          - External entities: <!ENTITY entityname [PUBLIC "public-identifier"] SYSTEM "system-identifier">
     *        (this is used for example to avid DTD validation set at DOCTYPE entity
     *         <!DOCTYPE record SYSTEM "dcr4.5.dtd">)
     *        The IGNORED external entities are provided in an array like publicId:systemId
     * @return 
     * @throws SAXException 
     */
    public static Document parse(final String xml,
    							 final String... ignoredEntityes) throws SAXException {
    	return XMLUtils.parse(new ByteArrayInputStream(xml.getBytes()),ignoredEntityes);
    }

 }