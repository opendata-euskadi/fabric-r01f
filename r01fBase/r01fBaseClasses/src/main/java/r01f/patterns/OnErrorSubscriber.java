package r01f.patterns;

public interface OnErrorSubscriber<T> {
	public void onError(final Throwable th);
}
