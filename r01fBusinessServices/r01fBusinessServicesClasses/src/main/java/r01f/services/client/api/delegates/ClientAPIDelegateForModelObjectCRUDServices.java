package r01f.services.client.api.delegates;

import javax.inject.Provider;

import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.exceptions.Throwables;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceException;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.types.dirtytrack.DirtyTrackAdapter;

/**
 * Adapts Persistence API method invocations to the service proxy that performs the core method invocations
 * @param <O>
 * @param <M>
 */
@Slf4j
public abstract class ClientAPIDelegateForModelObjectCRUDServices<O extends PersistableObjectOID,M extends PersistableModelObject<O>> 
	          extends ClientAPIServiceDelegateBase<CRUDServicesForModelObject<O,M>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForModelObjectCRUDServices(final Provider<SecurityContext> SecurityContextProvider,
													   final Marshaller modelObjectsMarshaller,
												   	   final CRUDServicesForModelObject<O,M> services) {
		super(SecurityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LOAD
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Loads a record
	 * @param oid
	 * @return
	 * @throws PersistenceException 
	 */
	public M load(final O oid) throws PersistenceException {
		CRUDResult<M> loadOpResult = this.getServiceProxy()
												.load(this.getSecurityContext(),
	 	  		 								      oid);
		log.debug(loadOpResult.debugInfo().toString());
		
		M outRecord = loadOpResult.getOrThrow();	
		if (outRecord instanceof DirtyStateTrackable) {
			ClientAPIModelObjectChangesTrack.startTrackingChangesOnLoaded(outRecord);
		}
		
		return outRecord;
	}
	/**
	 * The normal load method throws a {@link PersistenceException} if the requested record
	 * is NOT found
	 * This method simply returns null and does NOT throw a {@link PersistenceException} if the requestedRecord
	 * is NOT found
	 * @param oid
	 * @return
	 * @throws PersistenceException
	 */
	public M loadOrNull(final O oid) throws PersistenceException {
		M outRecord = null;
		try {
			outRecord = this.load(oid);
		} catch(PersistenceException persistEx) {
			if (!persistEx.isEntityNotFound()) throw persistEx;  
		}
		return outRecord;
	}
	/**
	 * Checks a record existence
	 * @param oid
	 * @return
	 * @throws PersistenceException
	 */
	public boolean exists(final O oid) throws PersistenceException {
		return this.loadOrNull(oid) != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	SAVE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Updates a record. This method is usually used when {@link DirtyStateTrackable} aspect is NOT being used
	 * @param record
	 * @return
	 * @throws PersistenceException
	 */
	public M update(final M record) throws PersistenceException {
		// [0] - If the record is a DirtyStateTrackable instance check that the instance is NEW
		if (record instanceof DirtyStateTrackable) {
			// TODO trckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
			// FIXME rckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
//			DirtyStateTrackable trckReceivedRecord = DirtyTrackAdapter.adapt(record);
//			if (!trckReceivedRecord.getTrackingStatus().isThisNew()) throw new IllegalStateException(Throwables.message("{} instance new... maybe you have to call create() or save() method instead of update",
//																													  	record.getClass()));
			if (((DirtyStateTrackable)record).getTrackingStatus().isThisNew()) throw new IllegalStateException(Throwables.message("{} instance new... maybe you have to call create() or save() method instead of update",
																													    		  record.getClass()));
			
		}
		// [1] - Do the update
		CRUDResult<M> saveOpResult = this.getServiceProxy()
											.update(this.getSecurityContext(),
						 				   		  	record);
		M outRecord = saveOpResult.getOrThrow();
		
		// [2] - After create or update, set the dirty status to both the CORE received saved object
		// 		 AND the original object sent to the CORE just for the case the caller continues using 
		// 		 this instance instead of the received by the CORE
		_postCreateOrUpdate(record,outRecord);
		
		// [3] - return
		return outRecord;
	}
	/**
	 * Creates a record. This method is usually used when {@link DirtyStateTrackable} aspect is NOT being used
	 * @param record
	 * @return
	 * @throws PersistenceException
	 */
	public M create(final M record) throws PersistenceException {
		// [0] - If the record is a DirtyStateTrackable instance check that the instance is NEW
		if (record instanceof DirtyStateTrackable) {
			// TODO trckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
			// FIXME rckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
//			DirtyStateTrackable trckReceivedRecord = DirtyTrackAdapter.adapt(record);
//			if (!trckReceivedRecord.getTrackingStatus().isThisNew()) throw new IllegalStateException(Throwables.message("{} instance is NOT new... maybe you have to call update() or save() method instead of create",
//																													  	record.getClass()));

			if (!((DirtyStateTrackable)record).getTrackingStatus().isThisNew()) throw new IllegalStateException(Throwables.message("{} instance is NOT new... maybe you have to call update() or save() method instead of create",
																													    		   record.getClass()));
		}
		// [1] - Do the creation
		CRUDResult<M> saveOpResult = this.getServiceProxy()
											.create(this.getSecurityContext(),
						 				   		  	record);
		M outRecord = saveOpResult.getOrThrow();
		
		
		// Adapt the returned object
		// [2] - After create or update, set the dirty status to both the CORE received saved object
		// 		 AND the original object sent to the CORE just for the case the caller continues using 
		// 		 this instance instead of the received by the CORE
		_postCreateOrUpdate(record,outRecord);
		
		// [3] - return
		return outRecord;
	}
	/**
	 * Saves a record. This method MUST be used when the model object is weaved with the {@link DirtyStateTrackable} aspect.
	 * If weaving is NOT being used, use the create() or update() methods instead
	 * @param record
	 * @return
	 * @throws PersistenceException 
	 */
	public M save(final M record) throws PersistenceException {
		// [0] - The record to save MUST be a trackable object
		//		 (otherwise we won't know if it's a creat operation or an update one)
		if (!(record instanceof DirtyStateTrackable)) throw new IllegalStateException(Throwables.message("{} is NOT a {} instance... " +
																										 "maybe the {} aspect is NOT weaved because the JVM is NOT started with the -javaagent:aspectjweaver.jar, " +
																										 "or maybe if weaving is NOT an option, the create() or update() method should be used instead of the save() method", 
																										 record.getClass(),DirtyStateTrackable.class.getSimpleName(),
																										 ConvertToDirtyStateTrackable.class.getSimpleName()));
		// [1]- Get a trackable version of the record
		DirtyStateTrackable trckReceivedRecord = DirtyTrackAdapter.adapt(record);
	
		// [2]  - Save
		M outRecord = null; 
		CRUDResult<M> saveOpResult = null;
		
		// Check if the record is dirty (is changed)	
		if (((DirtyStateTrackable)record).getTrackingStatus().isThisNew()) {
			// 2.1) the record is new
			saveOpResult = this.getServiceProxy()
									.create(this.getSecurityContext(),
				 				   		  	record);
			outRecord = saveOpResult.getOrThrow();
			
		} else if (trckReceivedRecord.isDirty()) {
			// 2.1) - The record already existed (it's an update)
			saveOpResult = this.getServiceProxy()
										.update(this.getSecurityContext(),
										 		record);
			outRecord = saveOpResult.getOrThrow();
			
		} else {
			// 2.1) - Nothing was done with the record
			log.warn("Record of type {} NOT updated, maybe you do NOT have to call api.save()",record.getClass());
			outRecord = record;
		}	
		// some debugging
		if (saveOpResult != null) log.debug(saveOpResult.debugInfo().toString());
		
		// [3] - After create or update, set the dirty status to both the CORE received saved object
		// 		 AND the original object sent to the CORE just for the case the caller continues using 
		// 		 this instance instead of the received by the CORE
		_postCreateOrUpdate(record,outRecord);
		
		// [4] - return
		return outRecord;
	}
	/**
	 * Saves a record. This method MUST be used when the model object is weaved with the {@link DirtyStateTrackable} aspect.
	 * If weaving is NOT being used, use the create() or update() methods instead
	 * @param record
	 * @param callbackspec
	 * @return
	 * @throws PersistenceException 
	 */
	public M save(final M record,
				  final PersistenceOperationCallbackSpec callbackSpec) {
		// [0] - The record to save MUST be a trackable object
		//		 (otherwise we won't know if it's a creat operation or an update one)
		if (!(record instanceof DirtyStateTrackable)) throw new IllegalStateException(Throwables.message("{} is NOT a {} instance... " +
																										 "maybe the {} aspect is NOT weaved because the JVM is NOT started with the -javaagent:aspectjweaver.jar, " +
																										 "or maybe if weaving is NOT an option, the create() or update() method should be used instead of the save() method", 
																										 record.getClass(),DirtyStateTrackable.class.getSimpleName(),
																										 ConvertToDirtyStateTrackable.class.getSimpleName()));
		// [1]- Get a trackable version of the record
		DirtyStateTrackable trckReceivedRecord = DirtyTrackAdapter.adapt(record);
	
		// [2]  - Save
		M outRecord = null; 
		CRUDResult<M> saveOpResult = null;
		
		// Check if the record is dirty (is changed)	
		// TODO trckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
		// FIXME rckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
		// DirtyStateTrackable trckReceivedRecord = DirtyTrackAdapter.adapt(record);
		// if (trckReceivedRecord.getTrackingStatus().isThisNew()) vs if ((DirtyStateTrackable)record).getTrackingStatus().isThisNew()
		if (((DirtyStateTrackable)record).getTrackingStatus().isThisNew()) {
			// 2.1) the record is new
			saveOpResult = this.getServiceProxy()
									.create(this.getSecurityContext(),
				 				   		  	record,
				 				   		  	callbackSpec);
			outRecord = saveOpResult.getOrThrow();
			
		} else if (trckReceivedRecord.isDirty()) {
			// 2.1) - The record already existed (it's an update)
			saveOpResult = this.getServiceProxy()
										.update(this.getSecurityContext(),
										 		record,
										 		callbackSpec);
			outRecord = saveOpResult.getOrThrow();
			
		} else {
			// 2.1) - Nothing was done with the record
			log.warn("Record of type {} NOT updated, maybe you do NOT have to call api.save()",record.getClass());
			outRecord = record;
		}	
		// some debugging
		if (saveOpResult != null) log.debug(saveOpResult.debugInfo().toString());
		
		// [3] - After create or update, set the dirty status to both the CORE received saved object
		// 		 AND the original object sent to the CORE just for the case the caller continues using 
		// 		 this instance instead of the received by the CORE
		_postCreateOrUpdate(record,outRecord);
		
		// [4] - return
		return outRecord;
	}
	/**
	 * After create or update, set the dirty status to both the CORE received saved object
	 * AND the original object sent to the CORE just for the case the caller continues using 
	 * this instance instead of the received by the CORE
	 * @param record
	 * @param outRecord
	 */
	private void _postCreateOrUpdate(final M record,final M outRecord) {
		if (outRecord != null && outRecord instanceof DirtyStateTrackable) { 
			ClientAPIModelObjectChangesTrack.startTrackingChangesOnSaved(outRecord);
		}
		if (record != null && record instanceof DirtyStateTrackable) {
			ClientAPIModelObjectChangesTrack.startTrackingChangesOnSaved(record);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Deletes a record
	 * @param record 
	 * @return the deleted record if the operation was successful, null otherwise
	 * @throws PersistenceException 
	 */
	public M delete(final M record) throws PersistenceException {
		return this.delete(record.getOid());
	}
	/**
	 * Deletes a record
	 * @param recordOid 
	 * @return the deleted record if the operation was successful, null otherwise
	 * @throws PersistenceException 
	 */
	public M delete(final O recordOid) throws PersistenceException {
		// [1] - Delete the record
		CRUDResult<M> deleteOpResult = this.getServiceProxy()
												.delete(this.getSecurityContext(),
	  	    										   	recordOid);
		log.debug(deleteOpResult.debugInfo().toString());
		
		// [2] - If the record has been deleted, it's a new record
		M outRecord = deleteOpResult.getOrThrow();
		
		if (outRecord instanceof DirtyStateTrackable) {
			ClientAPIModelObjectChangesTrack.startTrackingChangesOnDeleted(outRecord);
		}
		// [2] - Return the deleted record
		return outRecord;
	}
}
