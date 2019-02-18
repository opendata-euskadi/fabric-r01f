package r01f.facets;

import r01f.guids.OID;

/**
 * Every model object which has an OID should implement this interface
 * @param <O> the oid type
 */
public interface HasOID<O extends OID>
	     extends Facet {
	
	/**
	 * gets the oid
	 * @return the oid
	 */
	public O getOid();
	/**
	 * Sets the oid
	 * @param oid the oid
	 */
	public void setOid(O oid);
	/**
	 * Sets the oid with no guarantee that a {@link ClassCastException} is thrown 
	 * if the provided oid does not match the expected type
	 * @param oid
	 */
	public void unsafeSetOid(final OID oid);
}
