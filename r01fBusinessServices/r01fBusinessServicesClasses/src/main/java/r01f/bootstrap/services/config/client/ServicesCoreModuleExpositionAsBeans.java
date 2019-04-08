package r01f.bootstrap.services.config.client;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesCoreModuleExpositionAsBeans 
  implements ServicesCoreModuleExposition {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getServiceImpl() {
		return ServicesImpl.Bean;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("BEAN exposed: services");
	}
}
