package r01f.types.geo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
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
//   Country
//     |_Territory
//     	 |_State
//     		 |_County
//     		 	|_Region
//     				|_Municipality
//     					|_District
//     						|_Street
//     							|_portal
/////////////////////////////////////////////////////////////////////////////////////////
	public interface GeoLocationBelongsToCountry {
		public GeoCountryID getCountryId();
		public void setCountryId(final GeoCountryID countryId);
	}
	public interface GeoLocationBelongsToTerritory {
		public GeoTerritoryID getTerritoryId();
		public void setTerritoryId(final GeoTerritoryID territoryId);
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
}
