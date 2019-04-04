package r01f.httpclient.jsse.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

// Referenced classes of package sun.net.www.protocol.https:
//            AbstractDelegateHttpsURLConnection, DelegateHttpsURLConnection, Handler

public class HttpsURLConnectionImpl 
	 extends HttpsURLConnection {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected DelegateHttpsURLConnection _flddelegate;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getContentLength() {
		return _flddelegate.getContentLength();
	}
	@Override
	public int getResponseCode() throws IOException {
		return _flddelegate.getResponseCode();
	}
	@Override
	public int hashCode() {
		return _flddelegate.hashCode();
	}
	@Override
	public long getDate() {
		return _flddelegate.getDate();
	}
	@Override
	public long getExpiration() {
		return _flddelegate.getExpiration();
	}
	@Override
	public long getIfModifiedSince() {
		return _flddelegate.getIfModifiedSince();
	}
	@Override
	public long getLastModified() {
		return _flddelegate.getLastModified();
	}
	@Override
	public void connect() throws IOException {
		_flddelegate.connect();
	}
	@Override
	public void disconnect() {
		_flddelegate.disconnect();
	}
	@Override
	protected void finalize() throws Throwable {
		// _flddelegate.finalize();
	}
	@Override
	public boolean getAllowUserInteraction() {
		return _flddelegate.getAllowUserInteraction();
	}
	@Override
	public boolean getDefaultUseCaches() {
		return _flddelegate.getDefaultUseCaches();
	}
	@Override
	public boolean getDoInput() {
		return _flddelegate.getDoInput();
	}
	@Override
	public boolean getDoOutput() {
		return _flddelegate.getDoOutput();
	}
	@Override
	public boolean getInstanceFollowRedirects() {
		return _flddelegate.getInstanceFollowRedirects();
	}
	@Override
	public boolean getUseCaches() {
		return _flddelegate.getUseCaches();
	}

	protected boolean isConnected() {
		return _flddelegate.isConnected();
	}
	@Override
	public boolean usingProxy() {
		return _flddelegate.usingProxy();
	}
	@Override
	public void setIfModifiedSince(long l) {
		_flddelegate.setIfModifiedSince(l);
	}
	@Override
	public void setAllowUserInteraction(boolean flag) {
		_flddelegate.setAllowUserInteraction(flag);
	}

	protected void setConnected(boolean flag) {
		_flddelegate.setConnected(flag);
	}
	@Override
	public void setDefaultUseCaches(boolean flag) {
		_flddelegate.setDefaultUseCaches(flag);
	}
	@Override
	public void setDoInput(boolean flag) {
		_flddelegate.setDoInput(flag);
	}
	@Override
	public void setDoOutput(boolean flag) {
		_flddelegate.setDoOutput(flag);
	}
	@Override
	public void setInstanceFollowRedirects(boolean flag) {
		_flddelegate.setInstanceFollowRedirects(flag);
	}
	@Override
	public void setUseCaches(boolean flag) {
		_flddelegate.setUseCaches(flag);
	}
	@Override
	public InputStream getErrorStream() {
		return _flddelegate.getErrorStream();
	}
	@Override
	public synchronized InputStream getInputStream() throws IOException {
		return _flddelegate.getInputStream();
	}
	@Override
	public synchronized OutputStream getOutputStream() throws IOException {
		return _flddelegate.getOutputStream();
	}
	@Override
	public Object getContent() throws IOException {
		return _flddelegate.getContent();
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( !(obj instanceof HttpsURLConnectionImpl) ) return false;
		
		HttpsURLConnectionImpl other = (HttpsURLConnectionImpl)obj;
		return _flddelegate != null ? _flddelegate.equals(other._flddelegate)
									: other._flddelegate != null ? false
																 : true;
	}
	@Override
	public String getCipherSuite() {
		return _flddelegate.getCipherSuite();
	}
	@Override
	public String getContentEncoding() {
		return _flddelegate.getContentEncoding();
	}
	@Override
	public String getContentType() {
		return _flddelegate.getContentType();
	}
	@Override
	public String getRequestMethod() {
		return _flddelegate.getRequestMethod();
	}
	@Override
	public String getResponseMessage() throws IOException {
		return _flddelegate.getResponseMessage();
	}
	@Override
	public String toString() {
		return _flddelegate.toString();
	}
	@Override
	public String getHeaderField(int i) {
		return _flddelegate.getHeaderField(i);
	}
	@Override
	public String getHeaderFieldKey(int i) {
		return _flddelegate.getHeaderFieldKey(i);
	}
	@Override
	public void setRequestMethod(String s) throws ProtocolException {
		_flddelegate.setRequestMethod(s);
	}
	@Override
	public int getHeaderFieldInt(String s, int i) {
		return _flddelegate.getHeaderFieldInt(s, i);
	}
	@Override
	public long getHeaderFieldDate(String s, long l) {
		return _flddelegate.getHeaderFieldDate(s, l);
	}
	@Override
	public URL getURL() {
		return _flddelegate.getURL();
	}
	protected HttpsURLConnectionImpl(URL url) {
		super(url);
	}
	protected void setNewClient(URL url) throws IOException {
		_flddelegate.setNewClient(url, false);
	}

	protected void setNewClient(URL url, boolean flag) throws IOException {
		_flddelegate.setNewClient(url, flag);
	}
	@Override
	public Permission getPermission() throws IOException {
		return _flddelegate.getPermission();
	}
	@Override
	public Certificate[] getLocalCertificates() {
		return _flddelegate.getLocalCertificates();
	}
	@Override
	public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
		return _flddelegate.getServerCertificates();
	}
	@Override @SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getHeaderFields() {
		return _flddelegate.getHeaderFields();
	}

	@Override @SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getRequestProperties() {
		return _flddelegate.getRequestProperties();
	}

	public X509Certificate[] getServerCertificateChain() {
		try {
			return _flddelegate.getServerCertificateChain();
		} catch (SSLPeerUnverifiedException ex) {
			ex.printStackTrace(System.out);
		}
		return null;
	}
	@Override @SuppressWarnings("rawtypes")
	public Object getContent(Class aclass[]) throws IOException {
		return _flddelegate.getContent(aclass);
	}
	@Override
	public String getHeaderField(String s) {
		return _flddelegate.getHeaderField(s);
	}
	@Override
	public String getRequestProperty(String s) {
		return _flddelegate.getRequestProperty(s);
	}
	@Override
	public void addRequestProperty(String s, String s1) {
		_flddelegate.addRequestProperty(s, s1);
	}
	@Override
	public void setRequestProperty(String s, String s1) {
		_flddelegate.setRequestProperty(s, s1);
	}

	protected void setProxiedClient(URL url, String s, int i) throws IOException {
		_flddelegate.setProxiedClient(url, s, i);
	}

	protected void setProxiedClient(URL url, String s, int i, boolean flag) throws IOException {
		_flddelegate.setProxiedClient(url, s, i, flag);
	}
	HttpsURLConnectionImpl(URL url, Handler handler) throws IOException {
		super(url);
		_flddelegate = new DelegateHttpsURLConnection(super.url, handler, this);
	}
}
