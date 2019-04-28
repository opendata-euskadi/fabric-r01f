package r01f.model;

/**
 * A model object summary used when returning persistence find results
 * @param <M>
 */
public interface SummarizedModelObject<M extends ModelObject>
		 extends SummarizedObject {
	/**
	 * Return the model object type
	 * @return
	 */
	public Class<M> getModelObjectType();
}
