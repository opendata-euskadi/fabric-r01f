package r01f.persistence.db.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.patterns.IsBuilder;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DBModuleConfigBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static final <CFG extends DBModuleConfig> CFG dbConfigFor(final XMLPropertiesForAppComponent xmlProps) {
		PersistenceUnitType persistenceUnitType = xmlProps.propertyAt("persistence/@unitType")
														  .asEnumElement(PersistenceUnitType.class,
																  		 PersistenceUnitType.DRIVER_MANAGER);	// use driverManager by default
		return persistenceUnitType.is(PersistenceUnitType.DRIVER_MANAGER)
							? (CFG)new DBModuleForPoolConnectionConfig(xmlProps)			// driver managed
							: (CFG)new DBModuleForDataSourceConnectionConfig(xmlProps);		// data-source
	}
	@SuppressWarnings("unchecked")
	public static <DBCFG extends DBModuleConfig> DBCFG dbModuleConfigFrom(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg) {
		return (DBCFG)coreCfg.getSubModuleConfigFor(CoreModule.DBPERSISTENCE);
	}
}
