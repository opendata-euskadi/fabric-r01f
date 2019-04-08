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
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * Municipality
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
 *		GeoMunicipality mun = new GeoLocality(GeoMunicipalityID.forId(34),
 *											  LanguageTexts.of(Language.SPANISH,"Bilbao")
 *														   .addForLang(Language.ENGLISH,"Bilbao"),
 *											  GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE).setLocation(lat,lon));
  * or
 * <pre class='brush:java'>
 * 		GeoMunicipality mun = GeoMunicipality.create(GeoMunicipalityID.forId(34))
 * 											 .withNameInLang(Language.SPANISH,"Bilbao")
 * 											 .positionedAt(GeoPosition2D.usingStandard(GeoPositionStandad.GOOGLE)
 *														 		 		.setLocation(lat,lon);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="geoMunicipality")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoMunicipality
     extends GeoLocationBase<GeoMunicipalityID,GeoMunicipality> 
  implements GeoLocationBelongsToTerritory,
  			 GeoLocationBelongsToState,
  			 GeoLocationBelongsToCounty,
  			 GeoLocationBelongsToRegion {

	private static final long serialVersionUID = 3066750375607990432L;
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
	
	@MarshallField(as="countyId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountyID _countyId;
	
	@MarshallField(as="regionId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoRegionID _regionId;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoMunicipality(final GeoMunicipalityID oid,
						   final String officialName,final LanguageTexts nameByLang,
						   final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoMunicipality(final GeoMunicipalityID oid,
					   	   final LanguageTexts nameByLang,
					   	   final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoMunicipality(final GeoMunicipalityID oid,
					   	   final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoMunicipality(final GeoMunicipalityID oid,
					   	   final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoMunicipality create() {
		return new GeoMunicipality();
	}
	public static GeoMunicipality create(final GeoMunicipalityID geoID) {
		GeoMunicipality outGeo = new GeoMunicipality();
		outGeo.setId(geoID);
		return outGeo;
	}
}
