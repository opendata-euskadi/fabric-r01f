package r01f.bootstrap.services.config;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.util.types.collections.CollectionUtils;


@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesBootstrapConfig 
  implements ServicesConfigObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServicesClientBootstrapConfig _clientConfig;
	@Getter private final Collection<? extends ServicesCoreBootstrapConfig> _coreModulesConfig;
	@Getter private final ServicesCoreModuleEventsConfig _coreEventsConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientApiAppCode getClientApiAppCode() {
		return _clientConfig.getClientApiAppCode();
	}
	@SuppressWarnings("unchecked")
	public <C extends ServicesClientBootstrapConfig> C getClientConfigAs(final Class<C> type) {
		return (C)_clientConfig;
	}
	@SuppressWarnings("unchecked")
	public <C extends ServicesCoreBootstrapConfig> C getCoreModuleConfig(final CoreAppCode coreAppCode,final CoreModule coreModule,
																		 final Class<C> type) {
		ServicesCoreBootstrapConfig coreCfg = FluentIterable.from(_coreModulesConfig)
														.filter(new Predicate<ServicesCoreBootstrapConfig>() {
																			@Override
																			public boolean apply(final ServicesCoreBootstrapConfig cfg) {
																				return cfg.getCoreAppCode().is(coreAppCode)
																					&& cfg.getCoreModule().is(coreModule);
																			}
																})
														.first().orNull();
		return (C)coreCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		StringBuilder dbg = new StringBuilder();
		if (_clientConfig != null) dbg.append(_clientConfig.debugInfo()).append("\n");
		if (CollectionUtils.hasData(_coreModulesConfig)) {
			for (Iterator<? extends ServicesCoreBootstrapConfig> coreCfgIt = _coreModulesConfig.iterator(); coreCfgIt.hasNext(); ) {
				dbg.append("\t").append(coreCfgIt.next().debugInfo());
				if (coreCfgIt.hasNext()) dbg.append("\n");
			}
		}
		if (_coreEventsConfig != null) {
			dbg.append("\nCore event handling config: ")
			   .append(_coreEventsConfig.debugInfo());
		}
		return dbg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
}
