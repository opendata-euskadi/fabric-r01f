package r01f.patterns;

import org.slf4j.Logger;


public interface Subscriber<T>
	     extends OnSuccessSubscriber<T>,
				 OnErrorSubscriber {
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Utility to create a {@link Subscriber} from another {@link Subscriber}
	 * @param <S>
	 * @param <T>
	 * @param other
	 * @return
	 */
	public static <S extends Subscriber<T>,T> Subscriber<T> wrap(final S other) {
		return new Subscriber<T>() {
						@Override
						public void onSuccess(final T result) {
							other.onSuccess(result);
						}
						@Override
						public void onError(final Throwable th) {
							other.onError(th);
						}
			   };
	}
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
	public static <T> Subscriber<T> from(final OnSuccessSubscriber<T> onSuccess) {
		return Subscriber.from(onSuccess,
							   // on error
							   th -> {  /* do nothing with the error */ });
	}
	/**
	 * Utility to create a {@link Subscriber} from an {@link OnSuccessSubscriber} and
	 * an {@link OnErrorSubscriber}
	 * ... this is handy when a {@link Subscriber} is required as a method param but
	 *     one want to use lambda expressions
	 * <pre class='brush:java'>
	 * 		util.doSomething(params,
	 * 						 UISubscriber.from(result -> { ..on success.. },
	 * 										   th -> {.. on error..}));
	 * </pre>
	 * @param <T>
	 * @param onSuccess
	 * @param onError
	 * @return
	 */
	public static <T> Subscriber<T> from(final OnSuccessSubscriber<T> onSuccess,
										 final OnErrorSubscriber onError) {
		// Create a subscriber delegating to the onSuccess & onError subscribers
		return new Subscriber<T>() {
						@Override
						public void onSuccess(final T result) {
							onSuccess.onSuccess(result);
						}
						@Override
						public void onError(final Throwable th) {
							onError.onError(th);
						}
			   };
	}
	public static <T> Subscriber<T> nop() {
		return Subscriber.from(result -> {},	// do nothing on success
							   error -> {});	// do nothing on error
	}
	public static <T> Subscriber<T> log(final Logger log) {
		return Subscriber.from(result -> {
												log.debug("{} on-success subscriber",Subscriber.class);
										   },
						       error -> {
							   				log.error("{} on-error subscriber: {}",Subscriber.class,error.getMessage(),error);
						   			    });
	}
}
