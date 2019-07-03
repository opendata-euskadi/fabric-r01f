package r01f.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.httpclient.HttpClient.RequestMethod;
import r01f.httpclient.HttpClientRequestBuilderConnectionRetrieveStepBase.HttpClientRequestConnectionProvider;
import r01f.httpclient.HttpClientRequestBuilderConnectionRetrieveSteps.HttpClientRequestBuilderConnectionRetrieveForHttpUrlConnectionStep;
import r01f.httpclient.HttpClientRequestBuilderConnectionRetrieveSteps.HttpClientRequestBuilderConnectionRetrieveForInputStreamStep;
import r01f.httpclient.HttpClientRequestBuilderConnectionRetrieveSteps.HttpClientRequestBuilderConnectionRetrieveForLinesStep;
import r01f.httpclient.HttpClientRequestBuilderConnectionRetrieveSteps.HttpClientRequestBuilderConnectionRetrieveForResponseStep;
import r01f.httpclient.HttpClientRequestBuilderConnectionRetrieveSteps.HttpClientRequestBuilderConnectionRetrieveForStringStep;
import r01f.mime.MimeType;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

public abstract class HttpClientRequestBuilderForMethodBase<SELF_TYPE extends HttpClientRequestBuilderForMethodBase<SELF_TYPE>>
       		  extends HttpClientRequestBuilderBase<HttpClientRequestBuilderForMethodBase<SELF_TYPE>> {
///////////////////////////////////////////////////////////////////////////////
// FIELDS
///////////////////////////////////////////////////////////////////////////////
	protected final Charset _targetServerCharset;	// destination server charset
	
	protected final RequestMethod _method;			// Request Method

	protected final Map<String,String> _headers;	// request headers
	protected final Map<String,String> _cookies;	// Cookies

	
	// Payload (usually only for POST method-calls BUT, either GET & DELETE could
	// support payloads
	protected HttpRequestPayload _payload;
	
	protected long _conxTimeOut;				// timeout to get a connection with server
	
	// proxy settings When is used this ¿?
	//private HttpClientProxySettings _proxySettings;

	// AuthDigest variables
	protected UserCode _authUserCode;
	protected Password _authPassword;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderForMethodBase(final RequestMethod newMethod,
										  final Url targetUrl,
										  final Charset targetServerCharset,
						  				  final Map<String,String> newRequestHeaders,final Map<String,String> newRequestCookies) {
		super(targetUrl);
		_targetServerCharset = targetServerCharset;
		_method = newMethod;
		_headers = CollectionUtils.hasData(newRequestHeaders) ? newRequestHeaders : Maps.<String,String>newHashMap();
		_cookies = newRequestCookies;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PAYLOAD API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a POSTed params
	 * @param params the params
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE withPayload(final HttpRequestPayload payload) {
		if (payload == null) throw new IllegalArgumentException("The payload for a POST request cannot be null");
		_payload = payload;
		return (SELF_TYPE)this;
	}
	/**
	 * Sets empty POSTed params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SELF_TYPE withoutPayload(final MimeType mimeType) {
		_payload = HttpRequestPayload.wrap("")
									 .mimeType(mimeType);
		return (SELF_TYPE)this;
	}
	public HttpRequestPayloadWrapper getPayloadWrapper() {
		return new HttpRequestPayloadWrapper(_payload);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the server response in an object that contains the stream in an InputStream
	 * and the server response code
	 */
	public HttpClientRequestBuilderConnectionRetrieveForResponseStep getResponse() throws IOException {
		HttpClientRequestConnectionProvider<HttpResponse> responseProvider = new HttpClientRequestConnectionProvider<HttpResponse>(_targetUrl,
																																   _targetServerCharset,
																																   _method,
																																   _headers,_cookies,
																																   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
															@Override
															public HttpResponse provideResponse(final HttpClientProxySettings proxySettings,
																							    final long conxTimeOut,
																							    final HttpTargetServerAuth auth) throws IOException {
																return _getResponse(proxySettings,
																					conxTimeOut,
																					auth,
																					false);		// do not use gae
															}
												  };
		return new HttpClientRequestBuilderConnectionRetrieveForResponseStep(responseProvider);
	}
	/**
	 * Gets the server response in an object that contains the stream in an InputStream
	 * and the server response code
	 * @param useGAEHttpFetch true if Google App Engine HTTPFetch is to be used
	 */
	public HttpClientRequestBuilderConnectionRetrieveForResponseStep getResponse(final boolean useGAEHttpFetch) throws IOException {
		HttpClientRequestConnectionProvider<HttpResponse> responseProvider = new HttpClientRequestConnectionProvider<HttpResponse>(_targetUrl,
																																   _targetServerCharset,
																																   _method,
																																   _headers,_cookies,
																																   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																						@Override
																						public HttpResponse provideResponse(final HttpClientProxySettings proxySettings,
																														    final long conxTimeOut,
																														    final HttpTargetServerAuth auth) throws IOException {
																							return _getResponse(proxySettings,
																												conxTimeOut,
																												auth,
																												useGAEHttpFetch);
																						}
																			  };
		return new HttpClientRequestBuilderConnectionRetrieveForResponseStep(responseProvider);												  
	}
	/**
	 * Gets a connection with server
	 */
	public HttpClientRequestBuilderConnectionRetrieveForHttpUrlConnectionStep getConnection() throws IOException {
		HttpClientRequestConnectionProvider<HttpURLConnection> responseProvider = new HttpClientRequestConnectionProvider<HttpURLConnection>(_targetUrl,
																																   			 _targetServerCharset,
																																   			 _method,
																																   			 _headers,_cookies,
																																   			 HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																							@Override
																							public HttpURLConnection provideResponse(final HttpClientProxySettings proxySettings,
																														    		 final long conxTimeOut,
																														    		 final HttpTargetServerAuth auth) throws IOException {
																								return _getConnection(proxySettings,
																													  conxTimeOut,
																													  auth,
																													  false);		// do not use gae
																							}
																				  };
		return new HttpClientRequestBuilderConnectionRetrieveForHttpUrlConnectionStep(responseProvider);	
	}
	/**
	 * Gets a server connection
	 * @param useGAEHttpFetch true si hay que utilizar Google App Engine HTTPFetch
	 */
	public HttpClientRequestBuilderConnectionRetrieveForHttpUrlConnectionStep getConnection(final boolean useGAEHttpFetch) throws IOException {
		HttpClientRequestConnectionProvider<HttpURLConnection> responseProvider = new HttpClientRequestConnectionProvider<HttpURLConnection>(_targetUrl,
																																   			 _targetServerCharset,
																																   			 _method,
																																   			 _headers,_cookies,
																																   			 HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																							@Override
																							public HttpURLConnection provideResponse(final HttpClientProxySettings proxySettings,
																															    	 final long conxTimeOut,
																															    	 final HttpTargetServerAuth auth) throws IOException {
																								return _getConnection(proxySettings,
																												   	  conxTimeOut,
																												   	  auth,
																												   	  useGAEHttpFetch);
																							}
																				  };
		return new HttpClientRequestBuilderConnectionRetrieveForHttpUrlConnectionStep(responseProvider);
	}
	/**
	 * Load the server response stream as a String
	 * @return a String containing the server response
	 * @throws IOException if a connection could not be retrieved
	 */
	public HttpClientRequestBuilderConnectionRetrieveForStringStep loadAsString() throws IOException {
		HttpClientRequestConnectionProvider<String> responseProvider = new HttpClientRequestConnectionProvider<String>(_targetUrl,
																										   			   _targetServerCharset,
																										   			   _method,
																										   			   _headers,_cookies,
																										   			   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																				@Override
																				public String provideResponse(final HttpClientProxySettings proxySettings,
																											  final long conxTimeOut,
																											  final HttpTargetServerAuth auth) throws IOException {
																					return _loadAsString(proxySettings,
																										 conxTimeOut,
																										 auth,
																										 false,
																									     Charset.defaultCharset());
																				}
																	  };
		return new HttpClientRequestBuilderConnectionRetrieveForStringStep(responseProvider);	
	}
	/**
	 * Load the server response stream as a String
	 * @return a String containing the server response
	 * @param serverSentDataCharset server sent data charset
	 * @throws IOException if a connection could not be retrieved
	 */
	public HttpClientRequestBuilderConnectionRetrieveForStringStep loadAsString(final Charset serverSentDataCharset) throws IOException {
		HttpClientRequestConnectionProvider<String> responseProvider = new HttpClientRequestConnectionProvider<String>(_targetUrl,
																										   			   _targetServerCharset,
																										   			   _method,
																										   			   _headers,_cookies,
																										   			   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																				@Override
																				public String provideResponse(final HttpClientProxySettings proxySettings,
																											  final long conxTimeOut,
																											  final HttpTargetServerAuth auth) throws IOException {
																					return _loadAsString(proxySettings,
																										 conxTimeOut,
																										 auth,
																										 false,
																										 serverSentDataCharset);
																				}
																	  };
		return new HttpClientRequestBuilderConnectionRetrieveForStringStep(responseProvider);
	}

	/**
	 * Load the server response stream as a String
	 * @param useGAEUrlFetch true if Google AppEngine HTTPFecth service is to be used
	 * @return a String containing the server response
	 * @throws IOException if a connection could not be retrieved
	 */
	public HttpClientRequestBuilderConnectionRetrieveForStringStep loadAsString(final boolean useGAEUrlFetch) throws IOException {
		HttpClientRequestConnectionProvider<String> responseProvider = new HttpClientRequestConnectionProvider<String>(_targetUrl,
																										   			   _targetServerCharset,
																										   			   _method,
																										   			   _headers,_cookies,
																										   			   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																				@Override
																				public String provideResponse(final HttpClientProxySettings proxySettings,
																											  final long conxTimeOut,
																											  final HttpTargetServerAuth auth) throws IOException {
																					return _loadAsString(proxySettings,
																										 conxTimeOut,
																										 auth,
																										 useGAEUrlFetch,
																										 Charset.defaultCharset());
																				}
																	  };
		return new HttpClientRequestBuilderConnectionRetrieveForStringStep(responseProvider);
	}
	/**
	 * Load the server response stream as a String
	 * @param useGAEUrlFetch true if Google AppEngine HTTPFecth service is to be used
	 * @param serverSentDataCharset server sent data charset
	 * @return a String containing the server response
	 * @throws IOException if a connection could not be retrieved
	 */
	public HttpClientRequestBuilderConnectionRetrieveForStringStep loadAsString(final boolean useGAEUrlFetch,
																				final Charset serverSentDataCharset) throws IOException {
		HttpClientRequestConnectionProvider<String> responseProvider = new HttpClientRequestConnectionProvider<String>(_targetUrl,
																										   			   _targetServerCharset,
																										   			   _method,
																										   			   _headers,_cookies,
																										   			   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																				@Override
																				public String provideResponse(final HttpClientProxySettings proxySettings,
																											  final long conxTimeOut,
																											  final HttpTargetServerAuth auth) throws IOException {
																					return _loadAsString(proxySettings,
																										 conxTimeOut,
																										 auth,
																										 useGAEUrlFetch,
																										 serverSentDataCharset);
																				}
																	  };
		return new HttpClientRequestBuilderConnectionRetrieveForStringStep(responseProvider);
	}
	/**
	 * Load the server response as a {@link Collection} of strings
	 * @return
	 * @throws IOException
	 */
	public HttpClientRequestBuilderConnectionRetrieveForLinesStep readLines() throws IOException {
		HttpClientRequestConnectionProvider<Collection<String>> responseProvider = new HttpClientRequestConnectionProvider<Collection<String>>(_targetUrl,
																										   			   						   _targetServerCharset,
																										   			   						   _method,
																										   			   						   _headers,_cookies,
																										   			   						   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																							@Override
																							public Collection<String> provideResponse(final HttpClientProxySettings proxySettings,
																															    	  final long conxTimeOut,
																															    	  final HttpTargetServerAuth auth) throws IOException {
																									return _readLines(proxySettings,
																													  conxTimeOut,
																													  auth,
																													  false,		// do not use gae
																													  Charset.defaultCharset());
																							}
																				  	};
		return new HttpClientRequestBuilderConnectionRetrieveForLinesStep(responseProvider);
	}
	/**
	 * Load the server response as a {@link Collection} of strings
	 * @param serverSentDataCharset server sent data charset
	 * @return
	 * @throws IOException
	 */
	public HttpClientRequestBuilderConnectionRetrieveForLinesStep readLines(final Charset serverSentDataCharset) throws IOException {
		HttpClientRequestConnectionProvider<Collection<String>> responseProvider = new HttpClientRequestConnectionProvider<Collection<String>>(_targetUrl,
																										   			   						   _targetServerCharset,
																										   			   						   _method,
																										   			   						   _headers,_cookies,
																										   			   						   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																							@Override
																							public Collection<String> provideResponse(final HttpClientProxySettings proxySettings,
																														    		  final long conxTimeOut,
																														    		  final HttpTargetServerAuth auth) throws IOException {
																								return _readLines(proxySettings,
																										 		  conxTimeOut,
																										 		  auth,
																										 		  false,	// do not use gae
																												  serverSentDataCharset);
																							}
																			  		};
		return new HttpClientRequestBuilderConnectionRetrieveForLinesStep(responseProvider);
	}
	/**
	 * Load the server response as a {@link Collection} of strings
	 * @param useGAEUrlFetch true if Google AppEngine HTTPFecth service is to be used
	 * @return
	 * @throws IOException
	 */
	public HttpClientRequestBuilderConnectionRetrieveForLinesStep readLines(final boolean useGAEUrlFetch) throws IOException {
		HttpClientRequestConnectionProvider<Collection<String>> responseProvider = new HttpClientRequestConnectionProvider<Collection<String>>(_targetUrl,
																										   			   						   _targetServerCharset,
																										   			   						   _method,
																										   			   						   _headers,_cookies,
																										   			   						   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																							@Override
																							public Collection<String> provideResponse(final HttpClientProxySettings proxySettings,
																														    		  final long conxTimeOut,
																														    		  final HttpTargetServerAuth auth) throws IOException {
																								return _readLines(proxySettings,
																										 		  conxTimeOut,
																										 		  auth,
																										 		  useGAEUrlFetch,		
																												  Charset.defaultCharset());
																							}
																			  		};
		return new HttpClientRequestBuilderConnectionRetrieveForLinesStep(responseProvider);
	}
	/**
	 * Load the server response as a {@link Collection} of strings
	 * @param useGAEUrlFetch true if Google AppEngine HTTPFecth service is to be used
	 * @param serverSentDataCharset server sent data charset
	 * @return
	 * @throws IOException
	 */
	public HttpClientRequestBuilderConnectionRetrieveForLinesStep readLines(final boolean useGAEUrlFetch,
																			final Charset serverSentDataCharset) throws IOException {
		HttpClientRequestConnectionProvider<Collection<String>> responseProvider = new HttpClientRequestConnectionProvider<Collection<String>>(_targetUrl,
																										   			   						   _targetServerCharset,
																										   			   						   _method,
																										   			   						   _headers,_cookies,
																										   			   						   HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																							@Override
																							public Collection<String> provideResponse(final HttpClientProxySettings proxySettings,
																														    		  final long conxTimeOut,
																														    		  final HttpTargetServerAuth auth) throws IOException {
																								return _readLines(proxySettings,
																										 		  conxTimeOut,
																										 		  auth,
																										 		  useGAEUrlFetch,
																										 		  serverSentDataCharset);
																							}
																			  		};
		return new HttpClientRequestBuilderConnectionRetrieveForLinesStep(responseProvider);
	}
	/**
	 * Load the server response stream as a {@link InputStream}
	 * @return an {@link InputStream} containing the server response
	 * @throws IOException if a connection could not be retrieved
	 */
	public HttpClientRequestBuilderConnectionRetrieveForInputStreamStep loadAsStream() throws IOException {
		HttpClientRequestConnectionProvider<InputStream> responseProvider = new HttpClientRequestConnectionProvider<InputStream>(_targetUrl,
																						   			   						     _targetServerCharset,
																						   			   						     _method,
																						   			   						     _headers,_cookies,
																						   			   						     HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																					@Override
																					public InputStream provideResponse(final HttpClientProxySettings proxySettings,
																												       final long conxTimeOut,
																												       final HttpTargetServerAuth auth) throws IOException {
																						return _loadAsStream(proxySettings,
																										 	 conxTimeOut,
																										 	 auth,
																										 	 false);		// do not use gae
																					}
																	  		};
		return new HttpClientRequestBuilderConnectionRetrieveForInputStreamStep(responseProvider);
	}
	/**
	 * Load the server response stream as a {@link InputStream}
	 * @param useGAEHttpFetch true si hay que utilizar Google App Engine HTTPFetch
	 * @return an {@link InputStream} containing the server response
	 * @throws IOException if a connection could not be retrieved
	 */
	public HttpClientRequestBuilderConnectionRetrieveForInputStreamStep loadAsStream(final boolean userGAEUrlFetch) throws IOException {
		HttpClientRequestConnectionProvider<InputStream> responseProvider = new HttpClientRequestConnectionProvider<InputStream>(_targetUrl,
																						   			   						     _targetServerCharset,
																						   			   						     _method,
																						   			   						     _headers,_cookies,
																						   			   						     HttpClientRequestBuilderForMethodBase.this.getPayloadWrapper()) {
																					@Override
																					public InputStream provideResponse(final HttpClientProxySettings proxySettings,
																												       final long conxTimeOut,
																												       final HttpTargetServerAuth auth) throws IOException {
																						return _loadAsStream(proxySettings,
																										 	 conxTimeOut,
																										 	 auth,
																										 	 userGAEUrlFetch);
																					}
																	  		};
		return new HttpClientRequestBuilderConnectionRetrieveForInputStreamStep(responseProvider);
	}

}
