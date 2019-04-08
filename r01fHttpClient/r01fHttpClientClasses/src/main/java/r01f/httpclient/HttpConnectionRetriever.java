package r01f.httpclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import r01f.types.url.Url;
import r01f.types.url.UrlProtocol;
import r01f.types.url.UrlProtocol.StandardUrlProtocol;

/**
 * Type in charge of retrieving a non-secure HTTP connection
 */
  class HttpConnectionRetriever 
extends ConnectionRetrieverBase {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  OVERRIDEN METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public HttpURLConnection _retrieveConnection(final Url targetUrl,
												 final HttpClientProxySettings proxySettings) throws IOException {
		// Get an url
		UrlProtocol proto = targetUrl.getProtocol();
		Url finalUrl = proto != null ? targetUrl
									 : Url.from(StandardUrlProtocol.HTTP.toUrlProtocol(),
											 	targetUrl);
		final URL url = new URL(finalUrl.asStringUrlEncodingQueryStringParamsValues());
		
		HttpURLConnection conx = null;
		if (proxySettings != null
		 && proxySettings.getProxyHost() != null 
		 && proxySettings.getProxyPort() > 0
		 && proxySettings.isEnabled()) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxySettings.getProxyHost().asString(),
																		  proxySettings.getProxyPort()));
			conx = (HttpURLConnection)url.openConnection(proxy);
		} else {
			conx = (HttpURLConnection)url.openConnection();
		}
		return conx;
		
	}
	
}
