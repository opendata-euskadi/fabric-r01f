package r01f.bootstrap.services.config.core;

import java.util.Collection;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.util.types.Strings;

/**
 * @see ServicesBootstrapConfigBuilder
 */
@Accessors(prefix="_")
abstract class ServicesCoreGuiceBootstrapConfigBase
	   extends ServicesCoreBootstrapConfigBase
    implements ServicesCoreGuiceBootstrapConfig {

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The guice module that bootstraps the core
	 */
	@Getter protected final Class<? extends ServicesCoreBootstrapGuiceModule> _coreBootstrapGuiceModuleType;
	/**
	 * Do this module MUST be binded inside a private module? (this is the case of the db modules)
	 */
	@Getter protected final boolean _isolate;			// should the module be binded in a private module
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreGuiceBootstrapConfigBase(final CoreAppCode coreAppCode,final CoreModule coreModule,
												final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapGuiceModuleType,
											    final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesConfig,
											    final boolean isolate) {
		super(coreAppCode,coreModule,
			  subModulesConfig);
		_coreBootstrapGuiceModuleType = coreBootstrapGuiceModuleType;
		_isolate = isolate;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} bootstrapped with {} {}",
								  super.debugInfo(),
								  (_isolate ? "(isolated)" : ""),
								  _coreBootstrapGuiceModuleType);
	}
}
