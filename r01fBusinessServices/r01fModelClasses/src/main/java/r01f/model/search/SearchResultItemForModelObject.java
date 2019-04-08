package r01f.model.search;

import r01f.model.IndexableModelObject;
import r01f.model.TrackableModelObject.HasTrackableFacet;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;



/**
 * Marker interface for search result items
 */
public interface SearchResultItemForModelObject<M extends IndexableModelObject>
		 extends SearchResultItem,
		 		 HasEntityVersion,
		 		 HasNumericID,
		 		 HasTrackableFacet {
/////////////////////////////////////////////////////////////////////////////////////////
//  MODEL OBJECT TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the model object type
	 */
	public Class<? extends M> getModelObjectType();
	/**
	 * Sets the model object type with no guarantee that a {@link ClassCastException} is thrown 
	 * if the model object's type is not the expected
	 * @param modelObject
	 */
	public <U extends IndexableModelObject> void unsafeSetModelObjectType(final Class<U> modelObjectType);
	/**
	 * @return a code for the model object type
	 */
	public long getModelObjectTypeCode();
	/**
	 * Sets a model object type code
	 * @param code
	 */
	public void setModelObjectTypeCode(final long code);
}
