package r01f.securitycontext;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.IsRole;
import r01f.guids.CommonOIDs.TokenBase;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.Passwords;

public abstract class SecurityIDS {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface IsSecurityID
					extends OIDTyped<String> {
		// just a marker interface
	}
	/**
	 * Base for every oid objects
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class SecurityIDBase
						 extends OIDBaseMutable<String>
					  implements IsSecurityID {

		private static final long serialVersionUID = 4162366466990455545L;

		public SecurityIDBase(final String id) {
			super(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="securityId")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class SecurityID
	     		      extends OIDBaseMutable<String> 
				   implements IsSecurityID {
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
	     		      extends TokenBase {
		private static final long serialVersionUID = -6056892755877680637L;

		public SecurityToken(final String oid) {
			super(oid);
		}
		public static SecurityToken from(final String id) {
			return new SecurityToken(id);
		}
		public static SecurityToken forId(final String id) {
			return new SecurityToken(id);
		}
		public static SecurityToken valueOf(final String id) {
			return new SecurityToken(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="loginId")
	@NoArgsConstructor
	public static class SecurityProviderID
		 		extends SecurityIDBase  {
	
		private static final long serialVersionUID = -3633101002845530783L;
		
		public SecurityProviderID(final String id) {
			super(id);
		}
		public static SecurityProviderID forId(final String idAsString) {
			return new SecurityProviderID(idAsString);
		}
		
		public static final SecurityProviderID USER_PASSWORD = SecurityProviderID.forId("usrpwd");
		public static final SecurityProviderID GOOGLE = SecurityProviderID.forId("google");
		public static final SecurityProviderID XLNETS = SecurityProviderID.forId("xlnets");
		public static final SecurityProviderID PAIRED_PHONE = SecurityProviderID.forId("pairedphone");
		public static final SecurityProviderID REGISTERED_DEVICE = SecurityProviderID.forId("registered-device");
		public static final SecurityProviderID SYSTEM = SecurityProviderID.forId("system");		// ie: app login
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
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
		public static UserRole named(final String id) {
			return new UserRole(id); 
		}
	}
	@Immutable
	@MarshallType(as="password")
	@EqualsAndHashCode(callSuper=true)
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static final class Password
	     			  extends OIDBaseMutable<String> {

		private static final long serialVersionUID = -4110070527400569196L;

		private final transient Memoized<PasswordHash> _hash = new Memoized<PasswordHash>() {
																		@Override
																		public PasswordHash supply() {
																			return PasswordHash.fromPassword(Password.this);
																		}
															   };
		public Password(final String pwd) {
			super(pwd);
		}
		public static Password forId(final String id) {
			return new Password(id);
		}
		public static Password valueOf(final String id) {
			return Password.forId(id);
		}
		public PasswordHash hash() {
			return _hash.get();
		}
		public char[] toCharArray() {
			return this.asString().toCharArray();
		}
		public boolean matchesHash(final PasswordHash hash) {
			return Passwords.createWithDefaultCost()
							.authenticate(this,			// the password
										  hash);		// the hash
		}
	}
	@Immutable
	@MarshallType(as="passwordHash")
	@EqualsAndHashCode(callSuper=true)
	@NoArgsConstructor
	public static final class PasswordHash
	     		      extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -4102923783713904433L;

		public PasswordHash(final String oid) {
			super(oid);
		}
		public static PasswordHash forId(final String id) {
			return new PasswordHash(id);
		}
		public static PasswordHash valueOf(final String id) {
			return PasswordHash.forId(id);
		}
		public static PasswordHash fromHash(final String hash) {
			return PasswordHash.forId(hash);
		}
		public static PasswordHash fromPassword(final String password) {
			return PasswordHash.fromPassword(new Password(password));
		}
		public static PasswordHash fromPassword(final Password password) {
			return Passwords.createWithDefaultCost()
							.hash(password);
		}
		public boolean matches(final Password password) {
			return Passwords.createWithDefaultCost()
							.authenticate(password,		// the received password
										  this);		// the stored hash
		}
		public char[] toCharArray() {
			return this.asString().toCharArray();
		}
		public byte[] getBytes() {
			return this.asString().getBytes();
		}
	}
	@Immutable
	@MarshallType(as="userAndPassword")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static final class LoginAndPassword
			       implements Serializable {
		private static final long serialVersionUID = 1549566021138557737L;

		@MarshallField(as="user",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private LoginID _user;

		@MarshallField(as="password",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter @Setter private Password _password;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ID's for data, login and authorizations of an user
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="loginId")
	@NoArgsConstructor
	public static final class LoginID
					  extends SecurityIDBase {
		private static final long serialVersionUID = -2959560256371887489L;
		public LoginID(final String oid) {
			super(oid);
		}
		public static LoginID forId(final String id) {
			return new LoginID(id);
		}
		public static LoginID valueOf(final String id) {
			return new LoginID(id);
		}
		public static LoginID fromString(final String id) {
			return new LoginID(id);
		}
		public static LoginID fromAppCode(final AppCode appCode) {
			return LoginID.forId(appCode.asString());
		}
		public static LoginID fromPhone(final Phone phone) {
			return LoginID.forId(phone.asString());
		}
		public static LoginID fromEMail(final EMail mail) {
			return LoginID.forId(mail.asString());
		}
		public static final LoginID ANONYMOUS = LoginID.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(ANONYMOUS);
		}
		public static final LoginID MASTER = LoginID.forId("master");
		public boolean isMaster() {
			return this.is(MASTER);
		}
		public static final LoginID ADMIN = LoginID.forId("admin");;
		public boolean isAdmin() {
			return this.is(ADMIN);
		}
		public static final LoginID SYSTEM = LoginID.forId("system");
		public boolean isSystem() {
			return this.is(SYSTEM);
		}
		
		public boolean isEMail() {
			EMail email = EMail.of(this.asString());
			return email == null || !email.isValid();
		}
		public boolean isPhone() {
			Phone phone = Phone.of(this.asString());
			return phone == null || !phone.isValid();
		}
	}
	@Immutable
	@MarshallType(as="userLoginEntryID")
	@NoArgsConstructor
	public static final class UserLoginEntryID
					  extends SecurityIDBase {
		private static final long serialVersionUID = -5890084484482886799L;
		public UserLoginEntryID(final String oid) {
			super(oid);
		}
		public static UserLoginEntryID forId(final String id) {
			return new UserLoginEntryID(id);
		}
		public static final UserLoginEntryID forUserNotConfirmed() {
			return new UserLoginEntryID("NOT_CONFIRMED");
		}
		public static final UserLoginEntryID forLoginOk() {
			return new UserLoginEntryID("LOGIN_OK");
		}
		public static final UserLoginEntryID forLoginError() {
			return new UserLoginEntryID("LOGIN_ERROR");
		}
		public static final UserLoginEntryID forPasswordRecoveryRequest() {
			return new UserLoginEntryID("PASSWORD_RECOVERY_REQUEST");
		}
		public static final UserLoginEntryID forPasswordChanged() {
			return new UserLoginEntryID("PASSWORD_CHANGED");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="authorizationCode")
	@NoArgsConstructor
	public static final class AuthorizationCode
					  extends SecurityIDBase {
		private static final long serialVersionUID = 962520569656270232L;
		public AuthorizationCode(final String oid) {
			super(oid);
		}
		public static AuthorizationCode forId(final String id) {
			return new AuthorizationCode(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="securityGroup")
	@NoArgsConstructor
	public static final class SecurityGroup
					  extends SecurityIDBase {
		private static final long serialVersionUID = -4252322371800429748L;
		public SecurityGroup(final String oid) {
			super(oid);
		}
		public static SecurityGroup forId(final String id) {
			return new SecurityGroup(id);
		}
	}
}
