package r01f.debug;


/**
 * Interface to be implemented by the debuggable objects
 */
public interface HierarchicalDebuggable {
	/**
	 * Returns debug info
	 * @param depthLevel
	 * @return the debug info
	 */
	public CharSequence debugInfo(final int depthLevel);
}
