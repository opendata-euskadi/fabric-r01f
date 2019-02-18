package r01f.types.geo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoStreetID;

/**
 * Street
 * <pre>
 * Country
 *   |_Territory
 *   	 |_State
 *   		 |_Locality
 *   			|_Municipality
 *   				|_District
 *   					|_Street
 *   						|_portal
 * </pre>
 * <pre class='brush:java'>
 *		GeoStreet street = new GeoStreet(GeoStreetID.forId(34),
 *									     LanguageTexts.of(Language.SPANISH,"General Concha")
 *												      .addForLang(Language.ENGLISH,"General Concha"),
 *										 GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
   * O tambien:
 * <pre class='brush:java'>
 * 		GeoStreet street = GeoStreet.create(GeoStreetID.forId(34))
 * 								    .withNameInLang(Language.SPANISH,"Bilbao")
 * 								    .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *											 		 		   .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoStreet")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoStreet
     extends GeoLocationBase<GeoStreetID,GeoStreet> {

	private static final long serialVersionUID = 7121715785159226136L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="number",
				   escape=true)
	@Getter @Setter private String _number;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoStreet(final GeoStreetID ID,final LanguageTextsMapBacked name,final GeoPosition2D position2D) {
		super(ID,name,position2D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoStreet create() {
		return new GeoStreet();
	}
	public static GeoStreet create(final GeoStreetID geoID) {
		GeoStreet outGeo = new GeoStreet();
		outGeo.setId(geoID);
		return outGeo;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoStreet number(final String number) {
		_number = number;
		return this;
	}
}
