package r01f.types.geo;


import lombok.NoArgsConstructor;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoOIDs.GeoCountryID;

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
 *		GeoCountry country = new R01MGeoCountry(GeoCountryID.forId(34),
 *											    LanguageTexts.of(Language.SPANISH,"Spain")
 *															 .addForLang(Language.ENGLISH,"Spain"),
 *												GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *															 .setLocation(lat,lon));
 * </pre>
 * or
 * <pre class='brush:java'>
 * 		GeoCountry country = GeoCountry.create(R01MGeoCountryID.forId(34))
 * 									   .withNameInLang(Language.SPANISH,"Spain")
 * 									   .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *													 		  	  .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoCountry")
@NoArgsConstructor
public class GeoCountry
     extends GeoLocationBase<GeoCountryID,GeoCountry> {

	private static final long serialVersionUID = -4324327721026938576L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCountry(final GeoCountryID oid,
					  final String officialName,final LanguageTexts nameByLang,
					  final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoCountry(final GeoCountryID oid,
					 final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoCountry(final GeoCountryID oid,
					  final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoCountry(final GeoCountryID oid,
					  final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoCountry create() {
		return new GeoCountry();
	}
	public static GeoCountry create(final GeoCountryID geoOid) {
		GeoCountry outGeo = new GeoCountry();
		outGeo.setId(geoOid);
		return outGeo;
	}
}
