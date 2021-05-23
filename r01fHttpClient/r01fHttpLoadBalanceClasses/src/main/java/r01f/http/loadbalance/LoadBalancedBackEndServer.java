package r01f.http.loadbalance;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedBackEndServerID;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;
import r01f.types.url.Url;

@Accessors(prefix="_")
public class LoadBalancedBackEndServer {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final LoadBalancedServiceID _serviceId;
	@Getter private final LoadBalancedBackEndServerID _id;
	@Getter private final Url _url;
	
	@Getter private LoadBalancedBackedServerListener _listener;
	
	@Setter private boolean _available = true;
			private boolean _lastAvailable;

	@Getter private final long _shortCircuitDuration;
			private volatile boolean _shortCircuited;
			private volatile long _shortCircuitExpiration;
			private volatile long _shortCircuitCount;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public LoadBalancedBackEndServer(final LoadBalancedServiceID serviceId,
							  		 final Url url) {
		this(serviceId,LoadBalancedBackEndServerID.from(url),
			 url,
			 30000);	// 30 sg = default short-circuit duration
	}
	public LoadBalancedBackEndServer(final LoadBalancedServiceID serviceId,
									 final Url url,
									 final long shortCircuitDuration) {
		this(serviceId,LoadBalancedBackEndServerID.from(url),
			 url,
			 shortCircuitDuration);
	}
	public LoadBalancedBackEndServer(final LoadBalancedServiceID serviceId,
									 final LoadBalancedBackEndServerID id,final Url url,
									 final long shortCircuitDuration) {
		_serviceId = serviceId;
		_id = id;
		_url = url;
		_shortCircuitDuration = shortCircuitDuration;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Is this instance expired, i.e. has not received a recently update.
	 * @return whether or not the server instance is expired
	 */
	@SuppressWarnings("static-method")
	public boolean isExpired() {
		return false;
	}
	/**
	 * Is this instance available to service requests?
	 * @return isAvailable
	 */
	public boolean isAvailable() {
		return !this.isExpired() 
			&& !this.isShortCircuited() 
			&& _available;
	}
	/**
	 * Is this instance currently in short circuit mode?
	 * @return whether or not the server instance is currently short-circuited
	 */
	public boolean isShortCircuited() {
		if (_shortCircuited) {
			long delta = System.currentTimeMillis() - _shortCircuitExpiration;
			if (delta >= 0) _shortCircuited = false;
		}
		return _shortCircuited;
	}
	/**
	 * Allow periodic bookkeeping of the instance.
	 * @return true if the server should be removed, false otherwise
	 */
	public boolean tick() {
		// notify listener if availability has changed
		boolean currentlyAvailable = this.isAvailable();
		if (currentlyAvailable != _lastAvailable) {		// changed availability status > tell the listener
			_lastAvailable = currentlyAvailable;
			if (_listener != null) _listener.onAvailabilityChange(currentlyAvailable);
		}
		return !this.isExpired();
	}
	/**
	 * trigger the circuit breaker on the server instance. 
	 * if already triggered, the amount of time the server will remain triggered will be increased
	 * exponentially.
	 */
	public void triggerCircuitBreaker() {
		long now = System.currentTimeMillis();

		// increment short circuit count for back off calculation
		if ((now - _shortCircuitExpiration) > _shortCircuitDuration) {
			_shortCircuitCount = 0;
		} else {
			_shortCircuitCount = Math.max(_shortCircuitCount + 1, 5);
		}

		// set time out using exponential back off
		long timeout = (long)(Math.pow(1.5,_shortCircuitCount) * _shortCircuitDuration);
		_shortCircuitExpiration = now + timeout;
		_shortCircuited = true;
	}
	/**
	 * Get the number of seconds until the server instance's circuit breaker is
	 * no longer tripped.
	 * @return remaining time
	 */
	public double getCircuitBreakerRemainingTime() {
		if (isShortCircuited()) {
			long delta = _shortCircuitExpiration - System.currentTimeMillis();
			if (delta > 0) {
				return delta / 1000.0;
			}
		}
		return 0.0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		int result = _id.hashCode();
		result = 31 * result + _serviceId.hashCode();
		return result;
	}
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof LoadBalancedBackEndServer)) return false;
		
		LoadBalancedBackEndServer other = (LoadBalancedBackEndServer)o;
		boolean idEqs = _id != null ? _id.is(other.getId())
									: other.getId() != null ? false
															: true;	// both null
		boolean nameEqs = _serviceId != null ? _serviceId.is(other.getServiceId())
											   : other.getServiceId() != null ? false
													   						  : true;		// both null
		return idEqs && nameEqs;
	}
}
