package r01f.http.loadbalance;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowReservoir;
import com.codahale.metrics.SlidingWindowReservoir;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Accessors(prefix="_")
public class LoadBalancedBackendServerStats {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final LoadBalancedBackEndServer _serverInstance;
	@Getter private final MetricRegistry _metrics;
	@Getter private final long _errorThreshold;
	
	protected final String objectId = UUID.randomUUID().toString();

	// reported metrics
	protected final Counter _openRequestCounter;
	protected final Counter _openSessionsCounter;
	protected final Meter _errorMeter;
	protected final Meter _sentMessageMeter;
	protected final Meter _receivedMessageMeter;
	protected final Counter circuitBreakerTrippedCounter;
	protected final Gauge<Double> _circuitBreakerTimeGauge;
	protected final Histogram _latencyHistogram;

	// internal metric tracking
	protected SlidingTimeWindowReservoir _errorsPerSecond;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LoadBalancedBackendServerStats(final LoadBalancedBackEndServer server,
										  final MetricRegistry metricRegistry) {
		this(server,
			 metricRegistry,
			 10);
	}
	public LoadBalancedBackendServerStats(final LoadBalancedBackEndServer server,
										  final MetricRegistry metricRegistry,
										  final long errorThreshold) {
		_serverInstance = server;
		_metrics = metricRegistry;
		_errorThreshold = errorThreshold;
		
		// Init metrics
		String serverId = _serverInstance.getId().asString();
		
		_openRequestCounter = _metrics.counter(MetricRegistry.name(serverId,
																   "open-requests"));
		_openSessionsCounter = _metrics.counter(MetricRegistry.name(_serverInstance.getId().asString(),
																	"open-sessions"));
		_sentMessageMeter = _metrics.meter(MetricRegistry.name(serverId,
															   "sent-messages"));
		_receivedMessageMeter = _metrics.meter(MetricRegistry.name(serverId,
																   "received-messages"));
		_errorMeter = _metrics.meter(MetricRegistry.name(serverId,
														 "errors"));
		this.circuitBreakerTrippedCounter = _metrics.counter(MetricRegistry.name(serverId,
																				 "short-circuit-tripped"));
		_circuitBreakerTimeGauge = new Gauge<Double>() {
											@Override
											public Double getValue() {
												return _serverInstance != null ? _serverInstance.getCircuitBreakerRemainingTime()
																			   : 0.0;
											}
								  };
		_metrics.register(MetricRegistry.name(objectId,serverId,"short-circuit-time-remaining"),
											  _circuitBreakerTimeGauge);
		_latencyHistogram = new Histogram(new SlidingWindowReservoir(100));
		_metrics.register(MetricRegistry.name(objectId,serverId,"latency"),
											  _latencyHistogram);

		// internal metrics
		_errorsPerSecond = new SlidingTimeWindowReservoir(1,TimeUnit.SECONDS,
														  new Clock() {
																private final long start = System.nanoTime();
																
																@Override
																public long getTick() {
																	return System.nanoTime() - start;
																}
														  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void incrementOpenRequests() {
		_openRequestCounter.inc();
	}
	public void decrementOpenRequests() {
		_openRequestCounter.dec();
	}
	public long getOpenRequestCount() {
		return _openRequestCounter.getCount();
	}
	public void recordLatency(final long latencyInMs) {
		_latencyHistogram.update(latencyInMs);
	}
	public void incrementOpenSessions() {
		_openSessionsCounter.inc();
	}
	public void decrementOpenSessions() {
		_openSessionsCounter.dec();
	}
	public long getOpenSessionsCount() {
		return _openSessionsCounter.getCount();
	}
	public void incrementErrors() {
		_errorMeter.mark();
		_errorsPerSecond.update(1);

		// should we short circuit the server?
		int errorCount = _errorsPerSecond.size();
		if ( errorCount >= _errorThreshold) {
			log.warn("Short circuiting <{},{}> because too many errors <{}>", 
					 this.getServerInstance().getId(), this.getServerInstance().getUrl(),errorCount);
			circuitBreakerTrippedCounter.inc();
			_serverInstance.triggerCircuitBreaker();
		}
	}
	public void incrementSentMessages() {
		_sentMessageMeter.mark();
	}
	public double getSentMessagesPerSecond() {
		return _sentMessageMeter.getOneMinuteRate();
	}
	public void incrementReceivedMessages() {
		_receivedMessageMeter.mark();
	}
	public double getReceivedMessagesPerSecond() {
		return _receivedMessageMeter.getOneMinuteRate();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public int hashCode() {
		return _serverInstance.hashCode();
	}
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof LoadBalancedBackendServerStats)) return false;

		LoadBalancedBackendServerStats other = (LoadBalancedBackendServerStats) o;
		if (!_serverInstance.equals(other._serverInstance)) return false;
		return true;
	}
}
