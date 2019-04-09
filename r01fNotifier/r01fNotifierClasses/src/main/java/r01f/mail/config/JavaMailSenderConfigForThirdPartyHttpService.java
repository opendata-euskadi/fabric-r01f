package r01f.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpClientProxySettings;
import r01f.types.url.Url;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class JavaMailSenderConfigForThirdPartyHttpService 
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _thirdPartyProviderUrl;
	@Getter private final HttpClientProxySettings _proxySettings;
	@Getter private final boolean _supportsMimeMessage;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForThirdPartyHttpService(final Url thirdPartyProviderUrl,
														final HttpClientProxySettings proxySettigs,
														final boolean supportsMimeMessage,
														final boolean disabled) {
		super(JavaMailSenderImpl.THIRD_PARTY_MAIL_HTTPSERVICE,
			  disabled);
		_thirdPartyProviderUrl = thirdPartyProviderUrl;
		_proxySettings = proxySettigs;
		_supportsMimeMessage = supportsMimeMessage;
	}
	public JavaMailSenderConfigForThirdPartyHttpService(final Url thirdPartyProviderUrl,
			final HttpClientProxySettings proxySettigs,
			final boolean disabled) {
		this(thirdPartyProviderUrl, proxySettigs, true, disabled);
	}
	
	public static JavaMailSenderConfigForThirdPartyHttpService createFrom(final XMLPropertiesForAppComponent xmlProps,
												      		  			  final String propsRootNode) {
		// check if a proxy is needed
		HttpClientProxySettings proxySettings = null;
		try {
			proxySettings = HttpClient.guessProxySettings(xmlProps,
														  propsRootNode);
		} catch(Throwable th) {
			log.error("Error while guessing the internet connection proxy settings to use Third Pary Mail HTTP Service: {}",th.getMessage(),th);
		    th.printStackTrace();
		 // Create the THIRD PARTY HTTP service
		}
		Url thirdPartyProviderUrl = thirdPartyMailServiceFromProperties(xmlProps,
																		propsRootNode);
		
		return new JavaMailSenderConfigForThirdPartyHttpService(thirdPartyProviderUrl,
																proxySettings,
																supportsMimeMessage(xmlProps,
																					propsRootNode),
																false);		// not disabled
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String THIRD_PARTY_MAIL_HTTPSERVICE_PROPS_XPATH = "/javaMailSenderImpls/javaMailSenderImpl[@id='THIRD_PARTY_MAIL_HTTPSERVICE']";
	static Url  thirdPartyMailServiceFromProperties(final XMLPropertiesForAppComponent props,final String propsRootNode) {
		Url url = props.propertyAt(propsRootNode + THIRD_PARTY_MAIL_HTTPSERVICE_PROPS_XPATH + "/url").asUrl();
		if (url == null) throw new IllegalStateException(Throwables.message("Cannot Third Party HTTP Mail Service: the properties file does NOT contains a the url at {} in {} properties file",
		propsRootNode + THIRD_PARTY_MAIL_HTTPSERVICE_PROPS_XPATH,props.getAppCode()));
		return url;
	}
	static boolean supportsMimeMessage(final XMLPropertiesForAppComponent props,final String propsRootNode) {
		return props.propertyAt(propsRootNode + THIRD_PARTY_MAIL_HTTPSERVICE_PROPS_XPATH + "/@supportsMimeMessage").asBoolean(true);
	}

}
