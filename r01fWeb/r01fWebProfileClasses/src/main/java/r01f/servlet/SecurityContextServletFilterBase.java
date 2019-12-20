package r01f.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import r01f.securitycontext.SecurityContext;
import r01f.securitycontext.SecurityContextStoreAtThreadLocalStorage;
import r01f.types.url.Host;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.util.types.collections.CollectionUtils;

/**
 * A filter with TWO functions:
 * [1] - Gets a {@link SecurityContext} object from the [web session] object and stores it at
 * 		 a {@link ThreadLocal} storage (see {@link SecurityContextStoreAtThreadLocalStorage})
 * 		 This thread-local storage is used by the {@link SecurityContext} provider to get it:
 * 		 <pre class='brush:java'>
 *			@Slf4j
 *			public class XXXSecurityContextProvider
 *			  implements Provider<AB72SecurityContext> {
 *				@Override
 *				public XXXSecurityContext get() {
 *					XXXSecurityContext outSecurityContext = SecurityContextStoreAtThreadLocalStorage.get();
 *					if (outSecurityContext != null) {
 *						log.trace("got a [security context] attached to the [thread local] storage for user={}",
 *								  outSecurityContext.getUserCode());
 *					} else {
 *						log.warn("NO [security context] attached to the [thread local] storage: no security filter in use!!");
 *						outSecurityContext = new XXXSecurityContext();
 *					}
 *					return outSecurityContext;
 *				}
 *			}
 * 		 </pre>
 *
 * [2] - sets a cookie that contains a token generated with a secret key
 * 		 This cookie is set by a legitimate part that is sure that the user was authenticated, in this case
 * 		 looking at the [web sesssion] object after a {@link SecurityContext} object
 * 		 (if there's a {@link SecurityContext} object at the [web session], the user is authenticated)
 *
 * 		 The cookie token can be verified by anyone knowing a public key (or the secret key if symmetric encryption is used)
 * 		 If anyone get access to this cookie it can rest assured that the user was authenticated by a legitimate part just by
 * 		 ensuring that the cookie value can be un-encripted with the PUBLIC key
 */
@Slf4j
public abstract class SecurityContextServletFilterBase
		   implements Filter {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String SECURITY_CONTEXT_WEBSESSION_PARAM_NAME = "securityContext";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _loginUrlPath;
	private final Collection<Pattern> _notFilteredResourcesPatterns;

	private FilterConfig _servletFilterConfig = null;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextServletFilterBase(final String loginUrlPath,
											final Collection<Pattern> notFilteredResourcesPatterns) {
		_loginUrlPath = loginUrlPath;
	   _notFilteredResourcesPatterns = notFilteredResourcesPatterns;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void init(final FilterConfig servletFilterConfig) throws ServletException {
	   _servletFilterConfig = servletFilterConfig;
	}
	@Override
	public void destroy() {
	   _servletFilterConfig = null;

	}
	@Override
	public void doFilter(final ServletRequest request,final ServletResponse response,
						 final FilterChain chain) throws IOException,
											  			 ServletException {
		try {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse res = (HttpServletResponse)response;

			// [0] Get the requested url and see if it matches one of the patterns defined at the auth config
//			Url url = _fullURI(req);
//			UrlPath urlPath = url.getUrlPath();

			// [1] - not filtered resources
			//		 beware!! do NOT move... always filter AFTER setting the security context at thread local
			if (CollectionUtils.hasData(_notFilteredResourcesPatterns)) {
				String reqUri = req.getRequestURI();
				for (Pattern notFilteredPattern : _notFilteredResourcesPatterns) {
					if (notFilteredPattern.matcher(reqUri).matches()) {
						log.debug("[SecurityContextServletFilter] uri {} is NOT filtered",
							   	  reqUri);
						chain.doFilter(request,response);
						return;
					}
					else {
						log.trace("[SecurityContextServletFilter] uri {} is filtered",
								  reqUri);
					}
				}
			}

			// [2] - Get the [securityContext] from the web session (set at the login page)
			//		 ... the user MUST have pass through the login page and authenticate
			HttpSession webSession = req.getSession(false);	// false = do not create session
			SecurityContext securityContext = webSession != null
												  ? (SecurityContext)webSession.getAttribute(SECURITY_CONTEXT_WEBSESSION_PARAM_NAME)
												  : null;
			// [3] - Attach the security context to the local thread
			// 		 (the security context provider will look after the [user context] at the local thread)
			if (securityContext != null) {
				// attath the security context to the thread local storage
				// (the security context provider will look after that [security context] at the thread local storage)
				SecurityContextStoreAtThreadLocalStorage.set(securityContext);
				log.debug("[SecurityContextServletFilter] security context available at [web session] for uri={}: store at thread-local storage",
						  req.getRequestURI());
			}

			// [4] - redir to login page if security context is not present
			if (securityContext == null) {
				log.info("[SecurityContextServletFilter] NO securiy context avaialble at web session when requesting {}: redir to login page {}",
						 req.getRequestURI(),_loginUrlPath);
				res.sendRedirect(_loginUrlPath);
				return;
			}

			// [5] - Create a cookie with a secret value that anyone can use to check if the
			//		 user was authenticated by a legitimate party
			if (webSession != null
			 && !_hasSecurityTokenCookie(req)) {
				// create a cookie that contains the security token
				// ... this security token can only be generated by a legitimate part owning a secret PRIVATE key
				//	   any other part can rest assured that the user has been loged in just by ensuring
				//	   that the cookie exists and that it's value was generated by a legitimate part using
				// 	   a PUBLIC key (or even the same PRIVATE key as the cookie generator)
				Cookie cookieContainingSecurityToken = _createSecurityTokenCookie(Url.from(req.getRequestURL().toString()));
				res.addCookie(cookieContainingSecurityToken);
			}

			// [4] - Do what ever ...
			chain.doFilter(request,response);

		} finally {
			// [99] - Remove the security context from the local thread
			SecurityContextStoreAtThreadLocalStorage.remove();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private Url _fullURI(final HttpServletRequest request) {
	   StringBuffer requestURL = request.getRequestURL();	//getRequestURI only returns URL BEFORE query string -- UrlPath.from(req.getRequestURI());
	   String queryString = request.getQueryString();

	   if (queryString == null) {
		  return Url.from(requestURL.toString());
	   } else {
		  return Url.from(requestURL.toString())
				  .joinWith(UrlQueryString.fromUrlEncodedParamsString(queryString));
	   }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SECURITY TOKEN COOKIE
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern HOST_PATTERN = Pattern.compile("([^.]+)\\.(cms\\..+)");
	private static final String SECURITY_TOKEN_COOKIE_NAME = "r01SecurityToken";

	private boolean _hasSecurityTokenCookie(final HttpServletRequest request) {
	   Cookie[] cookies = request.getCookies();
	   boolean outHasCookie = false;
	   for (Cookie cookie : cookies) {
		  if (cookie.getName().equals(SECURITY_TOKEN_COOKIE_NAME)) {
			 outHasCookie = true;
			 break;
		  }
	   }
	   log.info("> Legitimate security token cookie named {} {}",
			  "r01SecurityToken",outHasCookie ? "EXISTS" : "DOES NOT EXISTS");
	   if (!outHasCookie) { // debug cookies
		  for (Cookie cookie : cookies) {
			 log.info("\t -Cookie {}: {}",
					cookie.getName(),cookie.getValue());
		  }
	   }
	   return outHasCookie;
	}
	private Cookie _createSecurityTokenCookie(final Url url) {
		// [1] - Guess the cookie domain (should be cms.euskadi.xxx)
		Host host = url.getHost();
		String cookieDomain = null;
		if (host.is(Host.localhost())) {
			cookieDomain = Host.localhost().asString();
		} else {
			Matcher m = HOST_PATTERN.matcher(host.asString());
			if (m.find()) {
				log.info("Creating legitimate security cookie for {}.{} > domain: {}",
						 m.group(1),m.group(2),
						 m.group(2));
				cookieDomain = m.group(2);
			} else {
				log.warn("{} is NOT a supported host to set {} cookie: it does NOT match {}",
						 host,SECURITY_TOKEN_COOKIE_NAME,HOST_PATTERN);
				 //throw new IllegalStateException(host.asString() + " is not a supported host for DEMO");
			}
		}

		// [2] - Create the cookie
		// ... this security token can only be generated by a legitimate part owning a secret PRIVATE key
		//	   any other part can rest assured that the user has been logged in just by ensuring
		//	   that the cookie exists and that it's value was generated by a legitimate part using
		// 	   a PUBLIC key (or even the same PRIVATE key as the cookie generator)
		Cookie outSecurityCookie = new Cookie(SECURITY_TOKEN_COOKIE_NAME,
									 _createAuthCookieSecretToken());
		outSecurityCookie.setDomain(cookieDomain);
		outSecurityCookie.setPath("/");

		// session cookies are removed when the browser is closed
		return outSecurityCookie;
	}
	protected abstract String _createAuthCookieSecretToken();
}
