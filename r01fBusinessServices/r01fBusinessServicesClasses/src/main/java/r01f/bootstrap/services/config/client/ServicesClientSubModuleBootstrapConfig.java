package r01f.bootstrap.services.config.client;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesSubModuleBootstrapConfigBase;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;

@Accessors(prefix="_")
public class ServicesClientSubModuleBootstrapConfig<C extends ContainsConfigData>
	 extends ServicesSubModuleBootstrapConfigBase<C> { 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesClientSubModuleBootstrapConfig(final AppComponent component,
												  final C config) {
		super(component,config);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <CFG extends ContainsConfigData> ServicesClientSubModuleBootstrapConfig<CFG> createFor(final AppComponent component,final CFG cfg) {
		return new ServicesClientSubModuleBootstrapConfig<CFG>(component,cfg);
	}
}
