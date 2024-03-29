package r01f.model;

/**
 * An model object extended with the {@link ModelObjectExtension}
 * @param <E>
 */
public interface ExtendedModelObject<E extends ModelObjectExtension<?>> 
		 extends ModelObject {
	/**
	 * Returns the content model object's extension
	 * This method is usually used at an aspect to get access to the extension and 
	 * hand it a reference to the extended object
	 *  	extensible.getExtension().extend(extensible);
	 * @return
	 */
	public E getExtension();
}
