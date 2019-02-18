package r01f.debug;


/**
 * Interface to be implemented by the debuggable objects
 */
public interface Debuggable {
	/**
	 * Returns debug info
	 * @return the debug info
	 */
	public CharSequence debugInfo();
}
