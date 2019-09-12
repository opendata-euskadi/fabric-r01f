package r01f.aspects.interfaces.logging;

/**
 * Used at @LoggedMethodCalls to set when to log
 */
public enum LoggedMethodCallsWhen {
	BEGIN,		// begin of method invocation
	END,		// end of method invocation
	AROUND;		// around method invocation
}
