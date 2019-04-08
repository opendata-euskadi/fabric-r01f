package r01f.patterns;

public interface Subscriber<T> {
	public void onSuccess(final T result);
	public void onError(final Throwable th);
}
