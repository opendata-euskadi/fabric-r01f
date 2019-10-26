package r01f.types.geo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Latitude & longitude
 * <code>
 * 		GeoPosition2D geo = GeoPosition2D.at(lat,lon)
 * 										 .encodedUsing(ISO);
 * </code>
 */
@Immutable
@MarshallType(as="geoPosition2D")
@Accessors(prefix="_")
public class GeoPosition2D
  implements Serializable {

	private static final long serialVersionUID = 3126318415213511386L;
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="standard",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoPositionStandard _standard;

	@MarshallField(as="x",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _x;

	@MarshallField(as="y",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _y;
/////////////////////////////////////////////////////////////////////////////////////////
//  STANDARDS
/////////////////////////////////////////////////////////////////////////////////////////
	public static enum GeoPositionStandard {
		GOOGLE,		// WGS84 / ETRS89
		ED50;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition2D() {
		// default no-args constructor
	}
	public GeoPosition2D(final GeoPositionStandard standard) {
		_standard = standard;
	}
	public GeoPosition2D(final GeoPositionStandard standard,
						 final double x,final double y) {
		this(standard);
		_x = x;
		_y = y;
	}
	public GeoPosition2D(final double x,final double y) {
		this(GeoPositionStandard.GOOGLE,
			 x,y);
	}
	public static GeoPosition2D at(final double x,final double y) {
		return new GeoPosition2D(x,y);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition2D encodedUsing(final GeoPositionStandard std) {
		_standard = std;
		return this;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_standard == null) ? 0 : _standard.hashCode());
		long temp;
		temp = Double.doubleToLongBits(_x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(_y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoPosition2D other = (GeoPosition2D) obj;
		if (_standard != other._standard)
			return false;
		if (Double.doubleToLongBits(_x) != Double.doubleToLongBits(other._x))
			return false;
		if (Double.doubleToLongBits(_y) != Double.doubleToLongBits(other._y))
			return false;
		return true;
	}

}
