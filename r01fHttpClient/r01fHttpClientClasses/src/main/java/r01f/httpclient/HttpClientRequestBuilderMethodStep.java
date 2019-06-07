package r01f.httpclient;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import r01f.mime.MimeType;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


public class HttpClientRequestBuilderMethodStep
     extends HttpClientRequestBuilderBase<HttpClientRequestBuilderMethodStep> {

///////////////////////////////////////////////////////////////////////////////
// FIELDS
///////////////////////////////////////////////////////////////////////////////
	protected Charset _targetServerCharset;	// charset utilizado por el servidor

	protected Map<String,String> _headers;	// request headers
	protected Map<String,String> _cookies;	// Cookies

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderMethodStep(final Url url) {
		super(url);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHARSET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the target server charset
	 * @param charset el charset
	 */
	public HttpClientRequestBuilderMethodStep usingCharset(final Charset charset) {
		_targetServerCharset = charset;
		if (_targetServerCharset == null) _targetServerCharset = Charset.defaultCharset();	// Asegurarse de que hay un charset establecido
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HEADERS & COOKIES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets cookies
	 * @param cookies
	 * @return
	 */
	public HttpClientRequestBuilderMethodStep settingCookies(final HttpRequestCookie... cookies) {
		if (CollectionUtils.hasData(cookies)) {
			final Map<String,String> cookiesMap = Maps.newHashMapWithExpectedSize(cookies.length);
			for (final HttpRequestCookie cookie : cookies) cookiesMap.put(cookie.getName(),
														     		cookie.getValue());
			this.settingCookies(cookiesMap);
		}
		return this;
	}
	/**
	 * Sets a cookie
	 * @param cookieName
	 * @param cookieValue
	 * @return
	 */
	public HttpClientRequestBuilderMethodStep settingCookie(final String cookieName,final String cookieValue) {
		if (Strings.isNullOrEmpty(cookieName) || Strings.isNullOrEmpty(cookieValue)) return this;
		_setCookie(cookieName,cookieValue);
		return this;
	}
	/**
	 * Sets the cookies to send to the server
	 * @param cookies the cookies
	 */
	public HttpClientRequestBuilderMethodStep settingCookies(final Map<String,String> cookies) {
		if (cookies == null || cookies.size() == 0) return this;
		for (final Map.Entry<String,String> me : cookies.entrySet()) _setCookie(me.getKey(),me.getValue());
		return this;
	}
	/**
	 * Sets a header to be sent to the server
	 * @param headers
	 * @return
	 */
	public HttpClientRequestBuilderMethodStep withHeaders(final HttpRequestHeader... headers) {
		if (CollectionUtils.hasData(headers)) {
			final Map<String,String> headersMap = Maps.newHashMapWithExpectedSize(headers.length);
			for (final HttpRequestHeader header: headers) headersMap.put(header.getName(),
														    	   header.getValue());
			this.withHeaders(headersMap);
		}
		return this;
	}
	/**
	 * Sets a header to be sent to the server
	 * (this method can be called multiple times)
	 * @param headers the header (name / value pair)
	 */
	public HttpClientRequestBuilderMethodStep withHeader(final String headerName,final String headerValue) {
		if (Strings.isNullOrEmpty(headerName) || Strings.isNullOrEmpty(headerValue)) return this;
		_setHeader(headerName,headerValue);
		return this;
	}
	/**
	 * Sets all the headers to send to the server
	 * @param headers all the headers (name/value pairs in a Map)
	 */
	public HttpClientRequestBuilderMethodStep withHeaders(final Map<String,String> headers) {
		if (CollectionUtils.isNullOrEmpty(headers)) return this;
		for (final Map.Entry<String,String> me : headers.entrySet()) _setHeader(me.getKey(),me.getValue());
		return this;
	}
	/**
	 * Sets all the headers to send to the server
	 * @param headers all the headers (name/value pairs in a Map)
	 */
	public HttpClientRequestBuilderMethodStep withAcceptHeader(final String accept) {
		return this.withHeader("accept",accept);
	}
	/**
	 * Sets a request header
	 * @param name name of the header
	 * @param value value of the header
	 */
	private void _setHeader(final String name,final String value) {
		if (_headers == null) _headers = new HashMap<String,String>();
		_headers.put(name,value);
	}
	/**
	 * Sets a cookie header
	 * @param cookieName name of the cookie
	 * @param cookieValue value of the cookie
	 */
	private void _setCookie(final String cookieName,final String cookieValue) {
		if (_cookies == null) _cookies = new HashMap<String,String>();
		_cookies.put(cookieName,cookieValue);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Disables the caching of content at proxies
	 */
	public HttpClientRequestBuilderMethodStep disablingProxyCache() {
		_setHeader("Cache-Control","no-cache,max-age=0");
		_setHeader("Pragma","no-cache");
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the contentType
	 * @param contentType the contentType
	 */
	public HttpClientRequestBuilderMethodStep settingContentTypeTo(final MimeType contentType) {
		if (contentType == null) return this;
		return this.settingContentTypeTo(contentType.asString());
	}
	/**
	 * Sets the contentType
	 * @param contentType the contentType
	 */
	public HttpClientRequestBuilderMethodStep settingContentTypeTo(final String contentType) {
		if (Strings.isNullOrEmpty(contentType)) return this;
		_setHeader("Content-Type",contentType);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODs
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the GET Http method
	 * @return
	 */
	public HttpClientRequestBuilderGETMethodStep GET() {
		return new HttpClientRequestBuilderGETMethodStep(_targetUrl,
														 _targetServerCharset,
														 _headers,_cookies);
	}
	/**
	 * Sets the POST Http method
	 * @return
	 */
	public HttpClientRequestBuilderHEADMethodStep HEAD() {
		return new HttpClientRequestBuilderHEADMethodStep(_targetUrl,
														  _targetServerCharset,
														  _headers,_cookies);
	}
	/**
	 * Sets the POST Http method
	 * @return
	 */
	public HttpClientRequestBuilderDELETEMethodStep DELETE() {
		return new HttpClientRequestBuilderDELETEMethodStep(_targetUrl,
															_targetServerCharset,
														    _headers,_cookies);
	}
	/**
	 * Sets the POST Http method
	 * @return
	 */
	public HttpClientRequestBuilderPOSTMethodStep POST() {
		return new HttpClientRequestBuilderPOSTMethodStep(_targetUrl,
														  _targetServerCharset,
														  _headers,_cookies);
	}
	/**
	 * Sets the POST Form Http method
	 * @return
	 */
	public HttpClientRequestBuilderPOSTFormURLEncodedMethodStep POSTForm() {
		return new HttpClientRequestBuilderPOSTFormURLEncodedMethodStep(_targetUrl,
																		_targetServerCharset,
																		_headers,_cookies);
	}
	/**
	 * Sets the POST MultiPart Http method
	 * @return
	 */
	public HttpClientRequestBuilderPOSTMultiPartMethodStep POSTMultiPart() {
		return new HttpClientRequestBuilderPOSTMultiPartMethodStep(_targetUrl,
																	_targetServerCharset,
																    _headers,_cookies);
	}
	/**
	 * Sets the POST Form Http method
	 * @return
	 */
	public HttpClientRequestBuilderPOSTFileMethodStep POSTFile() {
		return new HttpClientRequestBuilderPOSTFileMethodStep(_targetUrl,
															   _targetServerCharset,
															   _headers,_cookies);
	}
	/**
	 * Sets the PUT Http method
	 * @return
	 */
	public HttpClientRequestBuilderPUTMethodStep PUT() {
		return new HttpClientRequestBuilderPUTMethodStep(_targetUrl,
														 _targetServerCharset,
														 _headers,_cookies);
	}
	/**
	 * Sets the PUT Form Http method
	 * @return
	 */
	public HttpClientBuilderPUTFormURLEncodedMethodStep PUTForm() {
		return new HttpClientBuilderPUTFormURLEncodedMethodStep(_targetUrl,
																_targetServerCharset,
																_headers,_cookies);
	}
	/**
	 * Sets the PUT Multipart Http method
	 * @return
	 */
	public HttpClientRequestBuilderPUTMultiPartMethodStep PUTMultiPart() {
		return new HttpClientRequestBuilderPUTMultiPartMethodStep(_targetUrl,
																  _targetServerCharset,
																  _headers,_cookies);
	}
}
