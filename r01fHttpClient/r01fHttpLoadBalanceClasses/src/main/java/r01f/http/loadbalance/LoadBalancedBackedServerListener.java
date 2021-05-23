package r01f.http.loadbalance;

/**
 * A listener which is notified when changes to a {@link ServerInstance} occurs.
 */
interface LoadBalancedBackedServerListener {
	/**
	 * called when a server instance's availability changes.
	 * @param isAvailable availability state of the server instance
	 */
	void onAvailabilityChange(boolean isAvailable);
}
