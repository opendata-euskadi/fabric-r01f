package r01f.bootstrap.services.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;

@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class ServicesSubModuleBootstrapConfigBase<C extends ContainsConfigData> { 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AppComponent _component;
	@Getter private final C _config;	
}
