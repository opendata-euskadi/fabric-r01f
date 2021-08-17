package r01f.securitycontext;

import java.util.UUID;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.facts.FactDimension;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.guids.PersistableObjectOID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.FactoryFrom;
import r01f.securitycontext.SecurityIDS.LoginID;

public abstract class SecurityOIDs {

/////////////////////////////////////////////////////////////////////////////////////////
// Base marker interface
/////////////////////////////////////////////////////////////////////////////////////////

	public static interface SecurityObjectOID
					extends PersistableObjectOID,OIDTyped<String>,
							FactDimension {
		// just a marker interface
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	OID base
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	* Base for every User oid objects
	*/
	@Immutable
	@NoArgsConstructor
	public static abstract class SecurityObjectOIDBase
						 extends OIDBaseMutable<String>
					  implements SecurityObjectOID {

		private static final long serialVersionUID = 7778880230670929915L;

		public SecurityObjectOIDBase(final String id) {
			super(id);
		}
		/**
		* Generates an oid
		* @return
		*/
		protected static String supplyId() {
			UUID uuid = UUID.randomUUID();
			return uuid.toString();
		}
		public String convertToDatabaseColumn(final SecurityObjectOID attribute) {
			return attribute.asString();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
// 	Security Base OID's
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="userOid")
	public static final class UserOID
					  extends SecurityObjectOIDBase
				   implements SecurityObjectOID,
							  PersistableObjectOID {

		private static final long serialVersionUID = -7020929167799107328L;

		public static final UserOID ALL = UserOID.forId("*");
		public UserOID() {
			super();
		}
		public UserOID(final String oid) {
			super(oid);
		}
		public static UserOID valueOf(final String s) {
			return UserOID.forId(s);
		}
		public static UserOID fromString(final String s) {
			return UserOID.forId(s);
		}
		public static UserOID forId(final String id) {
			return new UserOID(id);
		}
		public static UserOID from(final LoginID loginId) {
			return new UserOID(loginId.asString());
		}
		public static UserOID supply() {
			return UserOID.forId(SecurityObjectOIDBase.supplyId());
		}
		public static FactoryFrom<String,UserOID> FACTORY_FROM_STRING = new FactoryFrom<String,UserOID>() {
																				@Override
																				public UserOID from(final String oid) {
																					return UserOID.forId(oid);
																				}
																		};
		public static final UserOID ANONYMOUS = UserOID.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(ANONYMOUS);
		}
		public static final UserOID MASTER = UserOID.forId("master");
		public boolean isMaster() {
			return this.is(MASTER);
		}
		public static final UserOID ADMIN = MASTER;
		public boolean isAdmin() {
			return this.is(ADMIN);
		}
	}
	@Immutable
	@MarshallType(as="loginOid")
	public static final class LoginOID
					  extends SecurityObjectOIDBase
				   implements SecurityObjectOID {

		private static final long serialVersionUID = -2162289370851262116L;

		public static final LoginOID ALL = LoginOID.forId("*");
		public LoginOID() {
			super();
		}
		public LoginOID(final String oid) {
			super(oid);
		}
		public static LoginOID valueOf(final String s) {
			return LoginOID.forId(s);
		}
		public static LoginOID fromString(final String s) {
			return LoginOID.forId(s);
		}
		public static LoginOID forId(final String id) {
			return new LoginOID(id);
		}
		public static LoginOID supply() {
			return LoginOID.forId(SecurityObjectOIDBase.supplyId());
		}
		public static FactoryFrom<String,LoginOID> FACTORY_FROM_STRING = new FactoryFrom<String,LoginOID>() {
																				@Override
																				public LoginOID from(final String oid) {
																					return LoginOID.forId(oid);
																				}
																		 };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	USER PASSWORD LOGIN
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="userLoginEntryOid")
	public static final class UserLoginEntryOID
					  extends SecurityObjectOIDBase
				   implements SecurityObjectOID {

		private static final long serialVersionUID = -2162289370851262116L;

		public UserLoginEntryOID() {
			super();
		}
		public UserLoginEntryOID(final String oid) {
			super(oid);
		}
		public static UserLoginEntryOID valueOf(final String s) {
			return UserLoginEntryOID.forId(s);
		}
		public static UserLoginEntryOID fromString(final String s) {
			return UserLoginEntryOID.forId(s);
		}
		public static UserLoginEntryOID forId(final String id) {
			return new UserLoginEntryOID(id);
		}
		public static UserLoginEntryOID supply() {
			return UserLoginEntryOID.forId(SecurityObjectOIDBase.supplyId());
		}
		public static FactoryFrom<String,UserLoginEntryOID> FACTORY_FROM_STRING = new FactoryFrom<String,UserLoginEntryOID>() {
																								@Override
																								public UserLoginEntryOID from(final String oid) {
																									return UserLoginEntryOID.forId(oid);
																								}
																						};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PAIRED PHONE LOGIN
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="pairedPhoneLoginConsentOid")
	public static final class PairedPhoneLoginConsentOID
					  extends SecurityObjectOIDBase
				   implements SecurityObjectOID {
	
		private static final long serialVersionUID = -2162289370851262116L;
		
		public PairedPhoneLoginConsentOID() {
			super();
		}
		public PairedPhoneLoginConsentOID(final String oid) {
			super(oid);
		}
		public static PairedPhoneLoginConsentOID valueOf(final String s) {
			return PairedPhoneLoginConsentOID.forId(s);
		}
		public static PairedPhoneLoginConsentOID fromString(final String s) {
			return PairedPhoneLoginConsentOID.forId(s);
		}
		public static PairedPhoneLoginConsentOID forId(final String id) {
			return new PairedPhoneLoginConsentOID(id);
		}
		public static PairedPhoneLoginConsentOID supply() {
			return PairedPhoneLoginConsentOID.forId(SecurityObjectOIDBase.supplyId());
		}
		public static FactoryFrom<String,PairedPhoneLoginConsentOID> FACTORY_FROM_STRING = new FactoryFrom<String,PairedPhoneLoginConsentOID>() {
					@Override
					public PairedPhoneLoginConsentOID from(final String oid) {
						return PairedPhoneLoginConsentOID.forId(oid);
					}
			};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	API-KEY LOGIN
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="apiKeyVersionOid")
	public static final class ApiKeyVersionOID
					  extends SecurityObjectOIDBase
				   implements SecurityObjectOID {
		private static final long serialVersionUID = 1158729335397111474L;
		public ApiKeyVersionOID() {
			super();
		}
		public ApiKeyVersionOID(final String oid) {
			super(oid);
		}
		public static ApiKeyVersionOID valueOf(final String s) {
			return ApiKeyVersionOID.forId(s);
		}
		public static ApiKeyVersionOID fromString(final String s) {
			return ApiKeyVersionOID.forId(s);
		}
		public static ApiKeyVersionOID forId(final String id) {
			return new ApiKeyVersionOID(id);
		}
		public static ApiKeyVersionOID supply() {
			return ApiKeyVersionOID.forId	(SecurityObjectOIDBase.supplyId());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	USER AUTHORIZATION ON RESOURCE
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="authorizationOnResourceOid")
	public static final class UserAuthorizationOnResourceOID
					  extends SecurityObjectOIDBase
				   implements SecurityObjectOID {
		
		private static final long serialVersionUID = 6831850273088749277L;
		
		public UserAuthorizationOnResourceOID() {
			super();
		}
		public UserAuthorizationOnResourceOID(final String oid) {
			super(oid);
		}
		public static UserAuthorizationOnResourceOID valueOf(final String s) {
			return UserAuthorizationOnResourceOID.forId(s);
		}
		public static UserAuthorizationOnResourceOID fromString(final String s) {
			return UserAuthorizationOnResourceOID.forId(s);
		}
		public static UserAuthorizationOnResourceOID forId(final String id) {
			return new UserAuthorizationOnResourceOID(id);
		}
		public static UserAuthorizationOnResourceOID supply() {
			return UserAuthorizationOnResourceOID.forId(SecurityObjectOIDBase.supplyId());
		}
		public static FactoryFrom<String,UserAuthorizationOnResourceOID> FACTORY_FROM_STRING = new FactoryFrom<String,UserAuthorizationOnResourceOID>() {
																										@Override
																										public UserAuthorizationOnResourceOID from(final String oid) {
																											return UserAuthorizationOnResourceOID.forId(oid);
																										}
																								};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTHORIZATION TARGET RESOURCE
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="authorizationTargetResourceOid")
	public static final class AuthorizationTargetResourceOID
					  extends SecurityObjectOIDBase
				   implements SecurityObjectOID {
		
		private static final long serialVersionUID = 6831850273088749277L;
		
		public AuthorizationTargetResourceOID() {
			super();
		}
		public AuthorizationTargetResourceOID(final String oid) {
			super(oid);
		}
		public static AuthorizationTargetResourceOID valueOf(final String s) {
			return AuthorizationTargetResourceOID.forId(s);
		}
		public static AuthorizationTargetResourceOID fromString(final String s) {
			return AuthorizationTargetResourceOID.forId(s);
		}
		public static AuthorizationTargetResourceOID forId(final String id) {
			return new AuthorizationTargetResourceOID(id);
		}
		public static AuthorizationTargetResourceOID supply() {
			return AuthorizationTargetResourceOID.forId(SecurityObjectOIDBase.supplyId());
		}
		public static FactoryFrom<String,AuthorizationTargetResourceOID> FACTORY_FROM_STRING = new FactoryFrom<String,AuthorizationTargetResourceOID>() {
																										@Override
																										public AuthorizationTargetResourceOID from(final String oid) {
																											return AuthorizationTargetResourceOID.forId(oid);
																										}
																								};
	}
}
