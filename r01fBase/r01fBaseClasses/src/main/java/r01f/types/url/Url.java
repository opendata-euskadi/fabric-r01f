package r01f.types.url;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.url.UrlProtocol.StandardUrlProtocol;
import r01f.util.types.StringConverter.StringConverterFilter;
import r01f.util.types.StringConverterWrapper;
import r01f.util.types.StringCustomizeUtils;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * Encapsulates an url as a {@link String} 
 * It's used to store the URLs as a {@link String} at an XML
 */
@MarshallType(as="url")
@Accessors(prefix="_")
public class Url 
  implements CanBeRepresentedAsString,
  			 Serializable,
  			 Debuggable {

	private static final long serialVersionUID = 5383405611707444269L;
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final String _url;	
			
			private final transient UrlParser _urlParser;	// not gwt-compatible BUT emulated (see [r01fbGWTClasses])
	
	private static UrlParser _createUrlParserFor(final String  url) {
		return new UrlParserRegExpBased(url);	// GWT see emulation at [r01fbGWTClasses])
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Url(final Url other) {
		_url = other.getUrl();
		_urlParser = _createUrlParserFor(_url);
	}
	public Url(final String url) {
		_url = url;
		_urlParser = _createUrlParserFor(_url);
	}
	public Url(final UrlComponents components) {
		_url = _asString(components,
						 false);		// do not encode
		_urlParser = _createUrlParserFor(_url);
	}
	public Url(final UrlPath path) {		
		_url = path.asAbsoluteString();
		_urlParser = _createUrlParserFor(_url);
	}
	public Url(final Host host) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
		     host.getUrlComponents().getHost(),
		     host.getUrlComponents().getPortOrDefault(StandardUrlProtocol.HTTP.getDefaultPort()));
	}
	public Url(final Host host,
			   final UrlPath urlPath) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 host.getUrlComponents().getPortOrDefault(StandardUrlProtocol.HTTP.getDefaultPort()),
			 urlPath);
	}
	public Url(final Host host,
			   final UrlPath urlPath,
			   final UrlQueryString queryString) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 host.getUrlComponents().getPortOrDefault(StandardUrlProtocol.HTTP.getDefaultPort()),
			 urlPath,
			 queryString);
	}
	public Url(final Host host,
			   final UrlPath urlPath,final String urlPathFragment,
			   final UrlQueryString queryString) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 host.getUrlComponents().getPortOrDefault(StandardUrlProtocol.HTTP.getDefaultPort()),
			 urlPath,urlPathFragment,
			 queryString);
	}
	@Deprecated
	public Url(final Host host,
			   final UrlPath urlPath,
			   final UrlQueryString queryString,
			   final String anchor) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 host.getUrlComponents().getPortOrDefault(StandardUrlProtocol.HTTP.getDefaultPort()),
			 urlPath,anchor,
			 queryString);
	}
	public Url(final Host host,final int port) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 port);
	}
	public Url(final Host host,final int port,
			   final UrlPath urlPath) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 port);
	}
	public Url(final Host host,final int port,
			   final UrlPath urlPath,
			   final UrlQueryString queryString) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 port,
			 urlPath,
			 queryString);
	}
	public Url(final Host host,final int port,
			   final UrlPath urlPath,final String urlPathFragment,
			   final UrlQueryString queryString) {
		this(host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 port,
			 urlPath,urlPathFragment,
			 queryString);
	}
	@Deprecated
	public Url(final Host host,final int port,
			   final UrlPath urlPath,
			   final UrlQueryString queryString,
			   final String anchor) {
		this(host,port,
			 urlPath,anchor,
			 queryString);
	}
	public Url(final UrlProtocol protocol,final Host host,final int port) {
		this(protocol != null ? protocol : host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 port,
			 null,(String)null,	// url path & fagmet
			 null);				// query string 
	}
	public Url(final UrlProtocol protocol,final Host host,final int port,
			   final UrlPath urlPath) {
		this(protocol != null ? protocol : host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 host.getUrlComponents().getHost(),
			 port,
			 urlPath,(String)null,	// null url path fragment
			 null);					// query string
	}
	public Url(final UrlProtocol protocol,final Host host,final int port,
			   final UrlPath urlPath,
			   final UrlQueryString queryString) {
		this(// protocol
			 protocol != null ? protocol 
							  : host.getUrlComponents().getProtocolOrDefault(UrlProtocol.HTTP),
			 // host
			 host != null && host.getUrlComponents() != null 
			 			? host.getUrlComponents().getHost()
			 			: host,
			 // port
			 port <= 0 && host != null && host.getUrlComponents() != null && host.getUrlComponents().getPort() >= 0
			 			? host.getUrlComponents().getPort()
			 			: port,
			 // urlPath
			 host != null && host.getUrlComponents() != null && host.getUrlComponents().getUrlPath() != null 
			 			? host.getUrlComponents().getUrlPath().joinedWith(urlPath)
					 	: urlPath,
			 // urlPath fragment
			 host != null && host.getUrlComponents() != null && host.getUrlComponents().getUrlPathFragment() != null
						? host.getUrlComponents().getUrlPathFragment()
						: null,
			 // query string
			 host != null && host.getUrlComponents() != null && host.getUrlComponents().getQueryString() != null 
			 			? host.getUrlComponents().getQueryString().joinWith(queryString)
					 	: queryString);		
	}
	@Deprecated
	public Url(final UrlProtocol protocol,final Host host,final int port,
			   final UrlPath urlPath,
			   final UrlQueryString queryString,final String anchor) {
		this(protocol,host,port,
			 urlPath,anchor,
			 queryString);
	}
	public Url(final UrlProtocol protocol,final Host host,final int port,
			   final UrlPath urlPath,final String urlPathFragment,
			   final UrlQueryString queryString) {
		// the host can be a complete url (it should NOT but...)
		// ... so we have to 'mix' some components
		UrlProtocol theProto = protocol != null 
									? protocol 
									: host != null ? host.getUrlComponents().getProtocol()
												   : UrlProtocol.HTTP;
		UrlPath theUrlPath = host != null 
						  && host.getUrlComponents() != null
						  && host.getUrlComponents().getUrlPath() != null 
									? host.getUrlComponents().getUrlPath()
															 .joinedWith(urlPath)
					 				: urlPath;
		String theUrlPathFragment = Strings.isNOTNullOrEmpty(urlPathFragment)
										? urlPathFragment
										: host != null ? host.getUrlComponents() != null ? host.getUrlComponents().getUrlPathFragment()
																						 : null
													   : null;
		UrlQueryString theUrlQueryString = host != null
									    && host.getUrlComponents() != null
									    && host.getUrlComponents().getQueryString() != null 
									    		? host.getUrlComponents().getQueryString()
					 										   			 .joinWith(queryString)
					 					   		: queryString;
		
		String theHostStr = host != null ? host.getUrlComponents().getHost().asString() : null;
		String theUrlPathStr = theUrlPath != null ? theUrlPath.asAbsoluteString() : "" ;
		String theUrlPathFragmentStr = theUrlPathFragment != null ? "#" + theUrlPathFragment : "";
		String theUrlQryStr = theUrlQueryString != null ? "?" + theUrlQueryString.asString() : "";
		if (theHostStr == null) {
			_url = Strings.customized("{}{}{}",theUrlPathStr,theUrlPathFragmentStr,theUrlQryStr);
//			_url = String.format("%s%s%s",theUrlPathStr,theUrlPathFragmentStr,theUrlQryStr);
		} else {	
			_url = Strings.customized("{}://{}:{}{}{}{}",
									  theProto != null ? theProto.asString() : StandardUrlProtocol.HTTP.getCode(),theHostStr,port,
									  theUrlPathStr,theUrlPathFragmentStr,
									  theUrlQryStr);
//			_url = String.format("%s://%s:%s%s%s%s",
//							     theProto != null ? theProto.asString() : StandardUrlProtocol.HTTP.getCode(),theHostStr,port,
//						         theUrlPathStr,theUrlPathFragmentStr,
//						         theUrlQryStr);
		}
		// do not forget to set the parser
		_urlParser = _createUrlParserFor(_url);
	}
	public Url(final UrlPath path,final UrlQueryString queryString) {
		this(null,null,0,
			 path,null,		// no url path fragment
			 queryString);
	}
	public Url(final UrlPath path,final String urlPathFragment) {
		this(null,null,0,
			 path,urlPathFragment,
			 null);
	}
	public Url(final UrlPath path,final String urlPathFragment,
			   final UrlQueryString queryString) {
		this(null,null,0,
			 path,urlPathFragment,
			 queryString);
	}
	@Deprecated
	public Url(final UrlPath path,final UrlQueryString queryString,final String anchor) {
		this(null,null,0,
			 path,anchor,
			 queryString);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static Url valueOf(final String url) {
		return Url.from(url);
	}	
	public static Url from(final Host host) {
		return host.asUrl();
	}
	public static Url from(final Host host,
						   final UrlPath urlPath) {
		return new Url(host,
					   urlPath);
	}
	public static Url from(final Host host,
						   final UrlPath urlPath,
						   final UrlQueryString queryString) {
		return new Url(host,
					   urlPath,
					   queryString);
	}
	public static Url from(final Host host,
						   final UrlPath urlPath,final String urlPathFragment,
						   final UrlQueryString queryString) {
		return new Url(host,
					   urlPath,urlPathFragment,
					   queryString);
	}
//	@Deprecated
//	public static Url from(final Host host,
//						   final UrlPath urlPath,
//						   final UrlQueryString queryString,final String anchor) {
//		return new Url(host,
//					   urlPath,anchor,
//					   queryString);
//	}
	public static Url from(final UrlComponents components) {
		String urlAsStr = _asString(components,
									false);
		return Url.from(urlAsStr);
	}
	public static Url from(final UrlProtocol protocol,final Host host,final int port) {
		return new Url(protocol,host,port);
	}
	public static Url from(final UrlProtocol protocol,final Host host,final int port,
						   final UrlPath urlPath) {
		return new Url(protocol,host,port,
					   urlPath);
	}
	public static Url from(final UrlProtocol protocol,final Host host,final int port,
						   final UrlPath urlPath,final UrlQueryString queryString) {
		return new Url(protocol,host,port,
					   urlPath,queryString);
	}
	@Deprecated
	public static Url from(final UrlProtocol protocol,final Host host,final int port,
						   final UrlPath urlPath,
						   final UrlQueryString queryString,final String urlPathFragment) {
		return new Url(protocol,host,port,
					   urlPath,urlPathFragment,
					   queryString);
	}
	public static Url from(final UrlProtocol protocol,final Host host,final int port,
						   final UrlPath urlPath,final String urlPathFragment,
						   final UrlQueryString queryString) {
		return new Url(protocol,host,port,
					   urlPath,urlPathFragment,
					   queryString);
	}
	public static Url from(final Host host,final int port) {
		return Url.from(UrlProtocol.HTTP,host,port);
	}
	public static Url from(final Host host,final int port,
						   final UrlPath urlPath) {
		return Url.from(UrlProtocol.HTTP,host,port,
						urlPath);
	}
	public static Url from(final Host host,final int port,
						   final UrlPath urlPath,final UrlQueryString queryString) {
		return Url.from(UrlProtocol.HTTP,host,port,
						urlPath,queryString);
	}
	@Deprecated
	public static Url from(final Host host,final int port,
						   final UrlPath urlPath,
						   final UrlQueryString queryString,final String urlPathFragment) {
		return Url.from(UrlProtocol.HTTP,host,port,
					    urlPath,urlPathFragment,
					    queryString);
	}
	public static Url from(final Host host,final int port,
						   final UrlPath urlPath,final String urlPathFragment,
						   final UrlQueryString queryString) {
		return Url.from(UrlProtocol.HTTP,host,port,
					    urlPath,urlPathFragment,
					    queryString);
	}
	public static Url from(final UrlPath path) {
		return Url.from(path.asAbsoluteString());
	}
	public static Url from(final UrlPath path,
						   final UrlQueryString queryString) {
		return new Url(path,queryString);
	}
	public static Url from(final UrlPath path,
						   final String anchor) {
		return new Url(path,anchor);
	}
	@Deprecated
	public static Url from(final UrlPath path,
						   final UrlQueryString queryString,final String urlPathFragment) {
		return new Url(path,urlPathFragment,
				       queryString);
	}
	public static Url from(final UrlPath path,final String urlPathFragment,
						   final UrlQueryString queryString) {
		return new Url(path,urlPathFragment,
				       queryString);
	}
	public static Url from(final Url other) {
		if (other == null) return null;
		Url outUrl = new Url(other.asString());
		return outUrl;
	}
	public static Url from(final UrlProtocol urlProtocol,
						   final Url other) {
		UrlComponents otherComps = other.getComponents();
		StandardUrlProtocol stdUrlProtocol = urlProtocol.asStandardProtocolOrNull();
		int port = otherComps.getPort() > 0 ? otherComps.getPort()
											: urlProtocol.asStandardProtocolOrNull() != null 
													? stdUrlProtocol != null ? stdUrlProtocol.getDefaultPort()
																			 : 0
													: 0;
		return Url.from(urlProtocol,otherComps.getHost(),port,
					    otherComps.getUrlPath(),otherComps.getUrlPathFragment(),
					    otherComps.getQueryString());
	}
	public static Url from(final UrlProtocol urlProtocol,
						   final String otherUrlStr) {
		Url otherUrl = Url.from(otherUrlStr);
		return urlProtocol.is(otherUrl.getProtocol()) ? otherUrl
													  : Url.from(urlProtocol,otherUrl);		// change protocol
	}
	public static Url from(final Url other,
						   final UrlPath path) {
		UrlComponents otherComps = other.getComponents();
		return Url.from(otherComps.getProtocol(),otherComps.getHost(),otherComps.getPort(),
					    otherComps.getUrlPath() != null ? otherComps.getUrlPath().joinedWith(path) : path,otherComps.getUrlPathFragment(),
					    otherComps.getQueryString());
	}
	public static Url from(final String url) {
		if (url == null) return null;
		Url outUrl = new Url(url);
		return outUrl;
	}
	public static Url from(final String url,
						   final Object... vars) {
		return Url.from(Strings.customized(url,vars));
	}
	/**
	 * Returns a NEW {@link Url} customizing the variable holders with the given values
	 * ie: 
	 * an url with placeholders like http://{site}:{port}/path/{oid}
	 * can be customized with a Map containing:
	 * 		site = mysite.com
	 * 		port = 80
	 * 		oid = myOid
	 * and the result url will be: http://mysite.com:80/path/myOid
	 * @param url
	 * @param varValues
	 * @return
	 */
	public static Url fromTemplate(final String urlTemplate,
								   final Map<String,String> varValues) {
		String urlAsStringCustomized = StringCustomizeUtils.replaceVariableValues(urlTemplate,
												   								  '{','}',
												   								  varValues);
		return Url.from(urlAsStringCustomized);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SANITIZE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sanitizes the url removing potential XSS threats
	 * It's usually used with OWASP like:
	 * <pre class='brush:java'>
	 *		protected static PolicyFactory policy = Sanitizers.FORMATTING
	 *											  		.and(Sanitizers.BLOCKS);
	 *	//										  		.and(Sanitizers.LINKS);		// do NOT escape @ character 
	 *		protected static StringConverterFilter SANITIZER_FILTER = (untrustedHtml) -> {
	 *																		String safeHtml = policy.sanitize(untrustedHtml);																					
	 *																		return safeHtml.replace("&#64;","@");		// mega-ñapa for emails
	 *																   }
	 * </pre>
	 * @param sanitizer
	 * @return
	 */
	public Url sanitizeUsing(final StringConverterFilter sanitizer) {
		// Sanitize the query string 
		UrlQueryString unsafeQryStr = this.getQueryString();
		UrlQueryString safeQryStr = unsafeQryStr != null ? unsafeQryStr.sanitizeUsing(sanitizer)
														 : null;
		// anchor sanitized
		String unsafeAnchor = this.getAnchor();
		String safeAnchor = Strings.isNOTNullOrEmpty(unsafeAnchor) ? sanitizer.filter(unsafeAnchor)
																   : null;
		// clone!
		return Url.from(this.getProtocol(),this.getHost(),this.getPort(),
						this.getUrlPath(),
						safeQryStr,
						safeAnchor);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ADD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a NEW {@link Url} object joining this one with then given url path
	 * @param urlPath
	 * @return
	 */
	public Url joinWith(final UrlPath urlPath) {
		return Urls.join(this,urlPath);
	}
	/**
	 * Returns a NEW {@link Url} object joining this one with then given url query string
	 * @param qryString
	 * @return
	 */
	public Url joinWith(final UrlQueryString qryString) {
		return Urls.join(this,qryString);
	}
	/**
	 * Returns a NEW {@link Url} object joining this one with then given url query string
	 * @param urlPath
	 * @param qryString
	 * @return
	 */
	public Url joinWith(final UrlPath urlPath,
						final UrlQueryString qryString) {
		return Urls.join(this,urlPath,qryString);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CLONE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @GwtIncompatible
	protected Object clone() throws CloneNotSupportedException {
		return Url.from(_url.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  URL COMPONENTS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlComponents getComponents() {
		return _urlParser.getComponents();
	}
	public UrlProtocol getProtocol() {
		return this.getComponents().getProtocol();
	}
	public UrlProtocol getProtocolOrDefault(final UrlProtocol def) {
		UrlProtocol outProto = this.getProtocol();
		return outProto != null ? outProto : def;
	}
	public Host getHost() {
		return this.getComponents().getHost();
	}
	public Host getHostOrDefault(final Host def) {
		Host outHost = this.getHost();
		return outHost != null ? outHost : def;
	}
	public int getPort() {
		return this.getComponents().getPort();
	}
	public int getPortOrDefault(final int def) {
		int outPort = this.getPort();
		return outPort >= 0 ? outPort : def;
	}
	public UrlPath getUrlPath() {
		return this.getComponents().getUrlPath();
	}
	public UrlPath getUrlPathOfDefault(final UrlPath def) {
		UrlPath outUrlPath = this.getUrlPath();
		return outUrlPath != null ? outUrlPath : def;
	}
	public UrlQueryString getQueryString() {
		return this.getComponents().getQueryString();
	}
	public UrlQueryString getQueryStringOrDefault(final UrlQueryString def) {
		UrlQueryString outQryString = this.getQueryString();
		return outQryString != null ? outQryString : def;
	}
	public Set<UrlQueryStringParam> getQueryStringParams() {
		return this.getQueryString() != null ? this.getQueryString().getQueryStringParams()
											 : null;
	}
	/**
	 * Checks if the url's query string contains a param with a provided name
	 * @param name
	 * @return
	 */
	public boolean containsQueryStringParam(final String name) {
		return this.getQueryStringParamValue(name) != null;
	}
	/**
	 * Checks if the url's query string contains a param with a provided name
	 * @param name
	 * @return
	 */
	public StringConverterWrapper getQueryStringParamValue(final String name) {
		String outValue = null;
		Set<UrlQueryStringParam> queryStringParams = this.getQueryStringParams();
		if (CollectionUtils.hasData(queryStringParams)) {
			for (UrlQueryStringParam param : queryStringParams) {
				if (param.getName().equals(name)) {
					outValue = param.getValue();
					break;
				}
			}
		}
		return outValue != null ? new StringConverterWrapper(outValue)
								: new StringConverterWrapper(null);
	}
	@Deprecated
	public String getAnchor() {
		return this.getUrlPathFragment();
	}
	public String getUrlPathFragment() {
		return this.getComponents().getUrlPathFragment();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return an object of type {@link URL} from the string representing the url
	 * @return
	 * @throws MalformedURLException
	 */
	@GwtIncompatible
	public URL asUrl() throws MalformedURLException {
		return new URL(_url.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  asString
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient Memoized<String> _asStringEncodingQryStr = new Memoized<String>() {
																				@Override
																				public String supply() {
																					UrlComponents comps = Url.this.getComponents();
																					return _asString(comps,
																									 true);		// encode param values
																				}
																		};
	private final transient Memoized<String> _asStringNotEncodingQryStr = new Memoized<String>() {
																				@Override
																				public String supply() {
																					UrlComponents comps = Url.this.getComponents();
																					return _asString(comps,
																									 false);	// do not encode param values
																				}
																		  };
	/**
	 * Returns the url obtained from it's components as {@link String} url-encoding the query string param values as
	 * @param urlComps
	 * @param encodeQueryStringParams
	 * @return
	 */
	private static String _asString(final UrlComponents urlComps,
								    final boolean encodeQueryStringParams) {
		return _asString(urlComps,
						 encodeQueryStringParams,
						 null);		// no default url protocol
	}
	/**
	 * Returns the url obtained from it's components as {@link String} url-encoding the query string param values as
	 * @param urlComps
	 * @param encodeQueryStringParams
	 * @param defaultUrlProtocol the default protocol to be used if none can be set
	 * @return
	 */
	private static String _asString(final UrlComponents urlComps,
								    final boolean encodeQueryStringParams,
								    final StandardUrlProtocol defaultUrlProtocol) {
		StringBuilder sb = new StringBuilder(200);
		
		if (urlComps.getProtocol() != null
		 && urlComps.getProtocol().is(StandardUrlProtocol.FILE)) {
			// file urls
			sb.append(urlComps.getProtocol().asString()).append("://").append(urlComps.getUrlPath().asRelativeString());
		} else {
			// usual http or https urls
			if (urlComps.getHost() != null) {
				if (urlComps.getProtocol() != null) {
					sb.append(urlComps.getProtocol().asString()).append("://").append(urlComps.getHost());
					if (urlComps.getProtocol().isStandard()
					 && urlComps.hasPort()
					 && urlComps.getProtocol().asStandardProtocolOrNull().isNOTDefaultPort(urlComps.getPort())) {
						sb.append(":").append(urlComps.getPort());
					}
				} else if (urlComps.getPort() == StandardUrlProtocol.HTTP.getDefaultPort()) {
					sb.append("http://").append(urlComps.getHost());
				} else if (urlComps.getPort() == StandardUrlProtocol.HTTPS.getDefaultPort()) {
					sb.append("https://").append(urlComps.getHost());
				} else if (urlComps.getPort() == StandardUrlProtocol.HTTPS_CLI.getDefaultPort()) {
					sb.append("https://").append(urlComps.getHost()).append(":").append(StandardUrlProtocol.HTTPS_CLI.getDefaultPort());
				} else if (urlComps.getPort() > 0) {
					// the protocol cannot be guessed
					sb.append(urlComps.getHost()).append(":").append(urlComps.getPort());
				} else if (defaultUrlProtocol != null) {
					// use the given default url protocol
					sb.append(defaultUrlProtocol.getCode()).append("://").append(urlComps.getHost());
				} else {
					// the protocol cannot be guessed
					sb.append(urlComps.getHost());
				}
			}
			sb.append(urlComps.getUrlPath() != null ? urlComps.getUrlPath().asAbsoluteString() 
													: "");
			if (urlComps.getQueryString() != null) {
				sb.append("?").append(urlComps.getQueryString().asString(encodeQueryStringParams));
			}
			if (Strings.isNOTNullOrEmpty(urlComps.getUrlPathFragment())) sb.append("#").append(urlComps.getUrlPathFragment());
		}
		return sb.toString();
	}
	/**
	 * Returns the url as {@link String} url-encoding the query string param values as 
	 * specified by the param
	 * @param encodeQueryStringParams
	 * @return
	 */
	public String asString(final boolean encodeQueryStringParams) {
		return encodeQueryStringParams ? _asStringEncodingQryStr.get()
									   : _asStringNotEncodingQryStr.get();
	}
	@Override
	public String asString() {
		return this.asStringNotUrlEncodingQueryStringParamsValues();
	}
	/**
	 * Returns the url as a {@link String} url-encoding the query string param values 
	 * @return
	 */
	public String asStringUrlEncodingQueryStringParamsValues() {
		return this.asString(true);
	}
	/**
	 * Returns the url as a {@link String} NOT encoding the query string param values 
	 * @return
	 */
	public String asStringNotUrlEncodingQueryStringParamsValues() {
		return this.asString(false);
	}
	/**
	 * Returns the url as {@link String} NORMALIZED: the query string params (if present)
	 * are ORDERED by param name, the url protocol is set
	 * BEWARE!!	The computation is NOT memoized ({@link Url}{@link #asString()} is MEMOIZED 
	 * 			so it's NOT computed every time it's called)
	 * @param encodeQueryStringParams
	 * @param defUrlProtocol
	 * @return
	 */
	public String asStringNormalized(final boolean encodeQueryStringParams,
									 final StandardUrlProtocol defUrlProtocol) {
		return _asString(this.getComponents(),
						 encodeQueryStringParams,
						 defUrlProtocol);
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final Url other) {
		return this.equals(other);
	}
	/**
	 * Use this method when one of the Urls whose equality is to be compared do NOT have protocol
	 * ie: compare www.euskadi.eus and http://www.euskadi.eus
	 * @param other
	 * @param defUrlProtocol
	 * @return
	 */
	public boolean is(final Url other,
					  final StandardUrlProtocol defUrlProtocol) {
		UrlComponents thisComps = this.getComponents();
		String thisAsString = _asString(thisComps,
						 			    false,				// do NOT encode param values
						 			    defUrlProtocol);	// use this protocol is none is set
		UrlComponents otherComps = other.getComponents();
		String otherAsString = _asString(otherComps,
						 			     false,				// do NOT encode param values
						 			     defUrlProtocol);	// use this protocol is none is set
		return thisAsString.equals(otherAsString);
	}
	public boolean isNot(final Url other) {
		return !this.is(other);
	}
	@Override 
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (this == other) return true;
		if (other instanceof Url) {
			return this.asString().equals(((Url) other).asString());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return _url.toString().hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUGGALE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String debugInfo() {
		StringBuffer sb = new StringBuffer(200);
		UrlComponents urlComps = this.getComponents();
		sb.append("URL: ").append(this.asStringNotUrlEncodingQueryStringParamsValues()).append("\r\n")
		  .append(urlComps.debugInfo());
		return sb.toString();
	}
}
