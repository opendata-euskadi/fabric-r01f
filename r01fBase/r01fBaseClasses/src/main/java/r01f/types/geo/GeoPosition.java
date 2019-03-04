package r01f.types.geo;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoOIDs.GeoZipCode;

/**
 * Data about a geographical point
 * <pre>
 *		- x,y
 *      - Country
 *          |_Territory
 *          	 |_State
 *          		 |_County
 *          		 	|_Region
 *          				|_Municipality
 *          					|_District
 *          						|_Street
 *          							|_portal
 *		- Textual info
 * </pre>
 * Uso:
 * <pre class='brush:java'>
 *		GeoPosition pos = GeoPosition.create()
 *							 .withPosition(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE))	
 *							 .withCountry(GeoCountry.create(GeoCountryID.forId(34))
 *	 							   			   .withNameInLang(Language.SPANISH,"Spain") 
 *	 							   			   .positionedAt(GeoPosition2D.at(2,2).encodedUsing(GeoPositionStandad.GOOGLE)));
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoPosition")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoPosition
  implements Serializable {

	private static final long serialVersionUID = 8722622151577217898L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Country
	 */
	@MarshallField(as="country")
	@Getter @Setter private GeoCountry _country;
	/**
	 * Territory (groups more than a single state)
	 */
	@MarshallField(as="territory")
	@Getter @Setter private GeoTerritory _territory;
	/**
	 * State or province
	 */
	@MarshallField(as="state")
	@Getter @Setter private GeoState _state;
	/**
	 * Locality / region (groups some municipalities)
	 */
	@MarshallField(as="locality")
	@Getter @Setter private GeoLocality _locality;
	/**
	 * Municipality
	 */
	@MarshallField(as="municipality")
	@Getter @Setter private GeoMunicipality _municipality;
	/**
	 * District
	 */
	@MarshallField(as="district")
	@Getter @Setter private GeoDistrict _district;
	/**
	 * Street
	 */
	@MarshallField(as="street")
	@Getter @Setter private GeoStreet _street;
	/**
	 * Street
	 */
	@MarshallField(as="portal")
	@Getter @Setter private GeoPortal _portal;
	/**
	 * Zip code
	 */
	@MarshallField(as="zipCode")
	@Getter @Setter private GeoZipCode _zipCode;
	/**
	 * Stairway
	 */
	@MarshallField(as="stairWay")
	@Getter @Setter private String _stairWay;
	/**
	 * Floor
	 */
	@MarshallField(as="floor")
	@Getter @Setter private String _floor;
	/**
	 * door
	 */
	@MarshallField(as="door")
	@Getter @Setter private String _door;
	/**
	 * X,Y position
	 */
	@MarshallField(as="position2D")
	@Getter @Setter private GeoPosition2D _position;
	/**
	 * Textual directions (language dependent)
	 */
	@MarshallField(as="directions")
	@Getter @Setter private LanguageTexts _directions;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoPosition create() {
		GeoPosition outPos = new GeoPosition();
		return outPos;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPosition withPosition(final GeoPosition2D pos) {
		_position = pos;
		return this;
	}
	public GeoPosition withCountry(final GeoCountry country) {
		_country = country;
		return this;
	}
	public GeoPosition withTerritory(final GeoTerritory territory) {
		_territory = territory;
		return this;
	}
	public GeoPosition withState(final GeoState state) {
		_state = state;
		return this;
	}
	public GeoPosition withLocality(final GeoLocality loc) {
		_locality = loc;
		return this;
	}
	public GeoPosition withMunicipality(final GeoMunicipality mun) {
		_municipality = mun;
		return this;
	}
	public GeoPosition withDistrict(final GeoDistrict dist) {
		_district = dist;
		return this;
	}
	public GeoPosition withStreet(final GeoStreet street) {
		_street = street;
		return this;
	}
	public GeoPosition withPortal(final GeoPortal portal) {
		_portal = portal;
		return this;
	}
	public GeoPosition withStairWay(final String stairWay) {
		_stairWay = stairWay;
		return this;
	}
	public GeoPosition withFloor(final String floor) {
		_floor = floor;
		return this;
	}
	public GeoPosition withDoor(final String door) {
		_door = door;
		return this;
	}
	public GeoPosition withZipCode(final GeoZipCode zipCode) {
		_zipCode = zipCode;
		return this;
	}
	public GeoPosition withDirections(final LanguageTexts directions) {
		_directions = directions;
		return this;
	}
}
