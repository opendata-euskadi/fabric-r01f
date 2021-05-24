package r01f.httpclient.loadbalanced;

import org.apache.http.HttpRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.http.loadbalance.balancer.LoadBalancerContext;

/**
 * Context used to help choosing a load balanced server
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public class LoadBalancerContextForHttpRequest
  implements LoadBalancerContext {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final HttpRequest _httpRequest;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public <C extends LoadBalancerContext> C as(final Class<C> type) {
		return (C)this;
	}
}
