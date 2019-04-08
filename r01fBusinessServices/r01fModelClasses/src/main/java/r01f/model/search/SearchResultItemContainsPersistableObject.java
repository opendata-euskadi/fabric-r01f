package r01f.model.search;

import r01f.facets.HasOID;
import r01f.guids.PersistableObjectOID;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableModelObject;

/**
 * A {@link SearchResultItemForModelObject} that wraps (or contains) a model object's instance
 * @param <M>
 */
public interface SearchResultItemContainsPersistableObject<O extends PersistableObjectOID,M extends PersistableModelObject<O> & IndexableModelObject>
		 extends SearchResultItemForModelObject<M>,
		 		 HasOID<O> {
	/**
	 * @return the model object
	 */
	public M getModelObject();
	/**
	 * Sets the model object
	 * @param modelObject
	 */
	public void setModelObject(final M modelObject);
	/**
	 * Sets the model object with no guarantee that a {@link ClassCastException} is thrown 
	 * if the model object's type is not the expected
	 * @param modelObject
	 */
	public <U extends IndexableModelObject> void unsafeSetModelObject(final U modelObject);	
}
