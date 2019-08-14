package r01f.httpclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.exceptions.Throwables;
import r01f.types.url.Host;
import r01f.types.url.Url;
import r01f.xmlproperties.XMLPropertiesForAppComponent;


/**
 * HttpClient in a fluent-API way
 *
 * IMPORTANT DEBUG TRICK: For an HTTPS connection the vm param -Djavax.net.debug=all can be used to debug
 *
 * Sample usage:
 * <pre class='brush:java'>
 * 		String response = HttpClient.forUrl("http://www.euskadi.net")
 * 										.withParameters(params)
 * 										.GET()
 * 										.usingProxy("intercon","8080","user","password")
 * 										.getConnection().getResponseMessage();
 *
 *  	InputStream is = HttpClient.forUrl("http://www.euskadi.net")
 * 										.withParameters(params)
 * 										.GET()
 * 										.usingProxy("intercon","8080","user","password")
 * 										.getConnection().getInputStream();
 * </pre>
 *
 * NOTE:
 * To debug HttpClient with Fiddler:
 * <ul>
 * 		<li>Install Fiddler</li>
 * 		<li>Force the request through fiddler proxy by setting:
 * 			System.setProperty("http.proxyHost", "localhost");
 *			System.setProperty("http.proxyPort", "8888");</li>
 * </ul>
 *
 *
 */
@Slf4j
public abstract class HttpClient {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor from an URL in serialized format to the destination
	 * @param newUrl destination url
	 * @throws MalformedURLException if the newURLStr is not a valid url
	 */
	public static HttpClientRequestBuilderMethodStep forUrl(final Url newUrl) throws MalformedURLException {
		return new HttpClientRequestBuilderMethodStep(newUrl);
	}
	/**
	 * Constructor from an URL in string format to the destination
	 * @param newURLStr destination url
	 * @throws MalformedURLException if the newURLStr is not a valid url
	 */
	public static HttpClientRequestBuilderMethodStep forHost(final Host host) throws MalformedURLException {
		return HttpClient.forUrl(host.asUrl());
	}
	/**
	 * Constructor from an URL in string format to the destination
	 * @param newURLStr destination url
	 * @throws MalformedURLException if the newURLStr is not a valid url
	 */
	public static HttpClientRequestBuilderMethodStep forUrl(final String newURLStr) throws MalformedURLException {
		return HttpClient.forUrl(Url.from(newURLStr));
	}
	/**
	 * Constructor from an URL to the destination
	 * @param newTargetURL destination url
	 * @throws MalformedURLException if newTargetURL is not a valid url
	 */
	public static HttpClientRequestBuilderMethodStep forUrl(final URL newTargetURL) throws MalformedURLException {
		return HttpClient.forUrl(newTargetURL.toExternalForm());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
public static enum RequestMethod
   implements EnumExtended<RequestMethod> {
		GET,
		POST,
		POST_FORM_URL_ENCODED,
		PUT,
		PUT_FORM_URL_ENCODED,
		DELETE,
		HEAD;

		private static EnumExtendedWrapper<RequestMethod> _wrap = new EnumExtendedWrapper<RequestMethod>(RequestMethod.values());

		@Override
		public boolean isIn(final RequestMethod... els) {
			return _wrap.isIn(this,els);
		}
		@Override
		public boolean is(final RequestMethod el) {
			return _wrap.is(this,el);
		}
		public boolean hasPayload() {
			return this.isIn(POST,POST_FORM_URL_ENCODED,
							 PUT,PUT_FORM_URL_ENCODED);
		}
		public boolean isPOST() {
			return this.isIn(POST,POST_FORM_URL_ENCODED);
		}
		public boolean isPUT() {
			return this.isIn(PUT,PUT_FORM_URL_ENCODED);
		}
		public boolean isGET() {
			return this.is(GET);
		}
		public boolean isHEAD() {
			return this.is(HEAD);
		}
		public boolean isDELETE() {
			return this.is(DELETE);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TEST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guesses the proxy settings trying to test if a proxy is needed to connect to the internet
	 * ... and if so, trying to load the proxy settings from a provided properties file like:
	 * <pre class='brush:java'>
	 * 		<propsRootNode>
	 * 			...
	 *			<proxy enabled='true'>
	 *				<host>proxyhost:port</host>
	 *				<user>user</user>
	 *				<password>password</password>
	 *			</proxy>
	 *			...
	 *		</propsRootNode>
	 * </pre>
	 * @param appCode
	 * @param props
	 * @param propsRootNode
	 * @return
	 */
	public static HttpClientProxySettings guessProxySettings(final XMLPropertiesForAppComponent props,
											 		  		 final String propsRootNode) {
		// Test proxy connection to see if proxy is needed
		final boolean directInetConx = HttpClient.testDirectInternetConnection();
		if (directInetConx) {
			log.warn("It seems that there's direct internet connection (no proxy)");
			return null;	// no proxy settings
		}

		log.warn("It seems that there's NO internet connection... try with proxy");
		HttpClientProxySettings proxySettings = HttpClientProxySettingsBuilder.loadFromProperties(props,
																								  propsRootNode + "/proxy");
		if (proxySettings == null) {
			throw new IllegalStateException(Throwables.message("It seems that there's NO direct internet connection; tried using a proxy BUT no config found at {} in {} properties file",
															   propsRootNode + "/proxy",props.getAppCode()));
		}

		// Try proxy
		if (proxySettings.getProxyHost() == null || proxySettings.getUser() == null || proxySettings.getPassword() == null) throw new IllegalStateException(Throwables.message("Cannot try internet connection through proxy since there's NO enough info at {} in {} properties file",
																																											   propsRootNode + "/proxy",props.getAppCode()));
		final boolean inetConxThroughProxy = HttpClient.testProxyInternetConnection(proxySettings,
																		 	  true);		// ignore proxySettings enabled state
		if (inetConxThroughProxy
		 && !proxySettings.isEnabled()) {
			log.warn("A proxy ({}:{}) is configured BUT not enabled at {} in {} properties file and it seems there's internet connection through it... overriding the enabled state of the config",
					 proxySettings.getProxyHost(),proxySettings.getProxyPort(),
					 propsRootNode + "/proxy",props.getAppCode());
			proxySettings = new HttpClientProxySettings(proxySettings,
														true);
		}
		if (!inetConxThroughProxy) {
			throw new IllegalStateException(Throwables.message("It seems that there's NO internet connection; tried direct connection and through a proxy at {}:{} BUT it both failed",
															   proxySettings.getProxyHost(),proxySettings.getProxyPort()));
		}
		return proxySettings;
	}
	/**
	 * Test the internet connection without proxy
	 * @return
	 */
	public static boolean testDirectInternetConnection() {
		boolean outConnection = false;
		try {
			final int responseCode = HttpClient.forUrl("http://www.google.com")
					  					 .HEAD()
					  					 .getConnection()
					  					 .notUsingProxy().withoutTimeOut().noAuth()
					  					 .getResponseCode();
			if (HttpResponseCode.of(responseCode).is(HttpResponseCode.OK)) {
				log.info("There's internet connection without proxy!");
				outConnection = true;
			}
		} catch(final IOException ioEx) {
			// ignore
			log.warn("There's NO direct internet connection (without proxy!)");
		} catch(final Throwable thEx) {
			// ignore
			log.warn("There's NO direct (internet connection without proxy!)");
		}
		return outConnection;
	}
	/**
	 * Test the internet connection with proxy
	 * @param proxySettings
	 * @return
	 */
	public static boolean testProxyInternetConnection(final HttpClientProxySettings proxySettings) {
		return HttpClient.testProxyInternetConnection(proxySettings,
												 false);	// do not force enable proxy
	}
	/**
	 * Test the internet connection with proxy
	 * @param proxySettings
	 * @param force enables the proxy even if it's disabled at proxySettings
	 * @return
	 */
	public static boolean testProxyInternetConnection(final HttpClientProxySettings proxySettings,
												 	  final boolean forceEnabled) {
		// no proxy?
		if (proxySettings == null) {
			return HttpClient.testDirectInternetConnection();		// no proxy
		} else if (!proxySettings.isEnabled() && !forceEnabled) {
			return HttpClient.testDirectInternetConnection();		// no proxy
		}
		// test with proxy
		boolean outConnection = false;
		try {
			final int responseCode = HttpClient.forUrl("http://www.google.com")
					  					 .HEAD()
					  					 .getConnection()
					  					 .usingProxy(proxySettings).withoutTimeOut().noAuth()
					  					 .getResponseCode();
			if (HttpResponseCode.of(responseCode).is(HttpResponseCode.OK)) {
				log.info("There's internet connection using proxy: {}:{}",
						 proxySettings.getProxyHost(),proxySettings.getProxyPort());
				outConnection = true;
			}
		} catch(final IOException ioEx) {
			// ignore
			log.warn("There's NO internet connection using proxy: {}:{}",
					 proxySettings.getProxyHost(),proxySettings.getProxyPort());
		} catch(final Throwable thEx) {
			// ignore
			log.warn("There's NO internet connection using proxy: {}:{}",
					 proxySettings.getProxyHost(),proxySettings.getProxyPort());
		}
		return outConnection;
	}
}
