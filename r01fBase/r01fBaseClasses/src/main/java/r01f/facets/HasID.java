package r01f.facets;

import r01f.guids.OID;

/**
 * Every model object which has an ID should implement this interface
 * @param <ID> the id type
 */
public interface HasID<ID extends OID>
	     extends Facet {
	
	/**
	 * gets the id
	 * @return the id
	 */
	public ID getId();
	/**
	 * Sets the id
	 * @param id the id
	 */
	public void setId(ID id);
	/**
	 * Sets the id with no guarantee that a {@link ClassCastException} is thrown 
	 * if the provided id does not match the expected type
	 * @param id
	 */
	public void unsafeSetId(final OID id);
}
