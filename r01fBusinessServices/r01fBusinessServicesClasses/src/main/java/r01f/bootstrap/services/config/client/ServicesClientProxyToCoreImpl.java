package r01f.bootstrap.services.config.client;

import r01f.bootstrap.services.config.ServicesConfigObject;
import r01f.bootstrap.services.config.ServicesImpl;

public interface ServicesClientProxyToCoreImpl 
		 extends ServicesConfigObject {
	
	public ServicesImpl getServiceImpl();
}
