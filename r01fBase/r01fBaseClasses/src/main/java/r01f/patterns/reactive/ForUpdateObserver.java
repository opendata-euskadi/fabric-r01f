package r01f.patterns.reactive;

/**
 * Observer about updates
 * @param <T> the updated object
 */
public interface ForUpdateObserver<T>
		 extends Observer {

	public void onUpdate(final T updatedData);
}
