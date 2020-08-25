package r01f.mime;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.collections.CollectionUtils;

/**
 * Encapsulates a {@link MimeType}
 * Usage:
 * <pre class='brush:java'>
 * 		MimeType mime = MimeTypes.forName("application/vnd.google-earth.kml+xml");
 * </pre>
 *
 * @see http://filext.com/
 */
@MarshallType(as="mimeType")
@Accessors(prefix="_")
public class MimeType
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 2645976149870626567L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final String _name;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MimeType(final String name) {
		_name = name;
	}
	public MimeType(final MimeType other) {
		_name = other.getName();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.toString();
	}
	@Override
	public String toString() {
		return _name.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final MimeType other) {
		return this.equals(other);
	}
	public boolean isNOT(final MimeType other) {
		return !this.is(other);
	}
	public boolean isIn(final MimeType... others) {
		if (CollectionUtils.isNullOrEmpty(others)) return false;
		boolean in = false;
		for (MimeType other : others) {
			if (other.is(this)) {
				in = true;
				break;
			}
		}
		return in;
	}
	public boolean isNOTIn(final MimeType... others) {
		return !this.isIn(others);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( !(obj instanceof MimeType) ) return false;

		MimeType otherMime = (MimeType)obj;
		return _name != null ? _name.equals(otherMime.getName())
							 : otherMime.getName() == null ? true
									 					   : false;
	}
	@Override
	public int hashCode() {
		return _name.hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public static MimeType TEXT_PLAIN = new MimeType("text/plain");
	public static MimeType APPLICATION_XML = new MimeType("application/xml");
	public static MimeType APPLICATION_JSON = new MimeType("application/json");
	public static MimeType OCTECT_STREAM = new MimeType("application/octet-stream");
	public static MimeType XHTML = new MimeType("application/xhtml+xml");
	public static MimeType HTML = new MimeType("text/html");
	public static MimeType JAVASCRIPT = new MimeType("application/javascript");
	public static MimeType STYLESHEET = new MimeType("text/css");
	public static MimeType IMAGE_JPEG = new MimeType("image/jpeg");
	public static MimeType IMAGE_PNG = new MimeType("image/png");
	public static MimeType PDF = new MimeType("application/pdf");
	
	
}
