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
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * Territory
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
@MarshallType(as="geoRegion")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoRegion
     extends GeoLocationBase<GeoRegionID,GeoRegion> 
  implements GeoLocationBelongsToCountry,
  			 GeoLocationBelongsToTerritory,
  			 GeoLocationBelongsToState,
  			 GeoLocationBelongsToCounty {

	private static final long serialVersionUID = 3765925107420809443L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="territoryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoTerritoryID _territoryId;
	
	@MarshallField(as="countryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountryID _countryId;
	
	@MarshallField(as="stateId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoStateID _stateId;
	
	@MarshallField(as="countyId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountyID _countyId;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoRegion(final GeoRegionID oid) {
		super(oid);
	}
	public GeoRegion(final GeoRegionID oid,
				     final String officialName,final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoRegion(final GeoRegionID oid,
				     final LanguageTexts nameByLang,
					 final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoRegion(final GeoRegionID oid,
					 final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoRegion(final GeoRegionID oid,
					 final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoRegion create() {
		return new GeoRegion();
	}
	public static GeoRegion create(final GeoRegionID geoID) {
		GeoRegion outGeo = new GeoRegion();
		outGeo.setId(geoID);
		return outGeo;
	}
}
