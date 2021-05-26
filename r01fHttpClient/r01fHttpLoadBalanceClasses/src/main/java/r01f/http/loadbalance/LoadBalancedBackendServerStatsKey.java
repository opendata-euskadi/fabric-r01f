package r01f.http.loadbalance;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedBackEndServerID;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class LoadBalancedBackendServerStatsKey 
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = -3638785769428932667L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final LoadBalancedServiceID _serviceId;
	@Getter private final LoadBalancedBackEndServerID _serverId;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean belongsTo(final LoadBalancedServiceID serviceId) {
		return _serviceId.is(serviceId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof LoadBalancedBackendServerStatsKey)) return false;
		
		LoadBalancedBackendServerStatsKey other = (LoadBalancedBackendServerStatsKey)o;
		boolean serviceIdEqs = this.getServiceId() != null && other.getServiceId() != null
									? this.getServiceId().is(other.getServiceId())
									: this.getServiceId() != null ? false
																 : true;		// both null
		boolean serverIdEqs = this.getServerId() != null && other.getServerId() != null
									? this.getServerId().is(other.getServerId())
									: this.getServerId() != null ? false
																 : true;		// both null
		return serviceIdEqs && serverIdEqs;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_serviceId,_serverId);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return Strings.customized("{};{}",
								 _serverId,_serverId);
	}
}
