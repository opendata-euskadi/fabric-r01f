package r01f.bootstrap.services.config.client;

import java.util.Collection;

import com.google.inject.Module;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.services.client.ClientAPI;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.Strings;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
@Accessors(prefix="_")
public class ServicesClientGuiceBootstrapConfigImpl  
	 extends ServicesClientBootstrapConfigBase
  implements ServicesClientGuiceBootstrapConfig {

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The guice module that bootstraps the client 
	 */
	@Getter private final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientBootstrapGuiceModuleType;
	@Getter private final Collection<Module> _moreClientBootstrapGuiceModules;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesClientGuiceBootstrapConfigImpl(final ClientApiAppCode clientApiAppCode,
												  final Class<? extends ClientAPI> clientApiType,
												  final Class<? extends ServiceInterface> serviceInterfaceBaseType,
												  final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientBootstrapGuiceModuleType,final Collection<Module> moreClientBootstrapGuiceModules,
												  final Collection<ServicesClientConfigForCoreModule<?,?>> coreModuleCfgs) {
		this(clientApiAppCode,
			 clientApiType,
			 serviceInterfaceBaseType,
			 clientBootstrapGuiceModuleType,moreClientBootstrapGuiceModules,
			 coreModuleCfgs,
			 null);	// no sub-module config		
	}
	public ServicesClientGuiceBootstrapConfigImpl(final ClientApiAppCode clientApiAppCode,
												  final Class<? extends ClientAPI> clientApiType,
												  final Class<? extends ServiceInterface> serviceInterfaceBaseType,
												  final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientBootstrapGuiceModuleType,final Collection<Module> moreClientBootstrapGuiceModules,
												  final Collection<ServicesClientConfigForCoreModule<?,?>> coreModuleCfgs,
												  final Collection<ServicesClientSubModuleBootstrapConfig<?>> subModulesCfgs) {
		super(clientApiAppCode, 
			  clientApiType, 
			  serviceInterfaceBaseType,
			  coreModuleCfgs,
			  subModulesCfgs);
		_clientBootstrapGuiceModuleType = clientBootstrapGuiceModuleType;
		_moreClientBootstrapGuiceModules = moreClientBootstrapGuiceModules;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} bootstraped by {}",
								  super.debugInfo(),
								  _clientBootstrapGuiceModuleType);
	}
}
