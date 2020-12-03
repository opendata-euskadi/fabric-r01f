package r01f.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClient.RequestMethod;
import r01f.patterns.Provider;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.types.url.Host;
import r01f.types.url.Url;

@Slf4j
public class HttpClientRequestBuilderConnectionRetrieveStepBase<T> {
///////////////////////////////////////////////////////////////////////////////
// FIELDS
///////////////////////////////////////////////////////////////////////////////	
	protected final HttpClientRequestConnectionProvider<T> _resultProvider;
	
	protected HttpClientProxySettings _proxySettings;	// proxy settings
		
	protected long _conxTimeOut;						// timeout to get a connection with server

	protected HttpTargetServerAuth _auth;				// target server auth 

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderConnectionRetrieveStepBase(final HttpClientRequestConnectionProvider<T> resultProvider) {
		_resultProvider = resultProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONNECTION
/////////////////////////////////////////////////////////////////////////////////////////
	public T directNoAuthConnected() throws IOException {
		return this.notUsingProxy()
				   .withoutTimeOut()
				   .noAuth();
	}
	public T directNoAuthConnectedWithTimeout(final long timeout) throws IOException {
		return this.notUsingProxy()
				   .withTimeOut(timeout)
				   .noAuth();
	}
	public T connectedUsing(final Provider<HttpClientProxySettings> proxySettingsProvider) throws IOException {
		return this.usingProxy(proxySettingsProvider)
				   .withoutTimeOut()
				   .noAuth();
	}
	public T connectedUsing(final HttpClientProxySettings proxySettings) throws IOException {
		return this.usingProxy(proxySettings)
				   .withoutTimeOut()
				   .noAuth();
	}
	public T connectedUsing(final Provider<HttpClientProxySettings> proxySettingsProvider,
							final long timeout) throws IOException {
		return this.usingProxy(proxySettingsProvider)
				   .withTimeOut(timeout)
				   .noAuth();
	}
	public T connectedUsing(final HttpClientProxySettings proxySettings,
							final long timeout) throws IOException {
		return this.usingProxy(proxySettings)
				   .withTimeOut(timeout)
				   .noAuth();
	}
	public HttpClientRequestBuilderConnectionTimeOutStep usingProxy(final Provider<HttpClientProxySettings> proxySettingsProvider) {
		HttpClientProxySettings proxySettings = proxySettingsProvider.provideValue();
		if (proxySettings == null || !proxySettings.isEnabled()) {
			return this.notUsingProxy();
		} else {
			return this.usingProxy(proxySettings);
		}
	}
	/**
	 * Sets de proxy data needed to get a connection with the server through a proxy
	 * @param proxySettings
	 */
	public HttpClientRequestBuilderConnectionTimeOutStep usingProxy(final HttpClientProxySettings proxySettings) {
		if (proxySettings == null) {
			log.debug("The proxy settings is null!!! ... trying without proxy");
			return this.notUsingProxy();
		}
		_proxySettings = proxySettings;
		return new HttpClientRequestBuilderConnectionTimeOutStep();
	}
	/**
	 * Sets de proxy data needed to get a connection with the server through a proxy
	 * @param proxyHost
	 * @param user user
	 * @param password
	 */
	public HttpClientRequestBuilderConnectionTimeOutStep usingProxy(final Host proxyHost,
																	final LoginID user,final Password password) {
		return this.usingProxy(proxyHost.asUrl().getHost(),proxyHost.asUrl().getPort(),
							   user,password);
	}
	/**
	 * Sets de proxy data needed to get a connection with the server through a proxy
	 * @param host host
	 * @param port port
	 * @param user user
	 * @param password password
	 */
	public HttpClientRequestBuilderConnectionTimeOutStep usingProxy(final Host host,final int port,
																    final LoginID user,final Password password) {
		if (host == null) throw new IllegalArgumentException("The proxy host cannot be null!");
		if (port == 0) throw new IllegalArgumentException("The proxy port cannot be zero!");
		HttpClientProxySettings proxySettings = new HttpClientProxySettings(host,port,
																			user,password);
		return this.usingProxy(proxySettings);
	}
	public HttpClientRequestBuilderConnectionTimeOutStep notUsingProxy() {
		return new HttpClientRequestBuilderConnectionTimeOutStep();
	}
	public class HttpClientRequestBuilderConnectionTimeOutStep {
		/**
		 * Sets the timeout to retrieve a server connection
		 * @param timeOutMillis max time (in millis) to get a connection
		 */
		public HttpClientRequestBuilderConnectionAuthStep withTimeOut(final long timeOutMillis) {
			_conxTimeOut = timeOutMillis > 0 ? timeOutMillis : -1;
			return new HttpClientRequestBuilderConnectionAuthStep();
		}
		public HttpClientRequestBuilderConnectionAuthStep withoutTimeOut() {
			_conxTimeOut = -1;
			return new HttpClientRequestBuilderConnectionAuthStep();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  AUTH
/////////////////////////////////////////////////////////////////////////////////////////
	public class HttpClientRequestBuilderConnectionAuthStep {
		public T noAuth() throws IOException {
			_auth = null;
			return _resultProvider.provideResponse(_proxySettings,
												   _conxTimeOut,
												   _auth);
		}
		/**
		 * Sets the auth info 
		 * The usr & pwd are sent header named "Authorization" with the content "Basic base64(usr:psswd)" 
		 * @param user
		 * @param password
		 */
		public T usingBasicAuthCredentials(final LoginID user,final Password password) throws IOException {
			_auth = new HttpTargetServerAuth(HttpTargetServerAuthType.BASIC,
											user,password);
			return _resultProvider.provideResponse(_proxySettings,
												   _conxTimeOut,
												   _auth);
		}
		/**
		 * Sets the auth info 
		 * @param user 
		 * @param password 
		 */
		public T usingDigestAuthCredentials(final LoginID user,final Password password) throws IOException {
			_auth = new HttpTargetServerAuth(HttpTargetServerAuthType.DIGEST,
											user,password);
			return _resultProvider.provideResponse(_proxySettings,
												   _conxTimeOut,
												   _auth);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public static abstract class HttpClientRequestConnectionProvider<R> {
		protected final Url _targetUrl;
		protected final Charset _targetServerCharset;	// destination server charset
		
		protected final RequestMethod _method;			// Request Method
	
		protected final Map<String,String> _headers;	// request headers
		protected final Map<String,String> _cookies;	// Cookies
	
		protected final HttpRequestPayloadWrapper _payload;			// Payload (usually only for POST method-calls BUT, either GET & DELETE could support payloads
		
		
		public abstract R provideResponse(final HttpClientProxySettings proxySettings,
										  final long conxTimeOut,
										  final HttpTargetServerAuth auth) throws IOException;
		
		HttpResponse _getResponse(final HttpClientProxySettings proxySettings,
								  final long conxTimeOut,
								  final HttpTargetServerAuth auth) throws IOException {
			HttpResponse outResponse = _getResponse(proxySettings,
													conxTimeOut,
													auth,
													false);		// do not use gae
			return outResponse;
		}
		HttpResponse _getResponse(final HttpClientProxySettings proxySettings,
								  final long conxTimeOut,
								  final HttpTargetServerAuth auth,
								  final boolean useGAEHttpFetch) throws IOException {
			return new HttpResponse(_getConnection(proxySettings,
												   conxTimeOut,
												   auth,
												   useGAEHttpFetch));
		}
		HttpURLConnection _getConnection(final HttpClientProxySettings proxySettings,
									  	 final long conxTimeOut,
									  	 final HttpTargetServerAuth auth) throws IOException {
			return _getConnection(proxySettings,
								  conxTimeOut,
								  auth,
								  false);	// do not use gae
		}
		HttpURLConnection _getConnection(final HttpClientProxySettings proxySettings,
										 final long conxTimeOut,
										 final HttpTargetServerAuth auth,
										 final boolean useGAEHttpFetch) throws IOException {
			return new HttpClientRequestBuilderResponseStep(_method,
															_targetUrl,
															_targetServerCharset,
											 				_headers,_cookies,
											 				_payload,
											 				proxySettings,
											 				conxTimeOut,
											 				auth)
								.getConnection(useGAEHttpFetch,
											   auth);
		}
		String _loadAsString(final HttpClientProxySettings proxySettings,
							 final long conxTimeOut,
							 final HttpTargetServerAuth auth) throws IOException {
			return _loadAsString(proxySettings,
								 conxTimeOut,
								 auth,
								 false,		// do not use gae
								 Charset.defaultCharset());
		}
		String _loadAsString(final HttpClientProxySettings proxySettings,
							 final long conxTimeOut,
							 final HttpTargetServerAuth auth,
							 final Charset serverSentDataCharset) throws IOException {
			return _loadAsString(proxySettings,
								conxTimeOut,
								auth,
								false,	// do not use gae
								 serverSentDataCharset);
		}
		String _loadAsString(final HttpClientProxySettings proxySettings,
							 final long conxTimeOut,
							 final HttpTargetServerAuth auth,
							 final boolean useGAEUrlFetch) throws IOException {
			return _loadAsString(proxySettings,
								 conxTimeOut,
								 auth,
								 useGAEUrlFetch,
							     Charset.defaultCharset());
		}
		String _loadAsString(final HttpClientProxySettings proxySettings,
							 final long conxTimeOut,
							 final HttpTargetServerAuth auth,
							 final boolean useGAEUrlFetch,
							 final Charset serverSentDataCharset) throws IOException {
			@Cleanup InputStream responseIs = _loadAsStream(proxySettings,
															conxTimeOut,
															auth,
															useGAEUrlFetch);
	        String outStr = IOUtils.toString(responseIs,
	        								 serverSentDataCharset);
	        return outStr;
		}
		Collection<String> _readLines(final HttpClientProxySettings proxySettings,
									  final long conxTimeOut,
									  final HttpTargetServerAuth auth) throws IOException {
			return _readLines(proxySettings,
													conxTimeOut,
							  auth,
							  false,
							  Charset.defaultCharset());
		}
		Collection<String> _readLines(final HttpClientProxySettings proxySettings,
									  final long conxTimeOut,
									  final HttpTargetServerAuth auth,
									  final Charset serverSentDataCharset) throws IOException {
			return _readLines(proxySettings,
							  conxTimeOut,
							  auth,
							  false,
							  serverSentDataCharset);
		}
		Collection<String> _readLines(final HttpClientProxySettings proxySettings,
									  final long conxTimeOut,
									  final HttpTargetServerAuth auth,
									  final boolean useGAEUrlFetch) throws IOException {
			return _readLines(proxySettings,
							  conxTimeOut,
							  auth,
							  useGAEUrlFetch,
							  Charset.defaultCharset());
		}
		Collection<String> _readLines(final HttpClientProxySettings proxySettings,
									  final long conxTimeOut,
									  final HttpTargetServerAuth auth,
									  final boolean useGAEUrlFetch,
									  final Charset serverSentDataCharset) throws IOException {
			@Cleanup InputStream responseIs = _loadAsStream(proxySettings,
															conxTimeOut,
															auth,
															useGAEUrlFetch);
			BufferedReader lineReader = new BufferedReader(new InputStreamReader(responseIs));
			Collection<String> outLines = Lists.newArrayList();
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				outLines.add(line);
			}
			return outLines;
		}
		InputStream _loadAsStream(final HttpClientProxySettings proxySettings,
								  final long conxTimeOut,
								  final HttpTargetServerAuth auth) throws IOException {
			return _loadAsStream(proxySettings,
								 conxTimeOut,
								 auth,
								 false);	// do not use gae
		}
		InputStream _loadAsStream(final HttpClientProxySettings proxySettings,
								  final long conxTimeOut,
								  final HttpTargetServerAuth auth,
								  final boolean userGAEUrlFetch) throws IOException {
			InputStream responseIs = _getConnection(proxySettings,
													conxTimeOut,
													auth,
													userGAEUrlFetch)
											.getInputStream();
			return responseIs;
		}
	}
}
