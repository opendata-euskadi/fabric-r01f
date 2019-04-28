package r01f.model.search;

import java.util.Collection;

import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.model.metadata.HasTypesMetaData;


/**
 * Interface for Search filters
 */
public interface SearchFilterForModelObject 
	     extends SearchFilter {
/////////////////////////////////////////////////////////////////////////////////////////
//  FILTERED MODEL OBJECT TYPES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the model object types to be filtered
	 */
	public Collection<Class<? extends ModelObject>> getFilteredModelObjectTypes();
	/**
	 * Returns the model object type codes to be filtered
	 * @param hasTypesMetaData
	 * @return
	 */
	public Collection<Long> getFilteredModelObjectTypesCodesUsing(final HasTypesMetaData hasTypesMetaData);
	/**
	 * Sets the model object types to be filtered
	 * @param modelObjectType
	 */
	public void setModelObjectTypesToBeFiltered(final Collection<Class<? extends ModelObject>> modelObjectTypes);
/////////////////////////////////////////////////////////////////////////////////////////
//  OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * If retrieving a specific a record from the search index
	 * (any other filter condition is ignored if oid is present) 
	 * @return
	 */
	public <O extends OID> O getOid();
	/**
	 * If retrieving a specific a record from the search index
	 * (any other filter condition is ignored if oid is present)
	 * @param oid  
	 */
	public <O extends OID> void setOid(final O oid);
}
