package r01f.httpclient.jsse.handler;

import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;

import r01f.httpclient.jsse.www.protocol.http.Handler;
import r01f.httpclient.jsse.www.protocol.http.HttpURLConnection;

//Referenced classes of package sun.net.www.protocol.https:
//         HttpsClient
public abstract class AbstractDelegateHttpsURLConnection
              extends HttpURLConnection {

	@Override
	public void connect() throws IOException {
		if (super.connected) {
			return;
		}
		plainConnect();
		if (!super.http.isCachedConnection() && super.http.needsTunneling()) {
			doTunneling();
		}
		((HttpsClient) super.http).afterConnect();
	}

	@Override
	protected void plainConnect() throws IOException {
		if (super.connected) return;

		super.http = HttpsClient.getHTTPSClient(getSSLSocketFactory(),
												super.url,
												getHostnameVerifier());
		super.connected = true;
		return;
	}

	public boolean isConnected() {
		return super.connected;
	}

	public void setConnected(final boolean flag) {
		super.connected = flag;
	}

	public String getCipherSuite() {
		if (super.http == null) throw new IllegalStateException("connection not yet open");
		return ((HttpsClient) super.http).b();
	}
	@Override
	public void setNewClient(final URL url) throws IOException {
		setNewClient(url, false);
	}
	@Override
	public void setNewClient(final URL url,final boolean flag) throws IOException {
		super.http = HttpsClient.getHTTPSClient(getSSLSocketFactory(), url, getHostnameVerifier(), flag);
		((HttpsClient) super.http).afterConnect();
	}
	public Certificate[] getLocalCertificates() {
		if (super.http == null) throw new IllegalStateException("connection not yet open");
		return ((HttpsClient)super.http).getLocalCertificates();
	}
	public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
		if (super.http == null) throw new IllegalStateException("connection not yet open");
		return ((HttpsClient) super.http).getPeerCertificates();
	}

	public X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
		if (super.http == null) throw new IllegalStateException("connection not yet open");
		return ((HttpsClient)super.http).getPeerCertificateChain();
	}

	protected abstract HostnameVerifier getHostnameVerifier();

	protected abstract SSLSocketFactory getSSLSocketFactory();

	@Override
	public void setProxiedClient(final URL url,final String s,final int i) throws IOException {
		this.setProxiedClient(url,s,i,false);
	}
	@Override
	protected void proxiedConnect(final URL aUrl,
								  final String proxyHost,final int proxyPort,
								  final boolean useCache) throws IOException {
		if (super.connected) return;

		SecurityManager securitymanager = System.getSecurityManager();
		if (securitymanager != null) {
			securitymanager.checkConnect(proxyHost, proxyPort);
		}
		super.http = HttpsClient.getHTTPSClient(this.getSSLSocketFactory(),
												aUrl,
												this.getHostnameVerifier(), 
												proxyHost,proxyPort, 	// proxy host & port
												useCache);
		super.connected = true;
	}
	@Override
	public void setProxiedClient(final URL url,final String s,final int i,final boolean flag) throws IOException {
		this.proxiedConnect(url,s,i,flag);
		if (!super.http.isCachedConnection()) {
			doTunneling();
		}
		((HttpsClient) super.http).afterConnect();
	}

	protected AbstractDelegateHttpsURLConnection(final URL url, final Handler handler) throws IOException {
		super(url, handler);
	}
}
