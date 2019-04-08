package r01f.facets;

import r01f.guids.CommonOIDs.TenantID;

/**
 * Every model object which has a {@link TenantID} should implement this interface
 */
public interface HasTenantID
	     extends Facet {
	/**
	 * gets the tenant id
	 * @return
	 */
	public TenantID getTenantId();

}