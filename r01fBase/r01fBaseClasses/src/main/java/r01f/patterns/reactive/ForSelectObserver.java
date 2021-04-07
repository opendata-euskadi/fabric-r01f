package r01f.patterns.reactive;

/**
 * Observer about selections
 * @param <T> the selected object
 */
public interface ForSelectObserver<T>
		 extends Observer {

	public void onSelect(final T selectedData);
}
