/**
 *
 */
package r01f.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import r01f.exceptions.Throwables;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

/**
 * Aux type to do REST client-calls.
 * 
 * HTTP defines a pre-defined set of actions or HTTP methods (verbs)
 * 		- A method is considered secure if it does NOT produce secondary effects (any server state update)
 * 		- A method is idempotent if it's repetated execution with the exact same params has the same efects as it was only executed once
 * 
 * Method     Secure Idempotend 	Semantics
 * ------	  ------ -----------    ---------
 *  GET 		Y 		Y 			resource read
 *  HEAD 		Y 		Y 			read resource headers
 *  PUT 		N 		Y 			update or create a resource
 *  DELETE 		N 		Y 			Delete a resource
 *  POST 		N 		N 			Any generic not-indempotent action
 *  OPTIONS 	Y 		Y 			Return the resouce available options
 *
 * Some notes:
 * 		- HEAD vs GET: both reads the resource but GET method returns both the headers and the resouce while HEAD only returns the headers
 * 		- POST is usually used to create a NEW resource, update an existing one or execute a generic not-indempotent action
 *		- OPTIONS is used to get the communications options of a resource such as what HTTP verbs are supported 
 *		  The OPTIONS response is NOT cacheable because the available verbs set can change with the resource status
 */
public class HttpRESTClient {
	/**
	 * Makes a GET call to an URL, read the state of web resource, returns web resource data and HTTP headers.
	 * @param url the url to GET
	 * @param headers the HTTP headers
	 * @return the server-returned result
	 */
	public static HttpResponse doGET(final Url url,
									 final Map<String, String> headers) {
		HttpResponse outResponse = null;
		try {
			outResponse = HttpClient.forUrl(url)	// url-encode los parametros! en otro caso NO funciona con jersey
									.disablingProxyCache()
									// .withConnectionTimeOut(20000)
									.withHeaders(headers)
									.GET()
										.getResponse()
											.notUsingProxy().withoutTimeOut().noAuth();
		} catch (IOException ioEx) {
			outResponse = new HttpResponse(500, new ByteArrayInputStream(Throwables.getStackTraceAsString(ioEx).getBytes()));
		}
		return outResponse;
	}
	/**
	 * Makes a HEAD call to an URL, read state of web resource, but only returns HTTP headers, not resource data.
	 * @param url the url to HEAD
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @return the server-returned result

	 */
	public static HttpResponse doHEAD(final Url url,
									  final Map<String,String> headers) {
		HttpResponse outResponse = null;
		try {
			outResponse = HttpClient.forUrl(url)	// url-encode los parametros! en otro caso NO funciona con jersey
									.disablingProxyCache()
									.withHeaders(headers)									
									.HEAD()
										.getResponse()
											.notUsingProxy().withoutTimeOut().noAuth();
		} catch (IOException ioEx) {
			outResponse = new HttpResponse(500,new ByteArrayInputStream(Throwables.getStackTraceAsString(ioEx).getBytes()));
		}
		return outResponse;
	}
	/**
	 * Makes a DELETE call to an URL, delete resource.
	 * @param url the url to DELETE
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @return the server-returned result
	 */
	public static HttpResponse doDELETE(final Url url,
										final Map<String,String> headers) {
		HttpResponse outResponse = null;
		try {
			outResponse = HttpClient.forUrl(url)	// url-encode los parametros! en otro caso NO funciona con jersey
									.disablingProxyCache()
									.withHeaders(headers)
									.DELETE()
										.getResponse()
											.notUsingProxy().withoutTimeOut().noAuth();
		} catch (IOException ioEx) {
			outResponse = new HttpResponse(500, new ByteArrayInputStream(Throwables.getStackTraceAsString(ioEx).getBytes()));
		}
		return outResponse;
	}
	/**
	 * Makes a PUT call to an URL, create or update web resource (idempotent).
	 * @param url the url to PUT
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @param postPayload the PUTed data
	 * @return the server-returned result
	 */
	public static HttpResponse doPUT(final Url url,
									 final Map<String,String> headers,
									 final InputStream postPayload) {
		HttpResponse outResponse = HttpRESTClient.doPUT(url,
														headers,
														postPayload,MimeTypes.APPLICATION_XML);
		return outResponse;
	}
	/**
	 * Makes a PUT call to an URL, create or update web resource (idempotent).
	 * @param url the url to PUT at
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @param postPayload the PUTed data
	 * @param mimeType the content-type of the posted data (if null application/xml is assumed)
	 * @return the server-returned result
	 */
	public static HttpResponse doPUT(final Url url,
									 final Map<String,String> headers,
									 final InputStream postPayload,
									 final MimeType mimeType) {
		HttpResponse outResponse = null;
		try {
			MimeType theMimeType = mimeType != null ? mimeType
													: MimeTypes.APPLICATION_XML;
			outResponse = HttpClient.forUrl(url)	// url-encode los parametros! en otro caso NO funciona con jersey
									.disablingProxyCache()
									.withHeaders(headers)
									.PUT().withPayload(HttpRequestPayload.wrap(postPayload)
																		 .mimeType(theMimeType))
									.getResponse()	
										.notUsingProxy().withoutTimeOut().noAuth();
		} catch (IOException ioEx) {
			outResponse = new HttpResponse(500, new ByteArrayInputStream(Throwables.getStackTraceAsString(ioEx).getBytes()));
		}
		return outResponse;
	}
	/**
	 * Makes a PUT call to an URL sending a FORM in the payload, create or update web resource (idempotent).
	 * @param url the url to PUT at
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @param formParams the PUTed form data
	 * @return the server-returned result
	 */
	public static HttpResponse doPUTForm(final Url url,
										 final Map<String,String> headers,
										 final Map<String,String> formParams) {
		HttpResponse outResponse = null;
		try {
			List<HttpRequestFormParameter> postFormParams = null;
			if (CollectionUtils.hasData(formParams)) {
				postFormParams = Lists.newArrayListWithExpectedSize(formParams.size());
				for (Map.Entry<String,String> me : formParams.entrySet()) {
					postFormParams.add(HttpRequestFormParameterForText.of(me.getValue())
															  .withName(me.getKey()));
				}
			}
			outResponse = HttpClient.forUrl(url)	// url-encode los parametros! en otro caso NO funciona con jersey
									.disablingProxyCache()
									.withHeaders(headers)
									.PUTForm().withPUTFormParameters(postFormParams)
									.getResponse()
										.notUsingProxy().withoutTimeOut().noAuth();
		} catch (IOException ioEx) {
			outResponse = new HttpResponse(500, new ByteArrayInputStream(Throwables.getStackTraceAsString(ioEx).getBytes()));
		}
		return outResponse;
	}
	/**
	 * Makes a POST call to an URL, create or update web resource (not idempotent action).
	 * @param url the url to POST at
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @param postPayload the POSTed data
	 * @param mimeType the content-type of the posted data (if null application/xml is assumed)
	 * @return the server-returned result
	 */
	public static HttpResponse doPOST(final Url url,
									  final Map<String,String> headers,
									  final InputStream postPayload,
									  final MimeType mimeType) {
		HttpResponse outResponse = null;
		try {
			MimeType theMimeType = mimeType != null ? mimeType
													: MimeTypes.APPLICATION_XML;
			outResponse = HttpClient.forUrl(url)	// url-encode los parametros! en otro caso NO funciona con jersey
									.disablingProxyCache()
									.withHeaders(headers)
									.POST()
										.withPayload(HttpRequestPayload.wrap(postPayload)
																	   .mimeType(theMimeType))
									.getResponse()
										.notUsingProxy().withoutTimeOut().noAuth();
		} catch (IOException ioEx) {
			outResponse = new HttpResponse(500, new ByteArrayInputStream(Throwables.getStackTraceAsString(ioEx).getBytes()));
		}
		return outResponse;
	}
	/**
	 * Makes a POST call to an URL, create or update web resource (not idempotent action).
	 * @param url the url to POST at
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @param postPayload the POSTed data
	 * @return the server-returned result
	 */
	public static HttpResponse doPOST(final Url url,
									  final Map<String,String> headers,
									  final InputStream postPayload) {
		HttpResponse outResponse = HttpRESTClient.doPOST(url,
														 headers,
														 postPayload,MimeTypes.APPLICATION_XML);
		return outResponse;
	}
	/**
	 * Makes a POST call to an URL sending a FORM in the payload, create or update web resource (not idempotent action).
	 * @param url the url to POST at
	 * @param urlParameters the parameters to encode at the url query string
	 * @param headers the HTTP headers
	 * @param formParams the POSTed form data
	 * @return the server-returned result
	 */
	public static HttpResponse doPOSTForm(final Url url,
										  final Map<String,String> headers,
										  final Map<String,String> formParams) {
		HttpResponse outResponse = null;
		try {
			List<HttpRequestFormParameter> postFormParams = null;
			if (CollectionUtils.hasData(formParams)) {
				postFormParams = Lists.newArrayListWithExpectedSize(formParams.size());
				for (Map.Entry<String,String> me : formParams.entrySet()) {
					postFormParams.add(HttpRequestFormParameterForText.of(me.getValue())
															  .withName(me.getKey()));
				}
			}
			outResponse = HttpClient.forUrl(url)	// url-encode los parametros! en otro caso NO funciona con jersey
									.disablingProxyCache()
									// .withConnectionTimeOut(20000)
									.withHeaders(headers)
									.POSTForm().withPOSTFormParameters(postFormParams)
									.getResponse()
										.notUsingProxy().withoutTimeOut().noAuth();
		} catch (IOException ioEx) {
			outResponse = new HttpResponse(500, new ByteArrayInputStream(Throwables.getStackTraceAsString(ioEx).getBytes()));
		}
		return outResponse;
	}
}
