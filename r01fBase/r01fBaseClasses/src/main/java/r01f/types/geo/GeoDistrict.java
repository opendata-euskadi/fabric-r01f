package r01f.types.geo;

import lombok.NoArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoOIDs.GeoDistrictID;

/**
 * Geo district data
 * <pre>
 * Country
 *   |_Territory
 *   	 |_State
 *   		 |_County
 *   		 	|_Region
 *   				|_Municipality
 *   					|_District
 *   						|_Street
 *   							|_portal
 * </pre>
 * <pre class='brush:java'>
 *		GeoDistrict dist = new GeoDistrict(GeoDistrictID.forId(34),
 *										   LanguageTexts.of(Language.SPANISH,"Abando")
 *													    .addForLang(Language.ENGLISH,"Abando"),
 *										   GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
  * or
 * <pre class='brush:java'>
 * 		GeoDistrict dist = GeoDistrict.create(GeoDistrictID.forId(34))
 * 									  .withNameInLang(Language.SPANISH,"Abando")
 * 									  .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *														 		 .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoDistrict")
@NoArgsConstructor
public class GeoDistrict
     extends GeoLocationBase<GeoDistrictID,GeoDistrict> {

	private static final long serialVersionUID = -2737392708278998225L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoDistrict(final GeoDistrictID oid,
					   final String officialName,final LanguageTexts nameByLang,
					  final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoDistrict(final GeoDistrictID oid,
					   final LanguageTexts nameByLang,
					   final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoDistrict(final GeoDistrictID oid,
					  final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoDistrict(final GeoDistrictID oid,
					  final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoDistrict create() {
		return new GeoDistrict();
	}
	public static GeoDistrict create(final GeoDistrictID geoID) {
		GeoDistrict outGeo = new GeoDistrict();
		outGeo.setId(geoID);
		return outGeo;
	}
}
