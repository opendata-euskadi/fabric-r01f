package r01f.rest.resources.delegates;

import java.net.URI;

import javax.ws.rs.core.Response;

import lombok.experimental.Accessors;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceException;
import r01f.rest.RESTOperationsResponseBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;

/**
 * Base type for REST services that encapsulates the common CRUD ops>
 */
@Accessors(prefix="_")
public abstract class RESTCRUDDelegateBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>> 
	          extends RESTDelegateForModelObjectBase<M> { 
/////////////////////////////////////////////////////////////////////////////////////////
//  NOT INJECTED STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final CRUDServicesForModelObject<O,M> _persistenceServices;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected <C extends CRUDServicesForModelObject<O,M>> C getCRUDServicesAs(@SuppressWarnings("unused") final Class<C> type) {
		return (C)_persistenceServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTCRUDDelegateBase(final Class<M> modelObjectType,
							    final CRUDServicesForModelObject<O,M> persistenceServices) {
		super(modelObjectType);
		_persistenceServices = persistenceServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PERSISTENCE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads a db entity 
	 * @param securityContext
	 * @param resourcePath
	 * @param oid
	 * @return
	 * @throws PersistenceException 
	 */
	public Response load(final SecurityContext securityContext,final String resourcePath,
						 final O oid) throws PersistenceException {
		
		CRUDResult<M> loadResult = _persistenceServices.load(securityContext,
									  					     oid);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
														    .at(URI.create(resourcePath))
															.build(loadResult);
		return outResponse;
	}
	/**
	 * Creates a db entity
	 * @param securityContext
	 * @param resourcePath
	 * @param modelObject
	 * @return
	 * @throws PersistenceException 
	 */ 
	public Response create(final SecurityContext securityContext,final String resourcePath,
						   final M modelObject) throws PersistenceException {
		CRUDResult<M> createResult = _persistenceServices.create(securityContext,
										   	   					 modelObject);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
														    .at(URI.create(resourcePath))
														    .build(createResult);
		return outResponse;
	}
	/**
	 * Updates a db entity
	 * @param securityContext
	 * @param resourcePath
	 * @param modelObject
	 * @return
	 * @throws PersistenceException 
	 */ 
	public Response update(final SecurityContext securityContext,final String resourcePath,
						   final M modelObject) throws PersistenceException {
		CRUDResult<M> updateResult = _persistenceServices.update(securityContext,
										   	      				 modelObject);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
															.at(URI.create(resourcePath))
															.build(updateResult);
		return outResponse;
	}
	/**
	 * Removes a db entity
	 * @param securityContext
	 * @param resourcePath
	 * @param oid
	 * @return
	 * @throws PersistenceException 
	 */
	public Response delete(final SecurityContext securityContext,final String resourcePath,
						   final O oid) throws PersistenceException {
		CRUDResult<M> deleteResult = _persistenceServices.delete(securityContext,
																 oid);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
														    .at(URI.create(resourcePath))
															.build(deleteResult);
		return outResponse;
	}
}
