package r01f.http.loadbalance.serverlist;

import java.util.Collection;

import r01f.http.loadbalance.LoadBalancedBackEndServer;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;

/**
 * A strategy for discovering a list of server instances within a service cluster.
 */
public interface LoadBalancedServerList {
	/**
	 * Gets the service cluster id
	 * @return service cluster id
	 */
	LoadBalancedServiceID getServiceId();

	/**
	 * Gets a list of server instances within a service cluster.
	 * @return the found list of {@link LoadBalancedBackEndServer}s
	 */
	Collection<LoadBalancedBackEndServer> getListOfServers();
}
