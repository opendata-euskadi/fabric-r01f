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
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToDistrict;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoDistrictID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoNeighborhoodCode;
import r01f.types.geo.GeoOIDs.GeoNeighborhoodID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * Geo district data
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
 *		GeoNeighborhood dist = new GeoNeighborhood(GeoNeighborhoodID.forId(34),
 *										   		   LanguageTexts.of(Language.SPANISH,"Abando")
 *													    		.addForLang(Language.ENGLISH,"Abando"),
 *										   		   GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
 * </pre>
  * or
 * <pre class='brush:java'>
 * 		GeoNeighborhood dist = GeoNeighborhood.create(GeoNeighborhoodID.forId(34))
 * 									  .withNameInLang(Language.SPANISH,"Abando")
 * 									  .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *														 		 .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoNeighborhood")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoNeighborhood
     extends GeoLocationBase<GeoNeighborhoodID,GeoNeighborhood>
  implements GeoLocationBelongsToTerritory,
  			 GeoLocationBelongsToState,
  			 GeoLocationBelongsToCounty,
  			 GeoLocationBelongsToRegion,
  			 GeoLocationBelongsToMunicipality,
  			 GeoLocationBelongsToDistrict {

	private static final long serialVersionUID = 436478627196961809L;
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
	
	@MarshallField(as="regionId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoRegionID _regionId;
	
	@MarshallField(as="municipalityId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoMunicipalityID _municipalityId;
	
	@MarshallField(as="districtId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoDistrictID _districtId;
	
	@MarshallField(as="neighborhoodCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoNeighborhoodCode _neighborhoodCode;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoNeighborhood(final GeoNeighborhoodID oid) {
		super(oid);
	}
	public GeoNeighborhood(final GeoNeighborhoodID oid,
					   	   final String officialName,final LanguageTexts nameByLang,
					   	   final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoNeighborhood(final GeoNeighborhoodID oid,
					   	   final LanguageTexts nameByLang,
					   	   final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoNeighborhood(final GeoNeighborhoodID oid,
					   	   final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoNeighborhood(final GeoNeighborhoodID oid,
					   	   final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoNeighborhood create() {
		return new GeoNeighborhood();
	}
	public static GeoNeighborhood create(final GeoNeighborhoodID geoID) {
		GeoNeighborhood outGeo = new GeoNeighborhood();
		outGeo.setId(geoID);
		return outGeo;
	}
}
