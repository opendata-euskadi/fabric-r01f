package r01f.httpclient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;
import r01f.types.url.Host;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpClientProxySettingsBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONFIG LOAD
/////////////////////////////////////////////////////////////////////////////////////////
	public static HttpClientProxySettings loadFromProperties(final XMLPropertiesForAppComponent props,
															 final String baseXPath) {
		boolean enabled = props.propertyAt(baseXPath + "/@enabled")
							   .asBoolean(true);
		Host proxyHost = props.propertyAt(baseXPath + "/host")
							   		  .asHost();
		UserCode userCode = props.propertyAt(baseXPath + "/user")
								 .asUserCode();
		Password password = props.propertyAt(baseXPath + "/password")
								 .asPassword();
		
		HttpClientProxySettings outProxySettings = null;
		if (proxyHost == null || userCode == null || password == null) {
			log.warn("Proxy info is NOT propertly configured at {}: there's no host, user or password info!",
					 baseXPath);
		} 
		else {
			outProxySettings = new HttpClientProxySettings(proxyHost,
														   userCode,password,
														   enabled);
		}
		return outProxySettings;
	}
}
