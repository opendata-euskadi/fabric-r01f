package r01f.persistence.db;

/**
 * Interface for {@link DBEntity}s that have a colum storing some kind of 
 * descriptor for the entity (ie an XML or JSON)
 */
public interface DBEntityHasModelObjectDescriptor {
	/**
	 * Sets the descriptor
	 * @param descriptor
	 */
	public void setDescriptor(final String descriptor);
	/**
	 * @return a descriptor
	 */
	public String getDescriptor();
}
