package r01f.bootstrap.services.core;

import java.util.Collection;

import com.google.inject.Module;

import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenRESTExposed;

public abstract class RESTImplementedServicesCoreBootstrapGuiceModuleBase
		      extends ServicesCoreBootstrapGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenRESTExposed coreModuleCfg) {
		super(coreModuleCfg);
	}
	protected RESTImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenRESTExposed coreBootstrapCfg,
														 		  final Collection<? extends Module> modulesToInstall) {
		super(coreBootstrapCfg,
			  modulesToInstall);
	}
	protected RESTImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenRESTExposed coreBootstrapCfg,
													     		  final Module m1,
													     		  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,
			  otherModules);
	}
	protected RESTImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenRESTExposed coreBootstrapCfg,
														 		  final Module m1,final Module m2,
														 		  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,
			  otherModules);
	}
	protected RESTImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenRESTExposed coreBootstrapCfg,
														 		  final Module m1,final Module m2,final Module m3,
														 		  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,m3,
			  otherModules);
	}
	protected RESTImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenRESTExposed coreBootstrapCfg,
																  final Module m1,final Module m2,final Module m3,final Module m4,
																  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,m3,m4,
			  otherModules);
	}
}
