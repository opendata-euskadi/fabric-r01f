package r01f.patterns.reactive;

/**
 * Observer about child additions or removals
 * @param <T>
 */
public interface ForChildAdditionOrRemovalObserver<T>
		 extends Observer {

	/**
	 * A child has been added
	 * @param addedChild
	 */
	public void onChildAddition(final T addedChild);
	/**
	 * A child has been removed
	 * @param removedChild
	 */
	public void onChildRemoval(final T removedChild);
}
