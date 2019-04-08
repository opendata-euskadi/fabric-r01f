package r01f.bootstrap.services.config.client;

import r01f.bootstrap.services.config.ServicesConfigObject;
import r01f.bootstrap.services.config.ServicesImpl;

public interface ServicesCoreModuleExposition 
		 extends ServicesConfigObject {
	/**
	 * @return the {@link ServicesImpl} enum
	 */
	public ServicesImpl getServiceImpl();
}
