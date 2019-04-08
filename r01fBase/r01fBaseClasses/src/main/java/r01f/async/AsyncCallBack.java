package r01f.async;


public interface AsyncCallBack<T> {
   /**
   	* Called when an asynchronous call fails to complete normally.
   	* @param caught failure encountered while executing a remote procedure call
   	*/
	public void onFailure(Throwable caught);

  	/**
  	 * Called when an asynchronous call completes successfully.
  	 * @param result the return value of the remote produced call
  	 */
  	public void onSuccess(T result);
}
