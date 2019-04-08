package r01f.bootstrap.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.concurrent.ExecutorServiceManager;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.persistence.jobs.AsyncEventBusProvider;
import r01f.persistence.jobs.ExecutorServiceManagerProvider;
import r01f.persistence.jobs.SyncEventBusProvider;
import r01f.service.ServiceHandler;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.types.ExecutionMode;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesComponentImpl;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Utility type that encapsulates the services life cycle operations
 * <ul>
 * 	<li>Guice injector creation</li>
 * 	<li>Start / Stop of services that needs an explicit starting (ie Persistence services, thread pools, indexexers, etc)</li>
 * </ul>
 *
 * This type is mainly used at:
 * <ul>
 * 	<li>ServletContextListeners of web apps that controls the lifecycle of the app</li>
 * 	<li>Test init classes</li>
 * </ul>
 */
@Slf4j
public class ServicesBootstrapUtil {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the guice modules to do the bootstrapping
	 * @param servicesBootstrapCfg
	 * @return
	 */
	public static ServicesMainGuiceBootstrapCommonBindingModules getBootstrapGuiceModules(final ServicesBootstrapConfig... servicesBootstrapCfg) {
		if (CollectionUtils.isNullOrEmpty(servicesBootstrapCfg)) throw new IllegalArgumentException();
		return ServicesBootstrapUtil.getBootstrapGuiceModules(Arrays.asList(servicesBootstrapCfg));
	}
	/**
	 * Returns the guice modules to do the bootstrapping
	 * @param servicesBootstrapCfg
	 * @return
	 */
	public static ServicesMainGuiceBootstrapCommonBindingModules getBootstrapGuiceModules(final Collection<ServicesBootstrapConfig> servicesBootstrapCfg) {
		return new ServicesMainGuiceBootstrapCommonBindingModules(servicesBootstrapCfg);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ServicesMainGuiceBootstrapCommonBindingModules {
		final Collection<ServicesBootstrapConfig> _servicesBootstrapCfg;
		private Module _commonEventsExecutorModule;

		public ServicesMainGuiceBootstrapCommonBindingModules withoutCommonEventsExecutor() {
			return this.withCommonEventsExecutor(null);
		}
		public ServicesMainGuiceBootstrapCommonBindingModules withCommonEventsExecutor(final ServicesCoreModuleEventsConfig coreEventsCfg) {
			if (coreEventsCfg == null) return this;

			// all bootsrap modules shares the same client api app code
			ClientApiAppCode clientApiAppCode = null;
			for (ServicesBootstrapConfig bootCfg : _servicesBootstrapCfg) {
				if (clientApiAppCode == null) {
					clientApiAppCode = bootCfg.getClientApiAppCode();
				} else if (clientApiAppCode.isNOT(bootCfg.getClientApiAppCode())) {
					throw new IllegalArgumentException("In order to use a common events executor, all services bootstrap MUST belong to the SAME clienta api appCode!");
				}
			}
			_commonEventsExecutorModule = ServicesBootstrapUtil.createCoreEventBusBindingModule(clientApiAppCode,
																								coreEventsCfg);
			return this;
		}
		public Iterable<Module> withoutCommonBindingModules() {
			Collection<Module> bootstrapModules = new ServicesBootstrap(_servicesBootstrapCfg)
														.loadBootstrapModuleInstances();
			return _commonEventsExecutorModule != null ? Iterables.concat(bootstrapModules,
																		  Lists.<Module>newArrayList(_commonEventsExecutorModule))
													   : bootstrapModules;
		}
		public Iterable<Module> withCommonBindingModules(final Module... modules) {
			return CollectionUtils.hasData(modules) ? this.withCommonBindingModules(Arrays.asList(modules))
													: this.withoutCommonBindingModules();
		}
		public Iterable<Module> withCommonBindingModules(final Collection<Module> modules) {
			Iterable<Module> bootstrapAndEventMods = this.withoutCommonBindingModules();
			Iterable<Module> allBootstrapModuleInstances = CollectionUtils.hasData(modules) ? Iterables.concat(bootstrapAndEventMods,
																											   modules)
																						    : bootstrapAndEventMods;
			return allBootstrapModuleInstances;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static Module createCoreEventBusBindingModule(final ClientApiAppCode clientApiAppCode,
														 final ServicesCoreModuleEventsConfig coreModuleEventsConfig) {
		return new Module() {
						@Override
						public void configure(final Binder binder) {
							if (coreModuleEventsConfig != null) {
								log.warn("EVENT HANDLING: {}",coreModuleEventsConfig.debugInfo());
								
								// The EventBus needs an ExecutorService (a thread pool) to manage events in the background
								if (coreModuleEventsConfig.getExecutionMode() == ExecutionMode.ASYNC) {
									ExecutorServiceManagerProvider execServiceManagerProvider = new ExecutorServiceManagerProvider(coreModuleEventsConfig.getNumberOfBackgroundThreads());
									binder.bind(ExecutorServiceManager.class)
										  .toProvider(execServiceManagerProvider)
										  .in(Singleton.class);
									// Expose the ServiceHandler to stop the exec manager threads
									String bindingName = Strings.customized("{}.backgroundTasksExecService",
																			clientApiAppCode);
									// do NO forget!!
									ServicesBootstrapUtil.bindServiceHandler(binder,
																			 ExecutorServiceManager.class,bindingName);
	
									// create the event bus provider
									binder.bind(EventBus.class)
										  .toProvider(AsyncEventBusProvider.class)	// AsyncEventBusProvider needs an ExecutorServiceManager that MUST be binded
										  .in(Singleton.class);
								} else {
									binder.bind(EventBus.class)
											 .toProvider(SyncEventBusProvider.class)
											 .in(Singleton.class);
								}
							} else {
								log.warn("NO [EventBus] is configured: if you're using events, review the [core] bootstrapping code > a default SYNC event bus is binded by default!!");
								binder.bind(EventBus.class)
										 .toProvider(SyncEventBusProvider.class)
										 .in(Singleton.class);
							}
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Starts services that needs to be started
	 * @param hasServiceHandlerTypes
	 * @param injector
	 */
	public static void startServices(final Injector injector) {
		if (injector == null) throw new IllegalStateException("Cannot start services: no injector present!");

		// Init JPA's Persistence Service, Lucene indexes and everything that has to be started
		// (see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree)
		Collection<Key<? extends ServiceHandler>> serviceHandlerBindingKeys = _getServiceHandlersGuiceBindingKeys(injector);
		if (CollectionUtils.hasData(serviceHandlerBindingKeys)) {
			for (Key<? extends ServiceHandler> key : serviceHandlerBindingKeys) {
				ServiceHandler serviceHandler = injector.getInstance(key);
				log.warn("\t--START SERVICE using {} type: {}",ServiceHandler.class.getSimpleName(),key);
				try {
					serviceHandler.start();
				} catch(Throwable th) {
					log.error("Error starting service with ServiceHandler key={}: {}",key,th.getMessage(),th);
				}
			}
		}
	}
	/**
	 * Stops services that needs to be started
	 * @param hasServiceHandlerTypes
	 * @param injector
	 */
	public static void stopServices(final Injector injector) {
		if (injector == null) {
			log.warn("NO injector present... cannot stop services");
			return;
		}

		// Close JPA's Persistence Service, Lucene indexes and everything that has to be closed
		// (see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree)
		Collection<Key<? extends ServiceHandler>> serviceHandlerBindingKeys = _getServiceHandlersGuiceBindingKeys(injector);
		if (CollectionUtils.hasData(serviceHandlerBindingKeys)) {
			for (Key<? extends ServiceHandler> key : serviceHandlerBindingKeys) {
				ServiceHandler serviceHandler = injector.getInstance(key);
				if (serviceHandler != null) {
					log.warn("\t--END SERVICE {} type: {}",ServiceHandler.class.getSimpleName(),key);
					try {
						serviceHandler.stop();
					} catch(Throwable th) {
						log.error("Error stopping service with ServiceHandler key={}: {}",key,th.getMessage(),th);
					}
				}
			}
		}
	}
	/**
	 * Binds a service handler type and exposes it if it's a private binder
	 * @param binder
	 * @param serviceHandlerType
	 * @param name
	 */
	public static void bindServiceHandler(final Binder binder,
										  final Class<? extends ServiceHandler> serviceHandlerType,final String name) {
		binder.bind(ServiceHandler.class)
			  .annotatedWith(Names.named(name))
			  .to(serviceHandlerType)
			  .in(Singleton.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Introspects the injector bindings to find all binding keys for {@link ServiceHandler} types
	 * @param injector
	 * @return
	 */
	private static Collection<Key<? extends ServiceHandler>> _getServiceHandlersGuiceBindingKeys(final Injector injector) {
		List<Binding<ServiceHandler>> bindings = injector.findBindingsByType(TypeLiteral.get(ServiceHandler.class));

//		Map<Key<?>, Binding<?>> m = injector.getAllBindings();
//		for (Key<?> k : m.keySet()) System.out.println("...." + k);

		Collection<Key<? extends ServiceHandler>> outKeys = Lists.newArrayListWithExpectedSize(bindings.size());
		for (Binding<ServiceHandler> binding : bindings) {
			Key<? extends ServiceHandler> key = binding.getKey();
			outKeys.add(key);
		}
		return outKeys;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BIND XMLProperties component
/////////////////////////////////////////////////////////////////////////////////////////
	public static void bindXMLPropertiesForAppComponent(final AppCode appCode,final AppComponent component,
														final AppComponent bindingName,
														final Binder binder) {
		log.warn("{} {} properties are available for injection as a {} annotated with @{}(\"{}\")",
				 appCode,component,
				 XMLPropertiesForAppComponent.class.getSimpleName(),XMLPropertiesForAppComponent.class.getSimpleName(),
				 bindingName);
		binder.bind(XMLPropertiesForAppComponent.class)
			  .annotatedWith(new XMLPropertiesComponentImpl(bindingName.asString())) // @XMLPropertiesComponent("xx.client")
			  .toProvider(// the provider
					  	  new Provider<XMLPropertiesForAppComponent>() {
					  				@Inject
					  				private XMLProperties _props;	// injected properties

									@Override
									public XMLPropertiesForAppComponent get() {
										return _props.forAppComponent(appCode,
																	  component);
									}
			  			  })
			  .in(Singleton.class);

		// Expose xml properties binding
//		if (binder instanceof PrivateBinder) {
//			PrivateBinder pb = (PrivateBinder)binder;
//			pb.expose(Key.get(XMLPropertiesForAppComponent.class,
//					  new XMLPropertiesComponentImpl(bindingName.asString())));
//		}
	}
}
