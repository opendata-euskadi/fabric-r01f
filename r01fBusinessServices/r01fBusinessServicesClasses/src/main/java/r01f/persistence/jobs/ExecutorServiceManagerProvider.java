package r01f.persistence.jobs;

import java.util.concurrent.ExecutorService;

import javax.inject.Provider;

import r01f.concurrent.ExecutorServiceLifeCycleManager;
import r01f.concurrent.ExecutorServiceManager;

/**
 * Provides an {@link ExecutorServiceManager} in charge of the life cycle of the
 * {@link ExecutorService} that handle crud events in the background
 */
public class ExecutorServiceManagerProvider
  implements Provider<ExecutorServiceManager> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final ExecutorServiceManager _execServiceManager;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public ExecutorServiceManagerProvider(final int numberOfBackgroundThreads) {
		// Create a daemon executor service life cycle manager
		_execServiceManager = new ExecutorServiceLifeCycleManager(numberOfBackgroundThreads);
	}
	public ExecutorServiceManagerProvider(final ExecutorService execService) {
		// Create a daemon executor service life cycle manager
		_execServiceManager = new ExecutorServiceLifeCycleManager(execService);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ExecutorServiceManager get() {
		_execServiceManager.start();

		return _execServiceManager;
	}

}
