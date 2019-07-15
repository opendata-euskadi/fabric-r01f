package r01f.guids;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.internal.Env;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
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
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * AppCode
	 */
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static abstract class AppCodeBase
	                     extends OIDBaseMutable<String> {
		private static final long serialVersionUID = 4050287656751295712L;

		public AppCodeBase(final String oid) {
			super(oid);
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
		public static AppCode forAuthenticatedUserId(final AuthenticatedActorID authActorId) {
			return new AppCode(authActorId.asString());
		}
	}
	/**
	 * AppCode component
	 */
	@EqualsAndHashCode(callSuper=true)
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
		public static AppComponent compose(final AppComponent one,final AppComponent other) {
			return AppComponent.forId(Strings.customized("{}.{}",
														 one,other));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ROLE
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
//
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
		public static WorkPlaceCode forAuthenticatedUserId(final AuthenticatedActorID authActorId) {
			return new WorkPlaceCode(authActorId.asString());
		}
		public static final WorkPlaceCode ANONYMOUS = WorkPlaceCode.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(WorkPlaceCode.ANONYMOUS);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
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
		public static BuildingCode forAuthenticatedUserId(final AuthenticatedActorID authActorId) {
			return new BuildingCode(authActorId.asString());
		}
		public static final BuildingCode ANONYMOUS = BuildingCode.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(BuildingCode.ANONYMOUS);
		}
	}
	@Immutable
	@MarshallType(as="userGroupCode")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class UserGroupCode
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -8145305261344081383L;

		public UserGroupCode(final String oid) {
			super(oid);
		}
		public static UserGroupCode forId(final String id) {
			return new UserGroupCode(id);
		}
		public static UserGroupCode valueOf(final String id) {
			return UserGroupCode.forId(id);
		}
	}
	@Immutable
	@MarshallType(as="userCode")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class UserCode
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -8145305261344081383L;

		public UserCode(final String oid) {
			super(oid);
		}
		public static UserCode forId(final String id) {
			return new UserCode(id);
		}
		public static UserCode valueOf(final String id) {
			return UserCode.forId(id);
		}
		public static UserCode forAuthenticatedUserId(final AuthenticatedActorID authActorId) {
			return new UserCode(authActorId.asString());
		}
		public static final UserCode ANONYMOUS = UserCode.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(ANONYMOUS);
		}
	}
	@Immutable
	@MarshallType(as="userRole")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class UserRole
	     		      extends OIDBaseMutable<String>
				   implements IsRole {
		private static final long serialVersionUID = 4547730052420260613L;
		public UserRole(final String oid) {
			super(oid);
		}
		public static UserRole forId(final String id) {
			return new UserRole(id);
		}
		public static UserRole valueOf(final String id) {
			return new UserRole(id);
		}
	}
	@Immutable
	@MarshallType(as="password")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class Password
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -4110070527400569196L;

		public Password(final String oid) {
			super(oid);
		}
		public static Password forId(final String id) {
			return new Password(id);
		}
		public static Password valueOf(final String id) {
			return Password.forId(id);
		}
	}
	@Immutable
	@MarshallType(as="userAndPassword")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static final class UserAndPassword
			       implements Serializable {
		private static final long serialVersionUID = 1549566021138557737L;

		@MarshallField(as="user",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private UserCode _user;

		@MarshallField(as="password",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private Password _password;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="authenticatedActor")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class AuthenticatedActorID
	     		      extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -7186228864961079493L;

		public static final AuthenticatedActorID MASTER = AuthenticatedActorID.forId("master",false);	// it's an app
		
		private boolean _app;	// sets if the auth actor is a physical user or an app

		public AuthenticatedActorID(final String id) {
			super(id);
		}
		public AuthenticatedActorID(final String id,
									final boolean isUser) {
			super(id);
			_app = !isUser;
		}
		public static AuthenticatedActorID forId(final String id,final boolean isUser) {
			return new AuthenticatedActorID(id,isUser);
		}
		public static AuthenticatedActorID valueOf(final String id) {
			return new AuthenticatedActorID(id);
		}
		public static AuthenticatedActorID forUser(final UserCode userCode) {
			return new AuthenticatedActorID(userCode.asString(),
											true);		// phisical user
		}
		public static AuthenticatedActorID forApp(final AppCode appCode) {
			return new AuthenticatedActorID(appCode.asString(),
											false);		// app
		}
		public boolean isApp() {
			return _app;
		}
		public boolean isUser() {
			return !this.isApp();
		}
	}
	@Immutable
	@MarshallType(as="securityId")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class SecurityID
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -8145305261344081383L;

		public SecurityID(final String oid) {
			super(oid);
		}
		public static SecurityID forId(final String id) {
			return new SecurityID(id);
		}
		public static SecurityID valueOf(final String id) {
			return new SecurityID(id);
		}
	}
	@Immutable
	@MarshallType(as="securityToken")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class SecurityToken
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -6056892755877680637L;
		
		public SecurityToken(final String oid) {
			super(oid);
		}
		public static SecurityToken forId(final String id) {
			return new SecurityToken(id);
		}
		public static SecurityToken valueOf(final String id) {
			return new SecurityToken(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
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
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
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
}
