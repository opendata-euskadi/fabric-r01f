package r01f.httpclient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Encapsulates an http cookie
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class HttpRequestCookie {
	@Getter private final String _name;
	@Getter private final String _value;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpRequestCookie create(final String name,final String value) {
		return new HttpRequestCookie(name,
									 value);
	}
}
