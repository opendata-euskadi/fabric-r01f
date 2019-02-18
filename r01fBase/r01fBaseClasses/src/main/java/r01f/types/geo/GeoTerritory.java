package r01f.types.geo;

import lombok.NoArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * Territory
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
 *		GeoTerritory territory = new GeoTerritory(GeoTerritoryID.forId(34),
 *												  LanguageTexts.of(Language.SPANISH,"Bizkaia")
 *														       .addForLang(Language.ENGLISH,"Biscay"),
 *												   GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
  * or:
 * <pre class='brush:java'>
 * 		GeoTerritory territory = GeoTerritory.create(GeoCountryID.forId(34))
 * 											 .withNameInLang(Language.SPANISH,"Spain")
 * 											 .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *													 		            .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoTerritory")
@NoArgsConstructor
public class GeoTerritory
     extends GeoLocationBase<GeoTerritoryID,GeoTerritory> {

	private static final long serialVersionUID = 3765925107420809443L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoTerritory(final GeoTerritoryID ID,final LanguageTextsMapBacked name,final GeoPosition2D position2D) {
		super(ID,name,position2D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoTerritory create() {
		return new GeoTerritory();
	}
	public static GeoTerritory create(final GeoTerritoryID geoID) {
		GeoTerritory outGeo = new GeoTerritory();
		outGeo.setId(geoID);
		return outGeo;
	}
}
