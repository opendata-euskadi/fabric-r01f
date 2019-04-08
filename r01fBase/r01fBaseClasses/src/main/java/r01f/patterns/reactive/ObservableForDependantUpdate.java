package r01f.patterns.reactive;

/**
 * Can be observed about updates on dependant objects
 */
public interface ObservableForDependantUpdate<T>
	     extends Observable {
	/**
	 * Notify any observer observing this object that this has been updated
	 * @param updatedDependant
	 */
	public void notifyObserversAboutDependantUpdate(final T updatedDependant);
}
