package r01f.concurrent;

import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible
public class TimeOutController {
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	private TimeOutController() {
		super();
	}
///////////////////////////////////////////////////////////////////////////////
//	METODOS
///////////////////////////////////////////////////////////////////////////////
    /**
     * Runs the task and waits the given timeout milis befoure returning
     * If the task does NOT returns within the given timeout milis the thread is interrupted and an exception is thrown
     * Caller must override Thread.interrupt() to kill the thread or anything
     * @param task
     * @param timeout
     * @throws TimeoutException 
     */
    public static void execute(final Thread task,final long timeout) throws TimeoutException {
        task.start();
        try {
            task.join(timeout);
        } catch (InterruptedException e) {
            /* Interrupted */
        }
        if (task.isAlive()) {
            task.interrupt();
            throw new TimeoutException();
        }
    }
    /**
     * Runs the task in a daemon Thread and waits the given time
     * @param task
     * @param timeout
     * @throws TimeoutException if time is out and task is not finished
     */
    public static void execute(final Runnable task,final long timeout) throws TimeoutException {
        Thread t = new Thread(task,"Timeout guard");	
        t.setDaemon(true);
        execute(t,timeout);
    }
    /**
     * Signals that the task timed out.
     */
    public static class TimeoutException 
    			extends Exception {
        private static final long serialVersionUID = 273515211929706600L;

        /** Create an instance */
        public TimeoutException() {
        }
    }
}
