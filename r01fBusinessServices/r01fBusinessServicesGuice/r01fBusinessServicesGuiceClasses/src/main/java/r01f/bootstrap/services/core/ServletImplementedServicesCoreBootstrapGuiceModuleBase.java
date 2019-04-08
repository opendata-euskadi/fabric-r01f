package r01f.bootstrap.services.core;

import java.util.Collection;

import com.google.inject.Module;

import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;

/**
 * Special kind of {@link ServicesCoreBootstrapGuiceModule} used to bootstap a servlet guice module
 * Note that this is NOT a full-fledged service as {@link RESTImplementedServicesCoreBootstrapGuiceModuleBase}, {@link BeanImplementedServicesCoreBootstrapGuiceModuleBase} or {@link EJBImplementedServicesCoreBootstrapGuiceModuleBase}
 * it's NOT used by a real client API: it's consumed by a web client like a browser so there's NO associated client-proxy
 */
public abstract class ServletImplementedServicesCoreBootstrapGuiceModuleBase
		      extends ServicesCoreBootstrapGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServletImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg) {
		super(coreBootstrapCfg);
	}
	protected ServletImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg,
														    		 final Collection<? extends Module> modulesToInstall) {
		super(coreBootstrapCfg,
			  modulesToInstall);
	}
	protected ServletImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg,
																	 final Module m1,
																	 final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,
			  otherModules);
	}
	protected ServletImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg,
																	 final Module m1,final Module m2,
																	 final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,
			  otherModules);
	}
	protected ServletImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg,
																	 final Module m1,final Module m2,final Module m3,
																	 final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,m3,
			  otherModules);
	}
	protected ServletImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg,
																	 final Module m1,final Module m2,final Module m3,final Module m4,
																	 final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,m3,m4,
			  otherModules);
	}
}
