package r01f.async;

import r01f.patterns.OnErrorSubscriber;
import r01f.patterns.OnSuccessSubscriber;
import r01f.patterns.Subscriber;

public interface AsyncCallBack<T>
		 extends Subscriber<T> {
	/**
	 * Utility to create a {@link Subscriber} from an {@link OnSuccessSubscriber} and
	 * an {@link OnErrorSubscriber}
	 * ... this is handy when a {@link Subscriber} is required as a method param but
	 *     one want to use lambda expressions
	 * <pre class='brush:java'>
	 * 		util.doSomething(params,
	 * 						 UISubscriber.from(result -> { ..on success.. }));
	 * </pre>
	 * @param <T>
	 * @param onSuccess
	 * @return
	 */
	public static <T> AsyncCallBack<T> from(final OnSuccessSubscriber<T> onSuccess) {
		return new AsyncCallBack<T>() {
						@Override
						public void onSuccess(final T result) {
							onSuccess.onSuccess(result);
						}
						@Override
						public void onError(final Throwable th) {
							// do nothing
						}
			   };
	}
}
