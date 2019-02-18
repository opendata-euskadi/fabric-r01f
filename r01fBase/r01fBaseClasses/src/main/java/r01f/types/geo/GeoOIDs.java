package r01f.types.geo;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
/**
 * Geo catalog model object's oids
 * <pre>
 * Country
 *   |_Territory
 *   	 |_State
 *   		 |_Locality
 *   			|_Municipality
 *   				|_District
 *   					|_Street
 * They're modeled after a long code that encapsulates the geo element (country, territory, street, etc code)
 * </pre>
 */
public class GeoOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
// 	base
/////////////////////////////////////////////////////////////////////////////////////////
	public interface GeoID
			 extends OIDTyped<Long> {
		/* just a marker interface */
	}
	/**
	 * Geo oid
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class GeoIDBase
						 extends OIDBaseMutable<Long>
					  implements GeoID {
		private static final long serialVersionUID = 6766060252605584309L;
		public GeoIDBase(final long id) {
			super(id);
		}
		public GeoIDBase(final Long id) {
			super(id);
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
		public GeoCountryID(final long oid) {
			super(oid);
		}
		public GeoCountryID(final Long oid) {
			super(oid);
		}
		public static GeoCountryID forId(final long id) {
			return new GeoCountryID(id);
		}
		public static GeoCountryID valueOf(final String str) {
			return new GeoCountryID(Long.parseLong(str));
		}
	}
	/**
	 * Territory
	 */
	@Immutable
	@MarshallType(as="geoTerritoryId")
	@NoArgsConstructor
	public static class GeoTerritoryID
				extends GeoIDBase {
		private static final long serialVersionUID = -5811800490034132576L;
		public GeoTerritoryID(final long oid) {
			super(oid);
		}
		public GeoTerritoryID(final Long oid) {
			super(oid);
		}
		public static GeoTerritoryID forId(final long id) {
			return new GeoTerritoryID(id);
		}
		public static GeoTerritoryID valueOf(final String str) {
			return new GeoTerritoryID(Long.parseLong(str));
		}
	}
	/**
	 * Estate
	 */
	@Immutable
	@MarshallType(as="geoStateId")
	@NoArgsConstructor
	public static class GeoStateID
				extends GeoIDBase {
		private static final long serialVersionUID = -7636328071565337389L;
		public GeoStateID(final long oid) {
			super(oid);
		}
		public GeoStateID(final Long oid) {
			super(oid);
		}
		public static GeoStateID forId(final long id) {
			return new GeoStateID(id);
		}
		public static GeoStateID valueOf(final String str) {
			return new GeoStateID(Long.parseLong(str));
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
		public GeoLocalityID(final long oid) {
			super(oid);
		}
		public GeoLocalityID(final Long oid) {
			super(oid);
		}
		public static GeoLocalityID forId(final long id) {
			return new GeoLocalityID(id);
		}
		public static GeoLocalityID valueOf(final String str) {
			return new GeoLocalityID(Long.parseLong(str));
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
		public GeoMunicipalityID(final long oid) {
			super(oid);
		}
		public GeoMunicipalityID(final Long oid) {
			super(oid);
		}
		public static GeoMunicipalityID forId(final long id) {
			return new GeoMunicipalityID(id);
		}
		public static GeoMunicipalityID valueOf(final String str) {
			return new GeoMunicipalityID(Long.parseLong(str));
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
		public GeoDistrictID(final long oid) {
			super(oid);
		}
		public GeoDistrictID(final Long oid) {
			super(oid);
		}
		public static GeoDistrictID forId(final long id) {
			return new GeoDistrictID(id);
		}
		public static GeoDistrictID valueOf(final String str) {
			return new GeoDistrictID(Long.parseLong(str));
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
