package r01f.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.IsPath;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Loads a file from an URL via HTTP
 */
@Accessors(prefix="_")
public class ResourcesLoaderFromURL 
     extends ResourcesLoaderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static final String PROXY_HOST_PROP = "proxyHost";
	static final String PROXY_PORT_PROP = "proxyPort";
	static final String PROXY_USER_PROP = "proxyUser";
	static final String PROXY_PASSWORD_PROP = "proxyPassword";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter @Setter private String _proxyHost;
	@Getter @Setter private String _proxyPort;
	@Getter @Setter private UserCode _proxyUser;
	@Getter @Setter private Password _proxyPassword;
///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	ResourcesLoaderFromURL(final ResourcesLoaderDef def) {
		super(def);
		if (CollectionUtils.hasData(def.getLoaderProps())) {
			String proxyHost = def.getProperty(PROXY_HOST_PROP);
			if (!Strings.isNullOrEmpty(proxyHost)) {
				_proxyHost = proxyHost;
				
				String proxyPort = def.getProperty(PROXY_PORT_PROP);
				_proxyPort = !Strings.isNullOrEmpty(proxyPort) ? proxyPort : "8080";
				
				String proxyUser = def.getProperty(PROXY_USER_PROP);
				String proxyPwd = def.getProperty(PROXY_PASSWORD_PROP);
				_proxyUser = !Strings.isNullOrEmpty(proxyUser) ? UserCode.forId(proxyUser) : null;
				_proxyPassword = !Strings.isNullOrEmpty(proxyPwd) ? Password.forId(proxyPwd) : null;
			}
		}
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		boolean outOK = true;	// none of the properties are mandatory
		return outOK;
	}
///////////////////////////////////////////////////////////////////////////////
// 	METHODS
///////////////////////////////////////////////////////////////////////////////
	@Override
	protected InputStream _doGetInputStream(final IsPath resourceUrl,
									        final boolean reload) throws IOException {
		try {
			InputStream outIs = null;
			
			// Timeouts
			RequestConfig reqCfg = RequestConfig.custom()
												.setConnectTimeout(1000)			// 1sg
												.setConnectionRequestTimeout(1000)	// 1sg
												.setSocketTimeout(1000)
												.build();
			
			HttpResponse response = null;
	        boolean useProxy = !Strings.isNullOrEmpty(_proxyHost);
	        
	        // proxy
	        if (useProxy) {
	        	HttpHost proxyHost = new HttpHost(_proxyHost,Integer.parseInt(_proxyHost));
	        	// credentials provider for the proxy
				CredentialsProvider provider = new BasicCredentialsProvider();
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(_proxyUser.asString(),
																						  _proxyPassword.asString());
				provider.setCredentials(new AuthScope(proxyHost), 
										credentials);
				// build th http client
				HttpClient client = HttpClientBuilder.create()
													 .setDefaultCredentialsProvider(provider)
													 .setProxy(proxyHost)
													 .setDefaultRequestConfig(reqCfg)		// timeouts
													 .build();			 
				response = client.execute(new HttpGet(resourceUrl.asAbsoluteString()));
	        }
	        // no proyx
	        else {
				HttpClient client = HttpClientBuilder.create()
												     .setDefaultRequestConfig(reqCfg)		// timeouts
													 .build();			 
				response = client.execute(new HttpGet(resourceUrl.asAbsoluteString()));
	        }
	        
	        // read the response
			int statusCode = response.getStatusLine()
									 .getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                outIs = entity != null ? entity.getContent() 
                					   : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + statusCode);
            }
	        return outIs;
	        
		} catch(IOException ioEx) {
			StringBuilder msg = new StringBuilder("Error when loading a resource from the url: " + resourceUrl);
			if (_proxyHost != null) {
				msg.append("(");
				msg.append(" proxy host:port=" + _proxyHost + ":" + _proxyPort);
				msg.append(" proxy usr/pwd=" + _proxyUser + "/" + _proxyPassword);
				msg.append(")");
			}
			throw new IOException(msg + " > " + ioEx.getMessage());
		}
	}
}
