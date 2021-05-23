package r01f.http.loadbalance;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedBackEndServerID;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;
import r01f.http.loadbalance.LoadBalancerManagerBuilder.LoadBalancerManagerBuilderMetricsRegistryStep;
import r01f.http.loadbalance.balancer.LoadBalancer;
import r01f.http.loadbalance.serverlist.LoadBalancedServerList;
import r01f.util.types.collections.CollectionUtils;

/**
 * Load balancer inspired by https://github.com/Kixeye/janus
 * 
 * Load balancing is splitted into two concerns:
 * 		[1] - server selection
 * 		[2] - interaction with the selected server
 * ... this manager is responsible for the first one: server selection
 * 
 * Usage
 * <pre class='brush:java'>
 * 		// [1] - Create the load balancer
 *		LoadBalancerManager loadBalancer = LoadBalancerManager.builder()
 *														  	  .usingNewMetricRegistry()
 *															  .withDefaultRefreshInterval()
 *															  .withRandomLoadBalancing()
 *															  .withServers(LoadBalancedServiceID.named("test"),
 *																		   Url.from("http://www.google.com"),Url.from("http://www.google.es"))
 *															  .build();
 *		LoadBalancedHttpClient loadBalancedHttpClient = new LoadBalancedHttpClient(loadBalancer,2);
 * </pre>
 */
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

	@Getter private final Map<LoadBalancedServiceID,LoadBalancedServerList> _serverList;
	@Getter private final LoadBalancer _loadBalancer;
	@Getter private final long _refreshInterval;

	// cache of server lists
	private final Map<LoadBalancedBackendServerStatsKey,LoadBalancedBackendServerStats> _serverStats = new ConcurrentHashMap<>();
	private final AtomicBoolean _updatingServer = new AtomicBoolean(false);
	private long _nextUpdateTime = -1;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Create an instance of {@link LoadBalancerManagerBuilder}
	 * @return the builder
	 */
	public static LoadBalancerManagerBuilderMetricsRegistryStep builder() {
		return new LoadBalancerManagerBuilder() { /* nothing */ }
						.new LoadBalancerManagerBuilderMetricsRegistryStep();
	}
	LoadBalancerManager(final LoadBalancer loadBalancer,
						final MetricRegistry metricsRegistry,
						final Collection<LoadBalancedServerList> serverList) {
		this(loadBalancer,
			 metricsRegistry,
			 DEFAULT_REFRESH_INTERVAL_IN_MILLIS,
			 serverList);
	}
	LoadBalancerManager(final LoadBalancer loadBalancer,
						final MetricRegistry metricsRegistry,
						final long refreshInterval,
						final Collection<LoadBalancedServerList> serverList) {
		_loadBalancer = loadBalancer;
		_metricRegistry = metricsRegistry;
		_refreshInterval = refreshInterval;
		
		// server list
		_serverList = Maps.newLinkedHashMapWithExpectedSize(serverList.size());
		FluentIterable.from(serverList)
					  .forEach(new Consumer<LoadBalancedServerList>() {
										@Override
										public void accept(final LoadBalancedServerList servers) {
											_serverList.put(servers.getServiceId(),servers);
										}
								 });
		// create stats for every server
		try {
			for (LoadBalancedServerList servers : _serverList.values()) {
				for (LoadBalancedBackEndServer server : servers.getListOfServers()) {
					LoadBalancedBackendServerStats stat = new LoadBalancedBackendServerStats(servers.getServiceId(),
																							 server,
																							 _metricRegistry);
					_serverStats.put(new LoadBalancedBackendServerStatsKey(servers.getServiceId(),server.getId()),stat);
				}
			}
		} catch (Exception e) {
			log.error("Exception initializing the server list", e);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get a single server instance chosen through the {@link LoadBalancerManager}
	 * @param serviceId
	 * @return a server instance chosen through the load balancer.
	 */
	public LoadBalancedBackendServerStats chooseServerFor(final LoadBalancedServiceID serviceId) {
		// update the server list > update server stats & tick servers (set availability)
		_updateServerList(serviceId);

		// filter the available servers
		Collection<LoadBalancedBackendServerStats> serverStats = this.getServerStatsOf(serviceId);
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
	private void _updateServerList(final LoadBalancedServiceID serviceId) {
		// only allow one thread to update the server list
		if (!_updatingServer.compareAndSet(false,true)) return;
		
		try {
			// has the update interval been met?
			long now = System.currentTimeMillis();
			if (_refreshInterval == 0 || _nextUpdateTime > now) return;	// nothing to do
			_nextUpdateTime = now + _refreshInterval;

			// update server stats with current availability
			LoadBalancedServerList servers = _serverList.get(serviceId);
			for (LoadBalancedBackEndServer server : servers.getListOfServers()) {
				LoadBalancedBackendServerStats stat = this.getServerStatsOf(servers.getServiceId(),server.getId());
				if (stat != null) {
					// there already exists stats for the server
					stat.getServerInstance()
						.setAvailable(server.isAvailable());
				} else {
					// there does NOT exists stats for the server
					stat = new LoadBalancedBackendServerStats(serviceId,
															  server,
															  _metricRegistry);
				}
			}
			// tick all the servers and remove from list if requested
			Collection<LoadBalancedBackEndServerID> serversToRemove = FluentIterable.from(this.getServerStatsOf(serviceId))
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
			for (LoadBalancedBackEndServerID serverId : serversToRemove) {
				_serverStats.remove(new LoadBalancedBackendServerStatsKey(serviceId,serverId));
			}
		} catch (Exception e) {
			log.error("[load balancer] > Exception updating the server list", e);
		} finally {
			_updatingServer.set(false);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public LoadBalancedServerList getServerListOf(final LoadBalancedServiceID serviceId) {
		return _serverList.get(serviceId);
	}
	public Collection<LoadBalancedBackendServerStats> getServerStatsOf(final LoadBalancedServiceID serviceId) {
		return this.getServerStatsWithKeyMatching(new Predicate<LoadBalancedBackendServerStatsKey>() {
															@Override
															public boolean apply(final LoadBalancedBackendServerStatsKey key) {
																return key.belongsTo(serviceId);
															}
												  });
	}
	public LoadBalancedBackendServerStats getServerStatsOf(final LoadBalancedServiceID serviceId,final LoadBalancedBackEndServerID serverId) {
		return _serverStats.get(new LoadBalancedBackendServerStatsKey(serviceId,serverId));
	}
	public Collection<LoadBalancedBackendServerStats> getServerStatsWithKeyMatching(final Predicate<LoadBalancedBackendServerStatsKey> keyPred) {
		return FluentIterable.from(_serverStats.entrySet())
							 .filter(new Predicate<Map.Entry<LoadBalancedBackendServerStatsKey,LoadBalancedBackendServerStats>>() {
												@Override
												public boolean apply(final Entry<LoadBalancedBackendServerStatsKey,LoadBalancedBackendServerStats> me) {
													return keyPred.apply(me.getKey());
												}
								 
									  })
							 .transform(new Function<Map.Entry<LoadBalancedBackendServerStatsKey,LoadBalancedBackendServerStats>,LoadBalancedBackendServerStats>() {
												@Override
												public LoadBalancedBackendServerStats apply(final Entry<LoadBalancedBackendServerStatsKey,LoadBalancedBackendServerStats> me) {
													return me.getValue();
												}
								 
										 })
							 .toList();
	}
}
