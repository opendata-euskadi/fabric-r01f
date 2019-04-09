package r01f.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import r01f.httpclient.HttpClientProxySettings;
import r01f.types.url.Url;

/**
 * Loads a resource from an url 
 */
public interface UrlResourceLoader {
	/**
	 * Loads a resource from an url 
	 * @param proxySettings
	 * @param url
	 * @param charset
	 * @param cookies
	 * @return
	 */
	public InputStream load(final HttpClientProxySettings proxySettings,
							final Url url,final Charset charset,
							final String[]... cookies) throws IOException;
}
