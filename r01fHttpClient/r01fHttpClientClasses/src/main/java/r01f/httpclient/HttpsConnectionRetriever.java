package r01f.httpclient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import lombok.extern.slf4j.Slf4j;
import r01f.types.url.Url;

/**
 * Type in charge of retrieving a securs server connection (HTTPS)
 */
@Slf4j
public class HttpsConnectionRetriever
     extends ConnectionRetrieverBase {
///////////////////////////////////////////////////////////////////////////////
// MIEMBROS
///////////////////////////////////////////////////////////////////////////////
	private boolean _streamHandlerInitialized = false;   // is the stream handler set??

///////////////////////////////////////////////////////////////////////////////
// CONSTANTES
///////////////////////////////////////////////////////////////////////////////
	// Different HTTPS imps
	private static String  _httpsDefaultConnectionClass ="r01f.httpclient.jsse.handler.HttpsURLConnectionImpl";
	//private static String  _httpsDefaultConnectionClass ="javax.net.ssl.HttpsURLConnection";
	private static String  _httpsSunConnectionClass = "com.sun.net.ssl.HttpsURLConnection";
	private static String  _httpsIBMConnectionClass = "com.ibm.net.ssl.HttpsURLConnection";

	// Different HTTPS StreamHandlers
	private static String  _defaultURLStreamHandler = "r01f.httpclient.jsse.handler.Handler";
	//private static String  _sunURLStreamHandler = "com.sun.net.ssl.internal.www.protocol.https.Handler";
	//private static String  _ibmURLStreamHandler = "com.ibm.net.ssl.internal.www.protocol.https.Handler";
	//private static String  _ibmURLStreamHandler = "com.ibm.net.ssl.www2.protocol.https.Handler";



///////////////////////////////////////////////////////////////////////////////
// METODOS
///////////////////////////////////////////////////////////////////////////////
	@Override
	public HttpURLConnection _retrieveConnection(final Url url,
												 final HttpClientProxySettings proxySettings) throws IOException {
		log.debug("..._retrieveConnection for url {}", url);
		URLStreamHandler streamHandler = _getURLStreamHandler();
		log.debug("...URLStreamHandler is  handled by {}", streamHandler.getClass().getName());

		URL theURL = new URL(null,
							 url.asStringUrlEncodingQueryStringParamsValues(),
							 streamHandler);
		theURL = new URL(theURL,theURL.toExternalForm(),streamHandler);	// Wrap to JSEE Handler (IBM or SUN) & Do HandShake */

		URLConnection conx = theURL.openConnection();

		if (proxySettings != null && proxySettings.isEnabled()) {
			log.debug("..connection must be tunneled becauses uses proxy : {}",
					  proxySettings.debugInfo());
			// Get the connexion type to guess it the url must be wrapped
			String connectionClassName = _httpsDefaultConnectionClass;

			// Call setSSLSocketFactory handing tunnelSocketFactory
			SSLSocketFactory tunnelSocketFactory = new SSLTunnelSocketFactory(proxySettings);
			_invokeSSLFactoryMethod(connectionClassName,
									conx,
									tunnelSocketFactory);
		}
		return (HttpURLConnection)conx;
	}

///////////////////////////////////////////////////////////////////////////////
// 	PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the URL Stream Handler 
	 * @return
	 * @throws IOException
	 */
	private static URLStreamHandler _getURLStreamHandler () throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();	// do NOT use Class.forName... problems at EAR
		String streamHandlerClass = _defaultURLStreamHandler;
		try {
			return (URLStreamHandler)cl.loadClass(streamHandlerClass).newInstance();
		} catch (ClassNotFoundException cnfEx) {
			throw new IOException("streamHandlerSSL type could not be found: '" + streamHandlerClass + "': " + cnfEx.getMessage(),cnfEx);
		} catch (InstantiationException instEx) {
			throw new IOException("streamHandlerSSL type instance could not be created '" + streamHandlerClass + "': " + instEx.getMessage(),instEx);
		} catch (IllegalAccessException illAccEx) {
			throw new IOException("streamHandlerSSL type illegal access '" + streamHandlerClass + "': " + illAccEx.getMessage(),illAccEx);
		}
	}
	/**
	 * calls SSLSocketFactory method
	 * @param connectionClassName
	 * @param conx
	 * @param theSocketFactory
	 * @throws IOException
	 */
	private static void _invokeSSLFactoryMethod(final String connectionClassName,
												final URLConnection conx,
												final SSLSocketFactory theSocketFactory) throws IOException {
		// Call setSSLSocketFactory 
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();	// DO NOT use Class.forName... problems with EARs
			Method setSSLSocketFactoryMethod = cl.loadClass(connectionClassName).getMethod("setSSLSocketFactory",new Class[] {SSLSocketFactory.class});
			setSSLSocketFactoryMethod.invoke(conx,new Object[] {theSocketFactory});
		} catch (ClassNotFoundException cnfEx) {
			cnfEx.printStackTrace(System.out);
			throw new IOException("getSSLMethodByClassName" + "className:" + connectionClassName + cnfEx.getMessage(),cnfEx);
		} catch (SecurityException secEx) {
			throw new IOException("getSSLMethodByClassName" + "className:" + connectionClassName + secEx.getMessage(),secEx);
		} catch (NoSuchMethodException nsmEx) {
			nsmEx.printStackTrace(System.out);
			throw new IOException("getSSLMethodByClassName" + "className:" + connectionClassName + nsmEx.getMessage(),nsmEx);
		} catch (InvocationTargetException invTgtEx) {
			invTgtEx.printStackTrace(System.out);
			throw new IOException("getSSLMethodByClassName" + "className:" + connectionClassName + invTgtEx.getMessage(),invTgtEx);
		} catch (IllegalAccessException illAccEx) {
			illAccEx.printStackTrace(System.out);
			throw new IOException("getSSLMethodByClassName" + "className:" + connectionClassName + illAccEx.getMessage(),illAccEx);
		}
	}
	@SuppressWarnings("unused")
	private static boolean _isHttpsDefaultConnectionInstance(final Class<?> classInstance) {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();	// DO NOT use Class.forName... problems with EARs
			Class<?> httpsDefaultConnectionClass = cl.loadClass(_httpsDefaultConnectionClass);
			return httpsDefaultConnectionClass.isAssignableFrom(classInstance);
		} catch (ClassNotFoundException cnfEx) {
			return false;
		}
	}
	@SuppressWarnings("unused")
	private static boolean _isSunConnectionInstance(final Class<?> classInstance) {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();	// DO NOT use Class.forName... problems with EARs
			Class<?> sunConnectionClass = cl.loadClass(_httpsSunConnectionClass);
			return sunConnectionClass.isAssignableFrom(classInstance);
		} catch (ClassNotFoundException cnfEx) {
			return false;
		}
	}
	@SuppressWarnings("unused")
	private static boolean _isIBMClassInstance(final Class<?> classInstance) {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();	// DO NOT use Class.forName... problems with EARs
			Class<?> ibmConnectionClass = cl.loadClass(_httpsIBMConnectionClass);
			return ibmConnectionClass.isAssignableFrom(classInstance);
		} catch (ClassNotFoundException cnfEx) {
			return false;
		}
	}
	/**
	 * Load SSL Stream Handler
	 */
	@SuppressWarnings("unused")
	private void _loadStreamHandler() {
		if ( !_streamHandlerInitialized ) {
			_streamHandlerInitialized = false;

			String szVendor = System.getProperty("java.vendor");
			String szVersion = System.getProperty("java.version");
			// [major].[minor].[release] (ie: 1.2.1)
			Double dVersion = new Double(szVersion.substring(0,3));

			// If Microsoft use MS stream handler
			if (-1 < szVendor.indexOf("Microsoft")) {
				try {
					ClassLoader cl = Thread.currentThread().getContextClassLoader();	// DO NOT use Class.forName... problems with EARs
					Class<?> clsFactory = cl.loadClass("com.ms.net.wininet.WininetStreamHandlerFactory");
					if ( null != clsFactory) {
						URL.setURLStreamHandlerFactory((URLStreamHandlerFactory)clsFactory.newInstance());
					}
					// If the steam handler is property initialized ensure the flag is set
					_streamHandlerInitialized = true;
				} catch (ClassNotFoundException cfe ) {
					throw new RuntimeException("Microsoft SSL Stream Handler could NOT be loaded: check that com.ms.net.wininet.WininetStreamHandlerFactory is classpath accesible: "  + cfe.toString());
				} catch (InstantiationException instEx ) {
					throw new RuntimeException("Could NOT create an instance of Microsoft SSL Stream Handler: " + instEx.toString() );
				} catch (IllegalAccessException illAccEx ) {
					throw new RuntimeException("Illegal access to Microsoft SSL Stream Handler: " + illAccEx.toString() );
				} catch (Exception ex ) {
					throw new RuntimeException("Unknown error loading Microsoft SSL StreamHandler: " + ex.toString() );
				}
			} else if ( 1.2 <= dVersion.doubleValue() ) {
				// Registers a SSL protocol handler
				// 		Usually this part is NOT necessary since this is done by the app server (ie: weblogic)
				//		if the property [weblogic.security.ssl.enable] is true: weblogic.security.ssl.enable=true 
				// This simply includes weblogic.net at the [java.protocol.handler.pkgs] system property

				final String JSSE_HANDLER = "com.sun.net.ssl.internal.www.protocol";
				final String WLS_HANDLER = "weblogic.net";

				// Detect weblogic or jsse
				String handler = (System.getProperty("weblogic.class.path") != null) ? WLS_HANDLER : JSSE_HANDLER;

				Properties sysProps = System.getProperties();
				String handlerValue = sysProps.getProperty("java.protocol.handler.pkgs");
				// Check if the handler was set
				if (handlerValue == null) {
					handlerValue = handler;
				} else if (handlerValue.indexOf(handler) == -1) {
					handlerValue += ("|" + handler);
				}
				sysProps.put("java.protocol.handler.pkgs", handlerValue);
				System.setProperties(sysProps);

				// If J2EE provider is available and it was NOT set, do it now and add it as provider
				if (handler.equals(WLS_HANDLER)) return;    // there's NO need to register the provider
				try {
					ClassLoader cl = Thread.currentThread().getContextClassLoader();				// DO NOT use Class.forName... problems with EARs
					Class<?> clsFactory = cl.loadClass("com.sun.net.ssl.internal.ssl.Provider");
					if ( (null != clsFactory) && (null == Security.getProvider("SunJSSE")) ) {
						Security.addProvider((Provider)clsFactory.newInstance());
					}
					// If the steam handler is property initialized ensure the flag is set
					_streamHandlerInitialized = true;
				} catch (ClassNotFoundException cfe ) {
					throw new RuntimeException("Could NOT load J2SE SSL Stream Handler, check that com.sun.net.ssl.internal.ssl.Provider is classpath accesible: "  + cfe.getMessage() );
				} catch (InstantiationException instEx ) {
					throw new RuntimeException("Could NOT create an instance of J2EE SSL Stream Handler: " + instEx.getMessage() );
				} catch (IllegalAccessException illAccEx ) {
					throw new RuntimeException("Illegal access to J2SE SSL Stream Handler: " + illAccEx.getMessage() );
				} catch (Exception ex ) {
					throw new RuntimeException("Unknown error loading J2SE SSL Stream Handler: " + ex.getMessage() );
				}
			}
		}
	}
}
