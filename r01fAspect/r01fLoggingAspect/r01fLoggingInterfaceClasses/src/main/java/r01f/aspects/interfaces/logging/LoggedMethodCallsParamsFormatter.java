package r01f.aspects.interfaces.logging;

/**
 * Used at @LoggedMethodCalls annotation
 * <pre class='brush:java'>
 * 		@LoggedMethodCalls(level=LogLevel.DEBUG,
 * 						   module="[CLIENT API]",start="[START]",end="[END]",
 * 						   paramsFormatter=MyMethodParamsFormatter.class)
 * 		public class R01MClientAPI {
 * 			public void myPublicMethod() {
 * 				...
 * 			}
 * 		}
 * </pre>
 */
public interface LoggedMethodCallsParamsFormatter {
	/**
	 * Formats method params
	 * @param params 
	 * @return
	 */
	public String formatParams(Object... params);
}
