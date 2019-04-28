package r01f.model;

import java.io.Serializable;

import r01f.model.metadata.MetaDataDescribable;

/**
 * Marker interface for Model Objects
 */
public interface ModelObject
         extends MetaDataDescribable,	// can have model object metadata
         		 Serializable {
	/* just a marker interface for model objects */
}
