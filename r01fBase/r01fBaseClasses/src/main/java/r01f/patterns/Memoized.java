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
public abstract class Memoized<T> 
		   implements Supplier<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Supplier<T> _supplier;
	/**
	 * The memoized instance
	 */
	@Setter protected T _instance;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public Memoized() {
		_supplier = this;
	}
	public Memoized(final Supplier<T> supplier) {
		_supplier = supplier;
	}
	public static <T> Memoized<T> using(final Supplier<T> supplier) {
		return new Memoized<T>(supplier) {
						@Override
						public T supply() {
							return supplier.supply();
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the memoized instance or a new one if it has NOT been supplied
	 * @return the memoized instance
	 */
	public T get() {
		if (_instance == null) _instance = _supplier.supply();
		return _instance;
	}
	/**
	 * Resets the memoized value
	 */
	public void reset() {
		_instance = null;
	}
}
