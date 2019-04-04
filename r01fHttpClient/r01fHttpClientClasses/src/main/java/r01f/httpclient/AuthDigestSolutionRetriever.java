package r01f.httpclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.protocol.BasicHttpContext;

import r01f.httpclient.HttpClient.RequestMethod;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.util.types.collections.CollectionUtils;


/**
 * Gets the value to be set at the "Authorization" header when the server requires authentication
 * Ver http://en.wikipedia.org/wiki/Digest_access_authentication
 */
public class AuthDigestSolutionRetriever {
	/**
	 * Gets the "Authorization" header
	 * @param code 
	 * @param headers
	 * @param requestMethod
	 * @param url
	 * @param authUser
	 * @param authPwd
	 * @return
	 */
	public static String getAuthorizationHeaderValue(final HttpURLConnection serverConnectionNoAuth,
									 				 final RequestMethod requestMethod,final Url theUrl,
									 				 final HttpTargetServerAuth auth) throws IOException,
									 				 										 AuthenticationException,
									 				 										 MalformedChallengeException {
		// A org.apache.http.impl.auth.DigestScheme instance is
		// what will process the challenge from the web-server
		final DigestScheme md5Auth = new DigestScheme();
		// Validate that we got an HTTP 401 back
		HttpResponseCode serverNoAuthResponseCode = HttpResponseCode.of(serverConnectionNoAuth.getResponseCode());
		if (serverNoAuthResponseCode.is(HttpResponseCode.UNAUTHORIZED)) {
			if (CollectionUtils.isNullOrEmpty(serverConnectionNoAuth.getHeaderFields())) throw new IllegalStateException("HTTP Headers not received!");
			// headers normalization
			Map<String,String> headersKeys = _normalizeHeadersKeys(serverConnectionNoAuth.getHeaderFields());
			if (headersKeys.containsKey("WWW-AUTHENTICATE")) {
				// [1] Get an HttpRequest object from the URL and the method (GET/POST/PUT/DELETE)
				HttpRequestBase commonsHttpRequest = _commonsHttpClientRequestFrom(requestMethod,
																				   theUrl.getUrlPath());
				
				// [2] Generate a solution Authentication header using the username and password.
				// 2.1 Get the challenge and solve.
				String challenge = serverConnectionNoAuth.getHeaderFields()
													   	 .get(headersKeys.get("WWW-AUTHENTICATE"))
													   	 .get(0);
				commonsHttpRequest.addHeader(headersKeys.get("WWW-AUTHENTICATE"),
											 challenge);
				md5Auth.processChallenge(commonsHttpRequest.getHeaders(headersKeys.get("WWW-AUTHENTICATE"))[0]);
				
				// 2.2 Compose a Header object for the "Authorization" header
				Header solution = md5Auth.authenticate(new UsernamePasswordCredentials(auth.getUser().asString(),
																					   auth.getPassword().asString()),
													   commonsHttpRequest,
													   new BasicHttpContext());
				return solution.getValue();		// the value of the composed Authorization header
				
			} 
			throw new IllegalStateException("A 401 response (unauthorized) has been received, but NO WWW-Authenticate header in this response!");
		} 
		throw new IllegalStateException("The request is supossed to be authenticated but the server response code was NOT 401 (unauthorized)");
	}
	/**
	 * Normalizes the header names returning a Map that indexes the header name (UPPERCASE) with the received header
	 * @param headers received headers
	 * @return the normalized headers
	 */
	private static Map<String,String> _normalizeHeadersKeys(final Map<String,List<String>> headers) {
		Map<String,String> headersKeys = new HashMap<String,String>(headers.size());
		Iterator<String> itKeys = headers.keySet().iterator();
		while (itKeys.hasNext()) {
			String key = itKeys.next();
			if (key == null) continue;
			headersKeys.put(key.toUpperCase(),key);
		}
		return headersKeys;
	}
	/**
	 * Creates a {@link HttpRequestBase} object from the {@link RequestMethod} method
	 * @param requestMethod
	 * @param the url
	 * @return the {@link HttpRequestBase} 
	 */
	private static HttpRequestBase _commonsHttpClientRequestFrom(final RequestMethod requestMethod,
												   		  		 final UrlPath urlPath) {
		HttpRequestBase outHttpReq = null;
		if (requestMethod.equals(RequestMethod.POST)) {
			outHttpReq = new HttpPost(urlPath.asAbsoluteString());
		} else if (requestMethod.equals(RequestMethod.GET)) {
			outHttpReq = new HttpGet(urlPath.asAbsoluteString());
		} else if (requestMethod.equals(RequestMethod.PUT)) {
			outHttpReq = new HttpPut(urlPath.asAbsoluteString());
		} else if (requestMethod.equals(RequestMethod.DELETE)) {
			outHttpReq = new HttpDelete(urlPath.asAbsoluteString());
		} else {
			throw new IllegalArgumentException("The http request method is NOT a valid one (GET, POST, PUT, DELETE).");
		}
		return outHttpReq;
	}
}
