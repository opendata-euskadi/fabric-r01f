package r01f.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Range;

import lombok.Cleanup;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.util.enums.Enums;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Wraps the http server response code
 */
@Slf4j
@Accessors(prefix="_")
public class HttpResponse {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS   
/////////////////////////////////////////////////////////////////////////////////////////
			private InputStream _inputStream;
			private int _code;
	@Getter private Map<String,List<String>> _headers;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR   
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpResponse(final HttpURLConnection urlConnection) throws IOException {
		
		_code = urlConnection.getResponseCode();
		log.debug(">>>>>>>>>>>>>>>>>>>> _httpResponseCode {} " , _code);
				
		if (this.isSuccess()) {
			_inputStream = urlConnection.getInputStream();
		} else {
			_inputStream = urlConnection.getErrorStream();
			if (_inputStream == null)_inputStream = urlConnection.getInputStream();	// should not happen... but...
		}
		_headers = urlConnection.getHeaderFields();
	}
	public HttpResponse(final int code,final InputStream is) {
		_code = code;
		_inputStream = is;
		_headers = null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS   
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Load data as a {@link String}
     * @return 
     */
    public String loadAsString() {
    	String outStr = null;
    	try {
	    	@Cleanup InputStream responseIs = _inputStream;
	        outStr = IOUtils.toString(responseIs,
	        					      Charset.defaultCharset());
	    	return outStr;
    	} catch(IOException ioEx) {
    		ioEx.printStackTrace(System.out);
    	}
    	return outStr;
    }
    /**
     * Gets an stream from the server response
     * @return el stream
     */
    public InputStream loadAsStream() {
    	return _inputStream;
    }
    /**
     * Discards the server response data (the inputStream)
     * @throws IOException
     */
    public void discard() throws IOException {
    	_inputStream.close();
    }
    public HttpResponseCode getCode() {
    	return HttpResponseCode.of(_code);
    }
    
    public int getCodeNumber() {
    	return _code;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  RESPONSE CODES
//  (ver http://www.iana.org/assignments/http-status-codes/http-status-codes.xml)
/////////////////////////////////////////////////////////////////////////////////////////
    public boolean isSuccess() {
    	return HttpResponse.isSuccess(_code);
    }
    public boolean isNotFound() {
    	return HttpResponse.isNotFound(_code);
    }
    public boolean isRedirection() {
    	return HttpResponse.isRedirection(_code);
    }
    public boolean isClientError() {
    	return HttpResponse.isClientError(_code);
    }
    public boolean isServerError() {
    	return HttpResponse.isServerError(_code);
    }
    public boolean isEntityUpdateConflict() {
    	return HttpResponse.isEntityUpdateConflict(_code);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  RESPONSE CODES
//  (ver http://www.iana.org/assignments/http-status-codes/http-status-codes.xml)
/////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isSuccess(final int statusCode) {
    	Range<Integer> successErrorCodes = Range.closedOpen(200,300);	// [200..300)
    	return successErrorCodes.contains(statusCode);
    }
    public static boolean isNotFound(final int statusCode) {
    	return statusCode == 404;
    }
    public static boolean isRedirection(final int statusCode) {
    	Range<Integer> successErrorCodes = Range.closedOpen(300,400);	// [300..400)
    	return successErrorCodes.contains(statusCode);
    }
    public static boolean isClientError(final int statusCode) {
    	Range<Integer> successErrorCodes = Range.closedOpen(400,500);	// [400..500)
    	return successErrorCodes.contains(statusCode);
    }
    public static boolean isEntityUpdateConflict(final int statusCode) {
    	return statusCode == 409;
    }
    public static boolean isServerError(int statusCode) {
    	Range<Integer> successErrorCodes = Range.closedOpen(500,600);	// [500..600)
    	return successErrorCodes.contains(statusCode);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns a header
     * @param name
     * @return
     */
    public List<String> getHeader(final String name) {
    	return CollectionUtils.hasData(_headers) ? _headers.get(name) : null;
    }
    public String getSingleValuedHeaderAsString(final String name) {
    	List<String> headerValues = this.getHeader(name);
    	return CollectionUtils.hasData(headerValues) ? CollectionUtils.of(headerValues).pickOneAndOnlyElement()
    												 : null;
    }
    public int getSingleValuedHeaderAsInt(final String name) {
    	String intStr = this.getSingleValuedHeaderAsString(name);
    	return Strings.isNOTNullOrEmpty(intStr) ? Integer.parseInt(intStr)
    											: -1;
    }
    public long getSingleValuedHeaderAsLong(final String name) {
    	String longStr = this.getSingleValuedHeaderAsString(name);
    	return Strings.isNOTNullOrEmpty(longStr) ? Long.parseLong(longStr)
    											 : -1;
    }
    public boolean getSingleValuedHeaderAsBoolean(final String name) {
    	String boolStr = this.getSingleValuedHeaderAsString(name);
    	return Strings.isNOTNullOrEmpty(boolStr) ? Boolean.parseBoolean(boolStr)
    											 : false;
    }
	public <E extends Enum<E>> E getSingleValuedHeaderAsEnum(final String name,final Class<E> enumType) {
    	String enumStr = this.getSingleValuedHeaderAsString(name);
    	return Strings.isNOTNullOrEmpty(enumStr) ? Enums.wrap(enumType).fromName(enumStr)
    											 : null;
    }
}
