package r01f.types.geo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.patterns.Supplier;
import r01f.util.types.Strings;
/**
 * Geo catalog model object's oids
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
 * They're modeled after a long code that encapsulates the geo element (country, territory, street, etc code)
 */
public class GeoOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
// 	base
/////////////////////////////////////////////////////////////////////////////////////////
	public interface GeoID
			 extends OIDTyped<String> {
		public long getCode();
		public String asStringPaddedWithZeros(final int numZeros);
	}
	/**
	 * Geo oid
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class GeoIDBase
						 extends OIDBaseMutable<String>
					  implements GeoID {
		private static final long serialVersionUID = 6766060252605584309L;
		public GeoIDBase(final String id) {
			super(id);
		}
		public GeoIDBase(final long id) {
			super(Long.toString(id));
		}
		public GeoIDBase(final Long id) {
			super(id.toString());
		}
		@Override
		public String asStringPaddedWithZeros(final int numZeros) {
			return Strings.leftPad(this.toString(),numZeros,'0');
		}
		@Override
		public long getCode() {
			return Long.parseLong(this.getId());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  oids
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Country
	 */
	@Immutable
	@MarshallType(as="geoCountryId")
	@NoArgsConstructor
	public static class GeoCountryID
				extends GeoIDBase {
		private static final long serialVersionUID = -3806247489287958499L;
		public GeoCountryID(final String oid) {
			super(oid);
		}
		public GeoCountryID(final long oid) {
			super(oid);
		}
		public GeoCountryID(final Long oid) {
			super(oid);
		}
		public static GeoCountryID forId(final String id) {
			return new GeoCountryID(id);
		}
		public static GeoCountryID forId(final long id) {
			return new GeoCountryID(id);
		}
		public static GeoCountryID valueOf(final String str) {
			return new GeoCountryID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
	}
	/**
	 * Territory: historic territory
	 */
	@Immutable
	@MarshallType(as="geoTerritoryId")
	@NoArgsConstructor
	public static class GeoTerritoryID
				extends GeoIDBase {
		private static final long serialVersionUID = -5811800490034132576L;
		public GeoTerritoryID(final String oid) {
			super(oid);
		}
		public GeoTerritoryID(final long oid) {
			super(oid);
		}
		public GeoTerritoryID(final Long oid) {
			super(oid);
		}
		public static GeoTerritoryID forId(final String id) {
			return new GeoTerritoryID(id);
		}
		public static GeoTerritoryID forId(final long id) {
			return new GeoTerritoryID(id);
		}
		public static GeoTerritoryID valueOf(final String str) {
			return new GeoTerritoryID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
	}
	/**
	 * State: historic territory
	 */
	@Immutable
	@MarshallType(as="geoStateId")
	@NoArgsConstructor
	public static class GeoStateID
				extends GeoIDBase {
		private static final long serialVersionUID = -7636328071565337389L;
		public GeoStateID(final String oid) {
			super(oid);
		}
		public GeoStateID(final long oid) {
			super(oid);
		}
		public GeoStateID(final Long oid) {
			super(oid);
		}
		public static GeoStateID forId(final String id) {
			return new GeoStateID(id);
		}
		public static GeoStateID forId(final long id) {
			return new GeoStateID(id);
		}
		public static GeoStateID valueOf(final String str) {
			return new GeoStateID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
	}
	/**
	 * County: provincia
	 */
	@Immutable
	@MarshallType(as="geoCountyId")
	@NoArgsConstructor
	public static class GeoCountyID
				extends GeoIDBase {
		private static final long serialVersionUID = 5383827300254174176L;
		public GeoCountyID(final String oid) {
			super(oid);
		}
		public GeoCountyID(final long oid) {
			super(oid);
		}
		public GeoCountyID(final Long oid) {
			super(oid);
		}
		public static GeoCountyID forId(final String id) {
			return new GeoCountyID(id);
		}
		public static GeoCountyID forId(final long id) {
			return new GeoCountyID(id);
		}
		public static GeoCountyID valueOf(final String str) {
			return new GeoCountyID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
	}
	/**
	 * Region: comarca
	 */
	@Immutable
	@MarshallType(as="geoRegionId")
	@NoArgsConstructor
	public static class GeoRegionID
				extends GeoIDBase {
		private static final long serialVersionUID = -5811800490034132576L;
		public GeoRegionID(final String oid) {
			super(oid);
		}
		public GeoRegionID(final long oid) {
			super(oid);
		}
		public GeoRegionID(final Long oid) {
			super(oid);
		}
		public static GeoRegionID forId(final String id) {
			return new GeoRegionID(id);
		}
		public static GeoRegionID forId(final long id) {
			return new GeoRegionID(id);
		}
		public static GeoRegionID valueOf(final String str) {
			return new GeoRegionID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
	}
	/**
	 * Municipality
	 */
	@Immutable
	@MarshallType(as="geoMunicipalityId")
	@NoArgsConstructor
	public static class GeoMunicipalityID
				extends GeoIDBase {
		private static final long serialVersionUID = -8855341000465307541L;
		public GeoMunicipalityID(final String oid) {
			super(oid);
		}
		public GeoMunicipalityID(final long oid) {
			super(oid);
		}
		public GeoMunicipalityID(final Long oid) {
			super(oid);
		}
		public static GeoMunicipalityID forId(final String id) {
			return new GeoMunicipalityID(id);
		}
		public static GeoMunicipalityID forId(final long id) {
			return new GeoMunicipalityID(id);
		}
		public static GeoMunicipalityID valueOf(final String str) {
			return new GeoMunicipalityID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
	}
	/**
	 * Locality
	 */
	@Immutable
	@MarshallType(as="geoLocalityId")
	@NoArgsConstructor
	public static class GeoLocalityID
				extends GeoIDBase {
		private static final long serialVersionUID = 8445129300980606911L;
		public GeoLocalityID(final String oid) {
			super(oid);
		}
		public GeoLocalityID(final long oid) {
			super(oid);
		}
		public GeoLocalityID(final Long oid) {
			super(oid);
		}
		public static GeoLocalityID forId(final String id) {
			return new GeoLocalityID(id);
		}
		public static GeoLocalityID forId(final long id) {
			return new GeoLocalityID(id);
		}
		public static GeoLocalityID valueOf(final String str) {
			return new GeoLocalityID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
	}
	/**
	 * DistrictCode
	 */
	@Immutable
	@MarshallType(as="geoDistrictCode")
	@NoArgsConstructor
	public static class GeoDistrictCode
				extends GeoIDBase {
		private static final long serialVersionUID = 3897470629554422906L;
		public GeoDistrictCode(final String oid) {
			super(oid);
		}
		public GeoDistrictCode(final long oid) {
			super(oid);
		}
		public GeoDistrictCode(final Long oid) {
			super(oid);
		}
		public static GeoDistrictCode forId(final String id) {
			return new GeoDistrictCode(id);
		}
		public static GeoDistrictCode forId(final long id) {
			return new GeoDistrictCode(id);
		}
		public static GeoDistrictCode valueOf(final String str) {
			return new GeoDistrictCode(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
	}
	/**
	 * District
	 */
	@Immutable
	@MarshallType(as="geoDistrictId")
	@NoArgsConstructor
	public static class GeoDistrictID
				extends GeoIDBase {
		private static final long serialVersionUID = -8855341000465307541L;
		public GeoDistrictID(final String oid) {
			super(oid);
		}
		public static GeoDistrictID forId(final String str) {
			return new GeoDistrictID(str);
		}
		public static GeoDistrictID valueOf(final String str) {
			return new GeoDistrictID(str);
		}
		public static GeoDistrictID from(final GeoCountyID countyId,
								         final GeoMunicipalityID municipalityId,
								         final long neigborhoodId) {
			return GeoDistrictID.forId(Strings.customized("{}{}{}{}",	// 4{2}{3}{8}
									  					  "4",
									  					  countyId.asString(),
									  					  municipalityId.asString(),
									  					  Strings.leftPad(Long.toString(neigborhoodId),8,'0')));
		}
		private static final transient Pattern PATTERN = Pattern.compile("2" +
																		 "([0-9]{2})" +		// county 
																		 "([0-9]{3})" +		// municipality 
																		 "([0-9]{8})");		// district
		private final Memoized<GeoCountyID> _memoCounty = Memoized.using(new Supplier<GeoCountyID>() {
																				@Override
																				public GeoCountyID supply() {
																					Matcher m = PATTERN.matcher(GeoDistrictID.this.getId());
																					if (!m.find()) throw new IllegalArgumentException("District code " + GeoDistrictID.this.getId() + " does NOT match " + PATTERN);
																					return GeoCountyID.valueOf(m.group(1));
																				}
																   });
		private final Memoized<GeoMunicipalityID> _memoMunicipality = Memoized.using(new Supplier<GeoMunicipalityID>() {
																							@Override
																							public GeoMunicipalityID supply() {
																								Matcher m = PATTERN.matcher(GeoDistrictID.this.getId());
																								if (!m.find()) throw new IllegalArgumentException("District code " + GeoDistrictID.this.getId() + " does NOT match " + PATTERN);
																								return GeoMunicipalityID.valueOf(m.group(2));
																							}
																					});
		private final Memoized<Long> _memoNeighborhood = Memoized.using(new Supplier<Long>() {
																				@Override
																				public Long supply() {
																					Matcher m = PATTERN.matcher(GeoDistrictID.this.getId());
																					if (!m.find()) throw new IllegalArgumentException("District code " + GeoDistrictID.this.getId() + " does NOT match " + PATTERN);
																					return Long.parseLong(m.group(3));
																				}
																		});
		public GeoCountyID getCountyId() {
			return _memoCounty.get();
		}
		public GeoMunicipalityID getMunicipalityId() {
			return _memoMunicipality.get();
		}
		public long getNeigborhoodId() {
			return _memoNeighborhood.get();
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(8);
		}
		@Override
		public long getCode() {
			return this.getNeigborhoodId();
		}
	}
	
	/**
	 * District
	 */
	@Immutable
	@MarshallType(as="geoNeighborhoodId")
	@NoArgsConstructor
	public static class GeoNeighborhoodID
				extends GeoIDBase {
		private static final long serialVersionUID = -1210892690137358726L;
		public GeoNeighborhoodID(final String oid) {
			super(oid);
		}
		public static GeoNeighborhoodID forId(final String str) {
			return new GeoNeighborhoodID(str);
		}
		public static GeoNeighborhoodID valueOf(final String str) {
			return new GeoNeighborhoodID(str);
		}
		public static GeoNeighborhoodID from(final GeoCountyID countyId,
								  		 	 final GeoMunicipalityID municipalityId,
								  		 	 final long neigborhoodId) {
			return GeoNeighborhoodID.forId(Strings.customized("{}{}{}{}",	// 4{2}{3}{8}
											  				  "4",
											  				  countyId.asString(),
											  				  municipalityId.asString(),
											  				  Strings.leftPad(Long.toString(neigborhoodId),8,'0')));
		}
		private static final transient Pattern PATTERN = Pattern.compile("4" +
																		 "([0-9]{2})" +		// county 
																		 "([0-9]{3})" +		// municipality 
																		 "([0-9]{8})");		// neighborhood
		private final Memoized<GeoCountyID> _memoCounty = Memoized.using(new Supplier<GeoCountyID>() {
																				@Override
																				public GeoCountyID supply() {
																					Matcher m = PATTERN.matcher(GeoNeighborhoodID.this.getId());
																					if (!m.find()) throw new IllegalArgumentException("Neighborhood code " + GeoNeighborhoodID.this.getId() + " does NOT match " + PATTERN);
																					return GeoCountyID.valueOf(m.group(1));
																				}
																   });
		private final Memoized<GeoMunicipalityID> _memoMunicipality = Memoized.using(new Supplier<GeoMunicipalityID>() {
																							@Override
																							public GeoMunicipalityID supply() {
																								Matcher m = PATTERN.matcher(GeoNeighborhoodID.this.getId());
																								if (!m.find()) throw new IllegalArgumentException("Neighborhood code " + GeoNeighborhoodID.this.getId() + " does NOT match " + PATTERN);
																								return GeoMunicipalityID.valueOf(m.group(2));
																							}
																					});
		private final Memoized<Long> _memoNeighborhood = Memoized.using(new Supplier<Long>() {
																				@Override
																				public Long supply() {
																					Matcher m = PATTERN.matcher(GeoNeighborhoodID.this.getId());
																					if (!m.find()) throw new IllegalArgumentException("Neighborhood code " + GeoNeighborhoodID.this.getId() + " does NOT match " + PATTERN);
																					return Long.parseLong(m.group(3));
																				}
																		});
		public GeoCountyID getCountyId() {
			return _memoCounty.get();
		}
		public GeoMunicipalityID getMunicipalityId() {
			return _memoMunicipality.get();
		}
		public long getNeigborhoodId() {
			return _memoNeighborhood.get();
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(8);
		}
		@Override
		public long getCode() {
			return this.getNeigborhoodId();
		}
	}
	/**
	 * Locality
	 */
	@Immutable
	@MarshallType(as="geoNeighborhoodCode")
	@NoArgsConstructor
	public static class GeoNeighborhoodCode
				extends GeoIDBase {
		private static final long serialVersionUID = -6250817034170766150L;
		
		public GeoNeighborhoodCode(final String oid) {
			super(oid);
		}
		public GeoNeighborhoodCode(final long oid) {
			super(oid);
		}
		public GeoNeighborhoodCode(final Long oid) {
			super(oid);
		}
		public static GeoNeighborhoodCode forId(final String id) {
			return new GeoNeighborhoodCode(id);
		}
		public static GeoNeighborhoodCode forId(final long id) {
			return new GeoNeighborhoodCode(id);
		}
		public static GeoNeighborhoodCode valueOf(final String str) {
			return new GeoNeighborhoodCode(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(2);
		}
	}
	/**
	 * Street
	 */
	@Immutable
	@MarshallType(as="geoStreetId")
	@NoArgsConstructor
	public static class GeoStreetID
				extends GeoIDBase {
		private static final long serialVersionUID = 8671822814362300783L;
		public GeoStreetID(final long oid) {
			super(oid);
		}
		public GeoStreetID(final Long oid) {
			super(oid);
		}
		public static GeoStreetID forId(final long id) {
			return new GeoStreetID(id);
		}
		public static GeoStreetID valueOf(final String str) {
			return new GeoStreetID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
	}
	/**
	 * Portal
	 */
	@Immutable
	@MarshallType(as="geoPortalId")
	@NoArgsConstructor
	public static class GeoPortalID
				extends GeoIDBase {
		private static final long serialVersionUID = -1968008874772952156L;
		public GeoPortalID(final long oid) {
			super(oid);
		}
		public GeoPortalID(final Long oid) {
			super(oid);
		}
		public static GeoPortalID forId(final long id) {
			return new GeoPortalID(id);
		}
		public static GeoPortalID valueOf(final String str) {
			return new GeoPortalID(Long.parseLong(str));
		}
		@Override
		public String asString() {
			return this.asStringPaddedWithZeros(3);
		}
	}
	/**
	 * Zip (postal code)
	 */
	@Immutable
	@MarshallType(as="geoZipCode")
	@NoArgsConstructor
	public static class GeoZipCode
				extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -3531756242105659769L;
		public GeoZipCode(final String oid) {
			super(oid);
		}
		public static GeoZipCode forId(final String id) {
			return new GeoZipCode(id);
		}
		public static GeoZipCode valueOf(final String str) {
			return new GeoZipCode(str);
		}
	}
}
