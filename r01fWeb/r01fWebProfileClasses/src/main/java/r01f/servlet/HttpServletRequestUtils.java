package r01f.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.url.Host;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Utils about servlet request
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpServletRequestUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the real requested host.
	 * This method deals with the case that the servlet engine is proxied
	 * and req.getServerName() returns the servlet engine's host, NOT the 
	 * client requested one
	 * 
	 * BEWARE!!!	This method reads the request body and this can be read only once.
	 * 				If you the body is readed in a filter, the target servlet will NOT be
	 * 				able to re-read it and this will also cause IllegalStateException
	 * 				... the only solution is to use a ServletRequestWrapper
	 * 					(see http://natch3z.blogspot.com.es/2009/01/read-request-body-in-filter.html
	 * 					 and ContentCachingRequestWrapper from Spring framework)
	 * @param realHttpReq
	 * @return
	 */
	public static Host clientRequestedHost(final HttpServletRequest realHttpReq) {
		// try to get the X-Forwarded-Host request header
		// 		X-Forwarded-For: 	The IP address of the client.
		//		X-Forwarded-Host: 	The original host requested by the client in the Host HTTP request header.
		// 		X-Forwarded-Server: The hostname of the proxy server.
		// BEWARE that these headers may contain more than a single value (comma separated)	
		// (see http://httpd.apache.org/docs/2.2/mod/mod_proxy.html)
		String forwardedHost = realHttpReq.getHeader("X-Forwarded-Host");
		String theForwardedHost = Strings.isNOTNullOrEmpty(forwardedHost) ? StringSplitter.using(Splitter.on(','))
																						  .at(forwardedHost)
																						  .group(0)
																		  : null;
		if (Strings.isNOTNullOrEmpty(theForwardedHost)) return Host.of(theForwardedHost);

		// r01 customized X-Forwarded-Host header
		String r01ForwardedHost = realHttpReq.getHeader("X-R01-Forwarded-Host");
		String theR01ForwardedHost = Strings.isNOTNullOrEmpty(r01ForwardedHost) ? StringSplitter.using(Splitter.on(','))
																						  		.at(r01ForwardedHost)
																						  		.group(0)
																				: null;
		if (Strings.isNOTNullOrEmpty(theR01ForwardedHost)) return Host.of(theR01ForwardedHost); 
		
		// it's NOT proxied
		return Host.of(realHttpReq.getServerName());
	}
	/**
	 * Returns the real client ip
	 * This method deals with the case that the servlet engine is proxied and the 
	 * requesting ip is the proxy one, not the client one
	 * 
	 * BEWARE!!!	This method reads the request body and this can be read only once.
	 * 				If you the body is readed in a filter, the target servlet will NOT be
	 * 				able to re-read it and this will also cause IllegalStateException
	 * 				... the only solution is to use a ServletRequestWrapper
	 * 					(see http://natch3z.blogspot.com.es/2009/01/read-request-body-in-filter.html
	 * 					 and ContentCachingRequestWrapper from Spring framework)
	 * @param realHttpReq
	 * @return
	 */
	public static String requestingClientIp(final HttpServletRequest realHttpReq) { 
		// X-Forwarded-For: 	The IP address of the client.
		// X-Forwarded-Host: 	The original host requested by the client in the Host HTTP request header.
		// X-Forwarded-Server: The hostname of the proxy server.
		// BEWARE that these headers may contain more than a single value (comma separated)	
		String ip = realHttpReq.getHeader("X-Forwarded-For");   
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = realHttpReq.getHeader("Proxy-Client-IP");   
		}   
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = realHttpReq.getHeader("WL-Proxy-Client-IP");   
		}   
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = realHttpReq.getHeader("HTTP_CLIENT_IP");   
		}   
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = realHttpReq.getHeader("HTTP_X_FORWARDED_FOR");   
		}   
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = realHttpReq.getRemoteAddr();   
		}   
		return ip;   
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isInternalIP(final HttpServletRequest realHttpReq) {
		String clientIp = HttpServletRequestUtils.requestingClientIp(realHttpReq);
		return HttpServletRequestUtils.isInternalIP(clientIp);
	}
	public static boolean isInternalIP(final String ip) {
		boolean internalIP = Strings.isNOTNullOrEmpty(ip)
						  && (ip.startsWith("10.")
						   || ip.startsWith("172.16.") 
						   || ip.startsWith("192.168.") 
						   || ip.startsWith("169.254.") 
						   || ip.equals("127.0.0.1") 
						   || ip.equals("0:0:0:0:0:0:0:1")
						   || ip.equals("::1"));		
		return internalIP;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IP ADDRESS MATCHER 
// 	see https://github.com/spring-projects/spring-security/blob/master/web/src/main/java/org/springframework/security/web/util/matcher/IpAddressMatcher.java
//		https://seancfoley.github.io/IPAddress/#_Toc456708511
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private final class IpAddressMatcher {
		
		private final int _nMaskBits;
		private final InetAddress _requiredAddress;
	
		/**
		 * Takes a specific IP address or a range specified using the IP/Netmask (e.g.
		 * 192.168.1.0/24 or 202.24.0.0/14).
		 *
		 * @param ipAddress the address or range of addresses from which the request must come.
		 */
		public IpAddressMatcher(final String ipAddress) {
			String theIpAddress = ipAddress;
			if (ipAddress.indexOf('/') > 0) {
				String[] addressAndMask = StringUtils.split(ipAddress,"/");
				theIpAddress = addressAndMask[0];
				_nMaskBits = Integer.parseInt(addressAndMask[1]);
			}
			else {
				_nMaskBits = -1;
			}
			_requiredAddress = _parseAddress(theIpAddress);
		}
		public boolean matches(final String address) {
			InetAddress remoteAddress = _parseAddress(address);
	
			if (!_requiredAddress.getClass().equals(remoteAddress.getClass())) {
				return false;
			}
			if (_nMaskBits < 0) {
				return remoteAddress.equals(_requiredAddress);
			}
			byte[] remAddr = remoteAddress.getAddress();
			byte[] reqAddr = _requiredAddress.getAddress();
	
			int oddBits = _nMaskBits % 8;
			int nMaskBytes = _nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
			byte[] mask = new byte[nMaskBytes];
	
			Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);
	
			if (oddBits != 0) {
				int finalByte = (1 << oddBits) - 1;
				finalByte <<= 8 - oddBits;
				mask[mask.length - 1] = (byte) finalByte;
			}
	
			// System.out.println("Mask is " + new sun.misc.HexDumpEncoder().encode(mask));
	
			for (int i = 0; i < mask.length; i++) {
				if ((remAddr[i] & mask[i]) != (reqAddr[i] & mask[i])) {
					return false;
				}
			}
			return true;
		}
		private InetAddress _parseAddress(final String address) {
			try {
				return InetAddress.getByName(address);
			} catch (UnknownHostException e) {
				throw new IllegalArgumentException("Failed to parse address" + address, e);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COOKIE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds all cookies matching
	 * @param req
	 * @param pred
	 * @return
	 */
	public static Collection<Cookie> cookiesMatching(final HttpServletRequest req,
									 		 		 final Predicate<Cookie> pred) {
		Cookie[] cookies = req.getCookies();
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
	public static Cookie cookieWithName(final HttpServletRequest req,
									    final String cookieName) {
		Collection<Cookie> cookiesMatching = HttpServletRequestUtils.cookiesMatching(req,
																					 new Predicate<Cookie>() {
																								@Override
																								public boolean apply(final Cookie cookie) {
																									return cookie.getName().equals(cookieName);
																								}
																			  		 });
		return CollectionUtils.hasData(cookiesMatching) ? CollectionUtils.firstOf(cookiesMatching) : null;
	}
}
