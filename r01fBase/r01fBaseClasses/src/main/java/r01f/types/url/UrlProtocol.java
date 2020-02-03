package r01f.types.url;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;

/**
 * Protocol
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class UrlProtocol
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 4733528269894276864L;
	/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final UrlProtocol HTTP = StandardUrlProtocol.HTTP.toUrlProtocol();
	public static final UrlProtocol HTTPS = StandardUrlProtocol.HTTPS.toUrlProtocol();
	public static final UrlProtocol HTTPS_CLI = StandardUrlProtocol.HTTPS_CLI.toUrlProtocol();
	public static final UrlProtocol FILE = StandardUrlProtocol.FILE.toUrlProtocol();
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _asString;
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _asString;
	}
	@Override
	public String toString() {
		return _asString;
	}
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof UrlProtocol) {
			UrlProtocol otherProto = (UrlProtocol)other;
			return this.is(otherProto);
		}
		else if (other instanceof StandardUrlProtocol) {
			StandardUrlProtocol otherStdProto = (StandardUrlProtocol)other;
			return this.is(otherStdProto);
		}
		return false;
	}
	/**
	 * Checks if it's the same protocol
	 * @param otherProto
	 * @return
	 */
	public boolean is(final UrlProtocol otherProto) {
		return Strings.isNOTNullOrEmpty(this.asString()) && Strings.isNOTNullOrEmpty(otherProto.asString())
					? this.asString().equals(otherProto.asString())
					: Strings.isNullOrEmpty(this.asString()) && Strings.isNullOrEmpty(otherProto.asString())
							? true		// both contains null strings
							: false;	// one contains a not null string while the other does not
	}
	public boolean isNOT(final UrlProtocol otherProto) {
		return !this.is(otherProto);
	}
	/**
	 * Checks if the given standard protocol is this protocol
	 * @param proto
	 * @return
	 */
	public boolean is(final StandardUrlProtocol proto) {
		return proto.toUrlProtocol().equals(this);
	}
	public boolean isNOT(final StandardUrlProtocol proto) {
		return !this.is(proto);
	}
	@Override
	public int hashCode() {
		return _asString != null ? _asString.hashCode() : 0;
	}
	/**
	 * Checks if the protocol is an standard one
	 * @return
	 */
	public boolean isStandard() {
		return this.asStandardProtocolOrNull() != null;
	}
	/**
	 * Return the protocol as a standard protocol if possible or null otherwise
	 * @return
	 */
	public StandardUrlProtocol asStandardProtocolOrNull() {
		StandardUrlProtocol outProto = null;
		for (StandardUrlProtocol p : StandardUrlProtocol.values()) {
			if (p.getCode().equals(_asString)) {
				outProto = p;
				break;
			}
		}
		return outProto;
	}
	/**
	 * Returns the protocol as a standard protocol if possible or the default value otherwise
	 * @param def
	 * @return
	 */
	public StandardUrlProtocol asStandardProtocolOrDefault(final StandardUrlProtocol def) {
		StandardUrlProtocol outProto = this.asStandardProtocolOrNull();
		return outProto != null ? outProto : def;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static UrlProtocol fromProtocol(final String proto) {
		return new UrlProtocol(proto);
	}
	public static UrlProtocol fromPort(final int port) {
		UrlProtocol outProto = null;
		for (StandardUrlProtocol p : StandardUrlProtocol.values()) {
			if (p.getDefaultPort() == port) {
				outProto = p.toUrlProtocol();
				break;
			}
		}
		return outProto;
	}
	public static UrlProtocol of(final Host host) {
		if (host == null) return null;
		return UrlProtocol.of(host.asString());
	}
	public static UrlProtocol valueOf(final String str) {
		return UrlProtocol.of(str);
	}
	public static UrlProtocol fromString(final String str) {
		return UrlProtocol.of(str);
	}
	/**
	 * using UrlProtocol.of(...) or UrlProtocol.from(...) tries to get the url protocol from a complete url string like http://xxx
	 * or an standard protocol string like http or https BUT if it's a CUSTOM protocol like myProto:// UrlProtocol.of(...) or UrlProtocol.from(...)
	 * returns NULL... if you're sure that the protocol string is a protocol, use UrlProtocol.forSure(...)
	 * @param str
	 * @return
	 */
	public static UrlProtocol of(final String str) {
		if (Strings.isNullOrEmpty(str)) {
			return null;
		}
		// [1] - Maybe it's a complete url like http://xxxx
		int p = str.indexOf("://");
		UrlProtocol outProto = p > 0 ? new UrlProtocol(str.substring(0,p).toLowerCase())
					 				 : null;
		// [2] - Maybe it's just the protocol as http or https
		String strNormalized = _normalizeJustProtocolString(str);
		if (outProto == null) {
			// try an standard protocol
			StandardUrlProtocol stdProto = null;
			for (StandardUrlProtocol std : StandardUrlProtocol.values()) {
				if (std.getCode().equals(strNormalized)) {
					stdProto = std;
					break;
				}
			}
			outProto = stdProto != null ? stdProto.toUrlProtocol()
										: null;
		}
		return outProto;
	}
	/**
	 * using UrlProtocol.of(...) or UrlProtocol.from(...) tries to get the url protocol from a complete url string like http://xxx
	 * or an standard protocol string like http or https BUT if it's a CUSTOM protocol like myProto:// UrlProtocol.of(...) or UrlProtocol.from(...)
	 * returns NULL. This method asumes that the given string is a protocol
	 * @param str
	 * @return
	 */
	public static UrlProtocol forSure(final String str) {
		if (Strings.isNullOrEmpty(str)) return null;
		return new UrlProtocol(str);
	}
	public static UrlProtocol from(final String str) {
		return UrlProtocol.of(str);
	}
	public static UrlProtocol fromOrDefault(final String str,
										    final UrlProtocol def) {
		UrlProtocol out = UrlProtocol.from(str);
		return out != null ? out : def;
	}
	public static String removeFrom(final String str) {
		if (Strings.isNullOrEmpty(str)) {
			return null;
		}
		int p = str.indexOf("://");
		return p > 0 ? str.substring(p + "://".length())
					 : str;
	}
	/**
	 * @param str  A protocol in different ways..but just the protocol, http, HTTPS, HTTP/1.1
	 * @return
	 */
	private static String _normalizeJustProtocolString(final String str) {
		// ServletRequest's getProtocol() method returns HTTP/1.1
		int slashPos = str.indexOf("/");
		return slashPos > 0 ? str.substring(0,slashPos).trim().toLowerCase()
							: str.trim().toLowerCase();
	}
	public static boolean is(final String str,final StandardUrlProtocol proto) {
		UrlProtocol p = UrlProtocol.of(str);
		return p != null ? p.equals(proto) : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STANDARD PROTOCOLS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("hiding")
	public enum StandardUrlProtocol
	 implements EnumWithCode<String,StandardUrlProtocol>,
	  		    CanBeRepresentedAsString {
		HTTP("http",80),
		HTTPS("https",443),
		HTTPS_CLI("https",444),	// DO NOT move before HTTPS
		FILE("file",80),
		FTP("ftp",21);

		@Getter private final String _code;
		@Getter private final int _defaultPort;
		@Getter private final Class<String> _codeType = String.class;

		private StandardUrlProtocol(final String code,final int defPort) {
			_code = code;
			_defaultPort = defPort;
		}

		private static EnumWithCodeWrapper<String,StandardUrlProtocol> _enums = new EnumWithCodeWrapper<String,StandardUrlProtocol>(StandardUrlProtocol.values());

		@Override
		public boolean isIn(final StandardUrlProtocol... status) {
			return _enums.isIn(this,status);
		}
		@Override
		public boolean is(final StandardUrlProtocol other) {
			return _enums.is(this,other);
		}
		@Override
		public String asString() {
			return this.getCode();
		}
		public boolean isNOTDefaultPort(final int port) {
			return !this.isDefaultPort(port);
		}
		public boolean isDefaultPort(final int port) {
			return _defaultPort == port;
		}
		public UrlProtocol toUrlProtocol() {
			return new UrlProtocol(_code);
		}
	}

}