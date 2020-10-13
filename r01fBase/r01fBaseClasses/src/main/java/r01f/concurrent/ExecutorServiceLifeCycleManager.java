package r01f.concurrent;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.GwtIncompatible;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages the life cycle of an {@link ExecutorService} for background (daemon) jobs
 * It uses a {@link DaemonThreadFactory} to create daemon threads<
 * 
 * USAGE NOTES INSIDE A SERVLER CONTAINER (ie Tomcat)
 * ==================================================
 * The key is that the {@link ExecutorService} is created when the {@link ServletContext} is
 * initialized and destroyed when {@link ServletContext} is destroyed
 * 
 * This type MUST be used at a {@link ServletContextListener} type as:
 * <pre class='brush:java'>
 * 		public class MyContextListener 
 * 		  implements ServletContextListener {
 * 
 * 			private ExecutorServiceLifeCycleManager _lifeCycleManager;
 * 
 * 			public void contextInitialized(ServletContextEvent servletContextEvent) {
 * 				// Create and initialize the daemon ExecutorService to run background jobs in tomcat
 *				_lifeCycleManager = new ExecutorServiceLifeCycleManager(5);
 * 			}
 * 			public void contextDestroyed(ServletContextEvent servletContextEvent) {
 * 				_lifeCycleManager.stop(); 
 * 			}
 * 		}
 * </pre>
 * 
 * http://stackoverflow.com/questions/4907502/running-a-background-java-program-in-tomcat
 */
@GwtIncompatible
@Slf4j
public class ExecutorServiceLifeCycleManager 
  implements ExecutorServiceManager,
  			 Serializable {

	private static final long serialVersionUID = -775988426740172684L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FINAL FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The thread pool
	 */
	private final ExecutorService _executor;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ExecutorServiceLifeCycleManager(final int numberOfThreadsInPool) {
		this(numberOfThreadsInPool,
			 new DaemonThreadFactory());
	}
	public ExecutorServiceLifeCycleManager(final int numberOfThreadsInPool,
										   final ThreadFactory threadFactory) {
        int theNumExecutors = numberOfThreadsInPool <= 0 ? 1 : numberOfThreadsInPool;
        log.warn("Creating a background jobs executor pool with size={}",theNumExecutors);
        if (theNumExecutors <= 1) {
        	_executor = Executors.newSingleThreadExecutor(threadFactory);
        } else {
        	_executor = Executors.newFixedThreadPool(theNumExecutors,
        											 threadFactory);
        }
	}
	public ExecutorServiceLifeCycleManager(final ExecutorService executor) {
		_executor = executor;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ServiceHandler
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start() {
		// nothing (everything was setup at the constructor)
	}
	@Override
	public void stop() {
		// Shutdown the thread pool or process/wait until all pending jobs are done
		log.warn("######################################################################################");
		log.warn("Stopping background jobs executor...");
		log.warn("######################################################################################");
        _executor.shutdownNow(); 	// this DO NOT close the executor service... simply tells it not to accept more tasks
									// see: http://java.dzone.com/articles/executorservice-10-tips-and
									//		http://java.dzone.com/articles/interrupting-executor-tasks
		try {
			// wait for running tasks to finalize... 10 seconds/cycle x 10 cycles = 100 seconds
			int cyclesAwaited = 1;
			int secondsPerCycle = 10;
			boolean done = _executor.awaitTermination(secondsPerCycle,TimeUnit.SECONDS);
			while (!done && cyclesAwaited < 10) {
				log.warn("\t--still pending jobs executing....");
				done = _executor.awaitTermination(secondsPerCycle,TimeUnit.SECONDS);
				cyclesAwaited++;
			}
		} catch (InterruptedException intEx) {
			intEx.printStackTrace();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ExecutorService getExecutorService() {
		return _executor;
	}
}
