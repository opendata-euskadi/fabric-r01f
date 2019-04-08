package r01f.model;

/**
 * Interface for types that store the model object's type  code
 */
public interface HasModelObjectTypeCode {
	/**
	 * @return the model object's type code 
	 */
	public long getModelObjectTypeCode();
	/**
	 * Sets the model object's type code
	 * @param typeCode
	 */
	public void setModelObjectTypeCode(final long typeCode);
}
