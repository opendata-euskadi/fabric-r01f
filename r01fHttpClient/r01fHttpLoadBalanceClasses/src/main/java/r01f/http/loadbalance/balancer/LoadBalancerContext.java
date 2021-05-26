package r01f.http.loadbalance.balancer;

/**
 * A marker interface for context data used when choosing a server
 */
public interface LoadBalancerContext {
	public <C extends LoadBalancerContext> C as(final Class<C> type);
}
