package r01f.patterns.reactive;

/**
 * Can be observed about updates
 */
public interface ObservableForUpdate
	     extends Observable {
	/**
	 * Notify any observer observing this object that this has been updated
	 */
	public void notifyObserversAboutUpdate();
}
