package r01f.services.interfaces;

import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;

/**
 * Marker interface for a service interface on a model object
 * @param <O>
 * @param <M>
 */
public interface ServiceInterfaceForModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
		 extends ServiceInterface {
	// just a marker interface
}
