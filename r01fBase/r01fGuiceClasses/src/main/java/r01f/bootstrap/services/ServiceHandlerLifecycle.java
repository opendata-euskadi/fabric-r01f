package r01f.bootstrap.services;

import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.service.ServiceHandler;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServiceHandlerLifecycle {
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
				} catch (Throwable th) {
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
					} catch (Throwable th) {
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
}
