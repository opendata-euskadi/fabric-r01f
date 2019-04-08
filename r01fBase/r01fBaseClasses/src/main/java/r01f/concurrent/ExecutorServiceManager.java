package r01f.concurrent;

import java.util.concurrent.ExecutorService;

import com.google.common.annotations.GwtIncompatible;

import r01f.service.ServiceHandler;

/**
 * Interface for types storing an {@link ExecutorService}
 */
@GwtIncompatible
public interface ExecutorServiceManager 
		 extends ServiceHandler {		// see ServletContextListenerBase
	/**
	 * Returns the {@link ExecutorService} if it has been created, that's AFTER {@link ServletContext}
	 * initialization
	 * @return the executor service
	 */
	public ExecutorService getExecutorService();
}
