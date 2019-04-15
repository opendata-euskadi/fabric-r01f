package r01f.ejie.xlnets.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Appointments service identifiers definitions.
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class XLNetsIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	OIDs
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface XLNetsModelObjectID
					extends OIDTyped<String> {
		/* a marker interface */
	}
	@Immutable
	public static abstract class XLNetsModelObjectIDBase
	              		  extends OIDBaseMutable<String> 	// normally this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable
					   implements XLNetsModelObjectID {
		private static final long serialVersionUID = -1535472178694265985L;
		public XLNetsModelObjectIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public XLNetsModelObjectIDBase(final String id) {
			super(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ORGANIZATION / DIVISION / SERVICE 
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface XLNetsOrgObjectID
					extends XLNetsModelObjectID {
		/* a marker interface */
	}
	@Immutable
	@MarshallType(as="xlnetsOrgid")
	@NoArgsConstructor
	public static class XLNetsOrganizationID
				extends XLNetsModelObjectIDBase
			 implements XLNetsOrgObjectID {
		private static final long serialVersionUID = 1797728634898350173L;
		public XLNetsOrganizationID(final String oid) {
			super(oid);
		}
		public static XLNetsOrganizationID valueOf(final String s) {
			return XLNetsOrganizationID.forId(s);
		}
		public static XLNetsOrganizationID fromString(final String s) {
			return XLNetsOrganizationID.forId(s);
		}
		public static XLNetsOrganizationID forId(final String id) {
			return new XLNetsOrganizationID(id);
		}
	}
	@Immutable
	@MarshallType(as="xlnetsOrgDivisionId")
	@NoArgsConstructor
	public static class XLNetsOrgDivisionID
				extends XLNetsModelObjectIDBase 
			 implements XLNetsOrgObjectID {
		private static final long serialVersionUID = -6238420865427110997L;
		public XLNetsOrgDivisionID(final String oid) {
			super(oid);
		}
		public static XLNetsOrgDivisionID valueOf(final String s) {
			return XLNetsOrgDivisionID.forId(s);
		}
		public static XLNetsOrgDivisionID fromString(final String s) {
			return XLNetsOrgDivisionID.forId(s);
		}
		public static XLNetsOrgDivisionID forId(final String id) {
			return new XLNetsOrgDivisionID(id);
		}
	}
	@Immutable
	@MarshallType(as="xlnetsOrgDivisionServiceId")
	@NoArgsConstructor
	public static class XLNetsOrgDivisionServiceID
				extends XLNetsModelObjectIDBase 
			 implements XLNetsOrgObjectID {
		private static final long serialVersionUID = 83200432891416102L;
		public XLNetsOrgDivisionServiceID(final String oid) {
			super(oid);
		}
		public static XLNetsOrgDivisionServiceID valueOf(final String s) {
			return XLNetsOrgDivisionServiceID.forId(s);
		}
		public static XLNetsOrgDivisionServiceID fromString(final String s) {
			return XLNetsOrgDivisionServiceID.forId(s);
		}
		public static XLNetsOrgDivisionServiceID forId(final String id) {
			return new XLNetsOrgDivisionServiceID(id);
		}
	}
}
