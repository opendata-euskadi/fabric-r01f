package r01f.patterns;

import lombok.Setter;

/**
 * Memoization pattern implementation (Memoization is similar to LazyLoading) 
 * based on a factory that can be handy when the basic {@link Memoized} type cannot be used
 * The normal usage is:
 * <pre class='brush:java'>
 * public class AnyClass {
 * 		private final MemoizedUponFactory<Integer> memoizedInt = new MemoizedUponFactory<Integer>();
 * 
 * 		public void someMethod(final securityContext securityContext) {
 * 			int theInt = memoizedInt.get(new Factory<Integer>() {
 * 											public Integer create() {
 * 												// use the user context to return the integer
 * 											}
 * 									 	});
 * 		}
 * }
 * </pre>
 * 
 * @param <T> the type to be memoized
 */
public class MemoizedUponFactory<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The memoized instance
	 */
	@Setter protected T _instance;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the memoized instance or a new one if it has NOT been supplied
	 * @return the memoized instance
	 */
	public T get(final Factory<T> factory) {
		if (_instance == null) _instance = factory.create();
		return _instance;
	}
	/**
	 * Resets the memoized value
	 */
	public void reset() {
		_instance = null;
	}
}
