package r01f.types.geo;

import lombok.NoArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;

/**
 * Municipality
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
 *		GeoMunicipality mun = new GeoLocality(GeoMunicipalityID.forId(34),
 *											  LanguageTexts.of(Language.SPANISH,"Bilbao")
 *														   .addForLang(Language.ENGLISH,"Bilbao"),
 *											  GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
  * or
 * <pre class='brush:java'>
 * 		GeoMunicipality mun = GeoMunicipality.create(GeoMunicipalityID.forId(34))
 * 											 .withNameInLang(Language.SPANISH,"Bilbao")
 * 											 .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *														 		 		.setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoMunicipality")
@NoArgsConstructor
public class GeoMunicipality
     extends GeoLocationBase<GeoMunicipalityID,GeoMunicipality> {

	private static final long serialVersionUID = 3066750375607990432L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoMunicipality(final GeoMunicipalityID ID,final LanguageTextsMapBacked name,final GeoPosition2D position2D) {
		super(ID,name,position2D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoMunicipality create() {
		return new GeoMunicipality();
	}
	public static GeoMunicipality create(final GeoMunicipalityID geoID) {
		GeoMunicipality outGeo = new GeoMunicipality();
		outGeo.setId(geoID);
		return outGeo;
	}
}
