package r01f.httpclient.jsse.handler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.jsse.misc.RegexpPool;
import r01f.httpclient.jsse.net.NetworkClient;
import r01f.httpclient.jsse.security.action.GetPropertyAction;
import r01f.httpclient.jsse.security.util.HostnameChecker;
import r01f.httpclient.jsse.www.http.HttpClient;

@Slf4j
final class HttpsClient
	extends HttpClient
 implements HandshakeCompletedListener {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int DEFAULT_PORT = 443;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private HostnameVerifier _hostNameVerifier;
	private SSLSocketFactory _sslSocketFactory;
	private String _proxyHostUsed;
	private int _proxyPortUsed;
	private SSLSession _sslSession;
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	static HttpClient getHTTPSClient(final SSLSocketFactory sslSocketFactory,
									 final URL url,
									 final HostnameVerifier hostNameVerifier) throws IOException {
		return HttpsClient.getHTTPSClient(sslSocketFactory,
										  url,
										  hostNameVerifier,
										  true);
	}

	static HttpClient getHTTPSClient(final SSLSocketFactory sslSocketFactory,
									 final URL url,
									 final HostnameVerifier hostNameVerifier,
									 final boolean useCache) throws IOException {
		return HttpsClient.getHTTPSClient(sslSocketFactory,
										  url, hostNameVerifier,
										  (String)null,
										  -1,
										  useCache);
	}

	static HttpClient getHTTPSClient(final SSLSocketFactory sslSocketFactory,
									 final URL url,
									 final HostnameVerifier hostNameVerifier,
									 final String proxyHost,final int proxyPort) throws IOException {
		return HttpsClient.getHTTPSClient(sslSocketFactory,
										  url,
										  hostNameVerifier,
										  proxyHost,
										  proxyPort,
										  true);
	}
	static HttpClient getHTTPSClient(final SSLSocketFactory sslSocketFactory,
									 final URL url,
									 final HostnameVerifier hostNameVerifier,
									 final String proxyHost,final int proxyPort,
									 final boolean useCache) throws IOException {
		log.debug("Get getHTTPSClient");
		HttpsClient httpsClient = null;
		if (useCache) {
			log.debug("...uses cache!");
			httpsClient = (HttpsClient)HttpClient.kac.get(url,sslSocketFactory);
			if (httpsClient != null) {
				httpsClient.cachedHttpClient = true;
			}
		}
		if (httpsClient == null) {
			log.debug("...new instance of HttpsClient ");
			httpsClient = new HttpsClient(sslSocketFactory,
										  url,
										  proxyHost, proxyPort);
		} else {
			SecurityManager securitymanager = System.getSecurityManager();
			if (securitymanager != null) {
				securitymanager.checkConnect(url.getHost(), url.getPort());
			}
			httpsClient.url = url;
		}
		httpsClient.setHostNameVerifier(hostNameVerifier);
		return httpsClient;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("deprecation")
	public HttpsClient(final SSLSocketFactory sslsocketfactory,
					   final URL url,
					   final String proxyHost,final int proxyPort) throws IOException {
		this.setSSLSocketFactory(sslsocketfactory);
		if (proxyHost != null) {
			this.setProxy(proxyHost, proxyPort);
		}
		super.proxyDisabled = true;
		try {
			InetAddress inetaddress = InetAddress.getByName(url.getHost());
			super.host = inetaddress.getHostAddress();
		} catch (UnknownHostException unknownhostexception) {
			super.host = url.getHost();
		}
		super.url = url;
		super.port = url.getPort();
		if (super.port == -1) {
			super.port = getDefaultPort();
		}
		openServer();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	static int getDefaultConnectTimeout() {
		return NetworkClient.defaultConnectTimeout;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected int getDefaultPort() {
		return DEFAULT_PORT;
	}
	/*
	 * private int getProxyPort() { // }
	 */
	@Override
	public int getProxyPortUsed() {
		return _proxyPortUsed;
	}

	@Override
	public void afterConnect() throws IOException,UnknownHostException {
		if (!isCachedConnection()) {
			SSLSocket sslsocket = null;
			SSLSocketFactory sslsocketfactory = _sslSocketFactory;
			try {
				if (!(super.serverSocket instanceof SSLSocket)) {
					sslsocket = (SSLSocket)sslsocketfactory.createSocket(super.serverSocket, 
																		 super.host, 
																		 super.port, 
																		 true);
				} else {
					sslsocket = (SSLSocket)super.serverSocket;
				}
			} catch (IOException ioexception) {
				try {
					sslsocket = (SSLSocket)sslsocketfactory.createSocket(super.host, 
																		 super.port);
				} catch (IOException ioexception1) {
					throw ioexception;
				}
			}
			// SSLSocketFactoryImpl.checkCreate(sslsocket);
			String protocols[] = _getProtocols();
			String ciphers[] = _getCipherSuites();
			if (protocols != null) {
				sslsocket.setEnabledProtocols(protocols);
			}
			if (ciphers != null) {
				sslsocket.setEnabledCipherSuites(ciphers);
			}
			sslsocket.addHandshakeCompletedListener(this);
			sslsocket.startHandshake();
			_sslSession = sslsocket.getSession();
			super.serverSocket = sslsocket;
			try {
				super.serverOutput = new PrintStream(new BufferedOutputStream(super.serverSocket.getOutputStream()), false, NetworkClient.encoding);
			} catch (UnsupportedEncodingException unsupportedencodingexception) {
				throw new InternalError(NetworkClient.encoding + " encoding not found");
			}
			this.checkURLSpoofing(_hostNameVerifier);
		} else {
			_sslSession = ((SSLSocket)super.serverSocket).getSession();
		}
	}
	@Override
	protected synchronized void putInKeepAliveCache() {
		HttpClient.kac.put(super.url, _sslSocketFactory, this);
	}
	private boolean isNonProxyHost() {
		RegexpPool nonProxyHosts;
		nonProxyHosts = _getNonProxyHosts();
		if (nonProxyHosts.match(super.url.getHost().toLowerCase()) != null) {
			return true;
		}
		String s;
		InetAddress inetaddress;
		try {
			inetaddress = InetAddress.getByName(super.url.getHost());

			s = inetaddress.getHostAddress();
			if (nonProxyHosts.match(s) != null) {
				return true;
			}
		} catch (UnknownHostException ex) {
			ex.printStackTrace(System.out);
		}
		/*
		 * break MISSING_BLOCK_LABEL_54; UnknownHostException
		 * unknownhostexception; unknownhostexception;
		 */
		return false;
	}
	@Override
	public boolean needsTunneling() {
		return _proxyHostUsed != null && !isNonProxyHost();
	}
	String b() {
		return _sslSession.getCipherSuite();
	}
	@Override
	public String getProxyHostUsed() {
		if (!needsTunneling()) return null;
		return _proxyHostUsed;
	}
	void setProxy(final String proxyHost, final int proxyPort) {
		_proxyHostUsed = proxyHost;
		_proxyPortUsed = proxyPort >= 0 ? proxyPort : getDefaultPort();
	}
	Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
		return _sslSession.getPeerCertificates();
	}
	public Certificate[] getLocalCertificates() {
		return _sslSession.getLocalCertificates();
	}
	@Override
	public void handshakeCompleted(final HandshakeCompletedEvent handshakecompletedevent) {
		_sslSession = handshakecompletedevent.getSession();
	}
	void setHostNameVerifier(final HostnameVerifier hostNameVerifier) {
		_hostNameVerifier = hostNameVerifier;
	}
	private void checkURLSpoofing(final HostnameVerifier hostnameVerifier) throws IOException {
		String theHost = super.url.getHost();
		if (theHost != null && theHost.startsWith("[") && theHost.endsWith("]")) {
			theHost = theHost.substring(1, theHost.length() - 1);
		}
		try {
			Certificate peerCerts[] = _sslSession.getPeerCertificates();
			X509Certificate peerCert;
			if (peerCerts[0] instanceof X509Certificate) {
				peerCert = (X509Certificate) peerCerts[0];
			} else {
				throw new SSLPeerUnverifiedException("");
			}
			HostnameChecker checker = HostnameChecker.getInstance((byte) 1);
			checker.match(theHost, peerCert);
			return;
		} catch (SSLPeerUnverifiedException sslpeerunverifiedexception) {
			/* ignore */
		} catch (CertificateException certificateexception) {
			/* ignore */
		}
		String cipher = _sslSession.getCipherSuite();
		if (cipher != null && cipher.indexOf("_anon_") != -1) {
			return;
		}
		if (hostnameVerifier != null && hostnameVerifier.verify(theHost, _sslSession)) return;

		super.serverSocket.close();
		_sslSession.invalidate();
		throw new IOException("HTTPS hostname wrong:  should be <" + super.url.getHost() + ">");
	}
	SSLSocketFactory getSSLSocketFactory() {
		return _sslSocketFactory;
	}
	void setSSLSocketFactory(final SSLSocketFactory sslSocketFactory) {
		_sslSocketFactory = sslSocketFactory;
	}
	public javax.security.cert.X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
		return _sslSession.getPeerCertificateChain();
	}
	@Override
	protected Socket doConnect(final String aHost,final int aPort) throws IOException,
																		UnknownHostException {
		Socket socket = _sslSocketFactory.createSocket(aHost,aPort);
		return socket;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static String[] _getCipherSuites() {
		String cipherString = AccessController.doPrivileged(new GetPropertyAction("https.cipherSuites"));
		String ciphers[];
		if (cipherString == null || "".equals(cipherString)) {
			ciphers = null;
		} else {
			Vector<String> vector = new Vector<String>();
			for (StringTokenizer stringtokenizer = new StringTokenizer(cipherString, ","); stringtokenizer.hasMoreElements(); vector.addElement((String) stringtokenizer.nextElement())) {
				/* nothing */
			}
			ciphers = new String[vector.size()];
			for (int i = 0; i < ciphers.length; i++) {
				ciphers[i] = vector.elementAt(i);
			}

		}
		return ciphers;
	}
	private static String[] _getProtocols() {
		String protocolString = AccessController.doPrivileged(new GetPropertyAction("https.protocols"));
		String protocols[];
		if (protocolString == null || "".equals(protocolString)) {
			protocols = null;
		} else {
			Vector<String> vector = new Vector<String>();
			for (StringTokenizer stringtokenizer = new StringTokenizer(protocolString, ","); stringtokenizer.hasMoreElements(); vector.addElement((String) stringtokenizer.nextElement())) {
				/* empty */
			}
			protocols = new String[vector.size()];
			for (int i = 0; i < protocols.length; i++) {
				protocols[i] = vector.elementAt(i);
			}
		}
		return protocols;
	}
	private static RegexpPool _getNonProxyHosts() {
		RegexpPool regexppool = new RegexpPool();
		String nonProxyHosts = AccessController.doPrivileged(new GetPropertyAction("http.nonProxyHosts"));
		if (nonProxyHosts != null) {
			StringTokenizer stringtokenizer = new StringTokenizer(nonProxyHosts, "|", false);
			try {
				while (stringtokenizer.hasMoreTokens()) {
					regexppool.add(stringtokenizer.nextToken().toLowerCase(), new Boolean(true));
				}
			} catch (Exception exception) {
				exception.printStackTrace(System.out);
			}
		}
		return regexppool;
	}
	static int getProxyPortUsed(final HttpsClient httpsclient) {
		return httpsclient._proxyPortUsed;
	}
	static String getProxyHostUsed(final HttpsClient httpsclient) {
		return httpsclient._proxyHostUsed;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////


}
