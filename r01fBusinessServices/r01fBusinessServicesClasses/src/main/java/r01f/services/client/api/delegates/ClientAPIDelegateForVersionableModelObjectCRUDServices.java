package r01f.services.client.api.delegates;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.inject.Provider;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
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
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForVersionableModelObject;
import r01f.types.dirtytrack.DirtyTrackAdapter;

/**
 * Delegate type for CRUD operations
 * @param <O>
 * @param <M>
 */
@Slf4j
public abstract class ClientAPIDelegateForVersionableModelObjectCRUDServices<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
	 		  extends ClientAPIServiceDelegateBase<CRUDServicesForVersionableModelObject<O,M>> {
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR 
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForVersionableModelObjectCRUDServices(final Provider<SecurityContext> securityContextProvider,
																  final Marshaller modelObjectsMarshaller,
																  final CRUDServicesForVersionableModelObject<O,M> services) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads a version whose activation start date is NOT null and it's lower than the provided date
	 * @param oid the record identifier
	 * @param date the date
	 * @return the record
	 * @throws PersistenceException 
	 */
	public M loadActiveVersionAt(final VersionIndependentOID oid,final Date date) throws PersistenceException {
		// [1] - Call services layer
		CRUDResult<M> recordLoadOpResult = this.getServiceProxy()
													.loadActiveVersionAt(this.getSecurityContext(),
					   		  							   		   		 oid,date);
		log.debug(recordLoadOpResult.debugInfo().toString());
		
		// [2] - Return record
		M outRecord = recordLoadOpResult.getOrThrow();
		ClientAPIModelObjectChangesTrack.startTrackingChangesOnLoaded(outRecord);
		
		return outRecord;
	}
	/**
	 * Loads a version whose activation start date is NOT null and it's lower than the provided date	 
	 * throws a {@link PersistenceException} if the requested entity is NOT found
	 * This method simply returns null and does NOT throw a {@link PersistenceException} if the requestedRecord
	 * is NOT found
	 * @param oid
	 * @param date
	 * @return
	 * @throws PersistenceException
	 */
	public M loadActiveVersionAtOrNull(final VersionIndependentOID oid,final Date date) throws PersistenceException {
		M outRecord = null;
		try {
			outRecord = this.loadActiveVersionAt(oid,date);
		} catch(PersistenceException persistEx) {
			if (!persistEx.isEntityNotFound()) throw persistEx;  
		}
		return outRecord;
	}
	/**
	 * Loads the active version right now: it's activation start date is NOT null, it's lower than the current date and the activation end date is NULL
	 * @param oid the record identifier
	 * @return the record
	 * @throws PersistenceException 
	 */
	public M loadActiveVersion(final VersionIndependentOID oid) throws PersistenceException {
		return this.loadActiveVersionAt(oid,
								  	    new Date());		// right now
	}
	/**
	 * Loads the active version right now: it's activation start date is NOT null, it's lower than the current date and the activation end date is NULL
	 * throws a {@link PersistenceException} if the requested entity is NOT found
	 * This method simply returns null and does NOT throw a {@link PersistenceException} if the requestedRecord
	 * is NOT found
	 * @param oid
	 * @return
	 * @throws PersistenceException
	 */
	public M loadActiveVersionOrNull(final VersionIndependentOID oid) throws PersistenceException {
		M outRecord = null;
		try {
			outRecord = this.loadActiveVersion(oid);
		} catch(PersistenceException persistEx) {
			if (!persistEx.isEntityNotFound()) throw persistEx;  
		}
		return outRecord;
	}
	/**
	 * Checks the existence of an active entity at a date: it's activation start date is NOT null and it's lower than the provided date
	 * @param oid
	 * @param date
	 * @return
	 * @throws PersistenceException
	 */
	public boolean existsActiveVersionAt(final VersionIndependentOID oid,final Date date) throws PersistenceException {
		return this.loadActiveVersionAtOrNull(oid,date) != null;
	}
	/**
	 * Loads the work version -if it exists-: it's activation start date is NOT null and lower than the current date and it's activation end date is NULL
	 * @param oid the record identifier
	 * @return the record
	 * @throws PersistenceException 
	 */
	public M loadWorkVersion(final VersionIndependentOID oid) throws PersistenceException {
		// [1] - Call services layer
		CRUDResult<M> recordLoadOpResult = this.getServiceProxy()
													.loadWorkVersion(this.getSecurityContext(),
					   		  							     	  	 oid);
		log.debug(recordLoadOpResult.debugInfo().toString());
		
		// [2] - Return record
		M outRecord = recordLoadOpResult.getOrThrow();
		ClientAPIModelObjectChangesTrack.startTrackingChangesOnLoaded(outRecord);
		
		return outRecord;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copies an object version to another. A copy operation is just a the creation of a clone of an existing object
	 * @param srcOid
	 * @param dstVersion
	 * @return the final object version 
	 * @throws PersistenceException 
	 */
	@SuppressWarnings("unchecked")
	public M copyVersion(final O srcOid,
						 final VersionOID dstVersion) throws PersistenceException {
		M outDstEntity = null;
		
		// [1] - Call services layer
		CRUDResult<M> srcEntityLoad = this.getServiceProxy()
										  .load(this.getSecurityContext(),
											    srcOid);
		// [2] - Create another entity (with another oid) from the source entity
		if (srcEntityLoad.hasSucceeded()) {
			// Get the source entity and change it's oid --> it's the destination entity
			M srcEntity = srcEntityLoad.getOrThrow();
			O dstOid = OIDs.createOIDForVersionableModelObject((Class<M>)srcEntity.getClass(),
															   srcOid.getOid(),dstVersion);
			srcEntity.setOid(dstOid);							// change the oid = the same version independent oid and other version oid
			srcEntity.asVersionable().setStartOfUseDate(null);	// ... and the version info
			srcEntity.asVersionable().setEndOfUseDate(null);
			srcEntity.asVersionable().setNextVersion(null);
			DirtyTrackAdapter.adapt(srcEntity)
							 .getTrackingStatus().setThisNew(true);		// important!! mark the object as NEW
			
			// the src object with it's oid changed and marked as NEW is persisted: a new entity is created
			CRUDResult<M> dstEntityCreate = this.getServiceProxy()
												.create(this.getSecurityContext(),
														srcEntity);	
			// Return or throw
			if (dstEntityCreate.hasSucceeded()) {
				if (dstEntityCreate.asCRUDOK().hasBeenCreated()) {					//asOK(CRUDOK.class)
					// OK
					outDstEntity = dstEntityCreate.getOrThrow();
				} else {
					// the operation did NOT result on an entity creation... normally it's a developer error
					throw new IllegalStateException(Throwables.message("A copy of a {} entity with oid={} was tried but the operation did NOT result on an entity creation",
															  		   srcEntity.getClass(),dstOid));
				}
			} else {
				// Throw the creation exception
				dstEntityCreate.asCRUDError().throwAsPersistenceException();		// asError(CRUDError.class)
			}
		} else {
			srcEntityLoad.asCRUDError().throwAsPersistenceException();				// asError(CRUDError.class)
		}
		return outDstEntity;
	}
	/**
	 * Deletes all versions of a record
	 * This method returns a {@link RecordPersistenceOperationResult} which contains a {@link Set} of the 
	 * deleted versions.
	 * <ul>
	 * 		<li>If all versions could be deleted, the failed attribute of the returned {@link RecordPersistenceOperationResult} is false
	 * 			and the {@link Set} contains all records deleted
	 * 		</li>
	 * 		<li>If NOT all versions could be deleted, the failed attribute of the returned {@link RecordPersistenceOperationResult} is true
	 * 			and the {@link Set} contains ONLY the deleted records<br/>
	 * 			In order to get the not deleted versions a call to loadAllVersions could be made afterwards
	 * 		<li>
	 * </ul> 
	 * @param oid the identifier of the record whose versions are to be deleted
	 * @return the deleted versions
	 * @throws PersistenceException 
	 */
	public Collection<M> deleteAllVersions(final VersionIndependentOID oid) throws PersistenceException {
		// [1] - Call services layer
		CRUDOnMultipleResult<M> deletionOpsResults = this.getServiceProxy()
															.deleteAllVersions(this.getSecurityContext(),
								   								   	  	 	   oid);
		log.debug(deletionOpsResults.debugInfo().toString());
		
		// [2] - Return the deleted records
		if (deletionOpsResults.haveSomeFailed()) {
			log.error("Some entities with oid={} versions delete operations have failed: {} OK and {} ERROR",
					  oid,deletionOpsResults.getNumberOfOperationsOK(),deletionOpsResults.getNumberOfOperationsNOK());
		}
		Collection<M> outDeletedRecords = deletionOpsResults.getAllSuccessfulOrThrow();
		ClientAPIModelObjectChangesTrack.startTrackingChangesOnDeleted(outDeletedRecords);
		
		return outDeletedRecords;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Activates an entity, that's:
	 * <ul>
	 * 		<li>Finds the currently active version (if there's any) and sets its activation end date to this moment</li>
	 * 		<li>Sets the entity activation start date to this moment (and null activation end date</li>
	 * </li>
	 * If the entity is active (has a not null activation start date), an {@link IllegalStateException} is raised
	 * @param entityToBeActivated
	 * @return
	 * @throws PersistenceException 
	 */
	public M activate(final O entityToBeActivatedOid) throws PersistenceException {
		M outActivatedEntity = null;
		
		// [1] - Load the entity to be activated
		CRUDResult<M> entityToBeActivatedLoad = this.getServiceProxy()
													.load(this.getSecurityContext(),
														  entityToBeActivatedOid);
		// [2] - Activate
		outActivatedEntity = this.activate(entityToBeActivatedLoad.getOrThrow());
		return outActivatedEntity;
	}
	/**
	 * Activates an entity, that's:
	 * <ul>
	 * 		<li>Finds the currently active version (if there's any) and sets its activation end date to this moment</li>
	 * 		<li>Sets the entity activation start date to this moment (and null activation end date</li>
	 * </li>
	 * If the entity is active (has a not null activation start date), an {@link IllegalStateException} is raised
	 * @param entityToBeActivated
	 * @return
	 * @throws PersistenceException 
	 */
	public M activate(final M entityToBeActivated) throws PersistenceException {
		// [1] - Call services layer
		CRUDResult<M> activationOpResult = this.getServiceProxy()
													.activate(this.getSecurityContext(),
		  	      									  	   	  entityToBeActivated);
		log.debug(activationOpResult.debugInfo().toString());
		
		// [2] - Return activated record
		M outActivatedRecord = activationOpResult.getOrThrow();
		ClientAPIModelObjectChangesTrack.startTrackingChangesOnSaved(outActivatedRecord);
		
		return outActivatedRecord;
	}
}

