package r01f.types.geo;

import lombok.NoArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoLocalityID;

/**
 * Locality
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
 *		GeoLocality loc = new GeoLocality(GeoLocalityID.forId(34),
 *										  LanguageTexts.of(Language.SPANISH,"Duranguesado")
 *													   .addForLang(Language.ENGLISH,"Duranguesado"),
 *										  GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
  * or
 * <pre class='brush:java'>
 * 		GeoLocality loc = GeoLocality.create(GeoLocalityID.forId(34))
 * 									 .withNameInLang(Language.SPANISH,"Duranguesado")
 * 									 .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *														 		.setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoLocality")
@NoArgsConstructor
public class GeoLocality
     extends GeoLocationBase<GeoLocalityID,GeoLocality> {

	private static final long serialVersionUID = -2036872631219754805L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoLocality(final GeoLocalityID ID,final LanguageTextsMapBacked name,final GeoPosition2D position2D) {
		super(ID,name,position2D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoLocality create() {
		return new GeoLocality();
	}
	public static GeoLocality create(final GeoLocalityID geoID) {
		GeoLocality outGeo = new GeoLocality();
		outGeo.setId(geoID);
		return outGeo;
	}
}
