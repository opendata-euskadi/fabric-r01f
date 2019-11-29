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
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

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
 *		GeoCounty dist = new GeoCounty(GeoCountyID.forId(34),
 *										   LanguageTexts.of(Language.SPANISH,"Abando")
 *													    .addForLang(Language.ENGLISH,"Abando"),
 *										   GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
  * or
 * <pre class='brush:java'>
 * 		GeoCounty dist = GeoCounty.create(GeoCountyID.forId(34))
 * 									  .withNameInLang(Language.SPANISH,"Abando")
 * 									  .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *														 		 .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoCounty")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoCounty
     extends GeoLocationBase<GeoCountyID,GeoCounty> 
  implements GeoLocationBelongsToCountry,
  			 GeoLocationBelongsToTerritory,
  			 GeoLocationBelongsToState {

	private static final long serialVersionUID = -2737392708278998225L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="countryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountryID _countryId;
	
	@MarshallField(as="territoryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoTerritoryID _territoryId;
	
	@MarshallField(as="stateId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoStateID _stateId;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCounty(final GeoCountyID oid) {
		super(oid);
	}
	public GeoCounty(final GeoCountyID oid,
					   final String officialName,final LanguageTexts nameByLang,
					  final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoCounty(final GeoCountyID oid,
					   final LanguageTexts nameByLang,
					   final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoCounty(final GeoCountyID oid,
					  final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoCounty(final GeoCountyID oid,
					  final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoCounty create() {
		return new GeoCounty();
	}
	public static GeoCounty create(final GeoCountyID geoID) {
		GeoCounty outGeo = new GeoCounty();
		outGeo.setId(geoID);
		return outGeo;
	}
}
