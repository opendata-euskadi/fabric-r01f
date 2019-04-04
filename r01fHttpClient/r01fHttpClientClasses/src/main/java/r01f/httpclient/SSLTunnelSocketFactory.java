package r01f.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.codec.binary.Base64;

import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.url.Host;

/** 
 * Used to get an HTTPS connection though a proxy
 */
public class SSLTunnelSocketFactory 
     extends SSLSocketFactory {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static String USER_AGENT = _composeUserAgent();
	/**
	 * An alternative to access sun.net.www.protocol.http.HttpURLConnection.userAgent that require a compile-time dependency 
	 * to jvm rt.jar (see http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/sun/net/www/protocol/http/HttpURLConnection.java#HttpURLConnection.0userAgent)
	 * @return
	 */
	private static String _composeUserAgent() {
		String version = System.getProperty("java.version");	// java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("java.version"));
        String agent = System.getProperty("http.agent"); 		// java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("http.agent"));
        if (agent == null) {
        	agent = "Java/" + version;
        } else {
        	agent = agent + " Java/"+version;
        }
        return agent;
	}
///////////////////////////////////////////////////////////////////////////////
// FIELDS
///////////////////////////////////////////////////////////////////////////////
	private SSLSocketFactory _dfactory;				// Creates sockets
	private HttpClientProxySettings _proxySettings;	// proxy settings

///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////
	public SSLTunnelSocketFactory(final HttpClientProxySettings proxySettings) {
		this((SSLSocketFactory)SSLSocketFactory.getDefault(),
			 proxySettings);
	}
	public SSLTunnelSocketFactory(final SSLSocketFactory sslSocketFactory,
								  final HttpClientProxySettings proxySettings) {
		_dfactory = sslSocketFactory;
		_proxySettings = proxySettings;
	}
///////////////////////////////////////////////////////////////////////////////
// METODOS
///////////////////////////////////////////////////////////////////////////////
	@Override
	public Socket createSocket(final String remoteHost,final int remotePort) throws IOException, 
																					UnknownHostException {
		return this.createSocket((Socket)null,
								 remoteHost,remotePort,true);
	}
	@Override
	public Socket createSocket(final InetAddress remoteHostAddr,final int remotePort) throws IOException {
		return this.createSocket((Socket)null,
								 remoteHostAddr.getHostName(),remotePort,true);
	}
	@Override
	public Socket createSocket(final String remoteHost,final int remotePort,
							   final InetAddress proxyAddr,final int proxyPort) throws IOException, 
							   														   UnknownHostException {
		_proxySettings = new HttpClientProxySettings(proxyAddr != null ? Host.of(proxyAddr.getHostName()) : _proxySettings.getProxyHost(),proxyPort,
													 _proxySettings.getUser(),_proxySettings.getPassword(),
													 true);	// enabled
		return this.createSocket((Socket)null,
								 remoteHost,remotePort,true);
	}
	@Override
	public Socket createSocket(final InetAddress remoteHostAddr,final int remotePort,
							   final InetAddress proxyAddr,final int proxyPort) throws IOException {
		_proxySettings = new HttpClientProxySettings(proxyAddr != null ? Host.of(proxyAddr.getHostName()) : _proxySettings.getProxyHost(),proxyPort,
													 _proxySettings.getUser(),_proxySettings.getPassword(),
													 true);	// enabled
		return this.createSocket((Socket)null,
								 remoteHostAddr.getHostName(),remotePort,true);
	}
	@Override @SuppressWarnings("resource")
	public Socket createSocket(final Socket socket,
							   final String remoteHost,final int remotePort,
							   final boolean flag) throws IOException, 
							   							  UnknownHostException {
		Socket proxySocket = socket != null ? socket 
											: new Socket(_proxySettings.getProxyHost().asString(),_proxySettings.getProxyPort());
		_doTunnelHandshake(proxySocket,
						   Host.of(remoteHost),remotePort);
		SSLSocket sslsocket = (SSLSocket)_dfactory.createSocket(proxySocket,
																remoteHost,remotePort,
																flag);
		return sslsocket;
	}
	@Override
	public String[] getDefaultCipherSuites() {
		return _dfactory.getDefaultCipherSuites();
	}
	@Override
	public String[] getSupportedCipherSuites() {
		return _dfactory.getSupportedCipherSuites();
	}
	@Override
	public String toString() {
		return "  <SSLTunnelSocketFactory proxyPort=" + _proxySettings.getProxyHost() + " proxyHost=" + _proxySettings.getProxyPort() + " delegate=" + _dfactory + "/>";
	}
	public void setDelegateFactory(final SSLSocketFactory sslsocketfactory) {
		_dfactory = sslsocketfactory;
	}
	public void setProxyAuth(final UserCode usr,final Password pwd) {
		_proxySettings = new HttpClientProxySettings(_proxySettings.getProxyHost(),_proxySettings.getProxyPort(),
												     usr,pwd,
												     _proxySettings.isEnabled());
	}
	
///////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * SSL handsake
	 * @param proxySocket 
	 * @param remoteHost remote host
	 * @param remotePort remote host port
	 * @throws IOException 
	 */
	@SuppressWarnings("resource")
	private void _doTunnelHandshake(final Socket proxySocket,
									final Host remoteHost,final int remotePort) throws IOException {
		// DO NOT use @Cleanup: java.net.SocketException: Socket is closed
		OutputStream outputstream = proxySocket.getOutputStream();
		String s1 = "";
		if (_proxySettings.getUser() != null) {
			// Use encode because encodeBuffer raises java.io.EOFException: SSL peer shut down incorrectly
			s1 = "Proxy-Authorization: Basic " + Base64.encodeBase64String((_proxySettings.getUser() + ":" + _proxySettings.getPassword()).getBytes()) + "\r\n";
		}
		String s2 = "CONNECT " + remoteHost + ":" + remotePort + " HTTP/1.0\n" + s1 + "User-Agent: " + USER_AGENT + "\r\n\r\n";
		
		byte abyte0[];
		try {
			abyte0 = s2.getBytes("ASCII7");
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			abyte0 = s2.getBytes();
		}
		outputstream.write(abyte0);
		outputstream.flush();
		byte abyte1[] = new byte[200];
		int j = 0;
		int k = 0;
		boolean flag = false;
		
		// Do not use @Cleanup annotation since it forces 
		// java.net.SocketException: Socket is closed
		InputStream inputstream = proxySocket.getInputStream();
		do {
			if (k >= 2) break;
			int l = inputstream.read();
			if (l < 0) throw new IOException("Unexpected EOF from proxy");
			if (l == 10) {
				flag = true;
				k++;
			} else if (l != 13) {
				k = 0;
				if (!flag && j < abyte1.length) abyte1[j++] = (byte)l;
			}
		} while (true);
		
		String s3;
		try {
			s3 = new String(abyte1,0,j,"ASCII7");
		} catch (UnsupportedEncodingException unsupportedencodingexception1) {
			s3 = new String(abyte1, 0, j);
		}		
		if (s3.toLowerCase().indexOf(" 200 ") == -1) throw new IOException("Unable to tunnel through " + _proxySettings.getProxyHost() + ":" + _proxySettings.getProxyPort() + ".  Proxy returns \"" + s3 + "\"");
	}
}
