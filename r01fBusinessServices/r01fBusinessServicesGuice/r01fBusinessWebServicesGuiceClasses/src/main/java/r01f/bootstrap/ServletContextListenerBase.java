package r01f.bootstrap;

import java.util.Collection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * Extends {@link GuiceServletContextListener} (that in turn extends {@link ServletContextListener})
 * to have the opportunity to:
 * <ul>
 * 	<li>When starting the web app: start JPA service</li>
 * 	<li>When closing the web app: stop JPA service and free lucene resources (the index writer)</li>
 * </ul>
 * If this is NOT done, an error is raised when re-deploying the application because lucene index
 * are still opened by lucene threads
 * This {@link ServletContextListener} MUST be configured at web.xml removing the default {@link ServletContextListener}
 * (if it exists)
 * <pre class='brush:xml'>
 *		<listener>
 *			<listener-class>r01e.rest.R01VRESTGuiceServletContextListener</listener-class>
 *		</listener>
 * </pre>
 */
@Slf4j
public abstract class ServletContextListenerBase
	          extends GuiceServletContextListener {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Collection<ServicesBootstrapConfig> _servicesBootstrapConfig;
	private final Collection<Module> _commonGuiceModules;
	private final ServicesCoreModuleEventsConfig _commonEventsConfig;

	protected Injector _injector;
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected ServletContextListenerBase(final ServicesBootstrapConfig... bootstrapCfgs) {
		this(Lists.newArrayList(bootstrapCfgs),
			 (Collection<Module>)null);		// no commong guice modules
	}
	protected ServletContextListenerBase(final ServicesBootstrapConfig bootstrapCfg,
										 final Module... commonModules) {
		this(Lists.newArrayList(bootstrapCfg),
			 CollectionUtils.hasData(commonModules) ? Lists.<Module>newArrayList(commonModules) : Lists.<Module>newArrayList());
	}
	protected ServletContextListenerBase(final Collection<ServicesBootstrapConfig> bootstrapCfgs,
										 final ServicesCoreModuleEventsConfig buildCommonModuleEventsConfig,
										 final Module... commonModules) {
		this(bootstrapCfgs,buildCommonModuleEventsConfig,
			 CollectionUtils.hasData(commonModules) ? Lists.<Module>newArrayList(commonModules) : Lists.<Module>newArrayList());
	}	
	
	protected ServletContextListenerBase(final Collection<ServicesBootstrapConfig> bootstrapCfgs,
										 final Module... commonModules) {
		this(bootstrapCfgs,
			 CollectionUtils.hasData(commonModules) ? Lists.<Module>newArrayList(commonModules) : Lists.<Module>newArrayList());
	}	
	
	protected ServletContextListenerBase(final Collection<ServicesBootstrapConfig> bootstrapCfg,										
										 final Collection<Module> commonGuiceModules) {
		this(bootstrapCfg,null,commonGuiceModules);
	}
	
	protected ServletContextListenerBase(final Collection<ServicesBootstrapConfig> bootstrapCfg,
										 final ServicesCoreModuleEventsConfig commonEventsConfig,
										 final Collection<Module> commonGuiceModules) {
		if (CollectionUtils.isNullOrEmpty(bootstrapCfg)) throw new IllegalArgumentException();
		_servicesBootstrapConfig = bootstrapCfg;
		_commonGuiceModules = commonGuiceModules;
		_commonEventsConfig = commonEventsConfig;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  Overridden methods of GuiceServletContextListener
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected Injector getInjector() {
		if (_injector == null) {
			_injector = Guice.createInjector(ServicesBootstrapUtil.getBootstrapGuiceModules(_servicesBootstrapConfig)
											 					                          .withCommonEventsExecutor(_commonEventsConfig)
																						  .withCommonBindingModules(_commonGuiceModules));
		} else {
			log.warn("The Guice Injector is already created!!!");
		}
		return _injector;
	}
	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		log.warn("=============================================");
		log.warn("Loading {} Servlet Context with {}...",
				 servletContextEvent.getServletContext().getContextPath(),
				 this.getClass().getSimpleName());
		log.warn("=============================================");

		super.contextInitialized(servletContextEvent);

		// Init JPA's Persistence Service, Lucene indexes and everything that has to be started
		// (see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree)
		ServicesBootstrapUtil.startServices(_injector);
	}
	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		log.warn("=============================================");
		log.warn("DESTROYING {} Servlet Context with {} > closing search engine indexes if they are in use, release background jobs threads and so on...",
				 servletContextEvent.getServletContext().getContextPath(),
				 this.getClass().getSimpleName());
		log.warn("=============================================");

		// Close JPA's Persistence Service, Lucene indexes and everything that has to be closed
		// (see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree)
		ServicesBootstrapUtil.stopServices(_injector);

		// finalize
		super.contextDestroyed(servletContextEvent);

		log.warn("=============================================");
		log.warn("{} Servlet Context DESTROYED!!...",
				 servletContextEvent.getServletContext().getContextPath());
		log.warn("=============================================");
	}
}
