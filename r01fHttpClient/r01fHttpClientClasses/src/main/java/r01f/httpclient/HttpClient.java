package r01f.httpclient;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.types.url.Host;
import r01f.types.url.Url;


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
}
