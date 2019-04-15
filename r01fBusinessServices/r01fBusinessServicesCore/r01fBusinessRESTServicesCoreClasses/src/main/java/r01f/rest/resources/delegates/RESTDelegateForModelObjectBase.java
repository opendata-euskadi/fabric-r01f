package r01f.rest.resources.delegates;

import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.model.metadata.MetaDataDescribable;

/**
 * Base type for REST services 
 */
@Accessors(prefix="_")
public abstract class RESTDelegateForModelObjectBase<M extends ModelObject & MetaDataDescribable> 
	       implements RESTDelegate { 
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT INJECTED STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Class<M> _modelObjectType;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTDelegateForModelObjectBase(final Class<M> modelObjectType) {
		_modelObjectType = modelObjectType;
	}
}
