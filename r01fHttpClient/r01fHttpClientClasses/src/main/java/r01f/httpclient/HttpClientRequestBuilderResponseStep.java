package r01f.httpclient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClient.RequestMethod;
import r01f.mime.MimeType;
import r01f.types.url.Url;
import r01f.types.url.UrlProtocol;
import r01f.types.url.UrlProtocol.StandardUrlProtocol;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class HttpClientRequestBuilderResponseStep 
     extends HttpClientRequestBuilderBase<HttpClientRequestBuilderResponseStep> {
///////////////////////////////////////////////////////////////////////////////
// STATUS
///////////////////////////////////////////////////////////////////////////////
	protected final RequestMethod _method;			// Request method
	
	protected final Charset _targetServerCharset;	// charset utilizado por el servidor	
	
	protected final Map<String,String> _headers;	// request headers
	protected final Map<String,String> _cookies;	// Cookies
	
	protected final HttpRequestPayloadWrapper _payload;
	
	// proxy settings
	private final HttpClientProxySettings _proxySettings;
	
	protected final long _conxTimeOut;				// timeout to get a connection with server
	
	protected final HttpTargetServerAuth _auth;				// target server auth
	
	private boolean _connected = false;
	private HttpURLConnection _conx;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	HttpClientRequestBuilderResponseStep(final RequestMethod newRequestMethod,
										  final Url targetUrl,
										  final Charset theTargetServerCharset,
						  				  final Map<String,String> newRequestHeaders,final Map<String,String> newRequestCookies,
						  				  final HttpRequestPayloadWrapper payload,
						  				  final HttpClientProxySettings proxySettings,
						  				  final long newConxTimeOut,
						  				  final HttpTargetServerAuth auth) {
		super(targetUrl);
		
		_method = newRequestMethod;
		
		_targetServerCharset = theTargetServerCharset;

		_payload = payload;
		
		_headers = newRequestHeaders;
		_cookies = newRequestCookies;

		_proxySettings = proxySettings;
		_conxTimeOut = newConxTimeOut;
		_auth = auth;
	}
		
	
///////////////////////////////////////////////////////////////////////////////
// API
///////////////////////////////////////////////////////////////////////////////
	public HttpURLConnection getConnection(final boolean useGAEHttpFetch,
										   final HttpTargetServerAuth auth) throws IOException {
		// [1]: Get the final Url
		log.trace("Conectig to: {}",_targetUrl);
		
		// [2]: If the request is authenticated
		if (auth != null 
		 && auth.getUser() != null && auth.getPassword() != null) {
			// if basic auth, set an Authorization header
			if (auth.getType() == HttpTargetServerAuthType.BASIC) {
				final String authString = auth.getUser().asString() + ":" + auth.getPassword().asString();
				final String authStringEncoded = Base64.encodeBase64String(authString.getBytes());
				_headers.put("Authorization","Basic " + authStringEncoded);
			}
			
			// If URI Digest Authorization is in use some steps must be followed:
			// http://en.wikipedia.org/wiki/Digest_access_authentication
			// The getAuthorizationHeaderValue method from AuthDigestSolutionRetriever type gets the value to be 
			// set at the Authorization header
			log.trace("...using user/password auth: {}/{}",
					  _auth.getUser(),_auth.getPassword());
			try {
				HttpURLConnection conxNOAuth = this.getConnection(useGAEHttpFetch,
																  null);		// no auth
				String authHeaderValue = AuthDigestSolutionRetriever.getAuthorizationHeaderValue(conxNOAuth,
																	   		  	 				 RequestMethod.GET,_targetUrl,
																	   		  	 				 auth);
				_headers.put("Authorization",authHeaderValue);
			} catch(AuthenticationException authEx) {
				throw new IOException(authEx);
			} catch(MalformedChallengeException mfcEx) {
				throw new IOException(mfcEx);
			}
		}
		
		// [3] -  IMPORTANT!!! If proxy is in use, an http header Proxy-Authorization MUST be set
		if (_proxySettings != null
		 && _proxySettings.getUser() != null
		 && _proxySettings.getPassword() != null) {
			final String authString = _proxySettings.getUser() + ":" + _proxySettings.getPassword();
			final String authStringEncoded = new String(Base64.encodeBase64String(authString.getBytes()));
	
			_headers.put("Proxy-Authorization","Basic " + authStringEncoded);
		}

		// [4]: Set the content-type & content-length header
		if (_method.hasPayload()) {
			MimeType payloadContentType = _payload.payloadContentType();
			if (payloadContentType != null) {
				String otherContentType = _headers != null ? _headers.get("Content-Type") : null;
				if (otherContentType != null && !otherContentType.equals(payloadContentType.getName())) throw new IllegalArgumentException("The Content-Type set for the http request is NOT the same as the one set for the payload!");
				_headers.put("Content-Type",payloadContentType.getName());
			}
			long payloadContentLength = _payload.payloadContentLength();
			if (payloadContentLength > 0) _headers.put("Content-Length",Long.toString(payloadContentLength));
		}
		
		// [5]: Set the cookies header
		if (!CollectionUtils.isNullOrEmpty(_cookies)) {
			StringBuilder cookiesStr = new StringBuilder(_cookies.size() * 15);
			for (Iterator<Map.Entry<String,String>> it = _cookies.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<String,String> cookie = it.next();
				cookiesStr.append(cookie.getKey() + "=" + cookie.getValue());
				if (it.hasNext()) cookiesStr.append(";");
			}
			_headers.put("Cookie",cookiesStr.toString());
		}
		if (log.isTraceEnabled() && CollectionUtils.hasData(_headers)) {
			StringBuilder headersDbg = new StringBuilder();
			headersDbg.append("[HEADERS]:\n");
			for (Iterator<Map.Entry<String,String>> hdIt = _headers.entrySet().iterator(); hdIt.hasNext(); ) {
				Map.Entry<String,String> hd = hdIt.next();
				headersDbg.append("\t* ").append(hd.getKey()).append(": ").append(hd.getValue());
				if (hdIt.hasNext()) headersDbg.append("\n");
			}
			log.trace(headersDbg.toString());
		}
		
		// [6]: Establish the connection
		/*if (!_connected && useGAEHttpFetch) {
			_doGAERequest(_targetUrl);
		} else*/
		 if (!_connected) {
			_doRequest(_targetUrl,
					  _proxySettings,
					  _conxTimeOut);			// <-- this is where the connection is really done
		}
		return _conx;
	}
///////////////////////////////////////////////////////////////////////////////
// METODOS PRIVADOS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Exec the server http call, sending headers and url parameters
	 * @param url the final url
	 * @param proxySettings
	 * @param timeout
	 * @return an InputStream to read the response
	 * @exception IOException if an I/O error occurs
	 */
	private void _doRequest(final Url url,
							final HttpClientProxySettings proxySettings,
							final long timeout) throws IOException {
		log.trace("...retrieving {} connection to {}",
				  _method,url);
		// Get the server connection and send headers
		HttpURLConnection conx = null;
		UrlProtocol proto = url.getProtocolOrDefault(StandardUrlProtocol.HTTP.toUrlProtocol());
		if (proto.is(StandardUrlProtocol.HTTPS)) {
			HttpsConnectionRetriever connectionRetriever = new HttpsConnectionRetriever();
			conx = connectionRetriever.getConnection(url,
													 proxySettings,
													 timeout);
		} else if (proto.is(StandardUrlProtocol.HTTP)) {
			HttpConnectionRetriever connectionRetriever = new HttpConnectionRetriever();
			conx = connectionRetriever.getConnection(url,
													 proxySettings,
													 timeout);
		} else {
			throw new IOException("Protocol NOT supported: '" + url + "'");
		}
		if (conx == null) throw new IOException( "No se ha podido obtener una conexi�n con '" + url + "'" );
		conx.setDoInput(true);
		conx.setUseCaches(false);
		log.trace("...connection retrieved!");
		
		_setConnectionRequestMethod(conx);				// Sets the http method POST/PUT/GET/HEAD/DELETE
		_sendHeaders(conx);								// Sends the http headers
		
		if (_method.hasPayload()) {
			_sendPayload(conx);	// Sends the payload 
		}
		_conx = conx;
	}
	/**
	 * Execs the server http call using GAE mechanics
	 * @param url the final url
	 * @throws IOException if an I/O error occurs
	 */
	/*private void _doGAERequest(final Url url) throws IOException {
		log.trace("...retrieving GAE connection using {} method",_method);
		HttpURLConnection conx = new HttpGoogleURLFetchConnectionWrapper(url,_conxTimeOut);
		conx.setDoInput(true);
		conx.setUseCaches(false);
		log.trace("...GAE connection retrieved!");
		
		_setConnectionRequestMethod(conx);				// Sets the http method POST/PUT/GET/HEAD/DELETE
		_sendHeaders(conx);								// Sends the http headers
		
		if (_method.hasPayload()) {
			_sendPayload(conx);	// Sends the payload 
		}
		_conx = conx;
	}*/
	private void _setConnectionRequestMethod(final HttpURLConnection conx) throws IOException {
		if (_method.isPOST()) {
			conx.setDoOutput(true);
			conx.setRequestMethod("POST");
		} else if (_method.isPUT()) {
			conx.setDoOutput(true);
			conx.setRequestMethod("PUT");
		} else if (_method.isDELETE()) {
			conx.setRequestMethod("DELETE");
		} else if (_method.isHEAD()) {
			conx.setDoOutput(false);
			conx.setRequestMethod("HEAD");
		} else if (_method.isGET()) {
			conx.setDoOutput(false);
			conx.setRequestMethod("GET");
		} else {
			throw new IllegalStateException(_method.name() + " is not supported!");
		}
	}
	/**
	 * Sends http headers to the servers
	 * @param conx the server http connection
	 * @throws IOException if an I/O error occurs
	 */
	private void _sendHeaders(final URLConnection conx) throws IOException {
		if (_headers != null) {
			for (Map.Entry<String,String> me : _headers.entrySet()) {
				conx.setRequestProperty(me.getKey(),me.getValue() );
			}
		}
	}
	/**
	 * Sends http call payload if the method is POST or PUT in any of its variants
	 * @param conx the server http connection
	 * @throws IOException if an I/O error occurs
	 */
	private void _sendPayload(final URLConnection conx) throws IOException {
		@Cleanup DataOutputStream out = new DataOutputStream(conx.getOutputStream());		
		_payload.payloadToOutputStream(out);
		out.flush();
	}
}
