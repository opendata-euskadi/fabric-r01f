package r01f.bootstrap.services.core;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Module;

import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.inject.HasMoreBindings;
import r01f.reflection.ReflectionUtils;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
abstract class ServicesCoreBootstrapGuiceModuleBase
    implements ServicesCoreBootstrapGuiceModule {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Core config
	 */
	protected final ServicesCoreBootstrapConfig _coreBootstrapCfg;
	/**
	 * The installed modules
	 */
	protected final Collection<Module> _installedModules;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfig coreModuleCfg) {
		_coreBootstrapCfg = coreModuleCfg;
		_installedModules = Lists.newArrayList();
	}
	protected ServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfig coreBootstrapCfg,
												   final Collection<? extends Module> modulesToInstall) {
		this(coreBootstrapCfg);
		
		// modules to install
		if (CollectionUtils.hasData(modulesToInstall)) {
			for (Module m : modulesToInstall) {
				_installedModules.add(m);
			}
		}
	}
	protected ServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfig coreBootstrapCfg,
												   final Module m1,
												   final Collection<? extends Module> otherModules) {
		this(coreBootstrapCfg);
		
		// modules to install
		if (m1 != null) {
			_installedModules.add(m1);
		}
		if (CollectionUtils.hasData(otherModules)) {		 
		     for (Module m : otherModules) {
		    	 _installedModules.add(m);
		     }
		}
	}
	protected ServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfig coreBootstrapCfg,
												   final Module m1,final Module m2,
												   final Collection<? extends Module> otherModules) {
		this(coreBootstrapCfg);
		
		// modules to install
		if (m1 != null) {
			_installedModules.add(m1);
		}
		if (m2 != null) {
			_installedModules.add(m2);
		}
		if (CollectionUtils.hasData(otherModules)) {		 
		     for (Module m : otherModules) {
		    	 _installedModules.add(m);
		     }
		}
	}
	protected ServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfig coreBootstrapCfg,
												   final Module m1,final Module m2,final Module m3,
												   final Collection<? extends Module> otherModules) {
		this(coreBootstrapCfg);
		
		// modules to install
		if (m1 != null) {
			_installedModules.add(m1);
		}
		if (m2 != null) {
			_installedModules.add(m2);
		}
		if (m3 != null) {
			_installedModules.add(m3);
		}
		if (CollectionUtils.hasData(otherModules)) {		 
		     for (Module m : otherModules) {
		    	 _installedModules.add(m);
		     }
		}
	}
	protected ServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfig coreBootstrapCfg,
												   final Module m1,final Module m2,final Module m3,final Module m4,
												   final Collection<? extends Module> otherModules) {
		this(coreBootstrapCfg);
		
		// modules to install
		if (m1 != null) {
			_installedModules.add(m1);
		}
		if (m2 != null) {
			_installedModules.add(m2);
		}
		if (m3 != null) {
			_installedModules.add(m3);
		}
		if (m4 != null) {
			_installedModules.add(m4);
		}
		if (CollectionUtils.hasData(otherModules)) {		 
		     for (Module m : otherModules) {
		    	 _installedModules.add(m);
		     }
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODULE INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		Binder theBinder = binder; 
		
		// [0]: Bind the core config
		binder.bind(ServicesCoreBootstrapConfigWhenBeanExposed.class)
			  .toInstance((ServicesCoreBootstrapConfigWhenBeanExposed)_coreBootstrapCfg);
		
		// [1] - Bind the core properties to be injected as @XmlPropertiesForAppComponent("{clientAppCode}.services")
		ServicesBootstrapUtil.bindXMLPropertiesForAppComponent(_coreBootstrapCfg.getCoreAppCode(),AppComponent.compose(_coreBootstrapCfg.getCoreModule(),
																													   CoreModule.SERVICES),
															   CoreModule.SERVICES,
															   theBinder);
		
		// [2]: Install Modules & bind specific properties
		if (CollectionUtils.hasData(_installedModules)) {
			for (Module m : _installedModules) {
				log.warn("Install {} guice module",
						 m.getClass());
				
				theBinder.install(m);
			}
		}		
		
		// [3]: Other bindings
		if (this instanceof HasMoreBindings) {
			((HasMoreBindings)this).configureMoreBindings(theBinder);
		}
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ServicesCoreBootstrapGuiceModule
/////////////////////////////////////////////////////////////////////////////////////////
//	@Override
	public Collection<Class<? extends Module>> getInstalledModuleTypes() {
		return FluentIterable.from(_installedModules)
							 .transform(new Function<Module,Class<? extends Module>>() {
												@Override
												public Class<? extends Module> apply(final Module mod) {
													return mod.getClass();
												}
							 			})
							 .toList();
	}
//	@Override
	public boolean isModuleInstalled(final Class<? extends Module> modType) {
		if (CollectionUtils.isNullOrEmpty(_installedModules)) return false;
		
		return FluentIterable.from(this.getInstalledModuleTypes())
							 .anyMatch(new Predicate<Class<? extends Module>>() {
											@Override
											public boolean apply(final Class<? extends Module> mT) {
												return ReflectionUtils.isSubClassOf(mT,modType);
											}
							 		   });
	}
}
