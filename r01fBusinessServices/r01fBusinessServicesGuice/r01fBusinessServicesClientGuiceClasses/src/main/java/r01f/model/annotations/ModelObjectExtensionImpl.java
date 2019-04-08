package r01f.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;



/**
 * Annotation to set that an object is an EXTENSION of a {@link ModelObject} 
 * This extension object normally encapsulates the server interaction logic
 * <ul>
 * 		<li>R01MContentExtension</li>
 * 		<li>R01MContentLangVersionExtension</li>
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
@BindingAnnotation						// it's used to select the fields to be injected with guice at types annotated with @R01MModelObjectExtended
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface ModelObjectExtensionImpl {
	/* nothing to do */
}
 