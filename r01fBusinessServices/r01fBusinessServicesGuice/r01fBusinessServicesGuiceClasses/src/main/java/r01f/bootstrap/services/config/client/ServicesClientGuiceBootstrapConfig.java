package r01f.bootstrap.services.config.client;

import java.util.Collection;

import com.google.inject.Module;

import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
public interface ServicesClientGuiceBootstrapConfig 
		 extends ServicesClientBootstrapConfig {
	
	public Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> getClientBootstrapGuiceModuleType();
	public Collection<Module> getMoreClientBootstrapGuiceModules();
}
