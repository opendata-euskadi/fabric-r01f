package r01f.async;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Future;

import r01f.util.types.collections.CollectionUtils;



/**
 * {@link Future} implementation based on https://code.google.com/p/gwt-async-future/
 * @param <T>
 * 
 * As stated at the javadoc for the {@link Future} interface, it represents the result of an asynchronous computation, 
 * providing methods to:
 * <ul>
 * 		<li>Check if the computation is complete</li>
 * 		<li>Wait for the computation to complete</li>
 * 		<li>Cancel the computation</li>
 * </ul>
 * The only method to get the computation result is the <b>get</b> method: if the result is ready it returns it, BUT if it's not
 * jet ready, the <b>get</b> method blocks until it's available.
 * 
 * This type complements the {@link Future} making possible to subscribe a call-able object implementing the {@link AsyncCallBack} interface
 * so the <b>onSuccess</b> is get called when the result is ready
 * The normal usage is:
 * <pre class='brush:java'>
 * 		public FutureResult<T> doSomeBackgroundComputation() {
 * 			FutureResult<T> outFuture = new FutureResult<T>();
 * 			Thread th = new Thread() {
 * 								@Override
 * 								public void run() {
 * 									... do domething and get the result...
 * 									outFuture.succeeded(result);
 * 								}
 * 						}
 * 		}
 * </pre>
 * The {@link FutureResult}'s succeeded(result) simply notifies every subscribed callback object that the result is ready 
 * calling it's <b>onSuccess</b> method so the normal usage is;
 * <pre class='brush:java'>
 * 		FutureResult<T> futureResult = doSomeBackgroundComputation();
 * 		futureResult.addCallback(new AsyncCallBack<T>() {
 *											@Override
 *											public void onSuccess(final T result) {
 *												// do something interesting with the result
 *											}
 * </pre>
 * The above is NOT the normal usage for the {@link Future}'s interface... normally it's:
 * <pre class='brush:java'>
 * 		FutureResult<T> futureResult = doSomeBackgroundComputation();
 * 		while (!futureResult.isDone()) {
 * 			// do some other things while background computation stills going on
 * 			// if a futureResult.get() is called, the flow BLOCKS until the result is ready
 * 		}
 *		// here the result must be ready
 *		T result = futureResult.get();
 * </pre>
 */
public class FutureResult<T>
  implements Serializable {

	private static final long serialVersionUID = -6062729611318957337L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private enum State {
		SUCCEEDED, 
		FAILED, 
		INCOMPLETE, 
		CANCELLED;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private String _name;
	private T _value = null;
	private State _state = State.INCOMPLETE;
	private Throwable _exception = null;
	private LinkedHashSet<AsyncCallBack<T>> _listeners = new LinkedHashSet<AsyncCallBack<T>>();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public FutureResult() {
		// default constructor
	}
	public FutureResult(final String name) {
		_name = name;
	}
	public FutureResult(final AsyncCallBack<T>... callBacks) {
		this();
		if (CollectionUtils.hasData(callBacks)) {
			for (AsyncCallBack<T> callBack : callBacks) {
				if (callBack != null) this.addCallback(callBack);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Blocks until the result is complete
	 * @return the result
	 */
	public T get() {
		do {
			// ...waiting for result
		} while(!this.isComplete() || !this.hasFailed() || !this.wasCancelled());
		return this.result();
	}
	/**
	 * Retuns the result if available or an exception if it's not ready or the request was cancelled
	 * @return
	 * @throws FutureExecutionException
	 */
	public T result() {
		switch (_state) {
		case INCOMPLETE:
			throw FutureExecutionException.becauseOfIncompleteResult();
		case FAILED: 
			throw FutureExecutionException.becauseOfExecutionException(_exception);
		case CANCELLED:
			throw FutureExecutionException.becauseOfRequestCancellation();
		case SUCCEEDED:
			return _value;
		default:
			throw new IllegalArgumentException();
		}
	}
	/**
	 * @return true if the request was executed, false if it's still executing
	 */
	public boolean isComplete() {
		return _state != State.INCOMPLETE;
	}
	/**
	 * @return true if the request was executed and succeeded
	 */
	public boolean wasSuccessful() {
		return _state == State.SUCCEEDED;
	}
	/**
	 * @return true if the request was executed but failed
	 */
	public boolean hasFailed() {
		return _state == State.FAILED;
	}
	/**
	 * @return true if the request was cancelled 
	 */
	public boolean wasCancelled() {
		return _state == State.CANCELLED;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void addCallback(final AsyncCallBack<T> callback) {
		if (callback == null) return;
		// ...beware the callback could be added after the result is available
		if (this.isComplete()) {
			if (this.wasSuccessful()) {
				callback.onSuccess(_value);
			} else {
				callback.onFailure(_exception);
			}
			return;
		}
		_listeners.add(callback);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Set the state of this result to SUCCEEDED;
	 * @param value
	 */
	public void succeeded(final T value) {
		if (this.isComplete()) throw new IllegalStateException("Cannot set result when already complete for " + this);
		_state = State.SUCCEEDED;
		_value = value;
		// Notify listeners implementing AsyncCallBack interface
		for (AsyncCallBack<T> callback : _copyCallbacksThenClear()) {
			callback.onSuccess(value);
		}
	}
	/**
	 * Sets the state of this result to FAILED
	 * @param th
	 */
	public void failedWithException(final Throwable th) {
		if (this.isComplete()) throw new IllegalStateException("Cannot fail when already complete for " + this,th);
		_state = State.FAILED;
		_exception = th;
		// Notify listeners implementing AsyncCallBack interface
		for (AsyncCallBack<T> callback : _copyCallbacksThenClear()) {
			callback.onFailure(_exception);
		}
	}
	/**
	 * Sets the state of this result to cancelled.
	 */
	protected void cancel() {
		if (this.isComplete()) return;		// you're late man...
		_state = State.CANCELLED;
		_exception = FutureExecutionException.becauseOfRequestCancellation();
		// Notify listeners implementing CancellableAsyncCallBack interface
		// note that if listenes do not implement CancellableAsyncCallBack BUT they implement AsyncCallBack,
		// the cancellation is notified as an exception
		for (AsyncCallBack<T> callback : _copyCallbacksThenClear()) {
			if (callback instanceof CancellableAsyncCallBack<?>) {
				((CancellableAsyncCallBack<?>)callback).onCancel();
			} else {
				callback.onFailure(_exception);		// _exception should be an ExecutionException because of request cancellation
			}
		}
	}
	private List<AsyncCallBack<T>> _copyCallbacksThenClear() {
		List<AsyncCallBack<T>> callbacks = new ArrayList<AsyncCallBack<T>>(_listeners);
		_listeners.clear();
		return callbacks;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public String getName() {
		return (_name != null && !_name.isEmpty()) ? _name 
												   : new StringBuilder("FutureResult")
												   			.append("<")
												   			.append(_value != null ? _composeSimpleName(_value.getClass()) : "?")
												   			.append(">")
												   			.toString();
	}
	public void setName(String name) {
		_name = name;
	}
	private static String _composeSimpleName(final Class<?> type) {
		String className = type.getName();
		int index = className.lastIndexOf('.');
		if (index == -1) return className;
		return className.substring(index + 1);
	}
	@Override
	public String toString() {
		switch (_state) {
		case CANCELLED:
			return getName() + " (cancelled)";
		case FAILED:
			return getName() + " (failed with cause:\n" + _exception + ")";
		case INCOMPLETE:
			return getName() + " (incomplete)";
		case SUCCEEDED:
			return getName() + " (succeeded with result = " + _value + ")";
		default:
			throw new IllegalArgumentException();
		}
	}
}
