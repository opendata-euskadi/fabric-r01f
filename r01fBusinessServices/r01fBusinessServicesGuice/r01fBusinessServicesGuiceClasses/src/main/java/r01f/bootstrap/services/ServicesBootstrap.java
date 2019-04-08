package r01f.bootstrap.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.R01FBootstrapGuiceModule;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.client.ServicesClientBootstrapGuiceModule;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenRESTExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfig;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;
import r01f.services.client.ClientAPI;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@Slf4j
@RequiredArgsConstructor
public class ServicesBootstrap {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * create a service matcher object in charge of finding the service interface to proxy or core impl matchings
	 */
	private final ServiceMatcher _serviceMatcher = new ServiceMatcher(this.getClass().getClassLoader());
	/**
	 * Bootstrap configs
	 */
	private final Collection<ServicesBootstrapConfig> _bootstrapCfgs;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
  	 * Load bootstrap module instances
	 *	- If there's more than a single api appCode a private module for every api appCode is returned so
	 *	  there's NO conflict between each api appCode
	 *	- If there's a single api appCode there's no need to isolate every api appcode in it's own private module
	 * @return
	 */
	Collection<Module> loadBootstrapModuleInstances() {
		final List<Module> bootstrapModules = Lists.newArrayList();
		
		// Usually there's a single ServicesBootstrapConfig for a client api appCode, BUT is perfectly possible to
		// bootstrap usings multiple ServicesBootstrapConfig for the SAME client api appCode or for different client api appCode
		// ... the first step is GROUP the ServicesBootstrapConfig byt client api appCode
		//	   (note that the ServiceInterface to CORE impl or PROXY binding Map is binded annotated by client api appCode)
		Map<ClientApiAppCode,Collection<ServicesBootstrapConfig>> bootstrapCfgByClientApiAppCode = _bootstrapCfgsByClientApiAppCode();
				
		for (final Map.Entry<ClientApiAppCode,Collection<ServicesBootstrapConfig>> me : bootstrapCfgByClientApiAppCode.entrySet()) {
			ClientApiAppCode clientApiAppCode = me.getKey();
			Collection<ServicesBootstrapConfig> bootstrapCfgs = me.getValue();
			
			if (CollectionUtils.isNullOrEmpty(bootstrapCfgs)) continue;
			
			log.warn("\n\n\n\n");
			log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			log.warn("BOOTSTRAPING: {}",clientApiAppCode);
			log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			
			// [1] - Consolidate the service interface matchings into a single collection
			ServiceInterfacesMatchings serviceInterfaceMatchings = _consolidateServiceInterfaceMatchings(bootstrapCfgs);
			
			// [2] - Create the client and core matchings
			for (final ServicesBootstrapConfig bootstrapCfg  : bootstrapCfgs) {
				
				// [a] - Create a guice module for the client and for every core module
				final Collection<Module> clientAndCodeBootstrap = _createClientAndCoreBootstrapModules(bootstrapCfg,
																									   serviceInterfaceMatchings);
				
				// [b] - Client bindings:
				//			- client api as singleton
				//			- every service interface to the core impl or the proxy to the core impl
				//		 	  this are the binding that the client will use
				//		 	  ... so if both the core impl and the proxy to the core impl are available, 
				//		          the core impl will be used
				clientAndCodeBootstrap.add(_createClientApiAndServiceInterfaceBindingsModule(bootstrapCfg.getClientConfig().getClientApiType(),
																							 serviceInterfaceMatchings));
				
				// [c] - bind event bus for core events
				Module coreEventBusBindingModule = ServicesBootstrapUtil.createCoreEventBusBindingModule(bootstrapCfg.getClientApiAppCode(),
																										 bootstrapCfg.getCoreEventsConfig());
				clientAndCodeBootstrap.add(coreEventBusBindingModule);
	
				if (CollectionUtils.hasData(clientAndCodeBootstrap)) bootstrapModules.addAll(clientAndCodeBootstrap);
			}
		}
		
		// sometimes there's NO client, this is the case of REST services bootstrapping: only the REST bootstrap module is needed
		Collection<ServicesBootstrapConfig> bootstrapCfgsWithoutClient = FluentIterable.from(_bootstrapCfgs)
																				.filter(new Predicate<ServicesBootstrapConfig>() {
																								@Override
																								public boolean apply(final ServicesBootstrapConfig cfg) {
																									return cfg.getClientConfig() == null;
																								}
																						})
																				.toList();
		if (CollectionUtils.hasData(bootstrapCfgsWithoutClient)) {
			for (final ServicesBootstrapConfig bootstrapCfg  : bootstrapCfgsWithoutClient) {
				final Collection<Module> clientAndCodeBootstrap = _createClientAndCoreBootstrapModules(bootstrapCfg,
																									   null);	// no client = no service interface matchings
				if (CollectionUtils.hasData(clientAndCodeBootstrap)) bootstrapModules.addAll(clientAndCodeBootstrap);
			}
		}
		
		// Add the mandatory R01F guice modules
		bootstrapModules.add(0,new R01FBootstrapGuiceModule());

		return bootstrapModules;
	}
	/**
	 * Groups {@link ServicesBootstrapConfig} objects by client api appCode
	 * @return
	 */
	private Map<ClientApiAppCode,Collection<ServicesBootstrapConfig>> _bootstrapCfgsByClientApiAppCode() {
		Map<ClientApiAppCode,Collection<ServicesBootstrapConfig>> outMap = Maps.newHashMap();
		for (final ServicesBootstrapConfig bootstrapCfg  : _bootstrapCfgs) {
			if (bootstrapCfg.getClientConfig() == null) continue;	// no client
			
			Collection<ServicesBootstrapConfig> cfgs = outMap.get(bootstrapCfg.getClientApiAppCode());
			if (cfgs != null) {
				cfgs.add(bootstrapCfg);
			} else {
				cfgs = Lists.newArrayList();
				cfgs.add(bootstrapCfg);
				outMap.put(bootstrapCfg.getClientApiAppCode(),
						   cfgs);
			}
		}
		return outMap;
	}
	/**
	 * Given a collection of {@link ServicesBootstrapConfig} for the SAME client api appCode, 
	 * this method consolidates in a SINGLE {@link ServiceInterfacesMatchings} object all the matchings
	 * @param bootstrapCfgs
	 * @return
	 */
	private ServiceInterfacesMatchings _consolidateServiceInterfaceMatchings(final Collection<ServicesBootstrapConfig> bootstrapCfgs) {
		ServiceInterfacesMatchings outMatchings = null;
		for (final ServicesBootstrapConfig bootstrapCfg  : bootstrapCfgs) {
			ServiceInterfacesMatchings serviceInterfaceMatchings = _serviceMatcher.serviceInterfacesToImplOrProxyMatchings(bootstrapCfg);
			if (outMatchings != null) {
				// consolidate
				outMatchings.consolidateWith(serviceInterfaceMatchings);
			} else {
				outMatchings = serviceInterfaceMatchings;
			}
		}
		return outMatchings;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a module for the API appCode that gets installed with:
	 * 	- A module with the client API bindings
	 *	- A private module with the core bindings for each core app module
	 * @param bootstrapCfg
	 * @param serviceInterfaceMatchings
	 * @return
	 */
	private Collection<Module> _createClientAndCoreBootstrapModules(final ServicesBootstrapConfig bootstrapCfg,
																	final ServiceInterfacesMatchings serviceInterfaceMatchings) {
		// contains all the guice modules to be bootstraped: client & core
		final List<Module> bootstrapModuleInstances = Lists.newArrayList();

		// [1] - Add the CLIENT bootstrap guice module
		if (bootstrapCfg.getClientConfig() != null) {
			ServicesClientGuiceBootstrapConfig clientBootstrapCfg = bootstrapCfg.getClientConfigAs(ServicesClientGuiceBootstrapConfig.class);
			
			// a) additional client bootstrap modules>
			if (CollectionUtils.hasData(clientBootstrapCfg.getMoreClientBootstrapGuiceModules())) {
				for (final Module mod : clientBootstrapCfg.getMoreClientBootstrapGuiceModules()) {
					bootstrapModuleInstances.add(0,mod);	// insert first
				}
			}
			// b) main client bootstrap
			final ServicesClientAPIBootstrapGuiceModuleBase clientModule = _createClientGuiceModuleInstance(clientBootstrapCfg);
			bootstrapModuleInstances.add(0,clientModule);	// insert first!
		} else {
			log.warn("NO client will be bootstrapped!");
		}
		
		// [2] - Add a module for each CORE appCode / module
		if (CollectionUtils.isNullOrEmpty(bootstrapCfg.getCoreModulesConfig())) {
			log.warn("NO core modules will be bootstrapped!");
			return bootstrapModuleInstances;	// no cores
		}
		
		for (final ServicesCoreBootstrapConfig coreModuleCfg : bootstrapCfg.getCoreModulesConfig()) {
			// Each core bootstrap modules (the ones implementing BeanImplementedServicesCoreBootstrapGuiceModuleBase) for every core appCode / module
			// SHOULD reside in it's own private guice module in order to avoid bindings collisions
			// (ie JPA's guice persist modules MUST reside in separate private guice modules -see https://github.com/google/guice/wiki/GuicePersistMultiModules-)
			// ... BUT the REST or Servlet core bootstrap modules (the ones extending RESTImplementedServicesCoreBootstrapGuiceModuleBase or ServletImplementedServicesCoreBootstrapGuiceModuleBase) 
			//     MUST be binded here in order to let the world see (specially the Guice Servlet filter) see the REST resources bindings
			ServicesCoreGuiceBootstrapConfig guiceCoreModuleCfg = (ServicesCoreGuiceBootstrapConfig)coreModuleCfg;
			
			if (guiceCoreModuleCfg.getCoreBootstrapGuiceModuleType() == null) {
				log.error("Could NOT bootstrap core module {}/{} since the guice module type is null",
						  coreModuleCfg.getCoreAppCode(),coreModuleCfg.getCoreModule());
				continue;
			}

			// a) find the service core impls that will be binded as singletons 
			//    (only for bean implemented core modules)
			Collection<ServiceInterfaceMatch> coreImplMatchings = coreModuleCfg.getImplType() == ServicesImpl.Bean
																		? serviceInterfaceMatchings != null ? serviceInterfaceMatchings.getServiceInterfacesCoreImplMatchingsFor(coreModuleCfg.getCoreAppCode(),
																																												 coreModuleCfg.getCoreModule())
																											: null
																	    : null;
			// b) create the guice module that bootstraps the core	
			ServicesCoreBootstrapGuiceModule coreBootstrapModule = _createCoreGuiceModuleInstance(guiceCoreModuleCfg);
			
			// c) create a private guice module that will:
			Module coreGuiceModule = null;							
			if (guiceCoreModuleCfg.isIsolate()) {
				if (bootstrapCfg.getClientConfig() == null) throw new IllegalStateException("Only modules with client can be isolated!");
				// At the private module:
				//	i - bind every service core impl as a singleton
				//	ii- bootstrap the core
				coreGuiceModule = new ServicesCoreBootstrapPrivateGuiceModule(coreImplMatchings,	// core impls that will be binded as singletons and exposed
																			  guiceCoreModuleCfg,	
																			  coreBootstrapModule);	// the module to be isolated
			} else {
				// not isolated core module (ie: REST or Servlet modules)
				// they usually DO NOT contain service core impls so there's no service core impl to bind 
				// ... just bootstrap the module
				coreGuiceModule = coreBootstrapModule;	
			}
			bootstrapModuleInstances.add(coreGuiceModule);
		}
		
		// [6] - return
		return bootstrapModuleInstances;
	}
	private Module _createClientApiAndServiceInterfaceBindingsModule(final Class<? extends ClientAPI> clientApiType,
																	 final ServiceInterfacesMatchings serviceInterfaceMatchings) {
		return new Module() {
						@Override
						public void configure(final Binder binder) {
							// [0] - Bind the client api as a singleton
							binder.bind(clientApiType)
								  .in(Singleton.class);
							
							// [1] - Bind client proxies as singletons
							// 		 BEWARE CORE impl was binded as singletons at the private module (see ServicesCoreBootstrapPrivateGuiceModule)
							if (serviceInterfaceMatchings.hasData()) {
								
								for (final ServiceInterfaceMatch ifaceMatch : serviceInterfaceMatchings) {
									Class<? extends ServiceInterface> iface = ifaceMatch.getServiceInterfaceType();
									Class<? extends ServiceInterface> implOrProxy = ifaceMatch.getProxyOrImplMatchingType();
									
									if (ifaceMatch.isProxy()
									 && !serviceInterfaceMatchings.existsCoreImplMatchingFor(ifaceMatch.getCoreAppCode(),ifaceMatch.getCoreModule(),
											 											     ifaceMatch.getServiceInterfaceType())) {
										// a proxy must NOT be binded if there's another matching for the core bean impl
										binder.bind(_captureSubType(implOrProxy))
											  .in(Singleton.class);
									} else if (ifaceMatch.isCoreImpl()) {
										// BEWARE! - the core impls usually are binded as singletons in the private module (see ServicesCoreBootstrapPrivateGuiceModule) and exposed to the outer world
									}
									
									// b) bind the service interface to the proxy or impl
									binder.bind(_captureType(iface))
										  .to(_captureSubType(implOrProxy));
								}
							}
							
							// [3] - Bind the service interface types to bean impl or proxy types as:
							//		 [a] - A MapBinder that binds the service interface type to the bean impl or proxy instance
							//			   This MapBinder is used at the API's proxy aggregator to inject the correct service interface bean impl or proxy 
							//			   (see ServicesClientProxyLazyLoaderGuiceMethodInterceptor)
							//		 [b] - A direct bind of the service interface type to the bean impl or proxy type
							Named mapBinderNamed = Names.named(serviceInterfaceMatchings.getClientApiAppCode().asString());
							@SuppressWarnings("rawtypes")
							MapBinder<Class,ServiceInterface> serviceIfaceTypeToImplOrProxyBinder = MapBinder.newMapBinder(binder,
																									 				 	   Class.class,ServiceInterface.class,
																									 				 	   mapBinderNamed);
							if (serviceInterfaceMatchings.hasData()) {
								for (final ServiceInterfaceMatch ifaceMatch : serviceInterfaceMatchings) {
									Class<? extends ServiceInterface> iface = ifaceMatch.getServiceInterfaceType();
									Class<? extends ServiceInterface> implOrProxy = ifaceMatch.getProxyOrImplMatchingType();
									
									// a an interface to impl / proxy binding to the Map used at ServicesClientProxyLazyLoaderGuiceMethodInterceptor
									serviceIfaceTypeToImplOrProxyBinder.addBinding(_captureType(iface))
													 			 	   .to(_captureSubType(implOrProxy));
								}
							}
						}
			   };
	}
	@SuppressWarnings("unchecked")
	private static <S extends ServiceInterface> Class<S> _captureType(final Class<? extends ServiceInterface> type) {
		return (Class<S>)type;
	}
	@SuppressWarnings("unchecked")
	private static <S extends ServiceInterface> Class<? extends S> _captureSubType(final Class<? extends ServiceInterface> type) {
		return (Class<? extends S>)type;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOTSTRAP GUICE MODULES CREATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a client bootstrap guice module instance
	 * @param servicesClientBootstrapCfg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <G extends ServicesClientBootstrapGuiceModule> G _createClientGuiceModuleInstance(final ServicesClientGuiceBootstrapConfig servicesClientBootstrapCfg) {
		try {
			G outMod =  ReflectionUtils.createInstanceOf((Class<G>)servicesClientBootstrapCfg.getClientBootstrapGuiceModuleType(),
												    	 new Class<?>[] { ServicesClientGuiceBootstrapConfig.class },
												    	 new Object[] { servicesClientBootstrapCfg });
			return outMod;
		} catch (ReflectionException refEx) {
			log.error("Could NOT create an instance of {} bootstrap guice module. The module MUST have {}-based constructor",
					  servicesClientBootstrapCfg.getClientBootstrapGuiceModuleType(),
					  ServicesClientGuiceBootstrapConfig.class);
			throw refEx;
		}
	}
	/**
	 * Creates a core bootstrap guice module instance
	 * @param servicesCoreBootstrapCfg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <G extends ServicesCoreBootstrapGuiceModule> G _createCoreGuiceModuleInstance(final ServicesCoreGuiceBootstrapConfig servicesCoreBootstrapCfg) {
		try {
			// find the constructor arg type
			Class<?> cfgType = null;
			if (servicesCoreBootstrapCfg.getImplType() == ServicesImpl.Bean) {
				cfgType = ServicesCoreBootstrapConfigWhenBeanExposed.class;
			} else if (servicesCoreBootstrapCfg.getImplType() == ServicesImpl.REST) {
				cfgType = ServicesCoreBootstrapConfigWhenRESTExposed.class;
			} else if (servicesCoreBootstrapCfg.getImplType() == ServicesImpl.Servlet) {
				cfgType = ServicesCoreBootstrapConfigWhenServletExposed.class;
			}
			// create the module
			G outMod =  ReflectionUtils.createInstanceOf((Class<G>)servicesCoreBootstrapCfg.getCoreBootstrapGuiceModuleType(),
												    	 new Class<?>[] { cfgType },
												    	 new Object[] { servicesCoreBootstrapCfg });
			return outMod;
		} catch (ReflectionException refEx) {
			log.error("Could NOT create an instance of {} bootstrap guice module. The module MUST have a {}-based constructor",
					  servicesCoreBootstrapCfg.getCoreBootstrapGuiceModuleType(),
					  ServicesCoreGuiceBootstrapConfig.class);
			throw refEx;
		}
	}
}
