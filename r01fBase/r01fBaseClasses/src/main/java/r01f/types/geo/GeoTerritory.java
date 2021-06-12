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
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoTerritoryID;

/**
 * Territory
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
@MarshallType(as="geoTerritory")
@Accessors(prefix="_")
@NoArgsConstructor
public class GeoTerritory
     extends GeoLocationBase<GeoTerritoryID,GeoTerritory> {

	private static final long serialVersionUID = 3765925107420809443L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="countryId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private GeoCountryID _countryId; 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoTerritory(final GeoTerritoryID oid) {
		super(oid);
	}
	public GeoTerritory(final GeoTerritoryID oid,
						final String officialName,final LanguageTexts nameByLang,
					    final GeoPosition2D position2D) {
		super(oid,
			  officialName,nameByLang,
			  position2D);
	}
	public GeoTerritory(final GeoTerritoryID oid,
						final LanguageTexts nameByLang,
					    final GeoPosition2D position2D) {
		super(oid,
			  null,nameByLang,	// no official name
			  position2D);
	}
	public GeoTerritory(final GeoTerritoryID oid,
					 	final String officialName,final LanguageTexts nameByLang) {
		super(oid,
			  officialName,nameByLang,
			  null);	// no position
	}
	public GeoTerritory(final GeoTerritoryID oid,
					 	final LanguageTexts nameByLang) {
		super(oid,
			  null,nameByLang,	// no official name
			  null);			// no position
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoTerritory create() {
		return new GeoTerritory();
	}
	public static GeoTerritory create(final GeoTerritoryID geoID) {
		GeoTerritory outGeo = new GeoTerritory();
		outGeo.setId(geoID);
		return outGeo;
	}
}
