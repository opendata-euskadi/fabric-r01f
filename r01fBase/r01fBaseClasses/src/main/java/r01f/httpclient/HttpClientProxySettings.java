package r01f.httpclient;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.debug.Debuggable;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Host;
import r01f.util.types.Strings;

/**
 * Proxy info
 */
@NoArgsConstructor
@MarshallType(as="proxySettings")
@Accessors(prefix="_")
public class HttpClientProxySettings
  implements Debuggable,
  			 ContainsConfigData,
  			 Serializable {

	private static final long serialVersionUID = -4581831883858484268L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="host",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private  Host _proxyHost;
	@MarshallField(as="port",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private int _proxyPort;
	@MarshallField(as="user",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private UserCode _user;
	@MarshallField(as="password",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private  Password _password;
	@MarshallField(as="enabled",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private boolean _enabled;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpClientProxySettings(final Host proxyHost,final int proxyPort,
								   final UserCode userCode,final Password password,
								   final boolean enabled) {
		_proxyHost = proxyHost;
		_proxyPort = proxyPort;
		_user = userCode;
		_password = password;
		_enabled = enabled;
	}
	public HttpClientProxySettings(final Host proxyHost,final int proxyPort,
								   final UserCode userCode,final Password password) {
		this(proxyHost,proxyPort,
			 userCode,password,
			 true);
	}
	public HttpClientProxySettings(final Host proxyHost,
								   final UserCode userCode,final Password password) {
		this(proxyHost,
			 userCode,password,
			 true);
	}
	public HttpClientProxySettings(final Host proxyHost,
								   final UserCode userCode,final Password password,
								   final boolean enabled) {
		this(proxyHost.asUrl().getHost(),proxyHost.asUrl().getPort(),
			 userCode,password,
			 enabled);
	}
	public HttpClientProxySettings(final HttpClientProxySettings other,
								   final boolean enabled) {
		this(other.getProxyHost(),other.getProxyPort(),
			 other.getUser(),other.getPassword(),
			 enabled);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		String outDbg = null;
		if (_enabled) {
			outDbg = Strings.customized("ENABLED [{}:{} {}/{}]",
										_proxyHost,_proxyPort,
										_user,_password);
		} else {
			outDbg = "DISABLED";
		}
		return outDbg;
	}
}
