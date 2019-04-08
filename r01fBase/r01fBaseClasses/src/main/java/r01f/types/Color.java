package r01f.types;

import java.io.Serializable;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;

@ConvertToDirtyStateTrackable
@MarshallType(as="color")
@Immutable
@Accessors(prefix="_")
public class Color 
  implements CanBeRepresentedAsString,
  			 Serializable {
	private static final long serialVersionUID = 5293979687149623550L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Color code
	 */
	@Getter private final String _code; 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public Color(@MarshallFrom("code") final String code) {
		_code = code;
	}
	public static Color from(final String code) {
		return new Color(code);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _code;
	}
	@Override
	public boolean equals(final Object o) {
		if (o == null) return false;
		if (this == o) return true;
		if ( !(o instanceof Color) ) return false;
		
		Color otherColor = (Color)o;
		return _code != null ? _code.equals(otherColor.getCode())
							 : otherColor.getCode() != null ? false
									 						: true;
	}
	@Override
	public String toString() {
		return _code != null ? _code.toString() : null;
	}
	@Override
	public int hashCode() {
		return _code != null ? Objects.hashCode(_code)
							 : super.hashCode();
	}

}