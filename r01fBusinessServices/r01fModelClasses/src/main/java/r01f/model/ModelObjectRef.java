package r01f.model;

import java.io.Serializable;

import r01f.types.CanBeRepresentedAsString;

/**
 * A reference to a parent model object
 */
public interface ModelObjectRef<M extends ModelObject> 
	     extends CanBeRepresentedAsString,
	     		 Serializable {
	//	just a marker interface
}
