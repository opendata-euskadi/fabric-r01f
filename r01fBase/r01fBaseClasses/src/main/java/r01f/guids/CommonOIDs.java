package r01f.guids;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.internal.Env;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CommonOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * OID of a void oid
	 */
	@Immutable
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static abstract class VoidOIDBase
	                     extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -1722486435358750241L;

		public VoidOIDBase(final String oid) {
			super(oid);
		}
	}
	/**
	 * OID of a void oid
	 */
	@Immutable
	@MarshallType(as="voidOid")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class VoidOID
	                  extends VoidOIDBase {

		private static final long serialVersionUID = 5898825736200388235L;

		public VoidOID(final String oid) {
			super(oid);
		}
		public static VoidOID forId(final String id) {
			return new VoidOID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	APPS
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="usageId")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class UsageID
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -4958918132166472496L;

		public UsageID(final String oid) {
			super(oid);
		}
		public static UsageID from(final String id) {
			return new UsageID(id);
		}
		public static UsageID forId(final String id) {
			return new UsageID(id);
		}
		public static UsageID valueOf(final String id) {
			return new UsageID(id);
		}
		public static final UsageID DEFAULT = UsageID.forId("default");
	}
	@Immutable
	@MarshallType(as="systemId")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class SystemID
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -6056892755877680637L;

		public SystemID(final String oid) {
			super(oid);
		}
		public static SystemID from(final String id) {
			return new SystemID(id);
		}
		public static SystemID forId(final String id) {
			return new SystemID(id);
		}
		public static SystemID valueOf(final String id) {
			return new SystemID(id);
		}
	}
	/**
	 * AppCode
	 */
	@NoArgsConstructor
	public static abstract class AppCodeBase
	                     extends OIDBaseMutable<String> {
		private static final long serialVersionUID = 4050287656751295712L;

		public AppCodeBase(final String oid) {
			super(oid);
		}
		@Override
		public boolean equals(final Object obj) {
			if (obj == null) return false;
			if (this == obj) return true;
			if (!(obj instanceof AppCodeBase)) return false;
			
			AppCodeBase other = (AppCodeBase)obj;
			return this.getId() != null && other.getId() != null ? this.getId().equals(other.getId())
																 : this.getId() != null && other.getId() == null ? false
																		 										 : this.getId() == null && other.getId() != null ? false
																		 												 										 : true;	// both null
		}
	}
	/**
	 * AppCode
	 */
	@MarshallType(as="appCode")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static class AppCode
	            extends AppCodeBase {
		private static final long serialVersionUID = -1130290632493385784L;

		public AppCode(final String oid) {
			super(oid);
		}
		public static AppCode forId(final String id) {
			return new AppCode(id);
		}
		public static AppCode valueOf(final String id) {
			return AppCode.forId(id);
		}
		public static AppCode forIdOrNull(final String id) {
			if (id == null) return null;
			return new AppCode(id);
		}
		public static AppCode named(final String id) {
			return AppCode.forId(id);
		}
		public static AppCode forLogin(final LoginID loginId) {
			return AppCode.forId(loginId.asString());
		}
	}
	/**
	 * AppCode component
	 */
	@NoArgsConstructor
	public static abstract class AppComponentBase
	                     extends OIDBaseMutable<String> {
		private static final long serialVersionUID = 2884200091000668089L;

		public static final AppComponent DEFAULT = AppComponent.forId("default");
		public static final AppComponent NO_COMPONENT = AppComponent.forId("_no_component_");

		public AppComponentBase(final String oid) {
			super(oid);
		}
		public boolean isNoComponent() {
			return this.is(NO_COMPONENT);
		}
		public abstract AppComponent asAppComponent();
		
		@Override
		public boolean equals(final Object obj) {
			if (obj == null) return false;
			if (this == obj) return true;
			if (!(obj instanceof AppComponentBase)) return false;
			
			AppComponentBase other = (AppComponentBase)obj;
			// compare objects of the same type, otherwise equals will fail
			return this.getId() != null && other.getId() != null ? this.getId().equals(other.getId())
																 : this.getId() != null && other.getId() == null ? false
																		 										 : this.getId() == null && other.getId() != null ? false
																		 												 										 : true;	// both null
		}
	}
	/**
	 * AppCode component
	 */
	@MarshallType(as="appComponent")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static class AppComponent
	            extends AppComponentBase {
		private static final long serialVersionUID = 137722031497569807L;

		public AppComponent(final String oid) {
			super(oid);
		}
		public static AppComponent forId(final String id) {
			return new AppComponent(id);
		}
		public static AppComponent valueOf(final String id) {
			return AppComponent.forId(id);
		}
		public static AppComponent forIdOrNull(final String id) {
			if (id == null) return null;
			return new AppComponent(id);
		}
		public static AppComponent named(final String id) {
			return AppComponent.forId(id);
		}
		public static AppComponent compose(final AppComponent one,final AppComponent other) {
			return AppComponent.forId(Strings.customized("{}.{}",
														 one,other));
		}
		public static AppComponent compose(final AppComponent one,final String other) {
			return AppComponent.forId(Strings.customized("{}.{}",
														 one,other));
		}
		public static AppComponent compose(final String one,final String other) {
			return AppComponent.forId(Strings.customized("{}.{}",
														 one,other));
		}
		@Override
		public AppComponent asAppComponent() {
			return this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WORKPLACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="workPlaceCode")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class WorkPlaceCode
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -9005995212776716044L;

		public WorkPlaceCode(final String oid) {
			super(oid);
		}
		public static WorkPlaceCode forId(final String id) {
			return new WorkPlaceCode(id);
		}
		public static WorkPlaceCode valueOf(final String id) {
			return WorkPlaceCode.forId(id);
		}
		public static final WorkPlaceCode UNKNOWN = WorkPlaceCode.forId("unknown");
		public boolean isUnknown() {
			return this.is(WorkPlaceCode.UNKNOWN);
		}
		public static final WorkPlaceCode ANONYMOUS = WorkPlaceCode.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(WorkPlaceCode.ANONYMOUS);
		}
	}
	@Immutable
	@MarshallType(as="buildingCode")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class BuildingCode
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -2459745121499121940L;

		public BuildingCode(final String oid) {
			super(oid);
		}
		public static BuildingCode forId(final String id) {
			return new BuildingCode(id);
		}
		public static BuildingCode valueOf(final String id) {
			return BuildingCode.forId(id);
		}
		public static final BuildingCode ANONYMOUS = BuildingCode.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(BuildingCode.ANONYMOUS);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface IsRole
					extends OID {
		// just a marker interface
	}
	@Immutable
	@MarshallType(as="role")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class Role
	     		      extends OIDBaseMutable<String>
				   implements IsRole {
		private static final long serialVersionUID = 7547259948658810158L;
		public Role(final String oid) {
			super(oid);
		}
		public static Role forId(final String id) {
			return new Role(id);
		}
		public static Role valueOf(final String id) {
			return new Role(id);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	GENERIC TOKEN
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsToken
			 extends OIDTyped<String> {
		public byte[] getBytes();
	}
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static abstract class TokenBase
	     		      	 extends OIDBaseMutable<String>
					  implements IsToken {
		
		private static final long serialVersionUID = 2597097651034832977L;

		public TokenBase(final String oid) {
			super(oid);
		}
		@Override
		public byte[] getBytes() {
			return this.asString().getBytes();
		}
	}
	@Immutable
	@MarshallType(as="token")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class Token
	     		      extends TokenBase {

		private static final long serialVersionUID = -5026236014365451126L;

		public Token(final String oid) {
			super(oid);
		}
		public static Token from(final String id) {
			return new Token(id);
		}
		public static Token forId(final String id) {
			return new Token(id);
		}
		public static Token valueOf(final String id) {
			return new Token(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENVIRONMENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Tenant identifier
	 */
	@Immutable
	@MarshallType(as="tenantId")
	@NoArgsConstructor
	public static final class TenantID
		 		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -7631726260644902005L;

		public static final TenantID DEFAULT = TenantID.forId("default");

		public TenantID(final String id) {
			super(id);
		}
		public static TenantID valueOf(final String s) {
			return TenantID.forId(s);
		}
		public static TenantID fromString(final String s) {
			return TenantID.forId(s);
		}
		public static TenantID forId(final String id) {
			return new TenantID(id);
		}
	}
	@Immutable
	@MarshallType(as="env")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class Environment
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -2820663417050382971L;

		public static Environment DEFAULT = Environment.forId("default");

		public Environment(final String oid) {
			super(oid);
		}
		public static Environment forId(final String id) {
			return new Environment(id);
		}
		public static Environment valueOf(final String id) {
			return Environment.forId(id);
		}
		public Env getEnv() {
			return Env.from(this);
		}
		public boolean isLocal() {
			return this.getId().toLowerCase().startsWith("loc");
		}
	}
	@Immutable
	@MarshallType(as="execContextOid")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class ExecContextId
				      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = 6876006770063375473L;

		public ExecContextId(final String oid) {
			super(oid);
		}
		public static ExecContextId forId(final String id) {
			return new ExecContextId(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WEB
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="webSessionOid")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class WebSessionOID
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = 1860937925564750174L;
		public WebSessionOID(final String oid) {
			super(oid);
		}
		public static WebSessionOID forId(final String id) {
			return new WebSessionOID(id);
		}
		public static WebSessionOID valueOf(final String id) {
			return WebSessionOID.forId(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTY ID
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="propertyId")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class PropertyID
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -7386816180797103655L;
		public PropertyID(final String oid) {
			super(oid);
		}
		public static PropertyID forId(final String id) {
			return new PropertyID(id);
		}
		public static PropertyID valueOf(final String id) {
			return PropertyID.forId(id);
		}
	}
}
