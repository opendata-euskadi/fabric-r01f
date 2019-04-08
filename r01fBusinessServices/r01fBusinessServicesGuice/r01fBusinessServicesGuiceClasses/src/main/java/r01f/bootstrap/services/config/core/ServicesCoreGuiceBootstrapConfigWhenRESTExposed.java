package r01f.bootstrap.services.config.core;

import java.util.Collection;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.core.RESTImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;


/**
 * @see ServicesBootstrapConfigBuilder
 */
@Accessors(prefix="_")
public class ServicesCoreGuiceBootstrapConfigWhenRESTExposed
	 extends ServicesCoreGuiceBootstrapConfigBase 
  implements ServicesCoreBootstrapConfigWhenRESTExposed {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreGuiceBootstrapConfigWhenRESTExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
														   final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												      	   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
												      	   final boolean isolate) {
		super(coreAppCode,coreModule,
			  coreBootstrapGuiceModule,
			  subModulesCfgs,
			  isolate);
	}
	public ServicesCoreGuiceBootstrapConfigWhenRESTExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
														   final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
														   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs) {
		this(coreAppCode,coreModule, 
		     coreBootstrapGuiceModule,
			 subModulesCfgs,
			 false);	// REST guice modules MUST NOT be binded as private modules; otherwise guice servlet filter cannot see REST resource bindings
	}
}
