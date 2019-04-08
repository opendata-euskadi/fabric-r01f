package r01f.bootstrap.services.config.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.types.url.Host;
import r01f.types.url.UrlPath;

/**
 * Builds {@link ServicesClientConfigForCoreModule} objects
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServicesClientConfigForCoreModuleBuilder 
	       implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesClientConfigForCoreModuleBuilderExpositionStep of(final CoreAppCode coreAppCode,final CoreModule coreModule) {
		return new ServicesClientConfigForCoreModuleBuilder() { /* nothing */ }
						.new ServicesClientConfigForCoreModuleBuilderExpositionStep(coreAppCode,coreModule);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ServicesClientConfigForCoreModuleBuilderExpositionStep {
		private final CoreAppCode _coreAppCode;
		private final CoreModule _coreModule;
		
		public ServicesClientConfigForCoreModule<ServicesCoreModuleExpositionAsBeans,
												 ServicesClientProxyForCoreBeanExposed> forCoreExposedAsBeans() {
			return new ServicesClientConfigForCoreModule<ServicesCoreModuleExpositionAsBeans,
												 		 ServicesClientProxyForCoreBeanExposed>(_coreAppCode,_coreModule,
														 										new ServicesCoreModuleExpositionAsBeans(),new ServicesClientProxyForCoreBeanExposed());
			
		}
		public ServicesClientConfigForCoreModuleBuilderClientProxyStep<ServicesCoreModuleExpositionAsRESTServices> forCoreExposedAsRESTServiceAt(final Host host,final UrlPath urlPath) {
			ServicesCoreModuleExpositionAsRESTServices restCoreExpCfg = new ServicesCoreModuleExpositionAsRESTServices(host,urlPath);
			return new ServicesClientConfigForCoreModuleBuilderClientProxyStep<ServicesCoreModuleExpositionAsRESTServices>(_coreAppCode,_coreModule,
																														   restCoreExpCfg);
		}
		public ServicesClientConfigForCoreModuleBuilderClientProxyStep<ServicesCoreModuleExpositionAsServlet> forCoreExposedAsServletAt(final Host host,final UrlPath urlPath) {
			ServicesCoreModuleExpositionAsServlet servletCoreExpCfg = new ServicesCoreModuleExpositionAsServlet(host,urlPath);
			return new ServicesClientConfigForCoreModuleBuilderClientProxyStep<ServicesCoreModuleExpositionAsServlet>(_coreAppCode,_coreModule,
																													  servletCoreExpCfg);			
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class ServicesClientConfigForCoreModuleBuilderClientProxyStep<E extends ServicesCoreModuleExposition> {
		private final CoreAppCode _coreAppCode;
		private final CoreModule _coreModule;
		private final E _coreExpositionCfg;
		
		@SuppressWarnings("unchecked")
		public <P extends ServicesClientProxyToCoreImpl> ServicesClientConfigForCoreModule<E,P> findClientProxyTypesExtending(final Class<? extends ServiceProxyImpl> proxyBaseType) {
			ServicesClientConfigForCoreModule<?,?> outCfg = null;
			if (_coreExpositionCfg instanceof ServicesCoreModuleExpositionAsRESTServices) {
				ServicesClientProxyForCoreRESTExposed clientProxyCfg = new ServicesClientProxyForCoreRESTExposed(proxyBaseType);
				ServicesCoreModuleExpositionAsRESTServices restCoreExpCfg = (ServicesCoreModuleExpositionAsRESTServices)_coreExpositionCfg;
				outCfg = new ServicesClientConfigForCoreModule<ServicesCoreModuleExpositionAsRESTServices,
															   ServicesClientProxyForCoreRESTExposed>(_coreAppCode,_coreModule,
																	 								  restCoreExpCfg,clientProxyCfg);
			}
			else if (_coreExpositionCfg instanceof ServicesCoreModuleExpositionAsServlet) {
				ServicesClientProxyForCoreServletExposed clientProxyCfg = new ServicesClientProxyForCoreServletExposed(proxyBaseType);
				ServicesCoreModuleExpositionAsServlet restCoreExpCfg = (ServicesCoreModuleExpositionAsServlet)_coreExpositionCfg;
				outCfg = new ServicesClientConfigForCoreModule<ServicesCoreModuleExpositionAsServlet,
															   ServicesClientProxyForCoreServletExposed>(_coreAppCode,_coreModule,
																	 								     restCoreExpCfg,clientProxyCfg);
			}
			else {
				throw new IllegalStateException(_coreExpositionCfg.getClass() + " is not supported!!");
			}
			return (ServicesClientConfigForCoreModule<E,P>)outCfg;
		}
	}
}
