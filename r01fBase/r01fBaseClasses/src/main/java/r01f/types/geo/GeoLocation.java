package r01f.types.geo;

import java.io.Serializable;

import r01f.debug.Debuggable;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCountry;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToCounty;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToDistrict;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToMunicipality;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToNeighborhood;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToRegion;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToState;
import r01f.types.geo.GeoFacets.GeoLocationBelongsToTerritory;
import r01f.types.geo.GeoOIDs.GeoID;

public interface GeoLocation<GID extends GeoID> 
		 extends HasLangDependentNamedFacet,
		 		 Debuggable,
				 Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public GID getId();
	public void setId(final GID id);
	
	public String getOfficialName();
	public void setOfficialName(final String name);
	
	public GeoPosition2D getPosition2D();
	public void setPosition2D(final GeoPosition2D pos);
/////////////////////////////////////////////////////////////////////////////////////////
// Territory											Europe
//  |_Country											Spain
//  	 |_State										Euskadi
//  		 |_County									Bizkaia
//  		 	|_Region								Gran Bilbao / valles alaveses
//  				|_Municipality						Bilbao
//  					|_District						01	
//  						|_Neighborhood 				Abando
//  							|_Street				General Concha
//  								|_portal			12
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isBelongsToTerritory();
	public GeoLocationBelongsToTerritory asBelongsToTerritory();
	
	public boolean isBelongsToCountry();
	public GeoLocationBelongsToCountry asBelongsToCountry();
	
	public boolean isBelongsToState();
	public GeoLocationBelongsToState asBelongsToState();
	
	public boolean isBelongsToCounty();
	public GeoLocationBelongsToCounty asBelongsToCounty();
	
	public boolean isBelongsToRegion();
	public GeoLocationBelongsToRegion asBelongsToRegion();
	
	public boolean isBelongsToMunicipality();
	public GeoLocationBelongsToMunicipality asBelongsToMunicipality();
	
	public boolean isBelongsToDistrict();
	public GeoLocationBelongsToDistrict asBelongsToDistrict();
	
	public boolean isBelongsToNeighborhood();
	public GeoLocationBelongsToNeighborhood asBelongsToNeighborhood();
}
