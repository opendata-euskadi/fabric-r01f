package r01f.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.annotations.GwtIncompatible;

/**
 * Hands out threads from the wrapped thread factory with setDeamon(true), so the
 * threads won't keep the JVM alive when it should otherwise exit.
 */
@GwtIncompatible
public class DaemonThreadFactory 
  implements ThreadFactory {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final ThreadFactory _factory;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Construct a ThreadFactory with setDeamon(true) using
	 * Executors.defaultThreadFactory()
	 */
	public DaemonThreadFactory() {
		this(Executors.defaultThreadFactory());
	}
	/**
	 * Construct a ThreadFactory with setDeamon(true) wrapping the given factory
	 * @param thread factory to wrap
	 */
	public DaemonThreadFactory(final ThreadFactory factory) {
		if (factory == null) throw new NullPointerException("factory cannot be null");
		_factory = factory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Thread newThread(final Runnable r) {
		final Thread t = _factory.newThread(r);
		t.setDaemon(true);		// this is important!!
		return t;
	}
}