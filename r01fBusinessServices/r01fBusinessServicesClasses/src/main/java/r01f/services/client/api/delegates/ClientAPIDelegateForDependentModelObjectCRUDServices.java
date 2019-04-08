package r01f.services.client.api.delegates;

import javax.inject.Provider;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.exceptions.Throwables;
import r01f.guids.PersistableObjectOID;
import r01f.model.ModelObjectRef;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceException;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForDependentModelObject;

/**
 * Adapts Persistence API method invocations to the service proxy that performs the core method invocations
 * @param <O>
 * @param <M>
 */
public class ClientAPIDelegateForDependentModelObjectCRUDServices<O extends PersistableObjectOID,M extends PersistableModelObject<O>,
																  P extends PersistableModelObject<?>> 
	 extends ClientAPIServiceDelegateBase<CRUDServicesForDependentModelObject<O,M,P>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForDependentModelObjectCRUDServices(final Provider<SecurityContext> securityContextProvider,
													   			final Marshaller modelObjectsMarshaller,
													   			final CRUDServicesForDependentModelObject<O,M,P> services) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Changes a record's parent
	 * @param oid
	 * @param newParentRef
	 * @return
	 * @throws PersistenceException
	 */
	@SuppressWarnings("unchecked")
	public <PR extends ModelObjectRef<P>> M changeParent(final O oid,final PR newParentRef) throws PersistenceException {
		// Do the update
		CRUDResult<M> saveOpResult = this.getServiceProxyAs(CRUDServicesForDependentModelObject.class)
											.changeParent(this.getSecurityContext(),
						 				   		  		  oid,newParentRef);
		M outRecord = saveOpResult.getOrThrow();
		
		// Adapt the returned object
		if (outRecord != null && outRecord instanceof DirtyStateTrackable) {
			// [2.1]- Update the returned object dirty status 
			ClientAPIModelObjectChangesTrack.startTrackingChangesOnSaved(outRecord);
		}
		return outRecord;
	}
	/**
	 * Creates a record. This method is usually used when {@link DirtyStateTrackable} aspect is NOT being used
	 * @param parentRef
	 * @param record
	 * @return
	 * @throws PersistenceException
	 */
	@SuppressWarnings("unchecked")
	public <PR extends ModelObjectRef<P>> M create(final PR parentRef,final M record) throws PersistenceException {
		// If the record is a DirtyStateTrackable instance check that the instance is NEW
		if (record instanceof DirtyStateTrackable) {
			// TODO trckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
			// FIXME rckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
//			DirtyStateTrackable trckReceivedRecord = DirtyTrackAdapter.adapt(record);
//			if (!trckReceivedRecord.getTrackingStatus().isThisNew()) throw new IllegalStateException(Throwables.message("{} instance is NOT new... maybe you have to call update() or save() method instead of create",
//																													  	record.getClass()));
			if (!((DirtyStateTrackable)record).getTrackingStatus().isThisNew()) throw new IllegalStateException(Throwables.message("{} instance is NOT new... maybe you have to call update() or save() method instead of create",
																													    		   record.getClass()));
		}
		// Do the creation
		CRUDResult<M> saveOpResult = this.getServiceProxyAs(CRUDServicesForDependentModelObject.class)
											.create(this.getSecurityContext(),
						 				   		  	parentRef,record);
		M outRecord = saveOpResult.getOrThrow();
		// Adapt the returned object
		if (outRecord != null && outRecord instanceof DirtyStateTrackable && record instanceof DirtyStateTrackable) {
			// [2.1]- Update the returned object dirty status AND the received object dirty status just for the case the caller continues using this instance instead of 
			//		  the received by the server
			ClientAPIModelObjectChangesTrack.startTrackingChangesOnSaved(outRecord);
			ClientAPIModelObjectChangesTrack.startTrackingChangesOnSaved(record);
		}
		return outRecord;
	}
	/**
	 * Returns a parent reference object
	 * @param oid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <PR extends ModelObjectRef<P>> PR parentReferenceOf(final O oid) {
		return (PR)this.getServiceProxyAs(CRUDServicesForDependentModelObject.class)
							.parentReferenceOf(this.getSecurityContext(),
											   oid)
							.getOrThrow();
	}
}
