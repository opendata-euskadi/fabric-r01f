package r01f.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserAndPassword;
import r01f.guids.CommonOIDs.UserCode;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class JavaMailSenderConfigForGoogleSMTP 
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final UserAndPassword _userAndPassword;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForGoogleSMTP(final UserAndPassword userAndPassword,
											 final boolean disabled) {
		super(JavaMailSenderImpl.GOOGLE_SMTP,
			  disabled);
		_userAndPassword = userAndPassword; 
	}
	public static JavaMailSenderConfigForGoogleSMTP createFrom(final XMLPropertiesForAppComponent xmlProps,
												      		   final String propsRootNode) {
		// Get the user & password from the properties file
		UserAndPassword userAndPassword = JavaMailSenderConfigForGoogleSMTP.googleSMTPServiceUserAndPassword(xmlProps,
																											 propsRootNode);
		return new JavaMailSenderConfigForGoogleSMTP(userAndPassword,
													 false);	// not disabled
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String GOOGLE_SMTP_PROPS_XPATH = "/javaMailSenderImpls/javaMailSenderImpl[@id='google_SMTP']";
	static UserAndPassword googleSMTPServiceUserAndPassword(final XMLPropertiesForAppComponent xmlProps,
											 				final String propsRootNode) {
		UserCode user = xmlProps.propertyAt(propsRootNode + GOOGLE_SMTP_PROPS_XPATH + "/user")
							 		.asUserCode();
		Password password = xmlProps.propertyAt(propsRootNode + GOOGLE_SMTP_PROPS_XPATH + "/password")
								 .asPassword();
		// Check
		if (user == null || password == null) {
			throw new IllegalStateException(Throwables.message("Cannot configure Google SMTP: the properties file does NOT contains a the user or password at {} in {} properties file",
															   propsRootNode + GOOGLE_SMTP_PROPS_XPATH,xmlProps.getAppCode()));
		}
		return new UserAndPassword(user,password);
	}

}
