package r01f.mime;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;

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
/////////////////////////////////////////////////////////////////////////////////////////
//  
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
}
