package r01f.http.loadbalance.balancer;

public class LoadBalancerContextVoidImpl 
  implements LoadBalancerContext {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public <C extends LoadBalancerContext> C as(final Class<C> type) {
		return (C)this;
	}
}
