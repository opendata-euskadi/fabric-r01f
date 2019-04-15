package r01f.rest.resources.delegates;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.Response;

import lombok.experimental.Accessors;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.OIDs;
import r01f.guids.PersistableObjectOID;
import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceException;
import r01f.rest.RESTOperationsResponseBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.CRUDServicesForVersionableModelObject;

/**
 * Base type for REST services that encapsulates the common CRUD ops
 * @param <M>
 * @param <F>
 * @param <I>
 */
@Accessors(prefix="_")
public class RESTVersionableCRUDDelegateBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet> 
     extends RESTCRUDDelegateBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTVersionableCRUDDelegateBase(final Class<M> modelObjectType,
									   	   final CRUDServicesForModelObject<O,M> persistenceServices) {
		super(modelObjectType,
			  persistenceServices);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads the entity with the oid and version provided
	 * @param securityContext
	 * @param resourcePath
	 * @param oid
	 * @param version
	 * @return
	 */
	public Response load(final SecurityContext securityContext,final String resourcePath,
						 final VersionIndependentOID oid,final VersionOID version) {
		O vOid = OIDs.createOIDForVersionableModelObject(_modelObjectType,
														 oid,version);
		return super.load(securityContext,resourcePath,
				   		  vOid);
	}
	/**
	 * Loads active version at a provided date
	 * @param securityContext
	 * @param resourcePath
	 * @param oid
	 * @param date
	 * @return
	 * @throws PersistenceException 
	 */
	@SuppressWarnings("unchecked")
	public Response loadActiveVersionAt(final SecurityContext securityContext,final String resourcePath,
						 				final VersionIndependentOID oid,final Date date) throws PersistenceException {
		CRUDResult<M> loadResult = this.getCRUDServicesAs(CRUDServicesForVersionableModelObject.class)
											.loadActiveVersionAt(securityContext,
												  				 oid,date);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
															.at(URI.create(resourcePath))
															.build(loadResult);
		return outResponse;
	}
	/**
	 * Loads work version 
	 * @param securityContext
	 * @param resourcePath
	 * @param oid
	 * @return
	 * @throws PersistenceException 
	 */
	@SuppressWarnings("unchecked")
	public Response loadWorkVersion(final SecurityContext securityContext,final String resourcePath,
						 		    final VersionIndependentOID oid) throws PersistenceException {
		CRUDResult<M> loadResult = this.getCRUDServicesAs(CRUDServicesForVersionableModelObject.class)
												.loadWorkVersion(securityContext,
												       	   		 oid);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
															.at(URI.create(resourcePath))
															.build(loadResult);
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads the entity with the oid and version provided
	 * @param securityContext
	 * @param resourcePath
	 * @param oid
	 * @param version
	 * @return
	 */
	public Response delete(final SecurityContext securityContext,final String resourcePath,
						   final VersionIndependentOID oid,final VersionOID version) {
		O vOid = OIDs.createOIDForVersionableModelObject(_modelObjectType,
														 oid,version);
		return super.delete(securityContext,resourcePath,
				   		    vOid);
	}
	/**
	 * Removes all db record versions
	 * @param securityContext
	 * @param resourcePath
	 * @param entityOid
	 * @param version
	 * @return
	 * @throws PersistenceException 
	 */
	@SuppressWarnings("unchecked")
	public Response deleteAllVersions(final SecurityContext securityContext,final String resourcePath,
						   			  final VersionIndependentOID entityOid) throws PersistenceException {
		CRUDOnMultipleResult<M> deleteResults = this.getCRUDServicesAs(CRUDServicesForVersionableModelObject.class)
																	.deleteAllVersions(securityContext,
																				       entityOid);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
															.at(URI.create(resourcePath))
															.build(deleteResults);	
		return outResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Activates a version
	 * @param securityContext
	 * @param resourcePath
	 * @param entityToBeActivated
	 * @return
	 * @throws PersistenceException 
	 */
	@SuppressWarnings("unchecked")
	public Response activateVersion(final SecurityContext securityContext,final String resourcePath,
						 			final M entityToBeActivated) throws PersistenceException {
		CRUDResult<M> activationResult = this.getCRUDServicesAs(CRUDServicesForVersionableModelObject.class)
													.activate(securityContext,
															  entityToBeActivated);
		Response outResponse = RESTOperationsResponseBuilder.crudOn(_modelObjectType)
															.at(URI.create(resourcePath))
															.build(activationResult);
		return outResponse;
	}
}
