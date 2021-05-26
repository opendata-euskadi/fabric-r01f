package r01f.http.loadbalance.exception;

import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;

/**
 * Thrown when there is no server available in a service cluster to perform a
 * remote action.
 */
public class LoadBalancerNOServerAvailableException
     extends Exception {

    private static final long serialVersionUID = 1158842315418519252L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    public LoadBalancerNOServerAvailableException(final LoadBalancedServiceID service) {
        super(String.format("[Load Balancer]: No servers available for service <%s>",service));
    }
}
