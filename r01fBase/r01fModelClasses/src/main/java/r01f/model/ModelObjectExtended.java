package r01f.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to set that a model object is EXTENDED with functionality implemented in a delegate object
 * that normally encapsulates the server interaction logic
 * <ul>
 * 		<li>R01MContent</li>
 * 		<li>R01MContentLangVersion</li>
 * 		<li>...</li>
 * </ul>
 * 
 * Create a model object type (implement an interface extending {@link r01mo.model.interfaces.R01MModelObject})
 * This type MUST contain a field annotated with @Inject y @R01MModelObjectExtension that will be injected automatically
 * by the infrastructure.
 * 	<pre class='brush:java'>
 * 		public class R01MContent 
 * 		  implements R01MModelObject {
 * 			...
 * 			@Inject @R01MModelObjectExtension
 *  		private transient R01MContentExtension _extension;	// this object encapsulates the server-interaction logic 
 *  		...
 * 		} 
 *	</pre>  
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ModelObjectExtended {
	/* nothing to do */
}
 