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
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCountry;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * State
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
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoState
     extends GeoLocationBase<GeoStateID,GeoState> 
  implements GeoLocationBelongsToCountry,
  			 GeoLocationBelongsToTerritory {

	private static final long serialVersionUID = -7592357470993330900L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="countryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountryID _countryId;
	
	@MarshallField(as="territoryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoTerritoryID _territoryId;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoState(final GeoStateID oid,
					final String officialName,final LanguageTexts nameByLang,
					final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoState(final GeoStateID oid,
					final LanguageTexts nameByLang,
					final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoState(final GeoStateID oid,
					final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoState(final GeoStateID oid,
					final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
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
