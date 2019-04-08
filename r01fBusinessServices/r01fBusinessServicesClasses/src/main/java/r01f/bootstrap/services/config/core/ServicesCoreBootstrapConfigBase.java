package r01f.bootstrap.services.config.core;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@RequiredArgsConstructor
abstract class ServicesCoreBootstrapConfigBase 
    implements ServicesCoreBootstrapConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The core app code
	 */
	@Getter protected final CoreAppCode _coreAppCode;
	/**
	 * The core module
	 */
	@Getter protected final CoreModule _coreModule;
	/**
	 * Sub-modules config
	 */
	@Getter protected final Collection<ServicesCoreSubModuleBootstrapConfig<?>> _subModulesCfgs;
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <C extends ServicesCoreBootstrapConfig> C as(final Class<C> type) {
		return (C)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getImplType() {
		if (this instanceof ServicesCoreBootstrapConfigWhenBeanExposed) {
			return ServicesImpl.Bean;
		} else if (this instanceof ServicesCoreBootstrapConfigWhenRESTExposed) {
			return ServicesImpl.REST;
		} else if (this instanceof ServicesCoreBootstrapConfigWhenServletExposed) {
			return ServicesImpl.Servlet;
		} 
		throw new IllegalStateException("Illegal exposition type: " + this.getClass());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-MODULE CONFIGS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <CFG extends ContainsConfigData> CFG getSubModuleConfigFor(final AppComponent component) {
		if (CollectionUtils.isNullOrEmpty(_subModulesCfgs)) {
//			log.warn("NO sub-modules config was set for {}",component);
			throw new IllegalStateException("NO sub-modules config was set for " + component);
		}
		ServicesCoreSubModuleBootstrapConfig<CFG> subCfg = (ServicesCoreSubModuleBootstrapConfig<CFG>)FluentIterable.from(_subModulesCfgs)
																	 .filter(new Predicate<ServicesCoreSubModuleBootstrapConfig<?>>() {
																					@Override
																					public boolean apply(final ServicesCoreSubModuleBootstrapConfig<?> cfg) {
																						return cfg.getComponent().is(component);
																					}
																	 		 })
																	 .first().orNull();
		return subCfg != null ? subCfg.getConfig() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{}/{} as {}",
								  _coreAppCode,_coreModule,
				   				  this.getImplType());
	}
}
