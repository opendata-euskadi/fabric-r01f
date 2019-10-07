package r01f.aspects.interfaces.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation that signs that a method call must not be logged
 * <pre class='brush:java'>
 * 		@LoggedMethodCalls(level=LogLevel.DEBUG,when=LoggedMethodCallsWhen.AROUND,
 * 						   module="[CLIENT API]",start="[START]",end="[END]",
 * 						   paramsFormatter=MyMethodsParamsFormatter.class)
 * 		public class ClientAPI {
 * 			public void myPublicMethod() {
 * 				...
 * 				@DoNotLog
 * 				public void notLoggedMethod() {
 * 					...
 * 				}
 * 			}
 * 		}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DoNotLog {
	/* just an interface */
}
