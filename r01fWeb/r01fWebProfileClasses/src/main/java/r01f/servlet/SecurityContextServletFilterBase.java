package r01f.servlet;

import java.io.IOException;
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
import r01f.types.url.UrlPath;
import r01f.types.url.UrlQueryString;

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
	public static final String AUTH_CONTEXT_SESSION_PARAM_NAME = "autxContext";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private FilterConfig _servletFilterConfig = null;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityContextServletFilterBase() {
		// default no-args constructor
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
			Url url = _fullURI(req);
	        UrlPath urlPath = url.getUrlPath();	
	        
			// [1] - Get the [securityContext] from the web session (set at the login page)
	        //		 ... the user MUST have pass through the login page and authenticate
			HttpSession webSession = req.getSession(false);	// false = do not create session
			SecurityContext securityContext = webSession != null	
													? (SecurityContext)webSession.getAttribute(AUTH_CONTEXT_SESSION_PARAM_NAME)
													: null;
			
	        // [2] - Attach the security context to the local thread 
			// 		 (the security context provider will look after the [user context] at the local thread)
			if (securityContext != null) {
				// attath the security context to the thread local storage
				// (the security context provider will look after that [security context] at the thread local storage)
				SecurityContextStoreAtThreadLocalStorage.set(securityContext);
			} else {
				log.warn("NO securiy context avaialble at web session: NO security cookie can be set");
			}
			
			// [3] - Create a cookie with a secret value that anyone can use to check if the user was authenticated by a legitimate party
			if (webSession != null 
			 && securityContext != null  
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
	private boolean _hasSecurityTokenCookie(final HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		boolean outHasCookie = false;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("r01SecurityToken")) {
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
			Pattern p = Pattern.compile("([^.]+)\\.(cms\\..+)");	// ie: (XXXX).(cms.euskadi.eus)
			Matcher m = p.matcher(host.asString());
			if (m.find()) {
				log.info("Creating legitimate security cookie for {}.{} > domain: {}",
						  m.group(1),m.group(2),
						  m.group(2));
				cookieDomain = m.group(2);
			} else {
				throw new IllegalStateException(host.asString() + " is not a supported host for DEMO");
			}
		}
		
		// [2] - Create the cookie
		// ... this security token can only be generated by a legitimate part owning a secret PRIVATE key
		//	   any other part can rest assured that the user has been loged in just by ensuring
		//	   that the cookie exists and that it's value was generated by a legitimate part using 
		// 	   a PUBLIC key (or even the same PRIVATE key as the cookie generator)
		Cookie outSecurityCookie = new Cookie("r01SecurityToken",
											  _createAuthCookieSecretToken());
		outSecurityCookie.setDomain(cookieDomain);
		outSecurityCookie.setPath("/");
		
		// session cookies are removed when the browser is closed
		return outSecurityCookie;
	}
	protected abstract String _createAuthCookieSecretToken();
}
