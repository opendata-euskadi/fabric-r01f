package r01f.services.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.bootstrap.services.ServiceMatcher;

/**
 * Sometimes the service interfaces extends some base service interfaces that are NOT exposed:
 * <pre class='brush:java'>
 * 		// some BASE service interface
 * 		public inteface MyServiceInterfaceBase 
 * 				extends ServiceInterface {
 * 			// some methods
 * 		}
 * 		// a CONCRETE service interface
 * 		public interface MyServiceInterface
 * 				 extends MyServiceInterfaceBase {
 * 			// some other methods
 *      }
 * </pre>
 * ... when matching service interfaces with their BEAN or PROXY implementations (see {@link ServiceMatcher}), 
 * 	   a match in BASE service interface IS NOT wanted so in order to know which of the interfaces are
 *     exposed (as BEANs or PROXIES) an annotation is needed
 */
@Target({ ElementType.TYPE }) 
@Retention(RetentionPolicy.RUNTIME)
public @interface ExposedServiceInterface {
	// nothing
}