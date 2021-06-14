package r01f.types.geo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoDistrictID;
import r01f.types.geo.GeoOIDs.GeoLocalityID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoNeighborhoodID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class GeoFacets {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public interface HasGeoPosition {
		public GeoPosition getGeoPosition();
		public void setGeoPosition(final GeoPosition position);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// Territory											Europe
//   |_Country											Spain										
//   	 |_State										Euskadi
//   		 |_County									Bizkaia
//   		 	|_Region								Gran Bilbao / valles alaveses
//   				|_Municipality						Bilbao
//  					|_Locality						Bilbao	
//   						|_District					01	
//   							|_Neighborhood 			Abando
//   								|_Street			General Concha
//   									|_portal		12
/////////////////////////////////////////////////////////////////////////////////////////
	public interface GeoLocationBelongsToTerritory {
		public GeoTerritoryID getTerritoryId();
		public void setTerritoryId(final GeoTerritoryID territoryId);
	}
	public interface GeoLocationBelongsToCountry {
		public GeoCountryID getCountryId();
		public void setCountryId(final GeoCountryID countryId);
	}
	public interface GeoLocationBelongsToState {
		public GeoStateID getStateId();
		public void setStateId(final GeoStateID stateId);
	}
	public interface GeoLocationBelongsToCounty {
		public GeoCountyID getCountyId();
		public void setCountyId(final GeoCountyID countyId);
	}
	public interface GeoLocationBelongsToRegion {
		public GeoRegionID getRegionId();
		public void setRegionId(final GeoRegionID regionId);
	}
	public interface GeoLocationBelongsToMunicipality {
		public GeoMunicipalityID getMunicipalityId();
		public void setMunicipalityId(final GeoMunicipalityID munId);
	}
	public interface GeoLocationBelongsToLocality {
		public GeoLocalityID getLocalityId();
		public void setLocalityId(final GeoLocalityID localityId);
	}
	public interface GeoLocationBelongsToDistrict {
		public GeoDistrictID getDistrictId();
		public void setDistrictId(final GeoDistrictID districtId);
	}
	public interface GeoLocationBelongsToNeighborhood {
		public GeoNeighborhoodID getNeighborhoodId();
		public void setNeighborhoodId(final GeoNeighborhoodID neighborhoodId);
	}
}
