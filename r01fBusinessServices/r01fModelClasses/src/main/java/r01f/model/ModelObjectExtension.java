package r01f.model;



public interface ModelObjectExtension<M extends ExtensibleModelObject<?>> {
	/**
	 * Extends a model object that implements {@link ExtensibleModelObject}
	 * @param extensible 
	 */
	public void extend(final M extensible);
	/**
	 * Returns the extension typed
	 * @param type
	 * @return
	 */
	public <E extends ModelObjectExtension<M>> E as(final Class<E> type);
	/**
	 * @return the extended model object
	 */
	public M getExtendedModelObj();
}
