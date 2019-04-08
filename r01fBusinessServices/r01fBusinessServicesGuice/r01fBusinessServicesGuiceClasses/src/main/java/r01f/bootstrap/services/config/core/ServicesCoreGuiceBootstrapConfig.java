package r01f.bootstrap.services.config.core;

import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;

/**
 * @see ServicesCoreBootstrapConfigBuilder
 */
public interface ServicesCoreGuiceBootstrapConfig 
         extends ServicesCoreBootstrapConfig {
	
	public Class<? extends ServicesCoreBootstrapGuiceModule> getCoreBootstrapGuiceModuleType();
	public boolean isIsolate();
}
