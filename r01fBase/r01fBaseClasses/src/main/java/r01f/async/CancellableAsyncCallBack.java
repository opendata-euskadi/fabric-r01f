package r01f.async;


public interface CancellableAsyncCallBack<T> 
		 extends AsyncCallBack<T> {
    /**
     * Called when an asynchronous call is cancelled.
     */
    public void onCancel();
}
