package r01f.bootstrap.services.config.core;

import java.util.Collection;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;

/**
 * @see ServicesBootstrapConfigBuilder
 */
@Accessors(prefix="_")
public class ServicesCoreGuiceBootstrapConfigWhenServletExposed
	 extends ServicesCoreGuiceBootstrapConfigBase
  implements ServicesCoreBootstrapConfigWhenServletExposed {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreGuiceBootstrapConfigWhenServletExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
															  final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												         	  final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
												         	  final boolean isolate) {
		super(coreAppCode,coreModule,
			  coreBootstrapGuiceModule,
			  subModulesCfgs,
			  isolate);
	}
	public ServicesCoreGuiceBootstrapConfigWhenServletExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
															  final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												      	 	  final Collection<ServicesCoreSubModuleBootstrapConfig<?>>  subModulesCfgs) {
		this(coreAppCode,coreModule, 
			 coreBootstrapGuiceModule,
			 subModulesCfgs,
			 false);	// Servlet guice modules MUST NOT be binded as private modules; otherwise guice servlet filter cannot see Servlet resource bindings
	}
}
