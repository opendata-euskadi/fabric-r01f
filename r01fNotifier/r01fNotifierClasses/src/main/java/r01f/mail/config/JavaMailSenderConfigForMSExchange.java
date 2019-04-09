package r01f.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.types.url.Host;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class JavaMailSenderConfigForMSExchange 
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Host _mailServerHost;
//	@Getter private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForMSExchange(final Host host,
											 final boolean disabled) {
		super(JavaMailSenderImpl.MICROSOFT_EXCHANGE,
			  disabled);
		_mailServerHost = host;
	}
	public static JavaMailSenderConfigForMSExchange createFrom(final XMLPropertiesForAppComponent xmlProps,
												      		   final String propsRootNode) {
		Host exchangeHost = JavaMailSenderConfigForMSExchange.microsoftExchangeHostFromProperties(xmlProps,
																					   			  propsRootNode);
		return new JavaMailSenderConfigForMSExchange(exchangeHost,
													 false);	// not disabled
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String MICROSOFT_EXCHANGE_PROPS_XPATH = "/javaMailSenderImpls/javaMailSenderImpl[@id='microsoft_exchange']";
	static Host microsoftExchangeHostFromProperties(final XMLPropertiesForAppComponent props,
											 		final String propsRootNode) {
		Host host = props.propertyAt(propsRootNode + MICROSOFT_EXCHANGE_PROPS_XPATH + "/host")
					  	 .asHost();
		if (host == null) throw new IllegalStateException(Throwables.message("Cannot configure Microsoft Exchange SMTP: the properties file does NOT contains a the host at {} in {} properties file",
														  propsRootNode + MICROSOFT_EXCHANGE_PROPS_XPATH,props.getAppCode()));
		return host;
	}

}
