package r01f.types.geo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * Country data
 * <pre>
 * Territory											Europe
 *   |_Country											Spain										
 *   	 |_State										Euskadi
 *   		 |_County									Bizkaia
 *   		 	|_Region								Gran Bilbao / valles alaveses
 *   				|_Municipality						Bilbao
 *  					|_Locality						Bilbao	
 *   						|_District					01	
 *   							|_Neighborhood 			Abando
 *   								|_Street			General Concha
 *   									|_portal		12
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
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoCountry
     extends GeoLocationBase<GeoCountryID,GeoCountry> 
  implements GeoLocationBelongsToTerritory {

	private static final long serialVersionUID = -4324327721026938576L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="territoryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoTerritoryID _territoryId;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCountry(final GeoCountryID oid) {
		super(oid);
	}
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
