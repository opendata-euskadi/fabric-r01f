package r01f.inject;

import com.google.inject.Binder;

/**
 * This interface allows Guice module sub-types to add bindings 
 * Example:
 * A base type that do some guice bindings:
 * <pre class='brush:java'>
 * 		public abstract class MyModuleBase()
 * 				   implements Module {
 * 				@Override
 *				public void configure(final Binder binder) {
 *					... do some bindings...
 *					if (this instanceof HasMoreBindings) {	
 *						((HasMoreBindings)this).configureMoreBindings(binder);		// give chance to sub-types to add bindings
 *					}
 *				}
 * 		}
 * </pre>
 * The MyModuleBase sub-type will be like:
 * <pre class='brush:java'>
 * 		public class MyConcreteModule 
 * 			 extends MyModuleBase
 * 		  implements HasMoreBindings {
 * 			@Override
 * 			public void configureMoreBindings(final Binder binder) {
 * 				... do more bindings...
 * 			}
 * 		}  
 * </pre>
 *
 */
public interface HasMoreBindings {
	public void configureMoreBindings(final Binder binder);
}
