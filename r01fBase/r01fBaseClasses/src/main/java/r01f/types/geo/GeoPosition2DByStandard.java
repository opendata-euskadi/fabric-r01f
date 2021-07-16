package r01f.types.geo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoPosition2D.GeoPositionStandard;

/**
 *  Collection Position in different formats
 *  new GeoPosition2DByStandard().add( GeoPosition2D.at(5,6)
 									 		    .encodedUsing(GeoPositionStandard.ED50))
    		                 	  .add( GeoPosition2D.at(5,6)
 									            .encodedUsing(GeoPositionStandard.GOOGLE))
	 * {
		  "position" : {
		    "ED50" : {
		      "standard" : "ED50",
		      "x" : 5.9
		      "y" : 6.0
		    },
		    "GOOGLE" : {
		      "standard" : "GOOGLE",
		      "x" : 5.0,
		      "y" : 6.9
		    }
		  }
		}

 * </pre>
 */
@MarshallType(as="geoPositionByStandard")
@Accessors(prefix="_")
public class GeoPosition2DByStandard
  implements Serializable {

	private static final long serialVersionUID = 7132954105884302430L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="position")
	@Getter(AccessLevel.PRIVATE) private LinkedHashMap<GeoPositionStandard,GeoPosition2D> _positionByFormats;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition2DByStandard() {
		_positionByFormats = new LinkedHashMap<GeoPositionStandard,GeoPosition2D>();
	}
	public GeoPosition2DByStandard add(final GeoPosition2D  pos) {
		if (pos == null) return this;
		_positionByFormats.put(pos.getStandard(),pos);
		return this;
	}
	public void forEach(BiConsumer<? super GeoPositionStandard, ? super GeoPosition2D> action) {
		_positionByFormats.forEach(action);
	}
	public boolean contains(final GeoPositionStandard  geoPositionStandard) {
		return _positionByFormats.containsKey(geoPositionStandard);
	}
	public GeoPosition2D get(final GeoPositionStandard  geoPositionStandard) {
		if (contains(geoPositionStandard)) {
			return _positionByFormats.get(geoPositionStandard);
		}
		return null;
	}
}