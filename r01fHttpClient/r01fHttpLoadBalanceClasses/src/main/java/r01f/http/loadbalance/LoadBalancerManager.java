package r01f.http.loadbalance;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedBackEndServerID;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;
import r01f.http.loadbalance.LoadBalancerManagerBuilder.LoadBalancerManagerBuilderServersStep;
import r01f.http.loadbalance.balancer.LoadBalancer;
import r01f.http.loadbalance.serverlist.LoadBalancedServerList;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@Accessors(prefix="_")
public class LoadBalancerManager {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REFRESH_INTERVAL_IN_MILLIS = "janus.refreshIntervalInMillis";
	public static final long DEFAULT_REFRESH_INTERVAL_IN_MILLIS = 30000;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
			private final MetricRegistry _metricRegistry;

	@Getter private final LoadBalancedServiceID _serviceId;
	@Getter private final LoadBalancedServerList _serverList;
	@Getter private final LoadBalancer _loadBalancer;
	@Getter private final long _refreshInterval;

	// cache of server lists
	private final Map<LoadBalancedBackEndServerID,LoadBalancedBackendServerStats> _serverStats = new ConcurrentHashMap<>();
	private final AtomicBoolean _updatingServer = new AtomicBoolean(false);
	private long _nextUpdateTime = -1;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Create an instance of {@link LoadBalancerManagerBuilder}
	 * @param serviceId the service cluster id that the {@link LoadBalancerManager} will use
	 * @return the builder
	 */
	public static LoadBalancerManagerBuilderServersStep builderForServiceWithName(final LoadBalancedServiceID serviceId) {
		return new LoadBalancerManagerBuilder() { /* nothing */ }
						.new LoadBalancerManagerBuilderServersStep(serviceId);
	}
	LoadBalancerManager(final LoadBalancedServiceID serviceId,final LoadBalancedServerList serverList,
						final LoadBalancer loadBalancer,
						final MetricRegistry metricsRegistry) {
		this(serviceId,serverList,
			 loadBalancer,
			 metricsRegistry,
			 DEFAULT_REFRESH_INTERVAL_IN_MILLIS);
	}
	LoadBalancerManager(final LoadBalancedServiceID serviceId,final LoadBalancedServerList serverList,
						final LoadBalancer loadBalancer,
						final MetricRegistry metricsRegistry,
						final long refreshInterval) {
		_serviceId = serviceId;
		_serverList = serverList;
		_loadBalancer = loadBalancer;
		_metricRegistry = metricsRegistry;
		_refreshInterval = refreshInterval;
		_initializeServerList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get a single server instance chosen through the {@link LoadBalancerManager}
	 * @return a server instance chosen through the load balancer.
	 */
	public LoadBalancedBackendServerStats chooseServer() {
		// update the server list > update server stats & tick servers (set availability)
		_updateServerList();

		// filter the available servers
		Collection<LoadBalancedBackendServerStats> serverStats = _serverStats.values();
		Collection<LoadBalancedBackendServerStats> availableServersStats = FluentIterable.from(serverStats)
																						 .filter(new Predicate<LoadBalancedBackendServerStats>() {
																										@Override
																										public boolean apply(final LoadBalancedBackendServerStats server) {
																											if (log.isDebugEnabled()) log.info("[load balancer]: server {} is{}available",
																														 					   server.getServerInstance().getId(),
																														 					   server.getServerInstance().isAvailable() ? " " : " NOT ");
																											return server.getServerInstance().isAvailable();
																										}
																								  })
																						 .toList();
		if (CollectionUtils.isNullOrEmpty(availableServersStats)) return null;	// ...no available servers

		// choose one
		return _loadBalancer.chooseWithin(availableServersStats);
	}
	private void _initializeServerList() {
		try {
			// create stats for every server
			for (LoadBalancedBackEndServer server : _serverList.getListOfServers()) {
				LoadBalancedBackendServerStats stat = new LoadBalancedBackendServerStats(server,
															  							 _metricRegistry);
				_serverStats.put(server.getId(),stat);
			}
		} catch (Exception e) {
			log.error("Exception initializing the server list", e);
		}
	}
	private void _updateServerList() {
		// only allow one thread to update the server list
		if (!_updatingServer.compareAndSet(false,true)) return;
		
		try {
			// has the update interval been met?
			long now = System.currentTimeMillis();
			if (_refreshInterval == 0 || _nextUpdateTime > now) return;	// nothing to do
			_nextUpdateTime = now + _refreshInterval;

			// update server stats with current availability
			for (LoadBalancedBackEndServer server : _serverList.getListOfServers()) {
				LoadBalancedBackendServerStats stat = _serverStats.get(server.getId());
				if (stat != null) {
					// there already exists stats for the server
					stat.getServerInstance()
						.setAvailable(server.isAvailable());
				} else {
					// there does NOT exists stats for the server
					stat = new LoadBalancedBackendServerStats(server,
															  _metricRegistry);
				}
			}

			// tick all the servers and remove from list if requested
			Collection<LoadBalancedBackEndServerID> serversToRemove = FluentIterable.from(_serverStats.values())
																					// filter servers that should be removed
																					.filter(new Predicate<LoadBalancedBackendServerStats>() {
																									@Override
																									public boolean apply(final LoadBalancedBackendServerStats serverStats) {
																										// tick returns true if the server is usable
																										boolean tick = serverStats.getServerInstance()
																														  		  .tick();
																										if (!tick) log.warn("[load balancer] > removing service instance <{}> due to discovery heartbeat timeout.",
																												 		    serverStats.getServerInstance().getId());
																										return !tick;
																									}
																							})
																					// get the server ids
																					.transform(new Function<LoadBalancedBackendServerStats,LoadBalancedBackEndServerID>() {
																										@Override
																										public LoadBalancedBackEndServerID apply(final LoadBalancedBackendServerStats serverStats) {
																											return serverStats.getServerInstance().getId();
																										}
																						
																							   })
																					.toList();
			for (LoadBalancedBackEndServerID id : serversToRemove) {
				_serverStats.remove(id);
			}
		} catch (Exception e) {
			log.error("[load balancer] > Exception updating the server list", e);
		} finally {
			_updatingServer.set(false);
		}
	}
}
