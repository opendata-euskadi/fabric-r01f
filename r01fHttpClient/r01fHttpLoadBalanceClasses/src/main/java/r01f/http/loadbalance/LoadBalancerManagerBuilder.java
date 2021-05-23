package r01f.http.loadbalance;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;
import r01f.http.loadbalance.balancer.LoadBalancer;
import r01f.http.loadbalance.balancer.LoadBalancerRandomImpl;
import r01f.http.loadbalance.serverlist.LoadBalancedServerList;
import r01f.http.loadbalance.serverlist.LoadBalancedServerListFromConfig;
import r01f.types.url.Url;

@NoArgsConstructor(access=AccessLevel.PACKAGE)
public abstract class LoadBalancerManagerBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderServersStep {
		private final LoadBalancedServiceID _serviceId;
		
		public LoadBalancerManagerBuilderStrategyStep withServers(final Url...urls) {
			return this.withServerList(new LoadBalancedServerListFromConfig(_serviceId,
																		    FluentIterable.from(urls)
																		    		.transform(new Function<Url,LoadBalancedBackEndServer>() {
																										@Override
																										public LoadBalancedBackEndServer apply(final Url url) {
																											return new LoadBalancedBackEndServer(_serviceId,
																																		  url);
																										}
																							   })
																					.toList()));
		}
		public LoadBalancerManagerBuilderStrategyStep withServerList(final LoadBalancedServerList serverList) {
			return new LoadBalancerManagerBuilderStrategyStep(_serviceId,
															  serverList);
		}
	}
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderStrategyStep {
		private final LoadBalancedServiceID _serviceId;
		private final LoadBalancedServerList _serverList;
		
		public LoadBalancerManagerBuilderMetricsRegistryStep withRandomLoadBalancing() {
			return this.withLoadBalancer(new LoadBalancerRandomImpl());
		}
		public LoadBalancerManagerBuilderMetricsRegistryStep withLoadBalancer(final LoadBalancer loadBalancer) {
			return new LoadBalancerManagerBuilderMetricsRegistryStep(_serviceId,
																	 _serverList,
																	 loadBalancer);
		}
	}
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderMetricsRegistryStep {
		private final LoadBalancedServiceID _serviceId;
		private final LoadBalancedServerList _serverList;
		private final LoadBalancer _loadBalancer;
		
		public LoadBalancerManagerBuilderRefreshIntervalStep usingMetricRegistry(final MetricRegistry metricRegistry) {
			return new LoadBalancerManagerBuilderRefreshIntervalStep(_serviceId,
																	 _serverList,
																	 _loadBalancer,
																	 metricRegistry);
		}
		public LoadBalancerManagerBuilderRefreshIntervalStep usingNewMetricRegistry() {
			return this.usingMetricRegistry(new MetricRegistry());
		}
	}
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderRefreshIntervalStep {
		private final LoadBalancedServiceID _serviceId;
		private final LoadBalancedServerList _serverList;
		private final LoadBalancer _loadBalancer;
		private final MetricRegistry _metricsRegistry;
		
		public LoadBalancerManagerBuilderhBuildStep withRefreshIntervalInMillis(final long refreshIntervalInMillis){
			return new LoadBalancerManagerBuilderhBuildStep(_serviceId,
															_serverList,
															_loadBalancer,
															_metricsRegistry,
															refreshIntervalInMillis);
		}
		public LoadBalancerManagerBuilderhBuildStep withDefaultRefreshInterval() {
			return this.withRefreshIntervalInMillis(LoadBalancerManager.DEFAULT_REFRESH_INTERVAL_IN_MILLIS);
		}
	}
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderhBuildStep {
		private final LoadBalancedServiceID _serviceId;
		private final LoadBalancedServerList _serverList;
		private final LoadBalancer _loadBalancer;
		private final MetricRegistry _metricsRegistry;
		private final Long _refreshIntervalInMillis;
		
		public LoadBalancerManager build() {
			return new LoadBalancerManager(_serviceId,
										   _serverList, 
										   _loadBalancer, 
										   _metricsRegistry, 
										   _refreshIntervalInMillis);
		}
	}
}