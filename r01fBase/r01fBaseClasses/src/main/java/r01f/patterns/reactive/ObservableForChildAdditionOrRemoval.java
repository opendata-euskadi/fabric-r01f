package r01f.patterns.reactive;

/**
 * Can be observed about child addition or removal
 */
public interface ObservableForChildAdditionOrRemoval<T>
	     extends Observable {
	/**
	 * Notify any observer observing this object that a child has been added
	 * @param addedChild
	 */
	public void notifyObserversAboutChildAddition(final T addedChild);
	/**
	 * Notify any observer observing this object that a child has been removed
	 * @param removedChild
	 */
	public void notifyObserversAboutChildRemoval(final T removedChild);
}
