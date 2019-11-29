package r01f.types.geo;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
/**
 * Geo catalog model object's oids
 * <pre>
 * Country
 *   |_Territory
 *   	 |_State
 *   		 |_County
 *   		 	|_Locality
 *   				|_Municipality
 *   					|_County
 *   						|_Street
 *   							|_portal
 * </pre>
 * They're modeled after a long code that encapsulates the geo element (country, territory, street, etc code)
 */
public class GeoOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
// 	base
/////////////////////////////////////////////////////////////////////////////////////////
	public interface GeoID
			 extends OIDTyped<Long> {
		public String asStringPaddedWithZeros(final int numZeros);
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
		@Override
		public String asStringPaddedWithZeros(final int numZeros) {
			return Strings.leftPad(this.toString(),numZeros,'0');
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
		public GeoCountyID(final long oid) {
			super(oid);
		}
		public GeoCountyID(final Long oid) {
			super(oid);
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
		public GeoRegionID(final long oid) {
			super(oid);
		}
		public GeoRegionID(final Long oid) {
			super(oid);
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
