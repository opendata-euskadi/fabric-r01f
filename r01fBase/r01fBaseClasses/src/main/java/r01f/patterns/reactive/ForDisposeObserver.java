package r01f.patterns.reactive;

/**
 * Observer about disposals
 * @param <T> the disposed object
 */
public interface ForDisposeObserver<T>
		 extends Observer {

	public void onDispose(final T disposedData);
}
