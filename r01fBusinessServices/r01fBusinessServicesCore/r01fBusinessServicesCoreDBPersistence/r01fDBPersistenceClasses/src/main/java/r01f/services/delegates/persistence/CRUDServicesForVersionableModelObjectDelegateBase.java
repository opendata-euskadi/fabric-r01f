package r01f.services.delegates.persistence;

import java.util.Collection;
import java.util.Date;

import com.google.common.eventbus.EventBus;

import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.events.PersistenceOperationEvents.PersistenceOperationErrorEvent;
import r01f.events.PersistenceOperationEvents.PersistenceOperationOKEvent;
import r01f.guids.OIDForVersionableModelObject;
import r01f.guids.PersistableObjectOID;
import r01f.guids.VersionIndependentOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDOK;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceOperationOK;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.persistence.db.DBCRUDForVersionableModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForVersionableModelObject;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public abstract class CRUDServicesForVersionableModelObjectDelegateBase<O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
	          extends CRUDServicesForModelObjectDelegateBase<O,M>
		   implements CRUDServicesForVersionableModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDServicesForVersionableModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
															 final Class<M> modelObjectType,
													  		 final DBCRUDForVersionableModelObject<O,M> crud,
													  		 final EventBus eventBus) {
		super(coreCfg,
			  modelObjectType,
			  crud,
			  eventBus);
	}
	public CRUDServicesForVersionableModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
															 final Class<M> modelObjectType,
													  		 final DBCRUDForVersionableModelObject<O,M> crud) {
		super(coreCfg,
			  modelObjectType,
			  crud,
			  null);	// no event bus
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LOAD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> loadActiveVersionAt(final SecurityContext securityContext,
						   					 final VersionIndependentOID oid,final Date date) {
		CRUDResult<M> outEntityLoadResult = null;

		// [0] - Check the parameters
		Date theDate = date != null ? date : new Date();
		if (oid == null || !oid.isValid()) {
			outEntityLoadResult = CRUDResultBuilder.using(securityContext)
															  .on(_modelObjectType)
															  .badClientRequestData(PersistenceRequestedOperation.LOAD,
																	  				"Cannot load {} entity since either the version independent oid is null",_modelObjectType)
																	  .about(oid,theDate).build();
		}
		// [1] - Do load
		outEntityLoadResult = this.getServiceImplAs(CRUDServicesForVersionableModelObject.class)
										.loadActiveVersionAt(securityContext,
															 oid,theDate);
		return outEntityLoadResult;
	}
	@Override
	public CRUDResult<M> loadWorkVersion(final SecurityContext securityContext,
							 			 final VersionIndependentOID oid) {
		CRUDResult<M> outEntityLoadResult = null;

		// [0] - Param checking
		if (oid == null) {
			outEntityLoadResult = CRUDResultBuilder.using(securityContext)
															  .on(_modelObjectType)
															  .badClientRequestData(PersistenceRequestedOperation.LOAD,
																	  				"Cannot load {} work version entity since the version independent oid is null",_modelObjectType)
																	  .aboutWorkVersion(oid).build();
		}
		// [1] - Do load
		outEntityLoadResult = this.getServiceImplAs(CRUDServicesForVersionableModelObject.class)
									.loadWorkVersion(securityContext,
		   									     	 oid);
		return outEntityLoadResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext,
													 final VersionIndependentOID oid) {
		return this.deleteAllVersions(securityContext,
									  oid,
									  null);	// no async callback
	}
	@Override
	public CRUDOnMultipleResult<M> deleteAllVersions(final SecurityContext securityContext,
													 final VersionIndependentOID oid,
													 final PersistenceOperationCallbackSpec callbackSpec) {
		// [0] - Check the version info
		if (oid == null) {
			CRUDResultBuilder.using(securityContext)
									    .on(_modelObjectType)
									    .badClientRequestData(PersistenceRequestedOperation.DELETE,
											  				  "Cannot delete all {} entity versions since the provided entity oid is null",_modelObjectType);
		}
		// [1] - Delete
		CRUDOnMultipleResult<M> outResults = this.getServiceImplAs(CRUDServicesForVersionableModelObject.class)
													.deleteAllVersions(securityContext,
				          					   	  	   				   oid);
		// [2] - Throw an event for each successful deletion
		//		 and another for each deletion failure
		_fireEvents(securityContext,
					outResults,
					callbackSpec);

		return outResults;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated) {
		return this.activate(securityContext,
					  		 entityToBeActivated,
					  		 null);	// no async callback
	}
	@Override
	public CRUDResult<M> activate(final SecurityContext securityContext,
								  final M entityToBeActivated,
								  final PersistenceOperationCallbackSpec callbackSpec) {
		// [0] - Check params
		if (entityToBeActivated == null) {
			return CRUDResultBuilder.using(securityContext)
									   .on(_modelObjectType)
									   .badClientRequestData(PersistenceRequestedOperation.CREATE,
											  				 "The {} entity cannot be null in order to activate that version",_modelObjectType)
									   .about(entityToBeActivated)
									   .build();
		}

		// [1] - Check that the requested version exists and is in DRAFT mode
		if (_readOnly) throw new IllegalStateException("The CRUD services object is in READ-ONLY status!");
		CRUDResult<M> storedEntityToBeActivatedLoad = this.getServiceImplAs(CRUDServicesForVersionableModelObject.class)
															.load(securityContext,
																  entityToBeActivated.getOid());
		if (storedEntityToBeActivatedLoad.hasSucceeded()) {
			// the version exists... check that it's in draft mode
			if (storedEntityToBeActivatedLoad.getOrThrow().asVersionable()
											 .isActive()) {
				return CRUDResultBuilder.using(securityContext)
										   .on(_modelObjectType)
										   .notUpdated()
									   	   .becauseTargetEntityWasInAnIllegalStatus("The entity with oid={} is NOT in draft mode. It cannot be activated!",
										  					  		  				entityToBeActivated.getOid())
										   .about(entityToBeActivated.getOid())
										   .build();

			}
		}
		// if the error is OTHER than the version to be activated does NOT exists
		else if (!storedEntityToBeActivatedLoad.asCRUDError()		// as(CRUDError.class)
											   .wasBecauseClientRequestedEntityWasNOTFound()) {
			return CRUDResultBuilder.using(securityContext)
									   .on(_modelObjectType)
									   .notUpdated()
									   .because(storedEntityToBeActivatedLoad.asCRUDError()							// as(CRUDError.class)
											   								 .getPersistenceException())
									   .about(entityToBeActivated.getOid())
									   .build();
		}

		// [2] the activation date is right now!
		Date activationDate = new Date();

		// [3] Find the currently active version and if it exists override it with the new version
		CRUDResult<M> currentlyActiveEntityLoad = this.getServiceImplAs(CRUDServicesForVersionableModelObject.class)
															.loadActiveVersionAt(securityContext,
			   						     							   	   		 entityToBeActivated.getOid().getOid(),	// version independent oid
			   						     							   	   		 new Date());							// this moment active entity
		if (currentlyActiveEntityLoad.hasSucceeded()) {
			Date passivationDate = activationDate;

			// the currently active version exists... passivate it
			M currentlyActiveEntity = currentlyActiveEntityLoad.getOrThrow();		// gets the record or throws an exception
			log.warn("{}'s with oid={}, is currently active and will be pasivated and {} will be activated",
					 _modelObjectType,
					 currentlyActiveEntity,
					 entityToBeActivated.getOid());

			// The currently active version must be overridden by the new version
			currentlyActiveEntity.asVersionable()
								 .overrideBy(entityToBeActivated.getOid().getVersion(),
											 passivationDate);	// passivated at the same time that the other entity is activated
			CRUDResult<M> currentlyActiveEntityPassivation = this.getServiceImplAs(CRUDServicesForVersionableModelObject.class)
																	.update(securityContext,
								   									     	currentlyActiveEntity);
			// send event about passivated record update
			_fireEvent(securityContext,
					   currentlyActiveEntityPassivation,
					   callbackSpec);

			M currentlyActiveRecordPassivated = null;
			if (currentlyActiveEntityPassivation.hasSucceeded()) {
				// passivation OK
				currentlyActiveRecordPassivated = currentlyActiveEntityPassivation.getOrThrow();
				if (!currentlyActiveRecordPassivated.asVersionable().isActive()) log.debug("... pasivation ok");
			} else {
				// passivation NOK
				CRUDError<M> persistError = currentlyActiveEntityPassivation.asCRUDError();		// as(CRUDError.class)
				return CRUDResultBuilder.using(securityContext)
												   .on(_modelObjectType)
												   .notUpdated()
												   .because(persistError.getPersistenceException())
												   		.about(entityToBeActivated.getOid()).build();
			}
		}
		// [4] Activate the version
		entityToBeActivated.asVersionable()
						   .activate(activationDate);
		CRUDResult<M> activationResult = this.getServiceImplAs(CRUDServicesForVersionableModelObject.class)
													.update(securityContext,
						   							  	 	entityToBeActivated);
		// [5] send event
		_fireEvent(securityContext,
				   activationResult,
				   callbackSpec);

		// [6] Return
		return activationResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EVENT FIRING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Throws an {@link EventBus} event
	 * @param securityContext
	 * @param opResult
	 */
	protected void _fireEvents(final SecurityContext securityContext,
							   final CRUDOnMultipleResult<M> opResults) {
		_fireEvents(securityContext,
				    opResults,
				    null);		// no async callback
	}
	/**
	 * Throws an {@link EventBus} event
	 * @param securityContext
	 * @param opResult
	 * @param callbackSpec
	 */
	protected void _fireEvents(final SecurityContext securityContext,
							   final CRUDOnMultipleResult<M> opResults,
							   final PersistenceOperationCallbackSpec callbackSpec) {
		if (this.getEventBus() == null) return;		// do nothing

		log.debug("Publishing events for: {}",opResults.getClass());

		// NOK ops
		Collection<CRUDError<M>> opsNOK = opResults.getOperationsNOK();
		if (CollectionUtils.hasData(opsNOK)) {
			for (CRUDError<M> opNOK : opsNOK) {
				PersistenceOperationErrorEvent errorEvent = new PersistenceOperationErrorEvent(securityContext,
													 					         	 		   opNOK,
													 					         	 		   callbackSpec);
				this.getEventBus().post(errorEvent);
			}
		}
		// OK ops
		Collection<CRUDOK<M>> opsOK = opResults.getOperationsOK();
		// Post OK results
		if (CollectionUtils.hasData(opsOK)) {
			for (PersistenceOperationOK opOk : opsOK) {
				PersistenceOperationOKEvent okEvent = new PersistenceOperationOKEvent(securityContext,
													 					      	  	  opOk,
													 					      	  	  callbackSpec);
				this.getEventBus().post(okEvent);
			}
		}
	}
}
