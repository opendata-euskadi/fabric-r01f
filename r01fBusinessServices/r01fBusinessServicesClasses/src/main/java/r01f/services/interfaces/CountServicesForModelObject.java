package r01f.services.interfaces;

import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CountResult;
import r01f.securitycontext.SecurityContext;

@ExposedServiceInterface
public interface CountServicesForModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
         extends ServiceInterfaceForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the total number of structures
	 * @param securityContext the user auth data & context info
	 * @return
	 */
	public CountResult<M> countAll(final SecurityContext securityContext);	
}