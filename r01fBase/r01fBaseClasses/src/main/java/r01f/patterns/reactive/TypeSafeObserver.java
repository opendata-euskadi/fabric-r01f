package r01f.patterns.reactive;

/**
 * A marker interface
 * @param <T>
 * @param <O>
 */
public interface TypeSafeObserver<T extends TypeSafeObserver<T,O>,
								  O extends TypeSafeObservable<O,T>>
		 extends Observer {
	/* just a marker interface */
}
