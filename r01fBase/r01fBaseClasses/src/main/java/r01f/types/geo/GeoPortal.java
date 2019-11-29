package r01f.types.geo;


import lombok.NoArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoOIDs.GeoPortalID;

/**
 * Country data
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
 *		GeoPortal portal = new GeoPortalID(GeoPortalID.forId(34),
 *										   LanguageTexts.of(Language.SPANISH,"Portal 5")
 *													 	.addForLang(Language.ENGLISH,"5th portal"),
 *											GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *													     .setLocation(lat,lon));
 * </pre>
 * or
 * <pre class='brush:java'>
 * 		GeoPortal portal = GeoPortal.create(R01MGeoCountryID.forId(34))
 * 									.withNameInLang(Language.SPANISH,"Portal 5")
 * 									.positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *													 		   .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoPortal")
@NoArgsConstructor
public class GeoPortal
     extends GeoLocationBase<GeoPortalID,GeoPortal> {

	private static final long serialVersionUID = 3510450562804176082L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoPortal(final GeoPortalID oid) {
		super(oid);
	}
	public GeoPortal(final GeoPortalID oid,
					 final String officialName,final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoPortal(final GeoPortalID oid,
					 final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoPortal(final GeoPortalID oid,
					 final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoPortal(final GeoPortalID oid,
					 final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoPortal create() {
		return new GeoPortal();
	}
	public static GeoPortal create(final GeoPortalID geoOid) {
		GeoPortal outGeo = new GeoPortal();
		outGeo.setId(geoOid);
		return outGeo;
	}
}
