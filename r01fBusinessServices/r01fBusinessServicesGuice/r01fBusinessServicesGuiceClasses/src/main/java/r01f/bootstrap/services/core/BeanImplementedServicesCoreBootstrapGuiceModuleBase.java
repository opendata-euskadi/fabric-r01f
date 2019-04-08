package r01f.bootstrap.services.core;



import java.util.Collection;

import com.google.inject.Module;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.services.interfaces.ServiceInterface;

/**
 * Creates the core-side bindings for the service interfaces (api)
 * 
 * Usually the core (server) side implements more than one {@link ServiceInterface} and sometime exists the need to access a {@link ServiceInterface} logic
 * from another {@link ServiceInterface}. In such cases in order to avoid the use of the client API (and "leave" the core to return to it through the client)
 * a {@link CoreServicesAggregator} exists at the core (server) side
 * This {@link CoreServicesAggregator} can be injected at core-side to cross-use the {@link ServiceInterface} logic.
 * 
 * If many {@link CoreServicesAggregator} types exists at the core side, they MUST be annotated with a type annotated with {@link CoreServiceAggregatorQualifier}
 * in order to distinguish one another
 */
@Accessors(prefix="_")
public abstract class BeanImplementedServicesCoreBootstrapGuiceModuleBase
  		      extends ServicesCoreBootstrapGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public BeanImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg) {
		super(coreBootstrapCfg);
	}
	protected BeanImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																  final Collection<? extends Module> modulesToInstall) {
		super(coreBootstrapCfg,
			  modulesToInstall);
	}
	protected BeanImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																  final Module m1,
																  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,
			  otherModules);
	}
	protected BeanImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																  final Module m1,final Module m2,
												   				  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,
			  otherModules);
	}
	protected BeanImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																  final Module m1,final Module m2,final Module m3,
												   				  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,m3,
			  otherModules);
	}
	protected BeanImplementedServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																  final Module m1,final Module m2,final Module m3,final Module m4,
												   				  final Collection<? extends Module> otherModules) {
		super(coreBootstrapCfg,
			  m1,m2,m3,m4,
			  otherModules);
	}
}
