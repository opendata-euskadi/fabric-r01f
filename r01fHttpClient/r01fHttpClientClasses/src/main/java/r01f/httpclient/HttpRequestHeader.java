package r01f.httpclient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Encapsulates an http header
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class HttpRequestHeader {
	@Getter private final String _name;
	@Getter private final String _value;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpRequestHeader create(final String name,final String value) {
		return new HttpRequestHeader(name,
									 value);
	}
}
