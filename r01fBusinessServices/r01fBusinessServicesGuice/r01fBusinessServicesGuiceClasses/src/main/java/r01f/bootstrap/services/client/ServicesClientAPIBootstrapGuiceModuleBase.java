package r01f.bootstrap.services.client;

import javax.inject.Singleton;

import com.google.inject.Binder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.inject.HasMoreBindings;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.TypeMetaDataInspector;

/**
 * This GUICE module is where the client-api bindings takes place
 *
 * This guice module is included from the bootstrap module: {@link ServicesBootstrap} (which is called when the injector is created)
 *
 * At this module some client-side bindings are done:
 * <ol>
 * 		<li>Client APIs: types that aggregates the services access</li>
 * 		<li>Model object extensions</li>
 * 		<li>Server services proxies (ie: REST, bean, ejb)</li>
 * </ol>
 *
 * The execution flow is something like:
 * <pre>
 * ClientAPI
 *    |----> ServicesClientProxy
 * 						|---------------[ Proxy between client and server services ]
 * 														  |
 * 														  |----- [ HTTP / RMI / Direct Bean access ]-------->[REAL server / core side Services implementation]
 * </pre>
 *
 * The API simply offers access to service methods to the client and frees it from the buzz of knowing how to deal with different
 * service implementations (REST, EJB, Bean...).
 * All the logic related to transforming client method-calls to core services method calls is done at the PROXIES.
 * There's one proxy per core service implementation (REST, EJB, Bean...)
 *
 * <b>See file services-architecture.txt :: there is an schema of the app high level architecture</b>
 * </pre>
 */
@Accessors(prefix="_")
@EqualsAndHashCode				// This is important for guice modules
public abstract class ServicesClientAPIBootstrapGuiceModuleBase
		   implements ServicesClientBootstrapGuiceModule {	// this is a client guice bindings module
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS (all are set at bootstraping time at {@link ServicesBootstrap})
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Client API config
	 */
	@Getter protected final ServicesClientGuiceBootstrapConfig _clientBootstrapCfg;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected ServicesClientAPIBootstrapGuiceModuleBase(final ServicesClientGuiceBootstrapConfig servicesClientBootstrapCfg) {
		_clientBootstrapCfg = servicesClientBootstrapCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODULE INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// [0] - Find the model object types & bind it
		TypeMetaDataInspector.singleton()
							 .init(_clientBootstrapCfg.getClientApiAppCode());
		binder.bind(HasTypesMetaData.class)
			  .toInstance(TypeMetaDataInspector.singleton());

		// [2] - Other module-specific bindings
		if (this instanceof HasMoreBindings) {
			((HasMoreBindings)this).configureMoreBindings(binder);
		}

		// [3] - Bind the client API aggregator types as singletons
		//		 The ClientAPI is injected with a service proxy aggregator defined at [2]
		binder.bind(_clientBootstrapCfg.getClientApiType())
			  .in(Singleton.class);
	}
}
