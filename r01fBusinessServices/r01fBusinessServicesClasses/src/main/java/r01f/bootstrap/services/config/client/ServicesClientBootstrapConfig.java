package r01f.bootstrap.services.config.client;

import java.util.Collection;

import r01f.bootstrap.services.config.ServicesConfigObject;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.client.ClientAPI;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
public interface ServicesClientBootstrapConfig
		 extends ServicesConfigObject {

	public ClientApiAppCode getClientApiAppCode();
	public Class<? extends ClientAPI> getClientApiType();
	public Class<? extends ServiceInterface> getServiceInterfacesBaseType();
	public <CFG extends ContainsConfigData> CFG getSubModuleConfigFor(final AppComponent component);
	
	public Collection<ServicesClientConfigForCoreModule<?,?>> getCoreModuleConfigs();
}
