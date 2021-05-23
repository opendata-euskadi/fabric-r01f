package r01f.types.url;

import r01f.patterns.Provider;
import r01f.types.url.UrlProtocol.StandardUrlProtocol;
import r01f.util.types.Strings;


public class Urls {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a new url from the given one joining the given path
	 * ie: if url=www.mysite.com/foo/ and path=/bar, the returned url is www.mysite.com/foo/bar
	 * @param host
	 * @param path
	 * @return
	 */
	public static Url join(final Host host,
						   final UrlPath path) {
		return Urls.join(host,
						 path,
						 null);
	}
	/**
	 * Creates a new url from the given one joining the given path
	 * ie: if url=www.mysite.com/foo/ and path=/bar, the returned url is www.mysite.com/foo/bar
	 * @param host
	 * @param path
	 * @param queryString
	 * @return
	 */
	public static Url join(final Host host,
						   final UrlPath path,
						   final UrlQueryString qryStr) {
		return Urls.join(host,
						 path,
						 qryStr,
						 null);		// no anchor
	}
	/**
	 * Creates a new url from the given one joining the given path
	 * ie: if url=www.mysite.com/foo/ and path=/bar, the returned url is www.mysite.com/foo/bar
	 * @param host
	 * @param queryString
	 * @return
	 */
	public static Url join(final Host host,
						   final UrlQueryString qryStr) {
		return Urls.join(host,
						 null,		// no path
						 qryStr,
						 null);		// no anchor
	}
	/**
	 * Creates a new url from the given one joining the given path
	 * ie: if url=www.mysite.com/foo/ and path=/bar, the returned url is www.mysite.com/foo/bar
	 * @param host
	 * @param path
	 * @param queryString
	 * @param anchor
	 * @return
	 */
	public static Url join(final Host host,
						   final UrlPath path,
						   final UrlQueryString qryStr,
						   final String anchor) {
		return Urls.join(host != null ? host.asUrl() : null,				
						 path,
						 qryStr,
						 anchor);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a new url from the given one joining the given path
	 * ie: if url=www.mysite.com/foo/ and path=/bar, the returned url is www.mysite.com/foo/bar
	 * @param url
	 * @param path
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlPath path) {
		return Urls.join(url,
						 path,
						 (String)null);	// no anchor
	}
	/**
	 * Creates a new url from the given one joining the given path
	 * ie: if url=www.mysite.com/foo/ and path=/bar, the returned url is www.mysite.com/foo/bar
	 * @param url
	 * @param path
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlPath path,
						   final String anchor) {
		return Urls.join(url,
						 path,
						 null,anchor);		// query string = null
	}
	/**
	 * Creates a new url from the given one joining the given queryString
	 * ie: if url=www.mysite.com/foo?param1=param1Value and queryString=param2=param2Value, the returned url is www.mysite.com/foo?param1=param1Value&param2=param2Value
	 * @param url
	 * @param qryString
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlQueryString qryString) {
		return Urls.join(url,
						 qryString,null);	// no anchor
	}
	/**
	 * Creates a new url from the given one joining the given queryString
	 * ie: if url=www.mysite.com/foo?param1=param1Value and queryString=param2=param2Value, the returned url is www.mysite.com/foo?param1=param1Value&param2=param2Value
	 * @param url
	 * @param qryString
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlQueryString qryString,
						   final String anchor) {
		return Urls.join(url,
						 null,			// no url path
						 qryString,
						 anchor);
	}
	/**
	 * Creates a new url from the given one joining the given path and query string
	 * ie: if url=www.mysite.com/foo?param1=param1Value, path=/bar and queryString=param2=param2Value, the returned url is www.mysite.com/foo/bar?param1=param1Value&param2=param2Value
	 * @param url
	 * @param path
	 * @param qryString
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlPath path,
						   final UrlQueryString qryString) {
		return Urls.join(url,
				  		 path,
				  		 qryString,null);	// no anchor
	}
	/**
	 * Creates a new url from the given one joining the given path and query stringa
	 * ie: if url=www.mysite.com/foo?param1=param1Value and queryString=param2=param2Value, the returned url is www.mysite.com/foo?param1=param1Value&param2=param2Value
	 * @param url
	 * @param qryStrParams
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlQueryStringParam... qryStrParams) {
		return Urls.join(url,
				  		 UrlQueryString.fromParams(qryStrParams));
	}
	/**
	 * Creates a new url from the given one joining the given path and query stringa
	 * ie: if url=www.mysite.com/foo?param1=param1Value, path=/bar and queryString=param2=param2Value, the returned url is www.mysite.com/foo/bar?param1=param1Value&param2=param2Value
	 * @param url
	 * @param path
	 * @param qryStrParams
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlPath path,
						   final UrlQueryStringParam... qryStrParams) {
		return Urls.join(url,
				  		 path,
				  		 UrlQueryString.fromParams(qryStrParams));
	}
	/**
	 * Creates a new url from the given one joining the given path and query string
	 * ie: if url=www.mysite.com/foo?param1=param1Value, path=/bar and queryString=param2=param2Value, the returned url is www.mysite.com/foo/bar?param1=param1Value&param2=param2Value
	 * @param url
	 * @param path
	 * @param qryString
	 * @param urlPathFragment
	 * @return
	 */
	public static Url join(final Url url,
						   final UrlPath path,
						   final UrlQueryString qryString,final String urlPathFragment) {
		UrlComponents urlComps = url != null ? url.getComponents()
											 : null;
		return Url.from(urlComps != null ? urlComps.getProtocol() : null,
						urlComps != null ? urlComps.getHost() : null,
						urlComps != null ? urlComps.getPort() : -1,
						// path & path fragmnet
						Urls.join(urlComps != null ? urlComps.getUrlPath() : null,
								  path),
					    Strings.isNOTNullOrEmpty(urlPathFragment) ? urlPathFragment 
					    								 		  : urlComps != null ? urlComps.getUrlPathFragment() : null,
						// query string
					    Urls.join(urlComps != null ? urlComps.getQueryString() : null,
					    		  qryString));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a new url joining the given path and query string
	 * ie: if url=www.mysite.com/foo?param1=param1Value, path=/bar and queryString=param2=param2Value, the returned url is www.mysite.com/foo/bar?param1=param1Value&param2=param2Value
	 * @param path
	 * @param qryString
	 * @return
	 */
	public static Url join(final UrlPath path,
						   final UrlQueryString qryString) {
		if (path == null) return null;
		if (qryString == null) return new Url(path);

		return Url.from(null,	// protocol
						null,	// host
						-1,		// port
					    path,
					    qryString);
	}
	/**
	 * Creates a new url joining the given path and query string
	 * ie: if url=www.mysite.com/foo?param1=param1Value, path=/bar and queryString=param2=param2Value, the returned url is www.mysite.com/foo/bar?param1=param1Value&param2=param2Value
	 * @param path
	 * @param qryString
	 * @param anchor
	 * @return
	 */
	public static Url join(final UrlPath path,
						   final UrlQueryString qryString,
						   final String anchor) {
		if (path == null) return null;
		if (qryString == null) return new Url(path,
											  anchor);

		return Url.from(null,	// protocol
						null,	// host
						-1,		// port
					    path,anchor,
					    qryString);
	}
	/**
	 * Joins a query string with the new one
	 * BEWARE that the {@link UrlQueryString} type is immutable so a new ParametersWrapper instance is created
	 * @param queryString
	 * @param paramName
	 * @param paramValue
	 * @return
	 */
	public static UrlQueryString join(final UrlQueryString queryString,
							   		  final String paramName,final String paramValue) {
		return queryString.add(paramName,paramValue);
	}
	/**
	 * Joins a query stirng with the new one
	 * BEWARE that the {@link UrlQueryString} type is immutable so a new ParametersWrapper instance is created
	 * @param queryString
	 * @param paramName
	 * @param paramValueProvider
	 * @return
	 */
	public static UrlQueryString join(final UrlQueryString queryString,
							   		  final String paramName,final Provider<String> paramValueProvider) {
		return queryString.add(paramName,paramValueProvider);
	}
	/**
	 * Joins two {@link UrlQueryString}
	 * @param other
	 * @return
	 */
	public static UrlQueryString join(final UrlQueryString queryString,
									  final UrlQueryString other) {

		if (queryString == null && other == null) return null;
		if (queryString == null) return other;
		if (other == null) return queryString;

		return queryString.joinWith(other);
	}
	/**
	 * Joins two {@link UrlPath}
	 * @param urlPath
	 * @param other
	 * @return
	 */
	public static UrlPath join(final UrlPath urlPath,
							   final UrlPath other) {
		if (urlPath == null && other == null) return null;
		if (urlPath == null) return other;
		if (other == null) return urlPath;
		
		return urlPath.joinedWith(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static String serverAndPort(final Host host,final int port) {
		if (port == StandardUrlProtocol.HTTP.getDefaultPort()) {
			return host.asString();
		}
		return host + ":" + port;
	}
}
