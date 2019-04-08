package r01f.bootstrap.services.config;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.patterns.IsBuilder;
import r01f.types.ExecutionMode;


/**
 * Builder for ServicesConfig
 * Usage: 
 * <pre class='brush:java'>
 * 
 * </pre>
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServicesBootstrapConfigBuilder 
	       implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILD 
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesBootstrapConfigCoreModulesStep forClient(final ServicesClientBootstrapConfig clientCfg) {
		return new ServicesBootstrapConfigBuilder() { /* nothing */ }
						.new ServicesBootstrapConfigCoreModulesStep(clientCfg);
	}
	public static ServicesBootstrapConfigCoreModulesStep noClient() {
		return new ServicesBootstrapConfigBuilder() { /* nothing */ }
						.new ServicesBootstrapConfigCoreModulesStep(null);	// no client
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE MODULES
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ServicesBootstrapConfigCoreModulesStep {
		private final ServicesClientBootstrapConfig _clientCfg;
		
		public ServicesClientBootstrapConfigCoreModulesEventsStep ofCoreModules(final ServicesCoreBootstrapConfig... coreModsCfg) {
			return new ServicesClientBootstrapConfigCoreModulesEventsStep(_clientCfg,
																	      Lists.newArrayList(coreModsCfg));
		}
		public ServicesClientBootstrapConfigCoreModulesBuildStep notBootstrappingCoreModules() {
			return new ServicesClientBootstrapConfigCoreModulesBuildStep(_clientCfg,
					 													 null,		// no cores
					 													 null);		// no core events
		}
		public ServicesBootstrapConfig build() {
			return this.notBootstrappingCoreModules()
					   .build();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE EVENTS
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ServicesClientBootstrapConfigCoreModulesEventsStep {
		private final ServicesClientBootstrapConfig _clientCfg;
		private final Collection<? extends ServicesCoreBootstrapConfig> _coreModulesConfig;
		
		public ServicesBootstrapConfig build() {
			return this.notUsingCoreEvents()
					   .build();
		}
		public ServicesClientBootstrapConfigCoreModulesBuildStep notUsingCoreEvents() {
			return _build(null);	// no core events used
		}
		public ServicesClientBootstrapConfigCoreModulesBuildStep usingCommonCoreEventsHandler() {
			return _build(null);	// Common Core Events Handler
		}
		public ServicesClientBootstrapConfigCoreModulesBuildStep coreEventsHandledAs(final ServicesCoreModuleEventsConfig cfg) {
			if (cfg.getExecutionMode().is(ExecutionMode.ASYNC)) {
				return this.coreEventsHandledAsynchronouslyByAnExecutorService(cfg.getNumberOfBackgroundThreads());
			} else if (cfg.getExecutionMode().is(ExecutionMode.SYNC)) {
				return this.coreEventsHandledSynchronously();
			} else {
				throw new IllegalArgumentException(cfg.getExecutionMode() + " is NOT supported!");
			}
		}
		public ServicesClientBootstrapConfigCoreModulesBuildStep coreEventsHandledSynchronously() {
			return _build(ServicesCoreModuleEventsConfig.syncEventHandling());
		}
		public ServicesClientBootstrapConfigCoreModulesBuildStep coreEventsHandledAsynchronouslyByAnExecutorService(final int numOfThreads) {
			return _build(ServicesCoreModuleEventsConfig.asyncEventHandlingUsingThreadPoolOf(numOfThreads));
		}
		private ServicesClientBootstrapConfigCoreModulesBuildStep _build(final ServicesCoreModuleEventsConfig eventsCfg) {
			return new ServicesClientBootstrapConfigCoreModulesBuildStep(_clientCfg,
					 													 _coreModulesConfig,
					 													 eventsCfg);		 
		}
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ServicesClientBootstrapConfigCoreModulesBuildStep {
		private final ServicesClientBootstrapConfig _clientCfg;
		private final Collection<? extends ServicesCoreBootstrapConfig> _coreModulesConfig;
		private final ServicesCoreModuleEventsConfig _eventsCfg;
		
		public ServicesBootstrapConfig build() {
			return new ServicesBootstrapConfig(_clientCfg,
											   _coreModulesConfig,
											   _eventsCfg);		 
		}
	}
}
