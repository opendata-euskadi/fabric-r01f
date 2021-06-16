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
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * Locality
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
 *		GeoLocality loc = new GeoLocality(GeoMunicipalityID.forId(34),
 *										  LanguageTexts.of(Language.SPANISH,"Bilbao")
 *													   .addForLang(Language.ENGLISH,"Bilbao"),
 *										  GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
  * or
 * <pre class='brush:java'>
 * 		GeoLocality loc = GeoLocality.create(GeoMunicipalityID.forId(34))
 * 									 .withNameInLang(Language.SPANISH,"Bilbao")
 * 									 .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *									 .setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoLocality")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoLocality 
     extends GeoLocationBase<GeoLocalityID,GeoLocality> 
  implements GeoLocationBelongsToTerritory,
  			 GeoLocationBelongsToCountry,
  			 GeoLocationBelongsToState,
  			 GeoLocationBelongsToCounty,
  			 GeoLocationBelongsToRegion,
  			 GeoLocationBelongsToMunicipality {
	
	private static final long serialVersionUID = 1915717178768446718L;
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
	
	@MarshallField(as="localityId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoLocalityID _localityId;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoLocality(final GeoLocalityID oid) {
		super(oid);
	}
	public GeoLocality(final GeoLocalityID oid,
					   final String officialName,final LanguageTexts nameByLang,
					   final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoLocality(final GeoLocalityID oid,
					   final LanguageTexts nameByLang,
					   final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoLocality(final GeoLocalityID oid,
					   final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoLocality(final GeoLocalityID oid,
					   final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoLocality create() {
		return new GeoLocality();
	}
	public static GeoLocality create(final GeoLocalityID geoID) {
		GeoLocality outGeo = new GeoLocality();
		outGeo.setId(geoID);
		return outGeo;
	}
}
