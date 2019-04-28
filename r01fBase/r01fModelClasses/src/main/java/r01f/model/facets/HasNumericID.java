package r01f.model.facets;


/**
 * Every model object which has a numeric id should implement this interface
 */
public interface HasNumericID
	     extends ModelObjectFacet {
	/**
	 * gets the numeric id
	 * @return the id
	 */
	public long getNumericId();
	/**
	 * Sets the numeric id
	 * @param id the numeric id
	 */
	public void setNumericId(final long numericId);
}
