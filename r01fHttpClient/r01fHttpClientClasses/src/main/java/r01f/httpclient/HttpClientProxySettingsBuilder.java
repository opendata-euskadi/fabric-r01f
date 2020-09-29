package r01f.httpclient;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;
import r01f.types.url.Host;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpClientProxySettingsBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONFIG LOAD
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpClientProxySettings loadFromProperties(final XMLPropertiesForAppComponent props,
															 final String baseXPath) {
		boolean enabled = props.propertyAt(baseXPath + "/@enabled")
							   .asBoolean(true);
		Host proxyHost = props.propertyAt(baseXPath + "/host")
							   		  .asHost();
		UserCode userCode = props.propertyAt(baseXPath + "/user")
								 .asUserCode();
		Password password = props.propertyAt(baseXPath + "/password")
								 .asPassword();
		
		HttpClientProxySettings outProxySettings = null;
		if (proxyHost == null || userCode == null || password == null) {
			log.warn("Proxy info is NOT propertly configured at {}: there's no host, user or password info!",
					 baseXPath);
		} 
		else {
			outProxySettings = new HttpClientProxySettings(proxyHost,
														   userCode,password,
														   enabled);
		}
		return outProxySettings;
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
		final boolean directInetConx = HttpClientProxySettingsBuilder.testDirectInternetConnection();
		if (directInetConx) {
			log.warn("It seems that there's direct internet connection (no proxy)");
			return null;	// no proxy settings
		}

		log.warn("It seems that there's NO internet connection... try with proxy");
		HttpClientProxySettings proxySettings = HttpClientProxySettingsBuilder.loadFromProperties(props,
																								  propsRootNode + "/proxySettings");
		if (proxySettings == null) {
			log.error("It seems that there's NO direct internet connection; tried using a proxy BUT no config found at {} in {} properties file",
					  propsRootNode + "/proxy",props.getAppCode());
			// create a fake proxy settings
			proxySettings = new HttpClientProxySettings(Host.forId("proxyhost"),800,
														UserCode.forId("proxyUser"),Password.forId("proxyPasswd"),
														false);		// not enabled!
		}
		// Try proxy
		if (proxySettings.getProxyHost() == null 
		 || proxySettings.getUser() == null 
		 || proxySettings.getPassword() == null) throw new IllegalStateException(Throwables.message("Cannot try internet connection through proxy since there's NO enough info at {} in {} properties file",
																								    propsRootNode + "/proxy",props.getAppCode()));
		boolean inetConxThroughProxy = HttpClientProxySettingsBuilder.testProxyInternetConnection(proxySettings,
																		 	  					  true);		// ignore proxySettings enabled state
		if (inetConxThroughProxy
		 && !proxySettings.isEnabled()) {
			log.warn("A proxy ({}:{}) is configured BUT not enabled at {} in {} properties file and it seems there's internet connection through it... overriding the enabled state of the config",
					 proxySettings.getProxyHost(),proxySettings.getProxyPort(),
					 propsRootNode + "/proxySettings",props.getAppCode());
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
		} catch (final IOException ioEx) {
			// ignore
			log.warn("There's NO direct internet connection (without proxy!)");
		} catch (final Throwable thEx) {
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
		return HttpClientProxySettingsBuilder.testProxyInternetConnection(proxySettings,
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
			return HttpClientProxySettingsBuilder.testDirectInternetConnection();		// no proxy
		} else if (!proxySettings.isEnabled() && !forceEnabled) {
			return HttpClientProxySettingsBuilder.testDirectInternetConnection();		// no proxy
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
		} catch (final IOException ioEx) {
			// ignore
			log.warn("There's NO internet connection using proxy: {}:{}",
					 proxySettings.getProxyHost(),proxySettings.getProxyPort());
		} catch (final Throwable thEx) {
			// ignore
			log.warn("There's NO internet connection using proxy: {}:{}",
					 proxySettings.getProxyHost(),proxySettings.getProxyPort());
		}
		return outConnection;
	}
}
