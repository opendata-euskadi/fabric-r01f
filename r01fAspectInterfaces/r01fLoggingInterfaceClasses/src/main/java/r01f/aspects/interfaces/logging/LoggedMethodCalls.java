package r01f.aspects.interfaces.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation used to sign methods that will be logged when invoked
 * <pre class='brush:java'>
 * 		@LoggedMethodCalls(level=LogLevel.DEBUG,when=LoggedMethodCallsWhen.AROUND,
 * 						   module="[CLIENT API]",start="[START]",end="[END]",
 * 						   paramsFormatter=MyMethodsParamsFormatter.class)
 * 		public class ClientAPI {
 * 			public void myPublicMethod() {
 * 				...
 * 			}
 * 		}
 * </pre>
 * It's the same as:
 * <pre class='brush:java'>
 * 		@Sl4fj
 * 		public class ClientAPI {
 * 			public void myPublicMethod() {
 * 				log.debug("[CLIENT API][START]: myPublicMethod");
 * 				...
 * 				log.debug("  [CLIENT API][END]: myPublicMethod");
 * 			}
 * 		}
 * </pre>
 * it injects logs before and after each method call
 *
 * Many formatters can be used
 * 		LoggedMethodCallsParamsDefaultFormatter --> Returns info about parameters
 * 		LoggedMethodCallsParamsVoidFormatter	--> Does not log anything
 * 		custom --> just implement {@link LoggedMethodCallsParamsFormatter}
 *
 * If invocations to a method should NOT be logged, just annotate it with @DoNotLog
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
@Target(ElementType.TYPE)
public @interface LoggedMethodCalls {
	/**
	 * Log level
	 */
	LogLevel level() default LogLevel.DEBUG;
	/**
	 * When to log
	 */
	LoggedMethodCallsWhen when() default LoggedMethodCallsWhen.AROUND;
	/**
	 * Module name (it appears at the beginning of the log message)
	 */
	String module() default "";
	/**
	 * Log indent (number of tabs before the log)
	 */
	int indent() default 0;
	/**
	 * Method call start
	 */
	String start() default "[START]";
	/**
	 * Method call end
	 */
	String end() default "[END]";
	/**
	 * Parameter formatter
	 */
	Class<? extends LoggedMethodCallsParamsFormatter> paramsFormatter() default LoggedMethodCallsParamsVoidFormatter.class;
}
