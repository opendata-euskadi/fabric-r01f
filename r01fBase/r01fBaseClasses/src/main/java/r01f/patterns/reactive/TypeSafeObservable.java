package r01f.patterns.reactive;

/**
 * Typesafe implementation of {@link java.util.Observer} pattern 
 * @see http://justonjava.blogspot.com.es/2010/12/type-safe-observerobservable.html
 * @see https://github.com/Netflix/RxJava/
 * 
 * Usage:
 * Create the observable type that extends {@link ObservableBase} and the observer type that implements {@link Observer}
 * 	   <pre class='brush:java'>
 * 			public class MyObservable
 * 				 extends ObservableBase<MyObservable,MyObserver> {
 * 				public void doSomething() {
 * 					this.notifyObservers();
 * 				}
 * 			}
 * 			public class MyObserver 
 * 			  implements Observer<MyObserver,MyObservable> {
 * 				public void update(final MyObservable obserbable) {
 * 					System.out.println("hey you!");
 * 				}
 * 			}
 * 
 * 			// Subscribe the observer to the observable and that's all
 *			MyObservable observable = new MyObservable();
 *			MyObserver observer = new MyObserver();
 *			observable.addObserver(observer);
 *				
 *			observable.doSomething();
 * 	   </pre>
 * </li>
 * @param <T>
 * @param <O>
 */
public interface TypeSafeObservable<T extends TypeSafeObservable<T,O>,
									O extends TypeSafeObserver<O,T>> {
	/**
	 * Adds an observer to the registered observers list
	 * @param observer
	 */
	public void addObserver(final O observer);
}
