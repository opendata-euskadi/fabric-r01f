package r01f.aspects.interfaces.logging;



/**
 * Simple params formatter
 * Used at @LoggedMethodCalls annotation
 */
public class LoggedMethodCallsParamsVoidFormatter 
  implements LoggedMethodCallsParamsFormatter {

	@Override
	public String formatParams(final Object... params) {
		return "";
	}
}
