package r01f.model.metadata;

import java.util.Collection;

import r01f.model.ModelObject;

/**
 * Interface for types that describes {@link ModelObject}s
 */

public interface HasMetaDataForModelObject 
		 extends HasFieldsMetaData {
	
	public long getTypeCode();
	public Class<?> getType();
	public Collection<Long> getTypeFacets();
	public long getNumericId();
}
