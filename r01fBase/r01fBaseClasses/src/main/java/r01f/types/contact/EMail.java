package r01f.types.contact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.patterns.Supplier;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.types.url.Host;
import r01f.util.types.Strings;



@MarshallType(as="email")
@Immutable
@NoArgsConstructor
@Accessors(prefix="_")
public class EMail
     extends ValidatedContactMeanBase {

	private static final long serialVersionUID = -6976066522439926427L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern EMAIL_MATCH_PATTERN = Pattern.compile("([^@]+)@(.+)");
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public EMail(final String mail) {
		super(mail);
	}
	public static EMail of(final String mail) {
		return Strings.isNOTNullOrEmpty(mail) ? new EMail(mail)
											  : null;
	}
	public static EMail valueOf(final String mail) {
		return EMail.of(mail);
	}
	public static EMail create(final String mail) {
		return EMail.of(mail);
	}
	public static EMail createValidating(final String mail) {
		if ( ! EMail.validate(mail)) {
			throw new IllegalArgumentException( Strings.customized(" {} is not a valid email address!!", mail));
		}
		return EMail.of(mail);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isValid() {
		return EMail.validate(this.asString());
	}
	private final transient Memoized<String[]> _parts = Memoized.using(new Supplier<String[]>() {
																				@Override
																				public String[] supply() {
																					if (!EMail.this.isValid()) throw new IllegalArgumentException(EMail.this + " is NOT a valid email!");
																					Matcher m = EMAIL_MATCH_PATTERN.matcher(EMail.this.toString());
																					if (!m.find()) throw new IllegalArgumentException(EMail.this + " is NOT a valid email!");	// this should NOT happen
																					String user = m.group(1);
																					String domain = m.group(2);
																					return new String[] { user,domain };
																				}
																	   });
	public LoginID getUser() {
		String userCodeStr = _parts.get()[0];
		return LoginID.forId(userCodeStr);
	}
	public Host getDomain() {
		String domainStr = _parts.get()[1];
		return Host.from(domainStr);
	}
	public boolean isGoogleEMail() {
		return _parts.get()[1]					// the domain
					 .startsWith("gmail");		// is gmail
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Validates eMail
	 * @param emailStr
	 * @return
	 */
	public static boolean validate(final String emailStr) {
		if (Strings.isNullOrEmpty(emailStr)) return false;

		boolean outValid = true;

		int atIndex = emailStr.indexOf('@');
		if (atIndex <= 0) {
			outValid = false;
		} else {
			String domain = emailStr.substring(atIndex+1);
			String local = emailStr.substring(0,atIndex);

			int domainLength = domain.length();
			int localLength = local.length();

			if (localLength < 1 || localLength > 64) {
				outValid = false;
			} else if (domainLength < 1 || domainLength > 255) {
				outValid = false;
			} else if (local.charAt(0) == '.' || local.charAt(localLength-1) == '.') {
				outValid = false;
			} else if (local.contains("..")) {
				outValid = false;	// local part has two consecutive dots
			} else if (!domain.matches("^[A-Za-z0-9\\-\\.]+$")) {
				outValid = false;
			} else if (domain.contains("..")) {
				outValid = false;	// domain part has two consecutive dots
			} else if (!local.replaceAll("\\\\","")
							 .matches("^(\\\\.|[A-Za-z0-9!#%&`_=\\/$\'*+?^{}|~.-])+$")) {
				// character not valid in local part unless local part is quoted
				if (!local.replaceAll("\\\\","")
							 .matches("^\"(\\\\\"|[^\"])+\"$")) {
					outValid = false;
				}
			}
//			// Domain checking
//			if (outValid && !(checkDNSrecord(domain,"MX") || checkDNSrecord(domain,"A"))) {
//				outValid = false;
//			}
		}
		return outValid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSFORM
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,EMail> FROM_STRING_TRANSFORM = new Function<String,EMail>() {
																			@Override
																			public EMail apply(final String email) {
																				return EMail.of(email);
																			}
																  	   };
	public static final Function<EMail,String> TO_STRING_TRANSFORM = new Function<EMail,String>() {
																			@Override
																			public String apply(final EMail email) {
																				return email.asString();
																			}
																  	   };
}
