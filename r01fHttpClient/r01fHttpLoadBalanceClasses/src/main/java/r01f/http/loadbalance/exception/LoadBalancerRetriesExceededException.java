package r01f.http.loadbalance.exception;

import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;

/**
 * Thrown when the maximum number of retries has been exhausted while
 * attempting to perform a remote action against a service cluster.
 */
public class LoadBalancerRetriesExceededException 
     extends Exception {

	private static final long serialVersionUID = 5783685794069347822L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public LoadBalancerRetriesExceededException(final LoadBalancedServiceID service, final long retries) {
        super(String.format("[load balancer]: Max retries <%d> for service <%s> was exceeded", 
        					retries,service));
    }
}
