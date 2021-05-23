package r01f.http.loadbalance;

import java.util.Collection;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

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
	public class LoadBalancerManagerBuilderMetricsRegistryStep {
		public LoadBalancerManagerBuilderRefreshIntervalStep usingMetricRegistry(final MetricRegistry metricRegistry) {
			return new LoadBalancerManagerBuilderRefreshIntervalStep(metricRegistry);
		}
		public LoadBalancerManagerBuilderRefreshIntervalStep usingNewMetricRegistry() {
			return this.usingMetricRegistry(new MetricRegistry());
		}
	}
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderRefreshIntervalStep {
		private final MetricRegistry _metricsRegistry;
		
		public LoadBalancerManagerBuilderStrategyStep withRefreshIntervalInMillis(final long refreshIntervalInMillis){
			return new LoadBalancerManagerBuilderStrategyStep(_metricsRegistry,
															  refreshIntervalInMillis);
		}
		public LoadBalancerManagerBuilderStrategyStep withDefaultRefreshInterval() {
			return this.withRefreshIntervalInMillis(LoadBalancerManager.DEFAULT_REFRESH_INTERVAL_IN_MILLIS);
		}
	}
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderStrategyStep {
		private final MetricRegistry _metricsRegistry;
		private final Long _refreshIntervalInMillis;
		
		public LoadBalancerManagerBuilderServersStep withRandomLoadBalancing() {
			return this.withLoadBalancer(new LoadBalancerRandomImpl());
		}
		public LoadBalancerManagerBuilderServersStep withLoadBalancer(final LoadBalancer loadBalancer) {
			return new LoadBalancerManagerBuilderServersStep(_metricsRegistry,
															 _refreshIntervalInMillis,
															 loadBalancer);
		}
	}
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public class LoadBalancerManagerBuilderServersStep {
		private final MetricRegistry _metricsRegistry;
		private final Long _refreshIntervalInMillis;
		private final LoadBalancer _loadBalancer;
		private final Collection<LoadBalancedServerList> _serverList = Lists.newArrayList();
		
		public LoadBalancerManagerBuilderServersStep withServers(final LoadBalancedServiceID serviceId,final Url... urls) {
			return this.withServers(serviceId,Lists.newArrayList(urls));
		}
		public LoadBalancerManagerBuilderServersStep withServers(final LoadBalancedServiceID serviceId,final Collection<Url> urls) {
			return this.withServerList(new LoadBalancedServerListFromConfig(serviceId,
																			FluentIterable.from(urls)
																		    		.transform(new Function<Url,LoadBalancedBackEndServer>() {
																										@Override
																										public LoadBalancedBackEndServer apply(final Url url) {
																											return new LoadBalancedBackEndServer(url);
																										}
																							   })
																					.toList()));
		}
		public LoadBalancerManagerBuilderServersStep withServerList(final LoadBalancedServerList serverList) {
			_serverList.add(serverList);
			return this;
		}
		public LoadBalancerManager build() {
			return new LoadBalancerManager(_loadBalancer, 
										   _metricsRegistry, 
										   _refreshIntervalInMillis,
										   _serverList);
		}
	}
}