package r01f.persistence.db;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.facets.HasID;
import r01f.facets.HasName;
import r01f.facets.LangDependentNamed;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.locale.Language;
import r01f.model.HasTrackingInfo;
import r01f.model.ModelObjectTracking;
import r01f.model.PersistableModelObject;
import r01f.model.facets.HasEntityVersion;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.reflection.ReflectionUtils;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.CRUDServicesForModelObjectDelegateBase;
import r01f.types.summary.LangDependentSummary;
import r01f.types.summary.LangIndependentSummary;
import r01f.types.summary.Summary;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Base type for every persistence layer type
 * @param <O>
 * @param <M>
 * @param <PK>
 * @param <DB>
 */
@Accessors(prefix="_")
@Slf4j
public abstract class DBCRUDForModelObjectBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>,
							     			   PK extends DBPrimaryKeyForModelObject,DB extends DBEntityForModelObject<PK>>
			  extends DBBaseForModelObject<O,M,
			 			     			   PK,DB>
	       implements DBCRUDForModelObject<O,M>,
	       			  TransfersModelObjectStateToDBEntity<M,DB> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Persistence event listener
	 */
	private final Collection<ListensToDBEntityPersistenceEvents<M,DB>> _dbEntityPersistenceEventsListeners = Lists.newArrayList();
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Deprecated
	public DBCRUDForModelObjectBase(final DBModuleConfig dbCfg,
									final Class<M> modelObjectType,final Class<DB> dbEntityType,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  entityManager,
			  marshaller);
		_registerDBEntityPersistenceOperationEventListenerIfCompletesDBEntityBeforeCreateOrUpdate();
	}
	@Deprecated
	public DBCRUDForModelObjectBase(final DBModuleConfig dbCfg,
									final Class<M> modelObjectType,final Class<DB> dbEntityType,
									final TransformsDBEntityIntoModelObject<DB,M> dbEntityIntoModelObjectTransformer,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  dbEntityIntoModelObjectTransformer,
			  entityManager,
			  marshaller);
		_registerDBEntityPersistenceOperationEventListenerIfCompletesDBEntityBeforeCreateOrUpdate();
	}
	public DBCRUDForModelObjectBase(final Class<M> modelObjectType,final Class<DB> dbEntityType,
									final DBModuleConfig dbCfg,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(modelObjectType,dbEntityType,
			  dbCfg,
			  entityManager,
			  marshaller);
		_registerDBEntityPersistenceOperationEventListenerIfCompletesDBEntityBeforeCreateOrUpdate();
	}
	public DBCRUDForModelObjectBase(final Class<M> modelObjectType,final Class<DB> dbEntityType,
									final TransformsDBEntityIntoModelObject<DB,M> dbEntityIntoModelObjectTransformer,
									final DBModuleConfig dbCfg,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(modelObjectType,dbEntityType,
			  dbEntityIntoModelObjectTransformer,
			  dbCfg,
			  entityManager,
			  marshaller);
		_registerDBEntityPersistenceOperationEventListenerIfCompletesDBEntityBeforeCreateOrUpdate();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EVENT LISTENER REGISTRATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Registers a listener that will be called BEFORE and AFTER any DBEntity persistence operation
	 * @param listener
	 */
	public void registerDBEntityPersistenceOperationsEventListener(final ListensToDBEntityPersistenceEvents<M,DB> listener) {
		_dbEntityPersistenceEventsListeners.add(listener);
	}
	@SuppressWarnings("unchecked")
	private void _registerDBEntityPersistenceOperationEventListenerIfCompletesDBEntityBeforeCreateOrUpdate() {
		// create an event listener...
		if (this instanceof CompletesDBEntityBeforeCreateOrUpdate) {
			final CompletesDBEntityBeforeCreateOrUpdate<M,DB> dbEntityCompletes = ((CompletesDBEntityBeforeCreateOrUpdate<M,DB>)this);
			this.registerDBEntityPersistenceOperationsEventListener(new ListensToDBEntityPersistenceEvents<M,DB>() {
																			@Override
																			public void onBeforDBEntityPersistenceOperation(final SecurityContext securityContext,
																														    final PersistencePerformedOperation op,
																														    final M modelObj,final DB dbEntity) {
																				dbEntityCompletes.completeDBEntityBeforeCreateOrUpdate(securityContext,
																							   	   									   op,
																							   	   									   modelObj,dbEntity);
																			}
																			@Override
																			public void onAfterDBEntityPersistenceOperation(final SecurityContext securityContext,
																														    final PersistencePerformedOperation op,
																														    final DB dbEntity,final M modelObj) {
																				// nothing after
																			}
																	});
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Model objet to DBEntity transform (WRITING)
/////////////////////////////////////////////////////////////////////////////////////////
	protected DB createDBEntityInstanceFor(final M modelObj) {
		DB outEntity = ReflectionUtils.<DB>createInstanceOf(_DBEntityType);
		return outEntity;
	}
	protected void _setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
										  			 final M modelObj,final DB dbEntity,
										  			 final PersistencePerformedOperation persistenceOp) {
		// ensure some common fields are set
		// (the user provided setDBEntityFieldsFromModelObject() method might not fill some dbEntity important info)
		if (persistenceOp == PersistencePerformedOperation.CREATED) {
			if (Strings.isNullOrEmpty(dbEntity.getOid())) {
				dbEntity.setOid(modelObj.getOid().asString());
			}
		}
		if (dbEntity instanceof HasTrackingInfo) {
			HasTrackingInfo dbEntityHasTrackingInfo = (HasTrackingInfo)dbEntity;
			ModelObjectTracking dbEntityTracking = dbEntityHasTrackingInfo.getTrackingInfo();		// always return a tracking info obj
			dbEntityTracking.mergeWith(modelObj.getTrackingInfo())
							.when(persistenceOp)
						    .by(securityContext);

			if (persistenceOp == PersistencePerformedOperation.CREATED) {
				if (dbEntityTracking.getCreatorUserCode() == null) dbEntityTracking.setCreatorUserCode(securityContext.getUserCode());
				dbEntityHasTrackingInfo.setCreatorUserCode(dbEntityTracking.getCreatorUserCode());

				// beware that the model object tracking info can be null when creating
				// ... since some tracking info has been computed here for the dbEntity,
				//	   just copy this info to the model object
				// ... this way, we're sure that the descriptor will have tracking info even if
				//	   the model obj to be created do
				if (modelObj.getTrackingInfo() == null) {
					modelObj.setTrackingInfo(dbEntityHasTrackingInfo.getTrackingInfo());
				}
			}
			else if (persistenceOp == PersistencePerformedOperation.UPDATED) {
				dbEntityHasTrackingInfo.setLastUpdatorUserCode(dbEntityTracking.getLastUpdatorUserCode());
			}
		}
		if (dbEntity instanceof HasEntityVersion) {
			HasEntityVersion hasEntityVersion = (HasEntityVersion)dbEntity;
			hasEntityVersion.setEntityVersion(modelObj.getEntityVersion());
		}
		if (dbEntity instanceof DBEntityHasID
		 && modelObj instanceof HasID) {
			HasID<?> hasId = (HasID<?>)modelObj;
			DBEntityHasID dbHasId = (DBEntityHasID)dbEntity;
			dbHasId.setId(hasId.getId() != null ? hasId.getId().asString()
												: null);
		}
		if (dbEntity instanceof DBEntityHasModelObjectDescriptor) {
			/*DBEntityHasModelObjectDescriptor hasDescriptor = (DBEntityHasModelObjectDescriptor)dbEntity;
			hasDescriptor.setDescriptor(_modelObjectsMarshaller.xmlFromBean(modelObj));*/
			this.setDescriptorForDBEntity(modelObj,dbEntity);
		}
		// set the db entity fields from the model object data
		this.setDBEntityFieldsFromModelObject(securityContext,
						     	  			  modelObj,dbEntity);
	}
    protected void setDescriptorForDBEntity(final M modelObj,final DB dbEntity) {
		DBEntityHasModelObjectDescriptor hasDescriptor = (DBEntityHasModelObjectDescriptor)dbEntity;
		hasDescriptor.setDescriptor(_modelObjectsMarshaller.forWriting().toXml(modelObj));
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> load(final SecurityContext securityContext,
							  final O oid) {
		// Compose the pk
		PK pk = this.dbEntityPrimaryKeyFor(oid);
		return this.doLoad(securityContext,
					   	   oid,pk);
	}
	@Override
	public CRUDResult<M> create(final SecurityContext securityContext,
								final M modelObj) {
		if (modelObj.getEntityVersion() > 0) throw new IllegalStateException(Throwables.message("Cannot create a {} entity because the model object received at the persistence layer received does have the entityVersion attribute with a NON ZERO value. This is a developer's fault; please check that when persisting the model object, the entityVersion is NOT set",
																							     _modelObjectType));
		return this.doCreateOrUpdateEntity(securityContext,
									  	   modelObj,
									  	   PersistenceRequestedOperation.CREATE);		// it's a creation
	}
	@Override
	public CRUDResult<M> create(final SecurityContext securityContext,
								final M modelObj,
								final PersistenceOperationCallbackSpec callbackSpec) {
		throw new IllegalStateException(Throwables.message("Implemented at service level (see {})",
														   CRUDServicesForModelObjectDelegateBase.class));
	}
	@Override
	public CRUDResult<M> update(final SecurityContext securityContext,
								final M entity) {
		// some checks to help developers...
		if (entity.getEntityVersion() == 0) throw new IllegalStateException(Throwables.message("Cannot update a {} entity because the model object received at the persistence layer received does NOT have the entityVersion attribute. This is a developer's fault; please check that when persisting the model object, the entityVersion is set",
																							   _modelObjectType));

		return this.doCreateOrUpdateEntity(securityContext,
									  	   entity,
									  	   PersistenceRequestedOperation.UPDATE);		// it's an update
	}
	@Override
	public CRUDResult<M> update(final SecurityContext securityContext,
								final M entity,
								final PersistenceOperationCallbackSpec callbackSpec) {
		throw new IllegalStateException(Throwables.message("Implemented at service level (see {})",
														   CRUDServicesForModelObjectDelegateBase.class));
	}
	@Override
	public CRUDResult<M> delete(final SecurityContext securityContext,
								final O oid) {
		return this.doDelete(securityContext,
				  		 	 oid);
	}
	@Override
	public CRUDResult<M> delete(final SecurityContext securityContext,
								final O oid,
								final PersistenceOperationCallbackSpec callbackSpec) {
		throw new IllegalStateException(Throwables.message("Implemented at service level (see {}",
														   CRUDServicesForModelObjectDelegateBase.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates or updates the entity by checking the entity existence in the first place
	 * and then creating or updating it
	 * @param securityContext
	 * @param modelObj
	 * @param requestedOp
	 * @return
	 */
	protected CRUDResult<M> doCreateOrUpdateEntity(final SecurityContext securityContext,
												   final M modelObj,
												   final PersistenceRequestedOperation requestedOp) {
		return this.doCreateOrUpdateEntity(securityContext,
										   modelObj,
										   requestedOp,
										   null);			// no single use db entity persistence event listener
	}
	/**
	 * Creates or updates the entity by checking the entity existence in the first place
	 * and then creating or updating it
	 * @param securityContext
	 * @param modelObj
	 * @param requestedOp
	 * @param singleUseDBEntityPersistenceEventListener
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected CRUDResult<M> doCreateOrUpdateEntity(final SecurityContext securityContext,
												   final M modelObj,
												   final PersistenceRequestedOperation requestedOp,
												   final ListensToDBEntityPersistenceEvents<M,DB> singleUseDBEntityPersistenceEventListener) {
		// [0]: Try to find a previously existing entity
		PK pk = null;
		DB dbEntityToPersist = null;
		if (modelObj.getOid() != null) {
			// Create the pk from the oid
			pk = this.dbEntityPrimaryKeyFor(modelObj);
			if (pk == null) return CRUDResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.notCreated()
													.becauseClientBadRequest("The provided {} entity do not have primary key data",_modelObjectType)
													  		.about(modelObj).build();

			// Find the entity
			dbEntityToPersist = this.getEntityManager().find(_DBEntityType,
													  		 pk);
			// If the entity exists BUT it's supposed to be new... it's an error
			if (dbEntityToPersist != null
			 && requestedOp.is(PersistenceRequestedOperation.CREATE)) {
				return CRUDResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.notCreated()
										.becauseClientRequestedEntityAlreadyExists()
										 		.about(modelObj).build();
			}
		} else if (requestedOp == PersistenceRequestedOperation.UPDATE) {
			throw new IllegalStateException("An UPDATE operation cannot be performed if the object has oid=null");
		}

		// [1]: Depending on the existence of the entity create or update
		PersistencePerformedOperation performedOp = null;
		if (dbEntityToPersist != null && pk != null) {
			// Update
			log.debug("> updating a {} entity with pk={} and entityVersion={}",_DBEntityType,
					  pk.asString(),modelObj.getEntityVersion());
			performedOp = PersistencePerformedOperation.UPDATED;
		}
		else {
			// Create
			log.debug("> creating a {} entity with pk={}",_DBEntityType,
					  (pk != null ? pk.asString() : "<should be generated at db>"));

			performedOp = PersistencePerformedOperation.CREATED;
			dbEntityToPersist = this.createDBEntityInstanceFor(modelObj);
		}

		// [2]: Check immutable properties
		if (performedOp == PersistencePerformedOperation.UPDATED
		 && this instanceof ChecksChangesInModelObjectIimmutableFieldsBeforeUpdate) {
			ChecksChangesInModelObjectIimmutableFieldsBeforeUpdate<M> checksImmutableFieldsChanges = (ChecksChangesInModelObjectIimmutableFieldsBeforeUpdate<M>)this;

			// a) get a model obj from the CUREENTLY-STORED data
			M actualStoredObj = _wrapDBEntityToModelObject(securityContext,
										     	   		   dbEntityToPersist);
			boolean immutableFieldChanged = checksImmutableFieldsChanges.isAnyImmutablePropertyChanged(securityContext,
																									   actualStoredObj,modelObj);
			if (immutableFieldChanged) return CRUDResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.notUpdated()
													.becauseClientBadRequest("Any of the object's immutable properties has been changed!")
														.about(modelObj).build();
		}

		// [3]: Set the db entity fields from the model object
		_setDBEntityFieldsFromModelObject(securityContext,
							   			  modelObj,dbEntityToPersist,
							   			  performedOp);

		// [4]: call the persistence event listeners
		if (CollectionUtils.hasData(_dbEntityPersistenceEventsListeners)) {
			for (ListensToDBEntityPersistenceEvents<M,DB> listener : _dbEntityPersistenceEventsListeners) {
				listener.onBeforDBEntityPersistenceOperation(securityContext,
															 performedOp,
															 modelObj,dbEntityToPersist);
			}
		}
		if (singleUseDBEntityPersistenceEventListener != null) {
			singleUseDBEntityPersistenceEventListener.onBeforDBEntityPersistenceOperation(securityContext,
															 							  performedOp,
															 							  modelObj,dbEntityToPersist);
		}

		// [5]: Persist
		try {
			return this.persistDBEntity(securityContext,
								   		dbEntityToPersist,
								   		requestedOp,performedOp,
								   		singleUseDBEntityPersistenceEventListener);

		} catch (PersistenceException persistEx) {
			// the previous existence of the entity is checked at the beginning of this method
			// BUT there's an edge case where the two threads concurrently try to create the same entity at the same moment
			// ... if both threads check the entity existence at the same time there's a remote situation in which both gets
			// a false result (the entity does not previously exists) BUT one thread create the entity and the other fails
			// because the entity already existed
			return _buildCRUDResultErrorIfEntityExistsOnConcurrencyOrThrow(securityContext,
																		   modelObj,
																		   requestedOp,
																		   persistEx);
		}
	}
	protected CRUDResult<M> persistDBEntity(final SecurityContext securityContext,
										    final DB dbEntityToPersist,
										    final PersistenceRequestedOperation requestedOp,final PersistencePerformedOperation performedOp) {
		return this.persistDBEntity(securityContext,
							   		dbEntityToPersist,
							   		requestedOp,performedOp,
							   		null);	// no single use db entity persistence event listener
	}
	protected CRUDResult<M> persistDBEntity(final SecurityContext securityContext,
										    final DB dbEntityToPersist,
										    final PersistenceRequestedOperation requestedOp,final PersistencePerformedOperation performedOp,
										    final ListensToDBEntityPersistenceEvents<M,DB> singleUseDBEntityPersistenceEventListener) {
		// [1]: persist > see the difference between persist & merge: http://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge
		//						- PERSIST > takes an entity instance, adds it to the context and makes that instance managed (ie future updates to the entity will be tracked)
		//						- MERGE   > creates a new instance of your entity, copies the state from the supplied entity, and makes the new copy managed
		//									The instance supplied to merge will NOT be managed (any changes will not be part of the transaction)

		// Using merge (it issues an additional DB read)
//		outManagedDBEntity = this.getEntityManager()
//									.merge(dbEntityToPersist);		// dbEntityToPersist is NOT managed anymore!


		DB outManagedDBEntity = dbEntityToPersist;

		// Using persist
		this.getEntityManager()
				.persist(dbEntityToPersist);

		// a flush() call is needed to get the jpa's assigned entity version
		this.getEntityManager()
				.flush();


		// [2]: build the result
		M outModelObj = _wrapDBEntityToModelObject(securityContext,
										     	   outManagedDBEntity);	// beware that the managed object is the merge's returned entity
																		// the one that contains the updated entity version...

		// [3]: call the persistence event listeners
		if (CollectionUtils.hasData(_dbEntityPersistenceEventsListeners)) {
			for (ListensToDBEntityPersistenceEvents<M,DB> listener : _dbEntityPersistenceEventsListeners) {
				listener.onAfterDBEntityPersistenceOperation(securityContext,
															 performedOp,
															 outManagedDBEntity,outModelObj);
			}
		}
		if (singleUseDBEntityPersistenceEventListener != null) {
			singleUseDBEntityPersistenceEventListener.onAfterDBEntityPersistenceOperation(securityContext,
															 							  performedOp,
															 							  outManagedDBEntity,outModelObj);
		}

		// [4]: Build the CRUD result
		CRUDResult<M> outResult = CRUDResultBuilder.using(securityContext)
												   .on(_modelObjectType)
												   .executed(requestedOp,performedOp)
												 		.entity(outModelObj);
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected CRUDResult<M> doDelete(final SecurityContext securityContext,
									 final O oid) {
		// get the pk
		PK pk = this.dbEntityPrimaryKeyFor(oid);

		// check the pk is NOT null
		if (pk == null) return CRUDResultBuilder.using(securityContext)
											    .on(_modelObjectType)
												.notDeleted()
												   .becauseClientBadRequest("The entity oid cannot be null in order to be deleted")
												  		.about(oid).build();

		log.debug("> deleting a {} entity with pk={}",_DBEntityType,pk.asString());

		CRUDResult<M> outResult = null;

		// [1]: Check the existence of the entity to be deleted
		DB dbEntity = this.getEntityManager().find(_DBEntityType,
								  	  	  		   pk);
		// [2]: Delete the entity
		if (dbEntity != null) {
			this.getEntityManager().refresh(dbEntity);		// refresh the dbentity since it could be modified (ie by setting a bi-directional relation)
			this.getEntityManager().remove(dbEntity);
			M outModelObj =  _wrapDBEntityToModelObject(securityContext,
												  		dbEntity);
			outResult = CRUDResultBuilder.using(securityContext)
										 .on(_modelObjectType)
										 .deleted()
											.entity(outModelObj);
		} else {
			outResult = CRUDResultBuilder.using(securityContext)
										 .on(_modelObjectType)
											.notDeleted()
												.becauseClientRequestedEntityWasNOTFound()
													.about(oid).build();
			log.warn(outResult.getDetailedMessage());
		}
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Tries to get a name from the object
	 * @param modelObject
	 * @return
	 */
	protected static <R extends PersistableModelObject<? extends OID>> String _persistableModelObjectInternalName(final R modelObject) {
		String outName = null;

		// [1] try to get the name from the summary
		if (modelObject.hasFacet(HasSummaryFacet.class)) {
			Summary summary = modelObject.asFacet(HasSummaryFacet.class)
											.asSummarizable()
												.getSummary();
			if (summary != null) {
				if (summary.isLangDependent()) {
					LangDependentSummary langDepSum = summary.asLangDependent();
					StringBuilder sb = new StringBuilder();
					for (Iterator<Language> langIt = langDepSum.getAvailableLanguages().iterator(); langIt.hasNext(); ) {
						Language lang = langIt.next();
						sb.append('[').append(lang).append("]: ").append(langDepSum.asString(lang));
						if (langIt.hasNext()) sb.append(", ");
					}
					outName = sb.toString();
				} else {
					LangIndependentSummary langIndepSum = summary.asLangIndependent();
					outName = langIndepSum.asString();
				}
			}
		}
		// [2]... if no name was retrieved, try to use the model object's name
		if (Strings.isNullOrEmpty(outName) && modelObject.hasFacet(HasName.class)) {
			if (modelObject.hasFacet(HasLangDependentNamedFacet.class)) {
				LangDependentNamed langNames = modelObject.asFacet(HasLangDependentNamedFacet.class)
												   		  .asLangDependentNamed();
				StringBuilder sb = new StringBuilder();
				for (Iterator<Language> langIt = langNames.getAvailableLanguages().iterator(); langIt.hasNext(); ) {
					Language lang = langIt.next();
					sb.append('[').append(lang).append("]: ").append(langNames.getNameIn(lang));
					if (langIt.hasNext()) sb.append(", ");
				}
				outName = sb.toString();
			} else {
				LangInDependentNamed name = modelObject.asFacet(HasLangInDependentNamedFacet.class)
													   .asLangInDependentNamed();
				outName = name.getName();
			}
		}
		// There's nowhere to get a summary
		if (Strings.isNullOrEmpty(outName)) outName = "Could NOT get the object's name neither from the summary or the name";

		return outName;
	}


	/**
	 * Build CRUD Result Error If Entity Exists On Concurrency Or Throw Exception :
	 * 	The previous existence of the entity is checked at the beginning of this method
			// BUT there's an edge case where the two threads concurrently try to create the same entity at the same moment
			// ... if both threads check the entity existence at the same time there's a remote situation in which both gets
			// a false result (the entity does not previously exists) BUT one thread create the entity and the other fails
			// because the entity already existed
	 * @param securityContext
	 * @param modelObj
	 * @param requestedOp
	 * @param persistEx
	 * @return
	 */
	protected CRUDError<M> _buildCRUDResultErrorIfEntityExistsOnConcurrencyOrThrow(final SecurityContext securityContext,
												   				               	   final M modelObj,
												   				               	   final PersistenceRequestedOperation requestedOp,
												   				               	   final  PersistenceException persistEx) {
		  log.warn(">>>_buildCRUDResultErrorIfEntityExistsOnConcurrencyOrThrow....");

		  if (persistEx.getCause() instanceof org.eclipse.persistence.exceptions.DatabaseException
			 && persistEx.getCause().getCause() instanceof SQLException) {

			  	SQLException sqlEx = (SQLException)persistEx.getCause().getCause();

			  	if(1 == sqlEx.getErrorCode()) {
					log.warn(">> CRUD Error ORA-00001 because Entity {} already exists! ",modelObj.getOid().asString());
					return CRUDResultBuilder.using(securityContext)
							.on(_modelObjectType)
							.notCreated()
							.becauseClientRequestedEntityAlreadyExists()
							.about(modelObj)
							.build();
				} else if(1400 == sqlEx.getErrorCode()) {
					log.warn(">> CRUD Error ORA-01400: CANNOT MAKE A NULL INSERT! ",modelObj.getOid().asString());
					return CRUDResultBuilder.using(securityContext)
							.on(_modelObjectType)
							.notCreated()
							.because(persistEx)
							.about(modelObj)
							.build();
				} else if ("23000".equals(sqlEx.getSQLState())) { //Check if this code number is in some Constants Class.
				    log.warn(">> CRUD SQL State Error with Entity {}. Check logs! ",modelObj.getOid().asString());
					return CRUDResultBuilder.using(securityContext)
											.on(_modelObjectType)
											.notCreated()
											.because(persistEx)
											.about(modelObj)
											.build();
				} else {
					// another type of exception
					log.warn(">> exception {} will be throw {}",persistEx.getLocalizedMessage());
					throw persistEx;
				}
			} else if (persistEx.getCause() instanceof org.eclipse.persistence.exceptions.DatabaseException
					&& persistEx.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {

				SQLIntegrityConstraintViolationException sqlEx = (SQLIntegrityConstraintViolationException)persistEx.getCause().getCause();

				if(1 == sqlEx.getErrorCode()) {
					log.warn(">> CRUD Error ORA-00001 because Entity {} already exists! ",modelObj.getOid().asString());
					return CRUDResultBuilder.using(securityContext)
							.on(_modelObjectType)
							.notCreated()
							.becauseClientRequestedEntityAlreadyExists()
							.about(modelObj)
							.build();
				} else if(1400 == sqlEx.getErrorCode()) {
					log.warn(">> CRUD Error ORA-01400: CANNOT MAKE A NULL INSERT! ",modelObj.getOid().asString());
					return CRUDResultBuilder.using(securityContext)
							.on(_modelObjectType)
							.notCreated()
							.because(persistEx)
							.about(modelObj)
							.build();
				} else if ("23000".equals(sqlEx.getSQLState())) { //Check if this code number is in some Constants Class.
				    log.warn(">> CRUD SQL State Error with Entity {}. Check logs! ",modelObj.getOid().asString());
					return CRUDResultBuilder.using(securityContext)
											.on(_modelObjectType)
											.notCreated()
											.because(persistEx)
											.about(modelObj)
											.build();
				} else {
					// another type of exception
					log.warn(">> exception {} will be throw {}",persistEx.getLocalizedMessage());
					throw persistEx;
				}
			} else {
				// another type of exception
				log.warn(">> exception {} will be throw {}",persistEx.getLocalizedMessage());
				throw persistEx;
			}
	}
}
