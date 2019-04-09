package r01f.mail.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.patterns.IsBuilder;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
@Slf4j
public abstract class JavaMailSenderConfigBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	  
/////////////////////////////////////////////////////////////////////////////////////////
	public static JavaMailSenderConfigBase createFrom(final XMLPropertiesForAppComponent xmlProps) {
		return JavaMailSenderConfigBuilder.createFrom(xmlProps,
											       	  "mail");
	}
	@SuppressWarnings("unchecked")
	public static <C extends JavaMailSenderConfigBase> C createFrom(final XMLPropertiesForAppComponent xmlProps,
												      				final String propsRootNode) {
		String thePropsRootNode = Strings.isNullOrEmpty(propsRootNode) ? "mail" : propsRootNode;
		C outConfig = null;
		
		// java mail sender impl
		log.debug(" propsRootNode {}",
				 thePropsRootNode);
		JavaMailSenderImpl impl = xmlProps.propertyAt(thePropsRootNode + "/javaMailSenderImpls/@active")
									    .asEnumElementIgnoringCase(JavaMailSenderImpl.class);
		log.debug("JavaMailSenderConfigBuilder impl based on {} ",
				   impl.getClass());
		
		// ==== MICROSOFT EXCHANGE
		if (impl == JavaMailSenderImpl.MICROSOFT_EXCHANGE) {
			JavaMailSenderConfigForMSExchange msExchangeCfg = JavaMailSenderConfigForMSExchange.createFrom(xmlProps,
																										   thePropsRootNode);
			outConfig = (C)msExchangeCfg;
		}

		// ==== GOOGLE GMAIL API
		else if (impl == JavaMailSenderImpl.GOOGLE_API) {
			JavaMailSenderConfigForGoogleAPI gApiCfg = JavaMailSenderConfigForGoogleAPI.createFrom(xmlProps,
																								   thePropsRootNode);
			outConfig = (C)gApiCfg;
		}

		// ==== GOOGLE GMAIL SMTP
		else if (impl == JavaMailSenderImpl.GOOGLE_SMTP) {
			JavaMailSenderConfigForGoogleSMTP gSMTPCfg = JavaMailSenderConfigForGoogleSMTP.createFrom(xmlProps,
																									  thePropsRootNode);
			outConfig = (C)gSMTPCfg;

		// ==== THIRD_PARTY_MAIL_HTTPSERVICE
		} else if (impl == JavaMailSenderImpl.THIRD_PARTY_MAIL_HTTPSERVICE ) {
			JavaMailSenderConfigForThirdPartyHttpService thirdPartyHttpCfg = JavaMailSenderConfigForThirdPartyHttpService.createFrom(xmlProps,
																																	 thePropsRootNode);
			outConfig = (C)thirdPartyHttpCfg;

		} else {
			throw new IllegalStateException(Throwables.message("JavaMailSender implementation was NOT configured at {} in {} properties file",
															   thePropsRootNode + "/javaMailSenderImpls/@active",xmlProps.getAppCode()));
		}
		return outConfig;
	}

}
