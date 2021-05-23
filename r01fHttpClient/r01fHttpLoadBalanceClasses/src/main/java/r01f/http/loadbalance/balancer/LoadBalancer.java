package r01f.http.loadbalance.balancer;

import java.util.Collection;

import r01f.http.loadbalance.LoadBalancedBackendServerStats;

/**
 * Strategy interface for the selection of server instances to connect to
 * or send messages to.  Implementations should use the given collection of
 * {@link LoadBalancedBackendServerStats} to determine the appropriate server to return.
 */
public interface LoadBalancer {
	/***
	 * Choose an available server from the list of servers.  
	 * The list may contain unavailable servers, i.e. those currently short circuited,
	 * so the load balancer implementation is responsible for filtering those out.
	 * @return server if available, null otherwise
	 */
	LoadBalancedBackendServerStats chooseWithin(Collection<LoadBalancedBackendServerStats> availableServerStats);
}
