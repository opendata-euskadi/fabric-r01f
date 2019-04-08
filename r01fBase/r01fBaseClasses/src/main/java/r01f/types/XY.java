package r01f.types;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

/**
 * Coordinates
 */
@Immutable
@MarshallType(as="xy")
@Accessors(prefix="_")
public class XY
  implements CanBeRepresentedAsString,
  			 Serializable {

	private static final long serialVersionUID = -3175355518015641559L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="x",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final float _x;

	@MarshallField(as="y",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final float _y;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public XY(@MarshallFrom("x") final float x,@MarshallFrom("y") final float y) {
		_x = x;
		_y = y;
	}
	public static XY valueOf(final String str) {
		float[] xy = _parse(str);
		return new XY(xy[0],xy[1]);
	}
	public static XY fromString(final String str) {
		return XY.valueOf(str);
	}
	private static float[] _parse(final String str) {
		String[] xy = str.split(",");
		if (xy.length != 2) throw new IllegalArgumentException(str + " does NOT have the correct format: 'x,y'");
		String x = xy[0];
		String y = xy[1];
		return new float[] { Float.parseFloat(x),Float.parseFloat(y) };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return Strings.customized("[},{}",_x,_y);
	}
}
