package r01f.persistence.db;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.model.HasParentModelObjectRef;
import r01f.model.ModelObjectRef;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.PersistenceErrorType;
import r01f.model.persistence.PersistenceOperationExecResult;
import r01f.model.persistence.PersistenceOperationExecResultBuilder;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;

/**
 * Base type for every persistence layer type
 * @param <O>
 * @param <M>
 * @param <PK>
 * @param <DB>
 */
@Accessors(prefix="_")
public abstract class DBCRUDDelegateForDependentModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>,P extends PersistableModelObject<?>,
							     			   	   			PK extends DBPrimaryKeyForModelObject,DB extends DBEntityForModelObject<PK>>
           implements DBCRUDForDependentModelObject<O,M,P>,
   			 		  TransfersParentModelObjectRefToDBEntity<P,DB> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Class<P> _parentObjType;
	@Getter protected final DBCRUDForModelObjectBase<O,M,PK,DB> _dbCRUD;
	@Getter protected final DBFindDelegateForDependentModelObject<O,M,P,PK,DB> _dbFindDelegateForDependent;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBCRUDDelegateForDependentModelObject(final Class<P> parentObjType,
												 final DBCRUDForModelObjectBase<O,M,PK,DB> dbCRUD) {
		this(parentObjType,
			 dbCRUD,
			 null);
	}
	public DBCRUDDelegateForDependentModelObject(final Class<P> parentObjType,
												 final DBCRUDForModelObjectBase<O,M,PK,DB> dbCRUD,
												 final DBFindDelegateForDependentModelObject<O,M,P,PK,DB> dbFindDelegateForDependent) {
		_parentObjType = parentObjType;
		_dbCRUD = dbCRUD;
		_dbFindDelegateForDependent = dbFindDelegateForDependent;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override 
	public <PR extends ModelObjectRef<P>> CRUDResult<M> create(final SecurityContext securityContext,
															   final PR parentRef,final M modelObj) {
		if (modelObj.getEntityVersion() > 0) throw new IllegalStateException(Throwables.message("Cannot create a {} entity because the model object received at the persistence layer received does have the entityVersion attribute with a NON ZERO value. This is a developer's fault; please check that when persisting the model object, the entityVersion is NOT set",
																							     _dbCRUD.getModelObjectType()));
		return _dbCRUD.doCreateOrUpdateEntity(securityContext,
									   		   modelObj,
									   		   PersistenceRequestedOperation.CREATE,			// it's a creation
									   		   _createDBEntityCreateEventListener(parentRef));	// a db entity create event listener that just sets the parent reference into the DB entity
	}
	private <PR extends ModelObjectRef<P>> ListensToDBEntityPersistenceEvents<M,DB> _createDBEntityCreateEventListener(final PR parentRef) {
		return new ListensToDBEntityPersistenceEvents<M,DB>() {
						@Override
						public void onBeforDBEntityPersistenceOperation(final SecurityContext securityContext, 
																		final PersistencePerformedOperation op,
																		final M modelObj,final DB dbEntity) {
							// if it's a DEPENDENT db entity, check that the parent oid is received
							if (parentRef == null) throw new UnsupportedOperationException(this.getClass() + " is a DEPENDENT entity: do NOT use create(modelObj), use create(parentOid,modelObj) instead to CREATE a DEPENDENT entity");
							DBCRUDDelegateForDependentModelObject.this.setDBEntityFieldsForParentModelObjectRef(securityContext,
																												parentRef,dbEntity);
						}
			
						@Override
						public void onAfterDBEntityPersistenceOperation(final SecurityContext securityContext,
																		final PersistencePerformedOperation op,
																		final DB dbEntity,final M aModelObj) {
							// nothing after
						}
		  	  };
	}
	@Override
	public <PR extends ModelObjectRef<P>> CRUDResult<M> changeParent(final SecurityContext securityContext,
				  					  								 final O oid,final PR newParentRef) {
		// Try to find a previously existing entity
		PK pk = _dbCRUD.dbEntityPrimaryKeyFor(oid);
		DB dbEntityToPersist = _dbCRUD.getEntityManager().find(_dbCRUD.getDBEntityType(),
													  		   pk);
		// If the entity does NOT exists... it's an error
		if (dbEntityToPersist == null) return CRUDResultBuilder.using(securityContext)
															   .on(_dbCRUD.getModelObjectType())
															   .notUpdated()
															   .becauseClientRequestedEntityWasNOTFound()
															   .build();
		// it's just an update
		this.setDBEntityFieldsForParentModelObjectRef(securityContext,
											  	      newParentRef,dbEntityToPersist);
		return _dbCRUD.persistDBEntity(securityContext,
									   dbEntityToPersist,
									   PersistenceRequestedOperation.UPDATE,PersistencePerformedOperation.UPDATED);
	}
	@Override @SuppressWarnings("unchecked")
	public <PR extends ModelObjectRef<P>> PersistenceOperationExecResult<PR> parentReferenceOf(final SecurityContext securityContext,
							   			   													   final O oid) {
		// load the entity
		CRUDResult<M> modelObjLoadResult = _dbCRUD.load(securityContext,
								  			  			oid);
		if (modelObjLoadResult.hasFailed()) return PersistenceOperationExecResultBuilder.using(securityContext)
																				.notExecuted("parentReferenceOf")
																				.because(modelObjLoadResult.asCRUDError());
		M modelObj = modelObjLoadResult.asCRUDOK()
									   .getOperationExecResult();
		if (modelObj instanceof HasParentModelObjectRef) {
			HasParentModelObjectRef<PR> hasParentObjRef = (HasParentModelObjectRef<PR>)modelObj;
			return PersistenceOperationExecResultBuilder.using(securityContext)
									.executed("parentReferenceOf")
									.returning(hasParentObjRef.getParentRef());
		}
		return PersistenceOperationExecResultBuilder.using(securityContext)
														.notExecuted("parentReferenceOf")
														.because(Strings.customized("The {} type is a DEPENDENT object BUT does NOT implements {}; the parent obj reference cannot be known",
																 					modelObj.getClass(),HasParentModelObjectRef.class),
																 PersistenceErrorType.SERVER_ERROR);
	}
	@Override
	public <PO extends OID> CRUDOnMultipleResult<M> deleteChildsOf(final SecurityContext securityContext,
												  				   final PO parentOid) {
		if (_dbFindDelegateForDependent == null) throw new IllegalStateException();
		
		FindResult<M> modelObjsToBeDeletedFindResult = _dbFindDelegateForDependent.findDependentsOf(securityContext, 
																						  			parentOid);
		if (modelObjsToBeDeletedFindResult.hasFailed()) {
			// TODO finish!!
		}
			Collection<M> modelObjsToBeDeleted = modelObjsToBeDeletedFindResult.getOrThrow();
		Collection<M> deletedObjs = Lists.newArrayListWithExpectedSize(modelObjsToBeDeleted.size());
		Collection<M> notDeletedObjs = Lists.newArrayList();
		for (M modelObjToBeDeleted : modelObjsToBeDeleted) {
			CRUDResult<M> delResult = _dbCRUD.delete(securityContext,
						   							 modelObjToBeDeleted.getOid());
			if (delResult.hasSucceeded()) {
				deletedObjs.add(delResult.getOrThrow());
			} else {
				notDeletedObjs.add(modelObjToBeDeleted);
			}
		}
		// TODO finish!!
		return null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
}
