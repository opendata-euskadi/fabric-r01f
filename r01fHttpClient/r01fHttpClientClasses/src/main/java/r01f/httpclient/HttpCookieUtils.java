package r01f.httpclient;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.SM;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.message.BasicHeader;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import r01f.util.types.collections.CollectionUtils;

/**
 * Cookie utils using apache http client
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpCookieUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Uses apache http client to parse the cookies
	 * @param uri
	 * @param cookieHeaders
	 * @return
	 */
	@SneakyThrows
	public static Collection<Cookie> parseCookies(final URI uri,
																		 final List<String> cookieHeaders) {
		CookieSpec cookieSpec = new DefaultCookieSpec();
		
		List<Cookie> cookies = new ArrayList<Cookie>();
		int port = (uri.getPort() < 0) ? 80 : uri.getPort();
		boolean secure = "https".equals(uri.getScheme());
		
		CookieOrigin origin = new CookieOrigin(uri.getHost(),port,
											   uri.getPath(), 
											   secure);
		for (String cookieHeader : cookieHeaders) {
			BasicHeader header = new BasicHeader(SM.SET_COOKIE,cookieHeader);
			cookies.addAll(cookieSpec.parse(header,
											 origin));
		}
		return cookies;
	}
	/**
	 * Finds all cookies matching
	 * @param req
	 * @param pred
	 * @return
	 */
	public static Collection<Cookie> apacheCookiesMatching(final Collection<Cookie> cookies,
									 		 		 							  final Predicate<Cookie> pred) {
		if (CollectionUtils.isNullOrEmpty(cookies)) return null;
		return FluentIterable.from(cookies)
							 .filter(pred)
							 .toList();
	}
	/**
	 * Finds a cookie with the given name (no matter the domain)
	 * @param req
	 * @param cookieName
	 * @return
	 */
	public static Cookie apacheCookieWithName(final Collection<Cookie> cookies,
									    	  						 final String cookieName) {
		Collection<Cookie> cookiesMatching = HttpCookieUtils.apacheCookiesMatching(cookies,
																										  new Predicate<Cookie>() {
																													@Override
																													public boolean apply(final Cookie cookie) {
																														return cookie.getName().equals(cookieName);
																													}
																								  		  });
		return CollectionUtils.hasData(cookiesMatching) ? CollectionUtils.firstOf(cookiesMatching) : null;
	}
}
