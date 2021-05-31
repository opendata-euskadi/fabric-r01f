package r01f.servlet.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import lombok.Cleanup;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.io.util.StringPersistenceUtils;
import r01f.types.url.Host;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.types.url.UrlProtocol.StandardUrlProtocol;
import r01f.types.url.UrlQueryString;
import r01f.types.url.Urls;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;



/**
 * A simple proxy for mock / testing purposes
 */
@Slf4j
@Accessors(prefix="_")
public class HttpProxyServletDelegate {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int FOUR_KB = 4196;
	
	private static final String LOCATION_HEADER = "Location";					// Key for redirect location header.
	private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";	// Key for content length header.
	private static final String HOST_HEADER_NAME = "Host";						// Key for host header
	
	public static final String GWT_COMPILEDCODE_PROXIEDWAR_RELPATH_HEADER = "X-gwtCodeRelPath";	// Key for proxy servlet informationKey for proxy servlet information
	/**
	 * The directory to use to temporarily store uploaded files
	 */
	private static final File FILE_UPLOAD_TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final HttpProxyServletConfig _config;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpProxyServletDelegate(final HttpProxyServletConfig config) {
		_config = config;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Performs an HTTP GET request
	 * @param originalRequest The {@link HttpServletRequest} object passed
	 *						  in by the servlet engine representing the
	 *						  client request to be proxied
	 * @param responseToClient The {@link HttpServletResponse} object by which
	 *						   we can send a proxied response to the client
	 */
	public void proxyGET(final HttpServletRequest originalRequest,
					  	 final HttpServletResponse responseToClient) throws IOException,
					  													 	ServletException {
		// [0] Get the endpoint url 
		HttpProxyEndPoint endPoint = _chooseEndPoint(originalRequest);
		
		// [1] Create a GET request
		//	   beware that R01F Url object when serialized to string does NOT include the final '/' char if present
		UrlPath urlPath = _getTargetUrlPath(_config,
											originalRequest);
		UrlQueryString urlQueryString  = UrlQueryString.fromParamsString(originalRequest.getQueryString());
		Url destinationUrl = Url.from(urlPath,urlQueryString);
		
		String theDestinationUrlStr = originalRequest.getRequestURL().toString().endsWith("/")
											? destinationUrl.asString() + "/"
											: destinationUrl.asString();
		log.warn("PROXY GET: requested url={} to url={}{}",
				 originalRequest.getRequestURL(),
				 endPoint.getUrl(),theDestinationUrlStr);

		HttpGet getRequestToBeProxied = new HttpGet(theDestinationUrlStr);

		// [2] Transfer the original request headers/cookies to the proxied request
		_transferRequestHeaders(originalRequest,
								endPoint.getUrl(),
								getRequestToBeProxied);
		_transferRequestCookies(originalRequest,
								endPoint.getUrl(),
								getRequestToBeProxied);

		// [3] Execute the proxy request
		_executeProxyRequest(originalRequest,responseToClient,
							 endPoint,
							 _config.isFollowRedirects(),
							 getRequestToBeProxied);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	POST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Performs an HTTP POST request
	 * @param originalRequest The {@link HttpServletRequest} object passed in by the servlet engine representing the
	 *					 	  client request to be proxied
	 * @param responseToClient The {@link HttpServletResponse} object by which we can send a proxied response to the client
	 */
	public void proxyPOST(final HttpServletRequest originalRequest,
						  final HttpServletResponse responseToClient) throws IOException,
					   													  	 ServletException {
		// [0] Get the endpoint url 
		HttpProxyEndPoint endPoint = _chooseEndPoint(originalRequest);
		
		// [1] Create the POST request
		UrlPath urlPath = _getTargetUrlPath(_config,
											originalRequest);
		UrlQueryString urlQueryString  = UrlQueryString.fromParamsString(originalRequest.getQueryString());
		Url destinationUrl = Url.from(urlPath,urlQueryString);
		
		String theDestinationUrlStr = originalRequest.getRequestURL().toString().endsWith("/")
											? destinationUrl.asString() + "/"
											: destinationUrl.asString();
		
		ContentType contentType = Strings.isNOTNullOrEmpty(originalRequest.getContentType())
												// beware that content type might be like [application/x-www-form-urlencoded; charset=UTF-8]
												// ... but the content-type is just the first part
												? ContentType.create(StringSplitter.using(Splitter.on(';'))
														   						   .at(originalRequest.getContentType())
														   						   .group(0))
												: null;
		log.warn("PROXY POST: requested url={} - Content-Type: {} - to url={}",
				  originalRequest.getRequestURL(),contentType,
				  theDestinationUrlStr);

		HttpPost postRequestToBeProxied = new HttpPost(theDestinationUrlStr);

		// [2] Transfer the original request headers/cookies to the proxied request
		_transferRequestHeaders(originalRequest,
								endPoint.getUrl(),
								postRequestToBeProxied);
		_transferRequestCookies(originalRequest,
								endPoint.getUrl(),
								postRequestToBeProxied);

		// [3] Transfer the data depending on the post way:
		//		- mulitpart (file upload) POST data to the proxied request
		//		- form-url encoded
		//		- raw post
		if (ServletFileUpload.isMultipartContent(originalRequest)) {
			_transferMultipartPost(originalRequest,
								   endPoint.getUrl(),
								   postRequestToBeProxied,
								   _config.getMaxFileUploadSize());
		} else if ((contentType == null)
				|| (originalRequest.getContentType() != null && originalRequest.getContentType().contains("application/x-www-form-urlencoded"))) {
			_transferFormUrlEncodedPost(originalRequest,
										endPoint.getUrl(),
										postRequestToBeProxied);
		} else {
			_transferContentPost(originalRequest,
								 endPoint.getUrl(),
								 postRequestToBeProxied);
		}

		// [4] Execute the proxy request
		_executeProxyRequest(originalRequest,responseToClient,
							 endPoint,
							 _config.isFollowRedirects(),
							 postRequestToBeProxied);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Executes the {@link HttpMethod} passed in and sends the proxy response
	 * back to the client via the given {@link HttpServletResponse}
	 * @param originalReq The origingal servlet request
	 * @param responseToClient An object by which we can send the proxied response back to the client
	 * @param endPoint the target endpoint url
	 * @param followRedirects
	 * @param requestToBeProxied An object representing the proxy request to be made
	 * @throws IOException	  Can be thrown by the {@link HttpClient}.executeMethod
	 * @throws ServletException Can be thrown to indicate that another error has occurred
	 */
	@SuppressWarnings("resource")
	private void _executeProxyRequest(final HttpServletRequest originalReq,final HttpServletResponse responseToClient,
									  final HttpProxyEndPoint endPoint,
									  final boolean followRedirects,
									  final HttpRequestBase requestToBeProxied) throws IOException,
									  											   	   ServletException {
		// [1] - Get the [end point] respones
		HttpResponse endPointResponse = _getEndPointResponse(endPoint,
															 requestToBeProxied);

		// [2]  Handle redirects (301) or client cache usage advices (304)
		if (followRedirects
		 && endPointResponse.getStatusLine().getStatusCode() >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
		 && endPointResponse.getStatusLine().getStatusCode() < HttpServletResponse.SC_NOT_MODIFIED 		/* 304 */) {

			boolean hasToContinue = _handleRedirection(originalReq,responseToClient,
									   				   requestToBeProxied,
									   				   endPoint.getUrl(),
									   				   endPointResponse);
			if (!hasToContinue) return;	// there is a redirection... do not continue

		}
		else if (!followRedirects
			  && endPointResponse.getStatusLine().getStatusCode() == HttpServletResponse.SC_MOVED_TEMPORARILY) {

			String loc = requestToBeProxied.getFirstHeader(LOCATION_HEADER).getValue();
			responseToClient.setStatus(HttpServletResponse.SC_OK);
			//endPointResponse.setStatusCode(HttpServletResponse.SC_OK);
			responseToClient.setHeader(LOCATION_HEADER,loc);

			return;
		}
		else if (endPointResponse.getStatusLine().getStatusCode() == HttpServletResponse.SC_NOT_MODIFIED) {
			// 304 needs special handling.  See: http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
			// We get a 304 whenever passed an 'If-Modified-Since' header and the data on disk has not changed;
			// server responds with a 304 saying I'm not going to send the body because the file has not changed.
			responseToClient.setIntHeader(CONTENT_LENGTH_HEADER_NAME, 0);
			responseToClient.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

			return;
		}

		// [3] - Pass the response code back to the client
		// [3.1] transfer the status code sent by the proxied server to the response to client
		responseToClient.setStatus(endPointResponse.getStatusLine().getStatusCode());

		// [3.2] Copy the headers of the proxied server to the client response
		_transferResponseHeaders(endPointResponse,
								 responseToClient);

		// [3.3] transfer the content sent by the proxied endpoint to the client
		//		 (the response from the proxied endpoint could be ziped... unzip befor transfer it to the response to client)
		InputStream endPointResponseIS = endPointResponse.getEntity()
														 .getContent();

		boolean endpointResponseIsGzipped = _isBodyParameterGzipped(endPointResponse);
		if (endpointResponseIsGzipped) {
			log.debug("GZipped: true");
			int length = 0;

			final byte[] bytes = _ungzip(endPointResponseIS);
			length = bytes.length;
			endPointResponseIS = new ByteArrayInputStream(bytes);

			responseToClient.setContentLength(length);
		}

		_copy(endPointResponseIS,
			  responseToClient.getOutputStream());

		log.debug("Received status code: {} - Response: {}",endPointResponse,
													  		endPointResponseIS);
	}
	@SuppressWarnings( {"static-method","resource"} )
	protected HttpResponse _getEndPointResponse(final HttpProxyEndPoint choosenEndPoint,
												final HttpRequest requestToBeProxied) throws IOException {
		// [1] - Create a default HttpClient
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();	// HttpParams httpClientParams = new BasicHttpParams();
		clientBuilder.disableRedirectHandling();						// httpClientParams.setParameter(ClientPNames.HANDLE_REDIRECTS,false);
																		// httpClientParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,false);
																		// HttpClientParams.setRedirecting(httpClientParams,false);
		HttpClient httpClient = clientBuilder.build(); 					// HttpClient httpClient = new SystemDefaultHttpClient(httpClientParams);

		// [2] - Execute the request
		HttpHost host = HttpHost.create(choosenEndPoint.getUrl().asString());
		HttpResponse endPointResponse = httpClient.execute(host,
														   requestToBeProxied);
		return endPointResponse;
	}
	private static final SecureRandom RANDOM = new SecureRandom(UUID.randomUUID().toString().getBytes());
	protected HttpProxyEndPoint _chooseEndPoint(final HttpServletRequest originalRequest) {
		Url url = null;
		if (_config.getEndPoints().size() == 1) {
			url = CollectionUtils.firstOf(_config.getEndPoints());
		} else {
			int index = RANDOM.nextInt(_config.getEndPoints().size());
			url = Iterables.get(_config.getEndPoints(),
								index);
		}
		return new HttpProxyEndPointUrlImpl(url);
	}
	private static UrlPath _getTargetUrlPath(final HttpProxyServletConfig config,
									  		 final HttpServletRequest originalRequest) {
		// get the servlet context
		String servletContext = originalRequest.getServletContext()
											   .getContextPath();
		UrlPath servletContextUrlPath = UrlPath.from(Strings.isNOTNullOrEmpty(servletContext) ? UrlPath.from(servletContext)
																					   		  : UrlPath.from("/"));	// default path
		
		// simply use whatever servlet path that was part of the request as opposed to
		// getting a preset/configurable proxy path
		String requestedServletPath = originalRequest.getServletPath();	// Returns the part of this request's URL that calls the servlet.
																		// This path starts with a "/" character and includes either the servlet name
																		// or a path to the servlet, but does not include any extra path information
																		// or a query string.
																		// This method will return an empty string ("") if the servlet used to process
																		// this request was matched using the "/*" pattern.

		String requestedServletPathInfo = originalRequest.getPathInfo();// Returns any extra path information associated with the URL the client sent
																		// when it made this request.
																		// The extra path information follows the servlet path but precedes the query string
																		// and will start with a "/" character.
																		// This method returns null if there was no extra path information.
		
		UrlPath requestedUrlPath = Strings.isNOTNullOrEmpty(requestedServletPathInfo)
												? UrlPath.preservingTrailingSlash()
														 .from(servletContextUrlPath)
														 .joinedWith(requestedServletPath,
																 	 requestedServletPathInfo)
												: UrlPath.preservingTrailingSlash()
														 .from(servletContextUrlPath)
														 .joinedWith(requestedServletPath);
		UrlPath targetUrlPath = null;
		
		// --- path trim 
		if (config.getPathTrim() == null) {
			// nothing to remove
			targetUrlPath = requestedUrlPath;
		}
		else if (requestedUrlPath.startsWith(config.getPathTrim())) {
			// remove the pathTrim part
			targetUrlPath = requestedUrlPath.urlPathAfter(config.getPathTrim());
			log.warn("path trim '{}' from url: resulting url > {}",
					 config.getPathTrim(),targetUrlPath);
		}
		else {
			// nothing to remove
			targetUrlPath = requestedUrlPath;
		}
		
		// --- path prepend
		if (config.getPathPrepend() != null) {
			targetUrlPath = config.getPathPrepend().joinedWith(targetUrlPath);
		}
		
		return targetUrlPath;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets up the given {@link PostMethod} to send the same form/url-encoded POST
	 * data as was sent in the given {@link HttpServletRequest}
	 * @param postRequestToBeProxied The {@link PostMethod} that we are
	 *							   configuring to send a standard POST request
	 * @param originalRequest	The {@link HttpServletRequest} that contains
	 *						   the POST data to be sent via the {@link PostMethod}
	 */
	@SuppressWarnings("unused")
	private static void _transferFormUrlEncodedPost(final HttpServletRequest originalRequest,
													final Url endPointUrl,
											 		final HttpPost postRequestToBeProxied) throws UnsupportedEncodingException {
		// Get the client POST data as a Map
		Map<String,String[]> postParams = originalRequest.getParameterMap();

		// Create a List to hold the NameValuePairs to be passed to the PostMethod
		List<NameValuePair> nameAndValuePairs = new ArrayList<NameValuePair>();
		for (String paramName : postParams.keySet()) {
			// Iterate the values for each parameter name
			String[] paramValues = postParams.get(paramName);
			for (String paramValue : paramValues) {
				NameValuePair nameValuePair = new BasicNameValuePair(paramName,paramValue);
				nameAndValuePairs.add(nameValuePair);
			}
		}
		// Set the proxy request POST data
		UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(nameAndValuePairs);
		postRequestToBeProxied.setEntity(paramEntity);
	}
	/**
	 * Sets up the given {@link PostMethod} to send the same content POST
	 * data (JSON, XML, etc.) as was sent in the given {@link HttpServletRequest}
	 * @param originalRequest	The {@link HttpServletRequest} that contains
	 *							   		the POST data to be sent via the {@link PostMethod}
  	 * @param endPointUrl
 	 * @param postRequestToBeProxied The {@link PostMethod} that we are
 	 *							   	 configuring to send a standard POST request
	 */
	private static void _transferContentPost(final HttpServletRequest originalRequest,
									  		 final Url endPointUrl,
									  		 final HttpPost postRequestToBeProxied) throws IOException {
		// [1] Read the original POST content
		log.debug("... reading {} POST",
				  originalRequest.getContentLength());
		String postContent = StringPersistenceUtils.load(originalRequest.getInputStream());

		// [2] Replace all the references to the original server with the proxied one
		ContentType contentType = ContentType.create(originalRequest.getContentType());
		if (contentType != null
		 && contentType.getMimeType() != null
		 && contentType.getMimeType().startsWith("text/x-gwt-rpc")) {
			String clientHost = originalRequest.getLocalName();
			if (clientHost.equals("127.0.0.1")) {
				clientHost = "localhost";
			}
			int clientPort = originalRequest.getLocalPort();
			String clientUrl = clientHost + ((clientPort != 80) ? ":" + clientPort : "");
			String serverUrl = Urls.serverAndPort(endPointUrl.getHost(),endPointUrl.getPort()) +
							   originalRequest.getServletPath();	
			//debug("Replacing client (" + clientUrl + ") with server (" + serverUrl + ")");
			postContent = postContent.replace(clientUrl,
											  serverUrl);
		}
		// [3] Hand the POST data to the proxied server
		log.debug("POST Content Type: {} - Content: {} ",contentType,
														 postContent);
		StringEntity entity = new StringEntity(postContent,
									  		   contentType);
		postRequestToBeProxied.setEntity(entity);
	}
	/**
	 * Sets up the given {@link PostMethod} to send the same multipart POST
	 * data as was sent in the given {@link HttpServletRequest}
	 * @param originalRequest The {@link HttpServletRequest} that contains
	 *						  the mutlipart POST data to be sent via the {@link PostMethod}
  	 * @param endPointUrl 
	 * @param postRequestToBeProxied The {@link PostMethod} that we are
	 *							   configuring to send a multipart POST request
	 */
	@SuppressWarnings("null")
	private static void _transferMultipartPost(final HttpServletRequest originalRequest,
											   final Url endPointUrl,
											   final HttpPost postRequestToBeProxied,
											   final int maxFileUploadSize) throws IOException,
																				   ServletException {
		// Get the contentType
		ContentType contentType = ContentType.create(originalRequest.getContentType(),
													 originalRequest.getCharacterEncoding());

		// Create a factory for disk-based file items
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		// Set factory constraints
		diskFileItemFactory.setSizeThreshold(maxFileUploadSize);
		diskFileItemFactory.setRepository(FILE_UPLOAD_TEMP_DIRECTORY);
		// Create a new file upload handler
		ServletFileUpload fileUploadServlet = new ServletFileUpload(diskFileItemFactory);

		// Parse the original request and hand it to the proxied endpoint
		List<FileItem> items = null;
		try {
			// Get the multipart items as a list (the FileUpload saves in a temp dir the file items)
			items = fileUploadServlet.parseRequest(originalRequest);

			// Process all parts
			Map<String,ContentBody> parts = Maps.newHashMap();
			for (FileItem item : items) {
				// If the current item is a form field, then create a string part
				// ... otherwise if the current item is a file item, create a filePart
				if (item.isFormField()) {
					StringBody stringPart = new StringBody(item.getString(),	 		// The field value
														   contentType);
					parts.put(item.getFieldName(),
							  stringPart);
				} else {
					@SuppressWarnings("resource")
					@Cleanup InputStream is = item.getInputStream();
					InputStreamBody isPart = new InputStreamBody(is,
																 contentType,
																 null);			// null filename
					parts.put(item.getFieldName(),
							  isPart);
				}
			}
			// Create the multi part and do the POST
			MultipartEntityBuilder multiPartEntityBuilder = MultipartEntityBuilder.create();
			if (CollectionUtils.hasData(parts)) {
				for (Map.Entry<String,ContentBody> partEntry : parts.entrySet()) {
					multiPartEntityBuilder.addPart(partEntry.getKey(),
												   partEntry.getValue());
				}
			}
			HttpEntity multiPartEntity = multiPartEntityBuilder.build();
			postRequestToBeProxied.setEntity(multiPartEntity);

			// The current content-type header (received from the client) IS of
			// type "multipart/form-data", but the content-type header also
			// contains the chunk boundary string of the chunks. Currently, this
			// header is using the boundary of the client request, since we
			// blindly copied all headers from the client request to the proxy
			// request. However, we are creating a new request with a new chunk
			// boundary string, so it is necessary that we re-set the
			// content-type string to reflect the new chunk boundary string
			postRequestToBeProxied.setHeader(multiPartEntity.getContentType());

		} catch (FileUploadException fileUploadException) {
			throw new ServletException(fileUploadException);
		} finally {
			// Temporal files cleanup
			if (CollectionUtils.hasData(items)) {
				for (FileItem item : items) item.delete();
			}
		}
	}
	/**
	 * Retrieves all of the headers from the servlet request and sets them on
	 * the proxy request
	 * @param originalRequest The request object representing the client's
	 *						  request to the servlet engine
 	 * @param endPointUrl endpoint url
	 * @param requestToBeProxied The request that we are about to send to the proxy host
	 */
	private static void _transferRequestHeaders(final HttpServletRequest originalRequest,
										 		final Url endPointUrl,
										 		final HttpRequestBase requestToBeProxied) {
		// Get an Enumeration of all of the header names sent by the client
		Enumeration<String> headerNames = originalRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			if (headerName.equalsIgnoreCase(CONTENT_LENGTH_HEADER_NAME)) {
				continue;
			}
			// As per the Java Servlet API 2.5 documentation:
			//  ﻿  Some headers, such as Accept-Language can be sent by clients
			//﻿  ﻿  as several headers each with a different value rather than
			//﻿  ﻿  sending the header as a comma separated list.
			// Thus, an Enumeration of the header values sent by the client is getted
			Enumeration<String> headerValues = originalRequest.getHeaders(headerName);
			while (headerValues.hasMoreElements()) {
				String headerValue = headerValues.nextElement();
				// In case the proxy host is running multiple virtual servers,
				// rewrite the Host header to ensure that the content from
				// the correct virtual server is retrieved
				if (headerName.equalsIgnoreCase(HOST_HEADER_NAME)) {
					headerValue = Urls.serverAndPort(endPointUrl.getHost(),endPointUrl.getPort());
				}
				Header header = new BasicHeader(headerName,headerValue);

				// Set the same header on the proxy request
				requestToBeProxied.setHeader(header);
			}
		}
	}
	/**
	 * Retrieves all of the cookies from the servlet request and sets them on the proxy request
	 * @param originalRequest The request object representing the client's request to the servlet engine
	 * @param endPointUrl
	 * @param requestToBeProxied The request that we are about to send to
	 *							 the proxy host
	 */
	private static void _transferRequestCookies(final HttpServletRequest originalRequest,
										 		final Url endPointUrl,
										 		final HttpRequestBase requestToBeProxied) {
		// Get an array of all of all the cookies sent by the client
		Cookie[] cookies = originalRequest.getCookies();
		if (cookies == null) {
			return;
		}
		String cookiesStr = "";
		for (Cookie cookie : cookies) {
			cookie.setDomain(endPointUrl.getHost().asString());
			cookie.setPath(originalRequest.getServletPath());
			cookiesStr = cookiesStr + " " + cookie.getName() + "=" + cookie.getValue() + "; Path=" + cookie.getPath() + ";";
		}
		requestToBeProxied.setHeader("Cookie", cookiesStr);
	}
	/**
	 * Transfers to the response to send to the client all the headers
	 * received in the proxied server's response
	 * @param endPointResponse	response received from the proxied server
	 * @param responseToClient 	response to send to the client
	 */
	private static void _transferResponseHeaders(final HttpResponse endPointResponse,
										  		 final HttpServletResponse responseToClient) {
		Header[] endPointResponseHeaders = endPointResponse.getAllHeaders();
		for (Header header : endPointResponseHeaders) {
			if ((header.getName().equals("Transfer-Encoding") && header.getValue().equals("chunked"))
				 ||
				(header.getName().equals("Content-Encoding") && header.getValue().equals("gzip"))  // don't copy gzip header
				 ||
				(header.getName().equals("WWW-Authenticate"))) { 	// don't copy WWW-Authenticate header so browser doesn't prompt on failed basic auth
				// proxy servlet does not support chunked encoding
			} else {
				responseToClient.setHeader(header.getName(),
										   header.getValue());
			}
		}
	}
	/**
	 * Handles the proxied server redirection responses
	 * The following code is adapted from org.tigris.noodle.filters.CheckForRedirect
	 * @param originalReq
	 * @param responseToClient
	 * @param endPointUrl
	 * @param requestToBeProxied
	 * @param endPointResponse
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	private static boolean _handleRedirection(final HttpServletRequest originalReq,final HttpServletResponse responseToClient,
									   		  final HttpRequestBase requestToBeProxied,
									   		  final Url endPointUrl,
									   		  final HttpResponse endPointResponse) throws IOException,
									   											   		  ServletException {
		String redirLocationStr = endPointResponse.getFirstHeader(LOCATION_HEADER) != null
										? endPointResponse.getFirstHeader(LOCATION_HEADER).getValue()
										: null;
		if (redirLocationStr == null) {
			log.warn("Received client-redir status code {} but no {} header was found in the response",
					 endPointResponse.getStatusLine().getStatusCode(),LOCATION_HEADER);
			return true;
		}
		Url redirLocationUrl = Url.from(redirLocationStr);
		
		if (redirLocationUrl.getHost() != null && endPointUrl.getHost().is(redirLocationUrl.getHost())				// same host
		 && redirLocationUrl.getPort() == endPointUrl.getPort()														// same port
		 && !redirLocationUrl.getUrlPath().startsWith(UrlPath.from(originalReq.getContextPath()))  			// does NOT start with the proxy context path
		 && UrlPath.from(UrlPath.from(originalReq.getRequestURI()).getPathElementsFrom(1))
		 		   .isNOT(redirLocationUrl.getUrlPath())) {													// NOT same path
			// Modify the redirect url to go to this proxy servlet rather than the proxied host
			Url originalUrl = Url.from(Host.of(originalReq.getServerName()),originalReq.getServerPort(),
									   UrlPath.preservingTrailingSlash()
										      .from(originalReq.getContextPath()));
			String strToReplace = endPointUrl.getProtocolOrDefault(StandardUrlProtocol.HTTP.toUrlProtocol()) + "://" + Urls.serverAndPort(endPointUrl.getHost(),endPointUrl.getPort());
			
			log.warn("...redir location: {} replace {} with {}",
					 redirLocationStr,strToReplace,originalUrl.asString());

			redirLocationStr = redirLocationStr.replace(strToReplace,
												  		originalUrl.asString());
		} else {
			log.warn("...redir to same url: original req={} requested redir={} > too many redirects",
					 UrlPath.from(originalReq.getRequestURI()).getPathElementsFrom(1));
			return true;
		}

		log.warn("Received client-redir status code {} (Location header={}) -follow redirects=true > redirect sent to client={}",
				 endPointResponse.getStatusLine().getStatusCode(),
				 endPointResponse.getFirstHeader(LOCATION_HEADER).getValue(),
				 redirLocationStr);
		if (redirLocationStr.contains("jsessionid")) {
			Cookie cookie = new Cookie("JSESSIONID",
									   redirLocationStr.substring(redirLocationStr.indexOf("jsessionid=") + 11));
			cookie.setPath("/");
			responseToClient.addCookie(cookie);
			//log.debug("redirecting: set jessionid (" + cookie.getValue() + ") cookie from URL");
		} else if (requestToBeProxied.getFirstHeader("Set-Cookie") != null) {
			Header header = requestToBeProxied.getFirstHeader("Set-Cookie");
			String[] cookieDetails = header.getValue().split(";");
			String[] nameValue = cookieDetails[0].split("=");

			Cookie cookie = new Cookie(nameValue[0], nameValue[1]);
			cookie.setPath("/");
			//log.debug("redirecting: setting cookie: " + cookie.getName() + ":" + cookie.getValue() + " on " + cookie.getPath());
			responseToClient.addCookie(cookie);
		}
		// Set the redir!!!
		responseToClient.sendRedirect(redirLocationStr);
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GZIP
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The response body will be assumed to be gzipped if the GZIP header has been set.
	 * @param responseHeaders of response headers
	 * @return true if the body is gzipped
	 */
	private static boolean _isBodyParameterGzipped(final HttpResponse response) {
		boolean outGzipped = false;
		Header[] responseHeaders = response.getAllHeaders();
		if (CollectionUtils.hasData(responseHeaders)) {
			for (Header header : responseHeaders) {
				if (header.getValue().equals("gzip")) {
					outGzipped = true;
					break;
				}
			}
		}
		return outGzipped;
	}
	/**
	 * A highly performant ungzip implementation. Do not refactor this without taking new timings.
	 * See ElementTest in ehcache for timings
	 * @param gzipped the gzipped content
	 * @return an ungzipped byte[]
	 * @throws java.io.IOException when something bad happens
	 */
	private static byte[] _ungzip(final InputStream gzipped) throws IOException {
		final GZIPInputStream inputStream = new GZIPInputStream(gzipped);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final byte[] buffer = new byte[FOUR_KB];
		int bytesRead = 0;
		while (bytesRead != -1) {
			bytesRead = inputStream.read(buffer,0,FOUR_KB);
			if (bytesRead != -1) {
				byteArrayOutputStream.write(buffer,0,bytesRead);
			}
		}
		byte[] ungzipped = byteArrayOutputStream.toByteArray();
		inputStream.close();
		byteArrayOutputStream.close();
		return ungzipped;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STREAM UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * Closes both streams when done.
	 *
	 * @param in  the stream to copy from
	 * @param out the stream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	private static long _copy(final InputStream in,
							  final OutputStream out) throws IOException {
		try {
			int byteCount = 0;
			byte[] buffer = new byte[FOUR_KB];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer,0,bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		} finally {
			_close(in);
			_close(out);
		}
	}
	/**
	 * Close the given stream if the stream is not null.
	 *
	 * @param s The stream
	 */
	private static void _close(final InputStream s) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception e) {
				log.error("Error closing stream: " + e.getMessage());
			}
		}
	}
	/**
	 * Close the given stream if the stream is not null.
	 *
	 * @param s The stream
	 */
	private static void _close(final OutputStream s) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception e) {
				log.error("Error closing stream: " + e.getMessage());
			}
		}
	}
}
