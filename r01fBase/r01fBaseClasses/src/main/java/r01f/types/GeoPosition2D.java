package r01f.types;

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
	@Getter @Setter private GeoPositionStandad _standard;

	@MarshallField(as="latitude",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _latitude;

	@MarshallField(as="longitude",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private double _longitude;
/////////////////////////////////////////////////////////////////////////////////////////
//  STANDARDS
/////////////////////////////////////////////////////////////////////////////////////////
	public static enum GeoPositionStandad {
		GOOGLE,
		ISO;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition2D() {
		// default no-args constructor
	}
	public GeoPosition2D(final GeoPositionStandad standard,
						 final double lat,final double lon) {
		_standard = standard;
		_latitude = lat;
		_longitude = lon;
	}
	public GeoPosition2D(final double lat,final double lon) {
		this(GeoPositionStandad.ISO,
			 lat,lon);
	}
	public static GeoPosition2D at(final double lat,final double lon) {
		return new GeoPosition2D(lat,lon);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition2D encodedUsing(final GeoPositionStandad std) {
		_standard = std;
		return this;
	}
}
