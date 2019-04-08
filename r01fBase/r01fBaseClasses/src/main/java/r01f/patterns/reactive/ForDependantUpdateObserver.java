package r01f.patterns.reactive;

/**
 * Observer about updates in a dependant object
 * @param <C>
 * @param <T>
 */
public interface ForDependantUpdateObserver<C,T>
		 extends Observer {

	public void onDependantUpdate(final C container,final T updatedData);
}
