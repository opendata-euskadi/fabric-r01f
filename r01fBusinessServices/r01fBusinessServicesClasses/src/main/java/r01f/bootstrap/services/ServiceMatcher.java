package r01f.bootstrap.services;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;
import r01f.bootstrap.services.config.client.ServicesClientConfigForCoreModule;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.outline.TypeOutline;
import r01f.reflection.scanner.SubTypeOfScanner;
import r01f.services.core.CoreService;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.services.interfaces.ServiceInterface;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class ServiceMatcher {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final ClassLoader _classLoader;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServiceMatcher() {
		// default non-args constructor
		this(Thread.currentThread().getContextClassLoader());
	}
	public ServiceMatcher(final ClassLoader classLoader) {
		_classLoader = classLoader;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds subTypes of a given type scanning at given packages
	 * Used at:
	 * 		{@link ServicesClientAPIFinder}#findClientAPIProxyAggregatorTypes()
	 * 		{@link ServicesCoreBootstrapModulesFinder}#_findCoreBootstrapGuiceModuleTypesByAppModule
	 * 		{@link ServicesClientBootstrapModulesFinder}
	 * 		{@link ServicesClientInterfaceToImplAndProxyFinder}.ServiceInterfaceImplementingTypes
	 * @param superType
	 * @param pckgNames
	 * @return
	 */
	private static <T> Set<Class<? extends T>> _findSubTypesOf(final Class<T> superType,
															   final Collection<JavaPackage> pckgNames,
													  		   final ClassLoader otherClassLoader) {
		return SubTypeOfScanner.findSubTypesAt(superType,
											   pckgNames,
											   otherClassLoader);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds {@link ServiceInterface}-implementing interfaces
	 * @param serviceInterfacesPckg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Class<? extends ServiceInterface>> findServiceInterfaceTypes(final Class<? extends ServiceInterface> serviceInterfacesBaseType) {
		log.warn("...........................................................................................................");
		log.warn("[1]: find service interface extending {}",
				 serviceInterfacesBaseType);

		// [0] - java packages where look for service interface types (required by org.reflections)
		Collection<JavaPackage> javaPackagesContainingServiceInterfaceTypes = _packagesWhereToLookForTypesExtending(serviceInterfacesBaseType);

		// [1] - do find
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = _findSubTypesOf((Class<ServiceInterface>)serviceInterfacesBaseType,
																			    	   javaPackagesContainingServiceInterfaceTypes,		// ServiceInterface.class
																			    	   _classLoader);
		// [2] - the found service interface set contains also the R01F base service interfaces; remove them
		//		 ... also remove interface types NOT annotated with @ExposedServiceInterface (this is because sometimes there are BASE service interfaces
		//			 that we do NOT want to be detected as REAL -final- service interfaces; these BASE service interfaces are NOT annotated
		//			 with @ExposedServiceInterface)
		serviceInterfaceTypes = FluentIterable.from(serviceInterfaceTypes)
											  .filter(new Predicate<Class<? extends ServiceInterface>>() {
															@Override
															public boolean apply(final Class<? extends ServiceInterface> serviceInterface) {
																// service interfaces MUST be annotated with @ExposedServiceInterface
																return ReflectionUtils.typeAnnotation(serviceInterface,ExposedServiceInterface.class) != null;		// it's annotated with @ServiceInterfaceFor
															}
											  		  })
											  .toSet();
		// [2] - checkings
		if (CollectionUtils.hasData(serviceInterfaceTypes)) {
			for (Class<? extends ServiceInterface> serviceInterfaceType : serviceInterfaceTypes) {
				if (!ReflectionUtils.isInterface(serviceInterfaceType)) throw new IllegalStateException(String.format("%s is NOT a valid %s: it MUST be an interface",
																													  serviceInterfaceType,ServiceInterface.class.getSimpleName()));
			}
		}
		// [3] - Return
		if (CollectionUtils.isNullOrEmpty(serviceInterfaceTypes)) throw new IllegalStateException("Could NOT find any " + ServiceInterface.class.getSimpleName() + " types extending " + serviceInterfacesBaseType + " at java packages " + javaPackagesContainingServiceInterfaceTypes +
																								  " ensure that the intefacees extends " + ServiceInterface.class + " and are annotated with @" + ExposedServiceInterface.class.getSimpleName());
		if (log.isDebugEnabled()) {
			log.debug("... found {} service interface extending {}",
					  serviceInterfaceTypes.size(),serviceInterfacesBaseType);
			for (Class<? extends ServiceInterface> serviceIface : serviceInterfaceTypes) log.debug("\t-{}",serviceIface);
		}
		return serviceInterfaceTypes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link ServiceInterfacesMatchings} object that encapsulates a collection of {@link ServiceInterfaceMatch} objects
	 * that in-turn models a {@link ServiceInterface} matching with a core impl or a proxy to a core impl
	 * @param servicesBootstrapCfg
	 * @return
	 */
	public ServiceInterfacesMatchings serviceInterfacesToImplOrProxyMatchings(final ServicesBootstrapConfig servicesBootstrapCfg) {		
		ServicesClientBootstrapConfig clientBootstrapCfg = servicesBootstrapCfg.getClientConfig();
		
		if (clientBootstrapCfg.getServiceInterfacesBaseType() == null) {
			log.warn("service interfaces will NOT be searched!");
			return ServiceInterfacesMatchings.createEmpty(clientBootstrapCfg.getClientApiAppCode());
		}
		
		log.warn("\n####################################################################################################");
		log.warn("[START]-Matching service interfaces to proxy or impl at {} core modules",
				 servicesBootstrapCfg.getCoreModulesConfig() != null ? servicesBootstrapCfg.getCoreModulesConfig().size() : 0);
		
		// [1] - Find service interfaces
		log.warn("\t- service interface MUST extend {}",
				 clientBootstrapCfg.getServiceInterfacesBaseType());
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = this.findServiceInterfaceTypes(clientBootstrapCfg.getServiceInterfacesBaseType());
		
		// [2] - Try to find matching for each service interface at the core modules
		ServiceInterfacesMatchings outMatchings = ServiceInterfacesMatchings.create(clientBootstrapCfg.getClientApiAppCode(),
																					serviceInterfaceTypes);
		
		// [2.1] - CORE impl matchings
		log.warn("\n....................................................................................................");
		for (final ServicesCoreBootstrapConfig coreModuleCfg : servicesBootstrapCfg.getCoreModulesConfig()) {
			if (coreModuleCfg.getImplType() != ServicesImpl.Bean) continue;		// skip no bean core modules
			
			Class<? extends CoreService> coreServiceImplsBaseType = coreModuleCfg.as(ServicesCoreBootstrapConfigWhenBeanExposed.class)
						 			   											 .getCoreServiceImplBaseType();
			
			log.warn("[START]-Matching service interfaces to CORE impl for {}/{}: core bean service interface implementations MUST extend {}",
					 coreModuleCfg.getCoreAppCode(),coreModuleCfg.getCoreModule(),
					 coreServiceImplsBaseType);

			Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> serviceIfaceTypeToCoreImplType = null;
			serviceIfaceTypeToCoreImplType = this.findServiceInterfaceMatchings(serviceInterfaceTypes,
												  								   null,	// no proxy
																				   coreServiceImplsBaseType);
			log.warn("[END]-Matching service interfaces to CORE impl for {}/{}: found {} matchings implementing {}",
					 coreModuleCfg.getCoreAppCode(),coreModuleCfg.getCoreModule(),
					 CollectionUtils.hasData(serviceIfaceTypeToCoreImplType) ? serviceIfaceTypeToCoreImplType.size() : 0,
					 coreServiceImplsBaseType);
			
			if (CollectionUtils.hasData(serviceIfaceTypeToCoreImplType)) {
				for (Map.Entry<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> matchEntry : serviceIfaceTypeToCoreImplType.entrySet()) {
					ServiceInterfaceMatch match = ServiceInterfaceMatchBuilder.serviceInterface(matchEntry.getKey())
																			  .matchesWith(matchEntry.getValue())
																			  .forCore(coreModuleCfg.getCoreAppCode(),coreModuleCfg.getCoreModule());
					outMatchings.addMatching(match);
				}
			}
		}
		
		// [2.2] - client proxy to CORE impl matchings
		for (final ServicesClientConfigForCoreModule<?,?> clientCfgForCore : clientBootstrapCfg.getCoreModuleConfigs()) {
			if (clientCfgForCore.getCoreImplType() == ServicesImpl.Bean) continue;		// skip bean cores (no client proxy is used)
			
			Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType = clientCfgForCore.getServiceProxyImplsBaseType();
			
			log.warn("[START]-Matching service interfaces to CLIENT PROXY TO CORE impl for {}/{}: client proxy to CORE impls MUST extend {}",
					 clientCfgForCore.getCoreAppCode(),clientCfgForCore.getCoreModule(),
					 serviceProxyImplsBaseType);
			
			Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> serviceIfaceTypeToProxyToCoreImplType = null;
		    serviceIfaceTypeToProxyToCoreImplType = this.findServiceInterfaceMatchings(serviceInterfaceTypes,
												  								       serviceProxyImplsBaseType,	
												  								       null);	// no bean impl
			log.warn("[END]-Matching service interfaces to CLIENT PROXY TO CORE impl for {}/{}: found {} matchings implementing {}",
					 clientCfgForCore.getCoreAppCode(),clientCfgForCore.getCoreModule(),
					 CollectionUtils.hasData(serviceIfaceTypeToProxyToCoreImplType) ? serviceIfaceTypeToProxyToCoreImplType.size() : 0,
					 serviceProxyImplsBaseType);
			
			if (CollectionUtils.hasData(serviceIfaceTypeToProxyToCoreImplType)) {
				for (Map.Entry<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> matchEntry : serviceIfaceTypeToProxyToCoreImplType.entrySet()) {
					ServiceInterfaceMatch match = ServiceInterfaceMatchBuilder.serviceInterface(matchEntry.getKey())
																			  .matchesWith(matchEntry.getValue())
																			  .forCore(clientCfgForCore.getCoreAppCode(),clientCfgForCore.getCoreModule());
					outMatchings.addMatching(match);
				}
			}
		}
		log.warn("\n....................................................................................................");
		
		log.warn("\n[Service interface matchings]:\n{}",
				 outMatchings.debugInfo());
		log.warn("\n####################################################################################################\n\n\n\n");
		
		// [3] - Ensure there's a matching for every service inteface
		outMatchings.checkMatchingsOrThrowfor(serviceInterfaceTypes);
		
		return outMatchings;
	}	
	/**
	 * First it finds the {@link ServiceInterface}-extending interfaces and then tries to find
	 * the best {@link ServiceInterface} matching: if a {@link CoreService} impl is available it matches this one,
	 * but if it's not, it tries to match a {@link ServiceProxyImpl} one; if none is available an exception is thrown
	 * @param serviceInterfacesPckg
	 * @param serviceProxiesPckg
	 * @param coreImplsPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> findServiceInterfaceMatchings(final Class<? extends ServiceInterface> serviceInterfacesBaseType,
																						 						  final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType,
																						 						  final Class<? extends CoreService> coreServicesBaseType) {
		// [1] - Find service interfaces
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = this.findServiceInterfaceTypes(serviceInterfacesBaseType);

		// [2] - Match
		return this.findServiceInterfaceMatchings(serviceInterfaceTypes,
												  serviceProxyImplsBaseType,
												  coreServicesBaseType);
	}
	/**
	 * Tries to find the best {@link ServiceInterface} matching: if a {@link CoreService} impl is available it matches this one,
	 * but if it's not, it tries to match a {@link ServiceProxyImpl} one; if none is available an exception is thrown
	 * @param serviceInterfaceTypes
	 * @param serviceProxiesPckg
	 * @param coreImplsPckg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> findServiceInterfaceMatchings(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																						 						  final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType,
																						 						  final Class<? extends CoreService> coreServiceBaseType) {
		log.warn("[2]: find matching for {} service interfaces: proxies MUST extend {} / core bean MUST extend {}",
				  serviceInterfaceTypes.size(),
				  serviceProxyImplsBaseType,// proxies
				  coreServiceBaseType);		// core impls

		// [1] - Find service interface to proxy matchings
		Map<Class<? extends ServiceInterface>,Class<? extends ServiceProxyImpl>> serviceInterfacesToProxyMatchs = serviceProxyImplsBaseType != null
																														? this.findServiceInterfaceToProxyMatch(serviceInterfaceTypes,
																																								serviceProxyImplsBaseType)
																														: null;	// no proxies
		if (log.isDebugEnabled()) {
			log.debug("... found {} proxy impls extending {}",
					  serviceInterfacesToProxyMatchs != null ? serviceInterfacesToProxyMatchs.size() : 0,
					  serviceProxyImplsBaseType);
			if (serviceInterfacesToProxyMatchs != null) for (Map.Entry<Class<? extends ServiceInterface>,Class<? extends ServiceProxyImpl>> me : serviceInterfacesToProxyMatchs.entrySet()) log.debug("\t{} > {}",me.getKey(),me.getValue());
		}

		// [2] - Find service interface to core matchings
		Map<Class<? extends ServiceInterface>,Class<? extends CoreService>> serviceInterfacesToCoreImplMatchs = coreServiceBaseType != null
																														? this.findServiceInterfaceToCoreImplMatch(serviceInterfaceTypes,
																																					     		   coreServiceBaseType)
																														: null;			// no bean impls
		if (log.isDebugEnabled()) {
			log.debug("... found {} core impls extending {}",
					  serviceInterfacesToCoreImplMatchs != null ? serviceInterfacesToCoreImplMatchs.size() : 0,
					  coreServiceBaseType);
			if (serviceInterfacesToCoreImplMatchs != null) for (Map.Entry<Class<? extends ServiceInterface>,Class<? extends CoreService>> me : serviceInterfacesToCoreImplMatchs.entrySet()) log.debug("\t{} > {}",me.getKey(),me.getValue());
		}
		
		// [3] - mix both
		log.warn("[3]: Match service interface with proxy or core impl");
		Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> outMatchings = Maps.newHashMapWithExpectedSize(serviceInterfaceTypes.size());
		for (Class<? extends ServiceInterface> serviceInterfaceType : serviceInterfaceTypes) {
			Class<? extends ServiceInterface> matchedType = null;

			// a) try to find a core impl type
			if (matchedType == null) {	// this if is not strictly necessary but helps in code comprehension
				Class<? extends CoreService> coreImplType = serviceInterfacesToCoreImplMatchs != null ? serviceInterfacesToCoreImplMatchs.get(serviceInterfaceType)
																									  : null;
				if (coreImplType != null) matchedType = (Class<? extends ServiceInterface>)coreImplType;
			}
			// b) try to find a proxy type
			if (matchedType == null) {
				Class<? extends ServiceProxyImpl> proxyType = serviceInterfacesToProxyMatchs != null ? serviceInterfacesToProxyMatchs.get(serviceInterfaceType)
																									 : null;
				if (proxyType != null) matchedType = (Class<? extends ServiceInterface>)proxyType;
			}
			// c) check
			if (matchedType == null) {
				log.debug("Could NOT find a matching for {}-implementing type {}, either as {} neither as {}",
						  ServiceInterface.class.getSimpleName(),serviceInterfaceType,
						  CoreService.class.getSimpleName(),ServiceProxyImpl.class.getSimpleName());
			}
			// c) add to the out matchings
			if (matchedType != null) outMatchings.put(serviceInterfaceType,matchedType);
		}
		// [4] - Return
		log.warn("... found {} service interface matchings",
				 outMatchings.size());
		return outMatchings;
	}
	/**
	 * First it finds the {@link ServiceInterface}-extending interfaces and then tries to find
	 * each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfacesPckg
	 * @param serviceProxiesPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceProxyImpl>> findServiceInterfaceToProxyMatch(final Class<? extends ServiceInterface> serviceInterfacesBaseType,
																													 final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType) {
		// [1] - Find service interfaces
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = this.findServiceInterfaceTypes(serviceInterfacesBaseType);

		// [2] - Match
		return this.findServiceInterfaceToProxyMatch(serviceInterfaceTypes,
												  	 serviceProxyImplsBaseType);
	}
	/**
	 * Given a collection of service interfaces, this method finds each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfaceTypes
	 * @param serviceProxiesPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceProxyImpl>> findServiceInterfaceToProxyMatch(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																													 final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType) {
		Collection<JavaPackage> javaPackagesWhereLookForProxyImpls = _packagesWhereToLookForTypesExtending(serviceProxyImplsBaseType);
		return _findServiceInterfaceMatchings(serviceInterfaceTypes,
											  ServiceProxyImpl.class,
											  javaPackagesWhereLookForProxyImpls);
	}
	/**
	 * First it finds the {@link ServiceInterface}-extending interfaces and then tries to find
	 * each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfacesPckg
	 * @param coreServicesPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends CoreService>> findServiceInterfaceToCoreImplMatch(final Class<? extends ServiceInterface> serviceInterfacesBaseType,
																												   final Class<? extends CoreService> coreServiceBaseType) {
		// [1] - Find service interfaces
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = this.findServiceInterfaceTypes(serviceInterfacesBaseType);
		// [2] - Match
		return this.findServiceInterfaceToCoreImplMatch(serviceInterfaceTypes,
					 								    coreServiceBaseType);
	}
	/**
	 * Given a collection of service interfaces, this method finds each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfaceTypes
	 * @param coreServicesPckg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Class<? extends ServiceInterface>,Class<? extends CoreService>> findServiceInterfaceToCoreImplMatch(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																												   final Class<? extends CoreService> coreServicesBaseType) {
		Collection<JavaPackage> javaPackagesWhereLookForCoreServices = _packagesWhereToLookForTypesExtending(coreServicesBaseType);
		return _findServiceInterfaceMatchings(serviceInterfaceTypes,
											  (Class<CoreService>)coreServicesBaseType, 	// CoreService.class,
											  javaPackagesWhereLookForCoreServices);
	}
	private <T> Map<Class<? extends ServiceInterface>,Class<? extends T>> _findServiceInterfaceMatchings(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																										 final Class<T> type,
																										 final Collection<JavaPackage> pckgs) {
		return _findServiceInterfaceMatchings(serviceInterfaceTypes,
											  type,
											  pckgs,
											  true);	// strict mode: throw if a service iface matching is not found
	}
	private <T> Map<Class<? extends ServiceInterface>,Class<? extends T>> _findServiceInterfaceMatchings(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																										 final Class<T> type,
																										 final Collection<JavaPackage> pckgs,
																										 final boolean strict) {
		// [1] - do find type implementing types
		Set<Class<? extends T>> typeImplTypes = _findSubTypesOf(type,
															    pckgs,
																this.getClass().getClassLoader());
		// [2] - Filter abstract or interface types
		typeImplTypes = FluentIterable.from(typeImplTypes)
									  .filter(new Predicate<Class<? extends T>>() {
													@Override
													public boolean apply(final Class<? extends T> implType) {
														return ReflectionUtils.isInstanciable(implType);
													}
									  		  })
									  .toSet();
		// [3] - Checkings
    	if (CollectionUtils.isNullOrEmpty(typeImplTypes)
    	 && CollectionUtils.hasData(serviceInterfaceTypes)) throw new IllegalStateException(String.format("Could NOT find any type implementing %s at %s for service interfaces: %s",
    			 																					  	  type,pckgs,serviceInterfaceTypes));

		// [4] - Match each type to it's corresponding ServiceInterface type
    	//		 (note that a service proxy could proxy for more than a single service interface)
    	Map<Class<? extends ServiceInterface>,Class<? extends T>> outServiceInterfaceMatchings = Maps.newHashMapWithExpectedSize(serviceInterfaceTypes.size());
		for (Class<? extends T> typeImplType : typeImplTypes) {
			boolean matched = false;
			for (Class<? extends ServiceInterface> serviceInterfaceType : serviceInterfaceTypes) {
				if (ReflectionUtils.isImplementing(typeImplType,
												   serviceInterfaceType)) {
					if (outServiceInterfaceMatchings.containsKey(serviceInterfaceType))	throw new IllegalStateException(String.format("There're TWO %s-implementing types that implements the SAME %s (%s): %s and %s, " +
												   																					  "this is usually the case for BASE service interfaces; " +
												   																					  "annotate EXPOSED service interfaces with @{} and DO NOT annotate the BASE service interface",
																	  																  type.getSimpleName(),ServiceInterface.class.getSimpleName(),
																	  																  serviceInterfaceType,
																	  																  typeImplType,outServiceInterfaceMatchings.get(serviceInterfaceType),
																	  																  ExposedServiceInterface.class.getSimpleName()));
					outServiceInterfaceMatchings.put(serviceInterfaceType,
											 		 typeImplType);
					matched = true;
				}
			}
			if (strict && !matched) throw new IllegalStateException(String.format("There's NO %s-implementing type for the %s %s",
																				  ServiceInterface.class.getSimpleName(),
																				  type.getSimpleName(),typeImplType));
		}
		// [3] - Return
		return outServiceInterfaceMatchings;
	}
	private static Collection<JavaPackage> _packagesWhereToLookForTypesExtending(final Class<?> iface) {
		// BEWARE!
		// org.reflections is used to scan subtypes of CoreService. This library requires
		// ALL the packages in the type hierarchy to be given to the scan methods:
		// <pre class='brush:java'>
		//  		CoreService
		//  			|-- interface 1
		//  					|--  interface 2
		//  							|-- all the core service impl
		// </pre>
		// The packages where CoreService, interface 1 and interface 2 resides MUST be handed
		// to the subtypeOfScan method of org.reflections

		// find the hierarchy between the given interface and CoreService
		TypeOutline typeOutline = new TypeOutline(iface);
		log.warn("{} type hierarchy outline:\n{}",
			     iface,typeOutline.debugInfo());
		return FluentIterable.from(typeOutline.getNodesFromGeneralToSpezialized())
						.transform(new Function<Class<?>,JavaPackage>() {
											@Override
											public JavaPackage apply(final Class<?> type) {
												return JavaPackage.of(type);
											}
								   })
						.filter(new Predicate<JavaPackage>() {
											@Override
											public boolean apply(final JavaPackage pckg) {
												return !pckg.isJavaLang();
											}
								})
						.toSet();
	}
}
