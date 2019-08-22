package r01f.mime;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;

/**
 * Encapsulates a {@link MimeBodyPartDispositionType}
 * Usage:
 * <pre class='brush:java'>
 * 		MimeType mime = MimeTypes.forName("application/vnd.google-earth.kml+xml");
 * </pre>
 *
 * @see http://filext.com/
 */
@MarshallType(as="mimeBodyPartDispositionType")
@Accessors(prefix="_")
public class MimeBodyPartDispositionType
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 2645976149870626567L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final String _name;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MimeBodyPartDispositionType(final String name) {
		_name = name;
	}
	public MimeBodyPartDispositionType(final MimeBodyPartDispositionType other) {
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
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( !(obj instanceof MimeBodyPartDispositionType) ) return false;

		MimeBodyPartDispositionType otherMime = (MimeBodyPartDispositionType)obj;
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
	public static MimeBodyPartDispositionType DISPOSITION_INLINE = new MimeBodyPartDispositionType("inline");
	public static MimeBodyPartDispositionType DISPOSITION_ATTACHMENT = new MimeBodyPartDispositionType("attachment");
	
}
