package r01f.bootstrap.services.config.core;

import r01f.bootstrap.services.config.ServicesConfigObject;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;

public interface ServicesCoreBootstrapConfig 
         extends ServicesConfigObject {
	
	public CoreAppCode getCoreAppCode();
	public CoreModule getCoreModule();

	public <CFG extends ContainsConfigData> CFG getSubModuleConfigFor(final AppComponent component);
	
	public <C extends ServicesCoreBootstrapConfig> C as(final Class<C> type);
	
	public ServicesImpl getImplType();
}
