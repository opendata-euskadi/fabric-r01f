package r01f.patterns.reactive;

import java.util.Collection;

public interface ForLazyLoadedObserver<T> 
         extends Observer {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The observable lazy loaded model object notifies the observer that the lazy loading is in progress 
	 * giving it chance to for example display a progress indicator
	 */
	public void onLazyLoadingInProgress();
	/**
	 * The observable lazy loaded model object notifies the observer that the lazy loading ended with error
	 * giving it chance to for example to show an error indicator
	 * @param err
	 */
	public void onLazyLoadingError(final String err);
	/**
	 * The observable lazy loaded model object notifies the observer that the lazy loading ended successfully
	 * giving it chance to for example remove the progress indicator and paint the loaded child
	 * @param children 
	 */
	public void onLazyLoadingSuccessful(final Collection<T> children);
}
