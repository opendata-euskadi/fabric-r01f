package r01f.model.search;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Search oids
 */
public class SearchOIDs {
	/**
	 * Search engine data base identifier
	 */
	@Immutable
	@MarshallType(as="searchEngineBDId")
	@NoArgsConstructor
	public static final class SearchEngineDBID 
					  extends OIDBaseMutable<String> { 	// normally this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable
		private static final long serialVersionUID = 5503685235211621466L;
		public SearchEngineDBID(final String oid) {
			super(oid);
		}
		public static SearchEngineDBID forId(final String id) {
			return new SearchEngineDBID(id);
		}
	}
	/**
	 * Search origin identifier
	 */
	@Immutable
	@MarshallType(as="searchSourceId")
	@NoArgsConstructor
	public static final class SearchSourceID 
					  extends OIDBaseMutable<String> { 	// normally this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable
		private static final long serialVersionUID = -6291130534743233006L;
		public SearchSourceID(final String oid) {
			super(oid);
		}
		public static SearchSourceID forId(final String id) {
			return new SearchSourceID(id);
		}
	}
}
