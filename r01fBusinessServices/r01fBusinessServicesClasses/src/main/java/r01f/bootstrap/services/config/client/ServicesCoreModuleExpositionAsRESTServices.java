package r01f.bootstrap.services.config.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.types.url.Host;
import r01f.types.url.UrlPath;
import r01f.util.types.Strings;


@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesCoreModuleExpositionAsRESTServices 
  implements ServicesCoreModuleExposition {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Host _host;
	@Getter private final UrlPath _baseUrlPath;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getServiceImpl() {
		return ServicesImpl.REST;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("REST exposed CORE at endpoint with url={}{}",
								  _host != null ? _host : "",
								  _baseUrlPath != null ? _baseUrlPath.asAbsoluteString() : "");
	}
}
