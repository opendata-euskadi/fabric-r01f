package r01f.httpclient;

import java.io.IOException;
import java.net.HttpURLConnection;

import r01f.concurrent.TimeOutController;
import r01f.types.url.Url;

abstract class ConnectionRetrieverBase {

///////////////////////////////////////////////////////////////////////////////
// PUBLIC INTERFACE
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a connection
	 * IMPORTANT!:
	 * The real work is delegated to the abstract _retrieveConnection method which must be implemented by
	 * concrete types:
	 * <ul>
	 * 		<li>{@link HttpConnectionRetriever}</li>
	 * 		<li>{@link HttpsConnectionRetriever}</li>
	 * </ul>
	 * 
	 * NOTE: To debug : -Djavax.net.debug=all
	 * 
	 * @param url url to connect with
	 * @param proxySettings the proxy settings
	 * @param timeout timeout to get the connection (this is not the timeout to get the response)
	 * @return the connection
	 * @throws IOException if a connection could not be retrieved
	 */
	public HttpURLConnection getConnection(final Url url,
										   final HttpClientProxySettings proxySettings,
										   final long timeout) throws IOException {
		HttpURLConnection outConx = null;
		if (timeout < 0) {
			outConx = _retrieveConnection(url,
										  proxySettings);
		} else {
			try {
				ObtainConnectionTask task = new ObtainConnectionTask() {
					@Override
					public void doit() throws IOException {
						this.conx = _retrieveConnection(url,
														proxySettings);
					}
				};
				TimeOutController.execute(task,timeout);
				outConx = task.conx;
				if (task.ioException != null) {
					throw task.ioException;
				}
			} catch (TimeOutController.TimeoutException timeOutEx) {
				throw new IOException("No se ha podido obtener la conexin con el host '" + url + "' en el tiempo especificado: " + timeout + " millis");
			}
		}
		return outConx;
	}
	
///////////////////////////////////////////////////////////////////////////////
//	ABSTRACT METHODS
///////////////////////////////////////////////////////////////////////////////
	/** 
	 * The real method where the the connection is getted
	 * This is implemented by concrete types:
	 * <ul>
	 * 		<li>{@link HttpConnectionRetriever}</li>
	 * 		<li>{@link HttpsConnectionRetriever}</li>
	 * </ul>
	 * 
	 * @param url url to connect with
	 * @param proxySettings
	 * @return the connection
	 * @throws IOException if a connection could not be retrieved
	 */
	public abstract HttpURLConnection _retrieveConnection(final Url url,
														  final HttpClientProxySettings proxySettings) throws IOException;

///////////////////////////////////////////////////////////////////////////////
// AUX TYPE TO HANDLE WITH CONNECTION TIMEOUT
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Aux type that encloses the connection timeout
	 */
	abstract class ObtainConnectionTask 
	    implements Runnable {
		
		public java.net.HttpURLConnection conx;
		public IOException ioException;
		/**
		 * Runs whatever
		 * @throws IOException if an error occurs..
		 */
		public abstract void doit() throws IOException;
		@Override
		public void run() {
			try {
				this.doit();		// Normally here a connection is retrieved
			} catch (IOException ioEx) {
				ioException = ioEx;
			}
		}
	}
	
}
