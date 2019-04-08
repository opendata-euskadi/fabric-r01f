package r01f.persistence.jobs;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import r01f.concurrent.ExecutorServiceManager;
import r01f.types.ExecutionMode;

public class AsyncEventBusProvider 
	 extends EventBusProviderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The jobs executor holder: manages a thread pool in charge of dispatching events
	 * In a web application environment (ie Tomcat), this thread pool MUST be destroyed
	 * when the servlet context is destroyed; to do so, the executor service manager is
	 * used in a {@link ServletContextListener}
	 * 
	 * ... so in a web app environment:
	 * 		This executor service manager MUST be binded at guice module with access to 
	 * 		the {@link ServletContext} (ie RESTJerseyServletGuiceModuleBase)
	 * 		
	 * 		This executor service manager is USED at a {@link ServletContextListener}'s destroy()
	 * 		method to kill the worker threads (ie R01VServletContextListener)
	 */
	private final ExecutorServiceManager _executorServiceManager;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AsyncEventBusProvider(final ExecutorServiceManager execServiceManager) {
		super(ExecutionMode.ASYNC);
		_executorServiceManager = execServiceManager;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected EventBus _createEventBusInstance() {
		ExecutorService execService = _executorServiceManager.getExecutorService();
		_eventBusInstance = new AsyncEventBus("R01 ASYNC EventBus",
								 		  	  execService);
		return _eventBusInstance;
	}
}
