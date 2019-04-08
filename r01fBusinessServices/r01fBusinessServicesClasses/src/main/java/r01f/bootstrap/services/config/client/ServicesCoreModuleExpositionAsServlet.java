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
public class ServicesCoreModuleExpositionAsServlet 
  implements ServicesCoreModuleExposition {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Host _servletEndPointHost;
	@Getter private final UrlPath _servletUrlPath;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getServiceImpl() {
		return ServicesImpl.Servlet;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("servlet exposed CORE at endpoint with url={}{}",
								  _servletEndPointHost,_servletUrlPath != null ? _servletUrlPath.asAbsoluteString() : "");
	}
}
