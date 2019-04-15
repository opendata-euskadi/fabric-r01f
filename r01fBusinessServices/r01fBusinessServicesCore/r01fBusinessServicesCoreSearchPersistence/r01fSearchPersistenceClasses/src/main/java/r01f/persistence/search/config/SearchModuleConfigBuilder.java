package r01f.persistence.search.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.patterns.IsBuilder;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SearchModuleConfigBuilder 
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static <SRCH extends SearchModuleConfig> SRCH searchModuleConfigFrom(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg) {
		return (SRCH)coreCfg.getSubModuleConfigFor(CoreModule.SEARCHPERSISTENCE);
	}
	@SuppressWarnings("unchecked")
	public static <SRCH extends SearchModuleConfig> SRCH searchModuleConfigFrom(final XMLPropertiesForAppComponent xmlProps) {
		return (SRCH)new SearchModuleConfig();
	}	
}
