package r01f.bootstrap.services.config.client;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceProxyImpl;

/**
 * Models how the CLIENT access the CORE impl
 * @param <E>
 * @param <P>
 */
@Accessors(prefix="_")
public class ServicesClientConfigForCoreModule<E extends ServicesCoreModuleExposition,
											   P extends ServicesClientProxyToCoreImpl> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final CoreAppCode _coreAppCode;
	@Getter private final CoreModule _coreModule;
	
	@Getter private final E _coreExpositionConfig;
	@Getter private final P _clientProxyToCoreConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesClientConfigForCoreModule(final CoreAppCode coreAppCode,final CoreModule coreModule,
											 final E coreExpositionCfg,final P clientProxyToCoreCfg) {
		_coreAppCode = coreAppCode;
		_coreModule = coreModule;
		
		_coreExpositionConfig = coreExpositionCfg;
		_clientProxyToCoreConfig = clientProxyToCoreCfg;
		
		// ensure the exposition and client proxy configs are for the same type of core
		if (_coreExpositionConfig.getServiceImpl().isNOT(_clientProxyToCoreConfig.getServiceImpl())) throw new IllegalArgumentException("Core exposition config " + coreExpositionCfg.getClass().getSimpleName() + " doew NOT match client proxy to core impl " + clientProxyToCoreCfg.getClass().getName()); 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the core impl type (must be the same for the core exposition config and the client proxy to core)
	 */
	public ServicesImpl getCoreImplType() {
		return _coreExpositionConfig.getServiceImpl();
	}
	/**
	 * @return the base type for all client proxy types
	 */
	public Class<? extends ServiceProxyImpl> getServiceProxyImplsBaseType() {
		if (this.getCoreImplType() == ServicesImpl.Bean) throw new IllegalStateException("BEAN cores DO NOT use client PROXY!!");
		
		Class<? extends ServiceProxyImpl> outProxiesBaseType = null;
		if (_clientProxyToCoreConfig instanceof ServicesClientProxyForCoreRESTExposed) {
			ServicesClientProxyForCoreRESTExposed restProxyCfg = (ServicesClientProxyForCoreRESTExposed)_clientProxyToCoreConfig;
			outProxiesBaseType = restProxyCfg.getServiceProxyImplsBaseType();
		}
		else if (_clientProxyToCoreConfig instanceof ServicesClientProxyForCoreServletExposed) {
			ServicesClientProxyForCoreServletExposed servletProxyCfg = (ServicesClientProxyForCoreServletExposed)_clientProxyToCoreConfig;
			outProxiesBaseType = servletProxyCfg.getServiceProxyImplsBaseType();
		} 
		else {
			throw new IllegalStateException(_clientProxyToCoreConfig + " is NOT a supported client proxy to core config type");
		}
		return outProxiesBaseType;
	}
}
