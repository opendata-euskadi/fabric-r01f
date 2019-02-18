package r01f.types.geo;

import lombok.NoArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTextsMapBacked;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoStateID;

/**
 * State
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
 *		GeoState state = new GeoState(GeoStateID.forId(34),
 *									  LanguageTexts.of(Language.SPANISH,"Bizkaia")
 *												   .addForLang(Language.ENGLISH,"Biscay"),
 *									  GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
   * O tambien:
 * <pre class='brush:java'>
 * 		GeoState state = GeoState.create(GeoStateID.forId(34))
 * 								 .withNameInLang(Language.SPANISH,"Bilbao")
 * 								 .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *														 	.setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoState")
@NoArgsConstructor
public class GeoState
     extends GeoLocationBase<GeoStateID,GeoState> {

	private static final long serialVersionUID = -7592357470993330900L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoState(final GeoStateID ID,final LanguageTextsMapBacked name,final GeoPosition2D position2D) {
		super(ID,name,position2D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoState create() {
		return new GeoState();
	}
	public static GeoState create(final GeoStateID geoID) {
		GeoState outGeo = new GeoState();
		outGeo.setId(geoID);
		return outGeo;
	}
}
