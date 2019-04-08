package r01f.types.html;

import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="cssStyleClassName")
public class CSSStyleClassName
	 extends OIDBaseMutable<String> 
  implements OID {

	private static final long serialVersionUID = 4544524456179310518L;

	public CSSStyleClassName() {
		// default no-args constructor
	}
	public CSSStyleClassName(final String oid) {
		super(oid);
	}
	public CSSStyleClassName(final CSSStyleClassName other) {
		this(other.asString());
	}
	public static CSSStyleClassName valueOf(final String id) {
		return new CSSStyleClassName(id);
	}
	public static CSSStyleClassName forId(final String id) {
		return new CSSStyleClassName(id);
	}
}
