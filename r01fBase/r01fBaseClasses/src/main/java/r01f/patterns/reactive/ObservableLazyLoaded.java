package r01f.patterns.reactive;

import java.util.Collection;

public interface ObservableLazyLoaded<T> 
	     extends Observable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Notify registered observers that the lazy loading is in progress giving them chance
	 * to for example display a progress indicator
	 */
	public void notifyObserversAboutLazyLoadingInProgress();
	/**
	 * Notify registered observers that the lazy loading ended with an error giving them chance
	 * to show an error indicator
	 * @param err
	 */
	public void notifyObserversAboutLazyLoadingError(final String err);
	/**
	 * Notify registered observers that the lazy loading ended successfully giving them chance
	 * for example to remove the loading progress indicator and paint the loaded child
	 * @param children 
	 */
	public void notifyObserversAboutLazyLoadingSuccessful(final Collection<T> children);
}
