package r01f.patterns;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Base type to help memoization pattern implementation (Memoization is similar to LazyLoading)
 * The normal usage is:
 * <pre class='brush:java'>
 * public class AnyClass {
 * 		private final Memoized<Integer> memoizedInt = new Memoized<Integer>() {
 * 															public Integer supply() {
 * 																return anyInt;
 * 															} 
 * 													  } 	
 * 		public void someMethod() {
 * 			int theInt = memoizedInt.get();
 * 		}
 * }
 * </pre>
 * 
 * Note that the Memoized's supply() method can only use info available upon object creation 
 * If the instance supply needs any info only available at the method call, see {@link MemoizedUponFactory}
 *  
 * @param <T> the type to be memoized
 */
@Accessors(prefix="_")
public abstract class Memoized<T> {
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
	public T get() {
		if (_instance == null) _instance = this.supply();
		return _instance;
	}
	/**
	 * Resets the memoized value
	 */
	public void reset() {
		_instance = null;
	}
	/**
	 * Supplies an instance to be memoized
	 * @return the memoized instance
	 */
	protected abstract T supply();
}
