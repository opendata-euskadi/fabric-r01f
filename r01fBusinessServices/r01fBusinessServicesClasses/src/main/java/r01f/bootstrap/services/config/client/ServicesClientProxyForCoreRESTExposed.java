package r01f.bootstrap.services.config.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesClientProxyForCoreRESTExposed
  implements ServicesClientProxyToCoreImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter final Class<? extends ServiceProxyImpl> _serviceProxyImplsBaseType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getServiceImpl() {
		return ServicesImpl.REST;
	}
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("package where REST service PROXIES extends {}",
								  _serviceProxyImplsBaseType);
	}
}
