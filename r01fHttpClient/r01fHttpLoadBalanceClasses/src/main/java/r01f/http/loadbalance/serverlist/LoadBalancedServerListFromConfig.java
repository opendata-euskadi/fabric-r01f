package r01f.http.loadbalance.serverlist;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.http.loadbalance.LoadBalancedBackEndServer;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;

@Accessors(prefix="_")
public class LoadBalancedServerListFromConfig
  implements LoadBalancedServerList {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final LoadBalancedServiceID _serviceId;
	@Getter private final AtomicReference<Collection<LoadBalancedBackEndServer>> _servers;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTROR
/////////////////////////////////////////////////////////////////////////////////////////
	public LoadBalancedServerListFromConfig(final LoadBalancedServiceID serviceId,
											final Collection<LoadBalancedBackEndServer> servers) {
		_serviceId = serviceId;
		_servers = new AtomicReference<>(servers);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<LoadBalancedBackEndServer> getListOfServers() {
		return _servers.get();
	}
}
