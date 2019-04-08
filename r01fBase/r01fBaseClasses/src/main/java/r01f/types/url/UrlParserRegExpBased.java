package r01f.types.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import r01f.exceptions.Throwables;
import r01f.types.url.UrlProtocol.StandardUrlProtocol;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

public class UrlParserRegExpBased
	 extends UrlParserBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlParserRegExpBased(final String url) {
		super(url);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	// see http://mathiasbynens.be/demo/url-regex
	private static final transient String PROTOCOL_REGEX = "(?:([a-zA-Z0-9]+)://)?";		// (?:(.+?)://)?
	private static final transient String SITE_REGEX = "([\\w\\.\\d-]*)";
	private static final transient String PORT_REGEX = "(?::(\\d+))?";
	private static final transient String PATH_REGEX = "([^?#]*)";
	private static final transient String QUERY_REGEX = "(?:\\?([^#]*))?";
	private static final transient String ANCHOR_REGEX = "(?:#(.*))?";

	private static final transient Pattern FILE_URL_PATTERN = Pattern.compile("^file://(.+)$");
	private static final transient Pattern FULL_URL_PATTERN = Pattern.compile("^" + PROTOCOL_REGEX + SITE_REGEX + PORT_REGEX + "/*" + PATH_REGEX + QUERY_REGEX + ANCHOR_REGEX + "$");
	private static final transient Pattern PATH_URL_PATTERN = Pattern.compile("^" + PATH_REGEX + QUERY_REGEX + ANCHOR_REGEX + "$");
/////////////////////////////////////////////////////////////////////////////////////////
// 	PRIVATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	UrlComponents _parse(final String theUrl) {
		UrlComponents outUrlComponents = null;
		// It's NOT known if the url includes host or not, that's to say, it's NOT known if the url is like:
		//		site/path?params
		// or simply:
		//		path?params
		// in the later case, it's difficult to know if it's an url including the site or not
		// for example in the relative url myPath?params, myPath could be interpreted as the site
		// The only way to know if it's really a path or a site is that "someone" knowing the possible sites "tells"
		// if myPath is a site or a path
		// This function could be done by either type WebUrlSecurityZone or WebUrlEnvironment because both of the
		// are supposed to "know" the sites
		if (UrlProtocol.of(theUrl) != null) {
			// It's sure that the url contains a site
			outUrlComponents = _parseFullUrl(theUrl);
		}
		else if (theUrl.startsWith("/")) {
			// It's sure that the url is an absolute url
			outUrlComponents = _parsePathUrl(theUrl);
		}
		else {
			// It can be a complete url (with the site) or a relative url
			if (theUrl.matches("[^/]+:.+")) {
				// the url is something like host:port/something
				outUrlComponents = _parseFullUrl(theUrl);
			} else if (theUrl.matches("(\\w+\\.\\w+)+.+")) {
				// the url is something like host.domain:port/something
				outUrlComponents = _parseFullUrl(theUrl);
			} else {
				// the url is something like host/something
				outUrlComponents = _parsePathUrl(theUrl);
			}
		}
		return outUrlComponents;

	}
	private UrlComponents _parseFullUrl(final String urlStr) {
		UrlComponents outComponents = null;
		if (UrlProtocol.is(urlStr,StandardUrlProtocol.FILE)) {
			Matcher m = FILE_URL_PATTERN.matcher(urlStr);
			if (m.find()) {
				String pathStr = m.group(1);

				outComponents = new UrlComponents(StandardUrlProtocol.FILE.toUrlProtocol(),null,0,
												  UrlPath.preservingTrailingSlash()		// BEWARE!!
												  		 .from(pathStr),
												  null,null);
			}
		}
		else {
			// split protocol://site/port and path?queryString#anchor
			Matcher m = FULL_URL_PATTERN.matcher(urlStr);
			if (m.find()) {
				String protocolStr = m.group(1);
				String siteStr = m.group(2);
				String portStr = m.group(3);
				String pathStr = m.group(4);
				String queryStr = m.group(5);
				String anchorStr = m.group(6);
				UrlProtocol protocol = Strings.isNOTNullOrEmpty(protocolStr)
													? UrlProtocol.forSure(protocolStr)
													: Strings.isNOTNullOrEmpty(portStr)  ? UrlProtocol.fromPort(Integer.parseInt(portStr))
																						 : Strings.isNOTNullOrEmpty(siteStr) && siteStr.startsWith("wwww")
																						 		? StandardUrlProtocol.HTTP.toUrlProtocol()
																						 		: null;
				StandardUrlProtocol stdProto = protocol != null ? protocol.asStandardProtocolOrNull()
																: null;
				Host host = Strings.isNOTNullOrEmpty(siteStr) ? new Host(siteStr)
															  : null;
				int port = Strings.isNOTNullOrEmpty(portStr) ? Integer.parseInt(portStr)
														     : stdProto != null ? stdProto.getDefaultPort()
																   			    : 0;
				UrlPath urlPath = Strings.isNOTNullOrEmpty(pathStr) ? UrlPath.preservingTrailingSlash()		// BEWARE!!!
																			 .from(pathStr)
																    : null;
				UrlQueryString qryString = Strings.isNOTNullOrEmpty(queryStr) ? UrlQueryString.fromParamsString(queryStr)
																		  	  : null;
				String anchor = anchorStr;

				outComponents = new UrlComponents(protocol,host,port,
												  urlPath,qryString,anchor);

			}
		}
		if (outComponents == null) throw new IllegalStateException(Throwables.message("{} is NOT a valid url",urlStr));
		return outComponents;
	}
	/**
	 * Parses an absolute url or a relative one without the protocol part protocol://site:port/
	 * @param pathUrl
	 * @return true if it's a valid url
	 */
	private UrlComponents _parsePathUrl(final String pathUrl) {
		UrlComponents outComponents = null;
		Matcher m = PATH_URL_PATTERN.matcher(pathUrl);
		if (m.find()) {
			String pathStr = m.group(1);
			String queryStr = m.group(2);
			String anchorStr = m.group(3);
			UrlPath urlPath = !Strings.isNullOrEmpty(pathStr) ? UrlPath.preservingTrailingSlash()	// BEWARE!
																	   .from(pathStr) 
															  : null;
			UrlProtocol protocol = null;
			Host host = null;
			int port = 0;
			if (urlPath != null
			 && CollectionUtils.hasData(urlPath.getPathElements()) && urlPath.getFirstPathElement().equals("localhost")) {
				protocol = StandardUrlProtocol.HTTP.toUrlProtocol();
				host = Host.localhost();
				port =  StandardUrlProtocol.HTTP.getDefaultPort();
				urlPath = urlPath.getPathElements().size() > 1 ? UrlPath.preservingTrailingSlash()
																		.from(urlPath.getPathElementsFrom(1))	// skip first element
															   : null;
			}
			UrlQueryString qryString = !Strings.isNullOrEmpty(queryStr) ? UrlQueryString.fromParamsString(queryStr) : null;
			String anchor = anchorStr;

			outComponents = new UrlComponents(protocol,host,port,
											  urlPath,qryString,anchor);
		}
		return outComponents;
	}
}
