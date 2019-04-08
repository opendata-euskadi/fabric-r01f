package r01f.bootstrap.services.config.client;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.client.ClientAPI;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
abstract class ServicesClientBootstrapConfigBase 
    implements ServicesClientBootstrapConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The client app code
	 */
	@Getter protected final ClientApiAppCode _clientApiAppCode;
	/**
	 * The client api that exposes the fine-grained services interfaces
	 */
	@Getter protected final Class<? extends ClientAPI> _clientApiType;
	/**
	 * The java package where the services interfaces can be found
	 */
	@Getter protected final Class<? extends ServiceInterface> _serviceInterfacesBaseType;
	/**
	 * How is this module exposed to the client API
	 */
	@Getter protected final Collection<ServicesClientConfigForCoreModule<?,?>> _coreModuleConfigs;
	/**
	 * any client sub-module config
	 */
	@Getter protected final Collection<ServicesClientSubModuleBootstrapConfig<?>> _subModulesCfgs;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONFIG FOR CORE
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <E extends ServicesCoreModuleExposition,
		    P extends ServicesClientProxyToCoreImpl> ServicesClientConfigForCoreModule<E,P> getConfigForCoreModule(final CoreAppCode coreAppCode,final CoreModule coreModule) {
		if (CollectionUtils.isNullOrEmpty(_coreModuleConfigs)) throw new IllegalStateException("NO config for core modules found at client config");
		ServicesClientConfigForCoreModule<?,?> outCfg = FluentIterable.from(_coreModuleConfigs)
															.filter(new Predicate<ServicesClientConfigForCoreModule<?,?>>() {
																			@Override
																			public boolean apply(final ServicesClientConfigForCoreModule<?,?> cfg) {
																				return cfg.getCoreAppCode().is(coreAppCode)
																					&& cfg.getCoreModule().is(coreModule);
																			}
																	})
															.first().orNull();
		return (ServicesClientConfigForCoreModule<E,P>)outCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-MODULE CONFIGS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <CFG extends ContainsConfigData> CFG getSubModuleConfigFor(final AppComponent component) {
		if (CollectionUtils.isNullOrEmpty(_subModulesCfgs)) throw new IllegalStateException("NO sub-modules config was set!");
		ServicesClientSubModuleBootstrapConfig<?> subCfg = FluentIterable.from(_subModulesCfgs)
																 .filter(new Predicate<ServicesClientSubModuleBootstrapConfig<?>>() {
																				@Override
																				public boolean apply(final ServicesClientSubModuleBootstrapConfig<?> cfg) {
																					return cfg.getComponent().is(component);
																				}
																 		 })
																 .first().orNull();
		return (CFG)(subCfg != null ? subCfg.getConfig() : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} client api {} for service interfaces extending {} ({} core modules)",
								  _clientApiAppCode,
								  _clientApiType,
								  _serviceInterfacesBaseType,
								  _coreModuleConfigs != null ? _coreModuleConfigs.size() : 0);
	}
}
