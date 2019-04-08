package r01f.bootstrap.services.config.core;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesSubModuleBootstrapConfigBase;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreModule;

@Accessors(prefix="_")
public class ServicesCoreSubModuleBootstrapConfig<C extends ContainsConfigData>
	 extends ServicesSubModuleBootstrapConfigBase<C> { 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreSubModuleBootstrapConfig(final AppComponent component,
												final C config) {
		super(component,config);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <CFG extends ContainsConfigData> ServicesCoreSubModuleBootstrapConfig<CFG> createFor(final AppComponent component,final CFG cfg) {
		return new ServicesCoreSubModuleBootstrapConfig<CFG>(component,cfg);
	}
	public static <CFG extends ContainsConfigData> ServicesCoreSubModuleBootstrapConfig<CFG> createForDBPersistenceSubModule(final CFG cfg) {
		return new ServicesCoreSubModuleBootstrapConfig<CFG>(CoreModule.DBPERSISTENCE,cfg);
	}	
	public static <CFG extends ContainsConfigData> ServicesCoreSubModuleBootstrapConfig<CFG> createForSearchPersistenceSubModule(final CFG cfg) {
		return new ServicesCoreSubModuleBootstrapConfig<CFG>(CoreModule.SEARCHPERSISTENCE,cfg);
	}
	public static <CFG extends ContainsConfigData> ServicesCoreSubModuleBootstrapConfig<CFG> createForFSPersistenceSubModule(final CFG cfg) {
		return new ServicesCoreSubModuleBootstrapConfig<CFG>(CoreModule.FSPERSISTENCE,cfg);
	}
}
