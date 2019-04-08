package r01f.bootstrap.services;

import java.util.Collection;

import com.google.inject.Binder;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfig;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.collections.CollectionUtils;

/**
 * When more than a single coreAppCode / module is found in the classpath there's a big chance for a collision of 
 * binded resources like JPA's EntityManager that MUST be binded at guice's {@link PrivateModule}s 
 * (see guice multiple persist modules at https://github.com/google/guice/wiki/GuicePersistMultiModules)
 * 
 * The solution is isolate core bindings for every coreAppCode / module at a separate private module and expose only 
 * the public service interface implementations.
 */
@Slf4j
@RequiredArgsConstructor
public class ServicesCoreBootstrapPrivateGuiceModule 
     extends PrivateModule {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * match for each service interfaces to a bean impl or a proxy
	 */
	private final Collection<ServiceInterfaceMatch> _coreImplMatchings;
	/**
	 * Core config
	 */
	private final ServicesCoreGuiceBootstrapConfig _coreModuleCfg;
	/**
	 * Core module
	 */
	private final ServicesCoreBootstrapGuiceModule _coreBootstrapModule;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void configure() {
		Binder privateBinder = this.binder();
		
		log.warn("\n\n\n\n\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		log.warn("[START]-Binding PRIVATE CORE guice module for {}/{} using {}",
				 _coreModuleCfg.getCoreAppCode(),_coreModuleCfg.getCoreModule(),
				 _coreModuleCfg.getCoreBootstrapGuiceModuleType());
		
		// [1] - Create the core bootstrap guice module and install it as PRIVATE module
		// 		 BEWARE!!!	do NOT install the REST core buide modules (they're binded at ServicesMainGuiceBootstrap, otherwise they're not visible
		// 					to the outside world and so the Guice Servlet filter cannot see REST resources)
		privateBinder.install(_coreBootstrapModule);
			
		log.warn("  [END]-Binding PRIVATE CORE guice module for {}/{}: {}",
				 _coreModuleCfg.getCoreAppCode(),_coreModuleCfg.getCoreModule(),
				 _coreModuleCfg.getCoreBootstrapGuiceModuleType());
		log.warn("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n\n\n\n");
		
		// [2] - Bind core impls and expose them to the outer modules 
		//		 (the core impl type will be binded to the service interface later at ServicesBootstrap)
		if (CollectionUtils.hasData(_coreImplMatchings)) {
			for (ServiceInterfaceMatch serviceInterfaceCoreImplMatch : _coreImplMatchings) {
				// core impl
				Class<? extends ServiceInterface> coreImplType = serviceInterfaceCoreImplMatch.getProxyOrImplMatchingType();		// sure it's a core imp
				
				// bind the service impl as singleton
				privateBinder.bind(coreImplType)		
					  		 .in(Singleton.class);
				
				// expose in order to be binded to the service interface type at ServiceBootstap 
				this.expose(coreImplType);
			}
		}
		
		// [3] - Expose bindings outside the private module if the core bootstrap guice module exposes any of it's bindings
		if (_coreBootstrapModule instanceof ServicesCoreBootstrapGuiceModuleExposesBindings) {
			ServicesCoreBootstrapGuiceModuleExposesBindings exposes = (ServicesCoreBootstrapGuiceModuleExposesBindings)_coreBootstrapModule;
			exposes.exposeBindings(this.binder());
		}
	}
}
