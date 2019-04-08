package r01f.bootstrap.services.config.client;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesClientProxyForCoreBeanExposed
  implements ServicesClientProxyToCoreImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getServiceImpl() {
		return ServicesImpl.Bean;
	}
	@Override
	public CharSequence debugInfo() {
		return "";
	}
}
