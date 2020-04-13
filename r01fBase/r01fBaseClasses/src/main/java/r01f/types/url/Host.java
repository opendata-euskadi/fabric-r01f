package r01f.types.url;

import lombok.experimental.Accessors;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="host")
@Accessors(prefix="_")
public class Host
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = -3712825671090881670L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Somethimes (not a canonical usage), the host contains protocol, port, urlPath, querystring or anchor
	 * ... this var stores these components
	 * BEWARE! it's NOT serialized
	 */
	@MarshallIgnoredField
	private final transient UrlComponents _urlComponents;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Host() {
		/* default no args constructor for serialization purposes */
		_urlComponents = null;
	}
	public Host(final String host) {
		this(new UrlParserNoRegExp(host)
						.getComponents());
	}
	private Host(final UrlComponents urlComponents) {
		super(urlComponents.getHost().asString());	// store the host
		_urlComponents = urlComponents;
	}
	public Host(final Host other) {
		this(other != null ? other.getId() : (String)null);
	}
	public static Host from(final String host) {
		return new Host(host);
	}
	public static Host of(final String host) {
		return new Host(host);
	}
	public static Host strict(final Host other) {
		return Host.strict(other.getId());
	}
	public static Host strict(final String host) {
		if (host.indexOf("/") > 0 || host.indexOf(":") > 0) throw new IllegalArgumentException(host + " does NOT seem to be strictly a host!!");
		Host theHost = new Host();	// beware! do not set the components
		theHost.setId(host);
		return theHost;
	}
	public static Host valueOf(final String host) {
		return new Host(host);
	}
	public static Host forId(final String host) {
		return new Host(host);
	}
	public static Host localhost() {
		return new Host("localhost");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the host as an url
	 */
	public Url asUrl() {
		return new Url(this.getUrlComponents());
	}
	/**
	 * @return the url components
	 */
	UrlComponents getUrlComponents() {
		// if the host was constructed as strict() url components is null
		return _urlComponents != null ? _urlComponents
									  : new UrlComponents(null,Host.strict(this.getId()),0,		// protocol / host / port
											  			  null,null,null);						// path / query string / anchor
	}
	/**
	 * Returns the TLD (top level domain)
	 * @return
	 */
	public String getTLD() {
		if (!this.getId().contains(".")) return null;
		int lastDotPos = this.getId().lastIndexOf('.');
		return this.getId().substring(lastDotPos+1);
	}
}
