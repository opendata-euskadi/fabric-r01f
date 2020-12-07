/*
 * Created on 12-feb-2005
 * 
 * @author IE00165H
 * (c) 2005 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.servlet;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletRequest;

import com.google.common.collect.Maps;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.util.types.StringConverterWrapper;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/** 
 * Query string params wrapper
 * Utility to access query string params
 * To get a param value as a given type:
 * <pre class='brush:java'>
 * 		long longParam = new RequestQueryStringParamsWrapper(req)
 * 									.paramWithName("myParam").asLong();	
 * </pre>
 */
@Slf4j
@Accessors(prefix="_")
public class HttpRequestQueryStringParamsWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    private final ServletRequest _req;        	// Request
    private final Charset _requestCharset;		// Charset in which the request query string params are encoded
    private final Charset _serverCharset;		// This server Charset
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
    public HttpRequestQueryStringParamsWrapper(final ServletRequest req) {
        _req = req;
        _requestCharset = Charset.defaultCharset();
        _serverCharset = Charset.defaultCharset();
    } 
    public HttpRequestQueryStringParamsWrapper(final ServletRequest req,
    										   final Charset requestCharset,final Charset serverCharset) {
    	_req = req;
    	_requestCharset = requestCharset;
    	_serverCharset = serverCharset;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns an array with the names of the given required query string params that are NOT present in 
     * the query string 
     * Returns null if all required query string params are present
     * @param required array of the required query string params
     * @return 
     */
    public String[] getMissingParameters(final String[] required) {
        Collection<String> missing = Lists.newArrayList();
        for (String paramName : required) {
            String val = _getParameterValueAsString(paramName);
            if (val == null) missing.add(paramName);
        }
        return CollectionUtils.hasData(missing) ? (String[])missing.toArray()
        										: null;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a query string parameter value
     * @param paramName param name
     * @return the param value encoded in the given {@link Charset}
     * @exception ServletRequestParameterNotFoundException si el parametro no se encuentra o es una cadena vaca
     */
    private String _getParameterValueAsString(final String paramName) {
    	String outValue = null;
        String[] values = _req.getParameterValues(paramName);
        if (values != null && values.length > 0) {
        	if (values.length > 1) {
	        	log.error("The request query string param with name {} has more than a single value: {}; returning just the first one",
	        			  paramName,values);
	        	outValue = values[0];
	        } else if (Strings.isNullOrEmpty(values[0])) {
	            log.error("The request query string param with name {} is empty",
	            		  paramName);
	        } else {
	        	outValue = values[0];
	        }
        }
        return outValue != null ? new String(outValue.getBytes(_requestCharset),	// bear the received param value charset
            				  				 _serverCharset)						// return the value in the required charset
        						: null;
    }
    /**
     * Returns a wrapper that enables the param value access in different types
     * <pre class='brush:java'>
     * 		long longParam = new RequestQueryStringParamParser(req)
     * 									.paramWithName("myParam").asLong();		
     * </pre>
     * @param name
     * @return
     */
    public StringConverterWrapper paramWithName(final String name) {
    	return new StringConverterWrapper(_getParameterValueAsString(name));
    }
    /**
     * Returns true if the request contains a given query string param
     * @param name
     * @return
     */
    public boolean containsParamWithName(final String name) {
    	String[] values = _req.getParameterValues(name);
    	return values != null && values.length > 0;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets a {@link Map} with the query string param values indexed by their names
     * <strong>IMPLEMENTATION NOTE</strong>:
     * Each param name and value are decoded using the query string {@link Charset}
     * an alternative of individual param name/value decoding would have been a global
     * query string decoding, BUT the param name or value could contain a = or & characters
     * that could be incorrectly interpreted as delimiters
     * @param queryString 
     * @param queryStringCharset
     * @return a {@link Map} with the query string param values indexed by their names 
     */
    public static Map<String,Collection<String>> queryStringParametersMap(final String queryString,
    														  			  final Charset queryStringCharset) {
        if (queryString != null && queryString.length() > 0) {
            byte[] bytes = null;
            if (queryStringCharset == null) {
                bytes = queryString.getBytes(Charset.defaultCharset());
            } else {
                bytes = queryString.getBytes(queryStringCharset);
            }
            return _parseQueryStringParameters(bytes,
            								   queryStringCharset);
        }
        return null;
    }
    private static Map<String,Collection<String>> _parseQueryStringParameters(final byte[] queryString,
    															  			  final Charset queryStringCharset) {
    	if (queryString == null || queryString.length == 0) return null;
    	
    	Map<String,Collection<String>> outMap = Maps.newHashMap();
    	
        int    ix = 0;
        int    ox = 0;
        String key = null;
        String value = null;
        while (ix < queryString.length) {
            byte c = queryString[ix++];
            switch ((char)c) {
            case '&':
                value = new String(queryString, 
                				   0,ox,
                				   queryStringCharset);
                if (key != null) {
                    _putMapEntry(outMap,
                    			 key,value);
                    key = null;
                }
                ox = 0;
                break;
            case '=':
                if (key == null) {
                    key = new String(queryString, 
                    				 0,ox,
                    				 queryStringCharset);
                    ox = 0;
                } else {
                    queryString[ox++] = c;
                }                   
                break;  
            case '+':
                queryString[ox++] = (byte)' ';
                break;
            case '%':
                queryString[ox++] = (byte)((_convertHexDigit(queryString[ix++]) << 4) + _convertHexDigit(queryString[ix++]));
                break;
            default:
                queryString[ox++] = c;
            }
        }
        // The last value does not ends with '&' 
        if (key != null) {
            value = new String(queryString,
            				   0,ox,
            				   queryStringCharset);
            _putMapEntry(outMap,
            			 key,value);
        }            
        return outMap; 
    }
    private static void _putMapEntry(final Map<String,Collection<String>> map,
    								 final String paramName,final String value) {
        Collection<String> values = map.get(paramName);
        if (values == null) {
            values = Lists.newArrayList();
            values.add(value);
        } else {
            values.add(value);
        }
        map.put(paramName,values);
    } 
    /**
     * Converts a character into it's hex representation
     * @param b the byte
     */
    private static byte _convertHexDigit( byte b ) {
        if ((b >= '0') && (b <= '9')) return (byte)(b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte)(b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte)(b - 'A' + 10);
        return 0;
    }
}
