package r01f.http.loadbalance.balancer;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.UUID;

import com.google.common.collect.Iterables;

import r01f.http.loadbalance.LoadBalancedBackendServerStats;

/**
 * Randomly chooses a server from the given collection of servers.
 */
public class LoadBalancerRandomImpl
  implements LoadBalancer {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private final SecureRandom RANDOM = new SecureRandom(UUID.randomUUID().toString().getBytes());
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public LoadBalancedBackendServerStats chooseWithin(final Collection<LoadBalancedBackendServerStats> availableServerStats) {
		int index = RANDOM.nextInt(availableServerStats.size());
		return Iterables.get(availableServerStats,
							 index);
	}
}
