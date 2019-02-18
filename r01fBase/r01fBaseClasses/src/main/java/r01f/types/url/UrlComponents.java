package r01f.types.url;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
public class UrlComponents 
  implements Debuggable {
	@Getter private final UrlProtocol _protocol;
	@Getter private final Host _host;
	@Getter private final int _port;
	@Getter private final UrlPath _urlPath;
	@Getter private final UrlQueryString _queryString;
	@Getter private final String _anchor;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlProtocol getProtocolOrDefault(final UrlProtocol def) {
		return _protocol != null ? _protocol : def;
	}
	public Host getHostOrDefault(final Host def) {
		return _host != null ? _host : def;
	}
	public int getPortOrDefault(final int def) {
		return _port > 0 ? _port : def;
	}
	public UrlPath getUrlPathOrDefault(final UrlPath def) {
		return _urlPath != null ? _urlPath : def;
	}
	public UrlQueryString getQueryStringOrDefault(final UrlQueryString def) {
		return _queryString != null ? _queryString : def;
	}
	public String getAnchorOrDefault(final String def) {
		return Strings.isNOTNullOrEmpty(_anchor) ? _anchor : def;
	}
	public boolean hasPort() {
		return _port > 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String debugInfo() {
		StringBuffer sb = new StringBuffer(200);
		sb.append("-   Protocol: ").append(this.getProtocol()).append("\r\n")
		  .append("-       Site: ").append(this.getHost()).append("\r\n")
		  .append("-       Port: ").append(this.getPort()).append("\r\n")
		  .append("-       Path: ").append(this.getUrlPath()).append("\r\n")
		  .append("Query String: ").append(this.getQueryString() != null ? this.getQueryString().asStringNotEncodingParamValues() : "").append("\r\n")
		  .append("-     Anchor: ").append(this.getAnchor());
		return sb.toString();
	}
}
