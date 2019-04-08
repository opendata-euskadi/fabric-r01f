package r01f.services.interfaces;

import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable;
import r01f.model.persistence.CRUDResult;
import r01f.persistence.callback.spec.PersistenceOperationCallbackSpec;
import r01f.securitycontext.SecurityContext;

/**
 * CRUD (create, read, update, delete) interface for not {@link Versionable} model object
 * @param <O>
 * @param <M>
 */
public interface CRUDServicesForModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>> 
		 extends ServiceInterfaceForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * @return the {@link DBEntity} type that models the persistence of the {@link PersistableModelObject}
//	 */
//	public Class<? extends DBEntity> getDBEntityType();
/////////////////////////////////////////////////////////////////////////////////////////
//	CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a entity from its identifier.
	 * If the entity is a {@link Versionable} {@link PersistableModelObject}, this method returns the 
	 * currently active version
	 * @param securityContext the user auth data & context info
	 * @param oid the entity identifier
	 * @return a {@link CRUDResult} that encapsulates the entity if it was loaded successfully
	 */
	public CRUDResult<M> load(final SecurityContext securityContext,
				  			  final O oid);	
	/**
	 * Creates a entity
	 * If the entity is a {@link Versionable} {@link PersistableModelObject}, and no other version exists 
	 * this creates a fresh new active version. Otherwise, if another version existed, it throws an exception
	 * @param securityContext the user auth data & context info
	 * @param modelObj the entity to be created
	 * @return a {@link CRUDResult} that encapsulates the created entity
	 */
	public CRUDResult<M> create(final SecurityContext securityContext,
				  				final M modelObj);
	/**
	 * Creates a entity
	 * If the entity is a {@link Versionable} {@link PersistableModelObject}, and no other version exists 
	 * this creates a fresh new active version. Otherwise, if another version existed, it throws an exception
	 * This method receives a {@link PersistenceOperationCallbackSpec} that describes how the caller can be
	 * notified about background (async) work executed after the create operation
	 * @param securityContext the user auth data & context info
	 * @param modelObj the entity to be created
	 * @param callbackSpec if the create operation executes some background (async) work, the caller can be notified
	 * @return a {@link CRUDResult} that encapsulates the created entity
	 */
	public CRUDResult<M> create(final SecurityContext securityContext,
				  				final M modelObj,
				  				final PersistenceOperationCallbackSpec callbackSpec);
	/**
	 * Updates a entity 
	 * If a entity is a {@link Versionable} {@link PersistableModelObject}, it updates the currently
	 * active version
	 * @param securityContext the user auth data & context info
	 * @param modelObj the entity to be updated
	 * @return a {@link CRUDResult} that encapsulates the updated entity
	 */
	public CRUDResult<M> update(final SecurityContext securityContext,
				  				final M modelObj);
	/**
	 * Updates a entity 
	 * If a entity is a {@link Versionable} {@link PersistableModelObject}, it updates the currently
	 * active version
	 * This method receives a {@link PersistenceOperationCallbackSpec} that describes how the caller can be
	 * notified about background (async) work executed after the update operation
	 * @param securityContext the user auth data & context info
	 * @param modelObj the entity to be updated
	 * @param callbackSpec if the update operation executes some background (async) work, the caller can be notified
	 * @return a {@link CRUDResult} that encapsulates the updated entity
	 */
	public CRUDResult<M> update(final SecurityContext securityContext,
				  				final M modelObj,
				  				final PersistenceOperationCallbackSpec callbackSpec);
	/**
	 * Deletes a entity
	 * If a entity is a {@link Versionable} {@link PersistableModelObject}, it deletes the currently 
	 * active version
	 * @param securityContext the user auth data & context info
	 * @param oid the identifier of the entity to be deleted
	 * @return a {@link CRUDResult} that encapsulates the deleted entity
	 */
	public CRUDResult<M> delete(final SecurityContext securityContext,
								final O oid);
	/**
	 * Deletes a entity
	 * If a entity is a {@link Versionable} {@link PersistableModelObject}, it deletes the currently 
	 * active version
	 * This method receives a {@link PersistenceOperationCallbackSpec} that describes how the caller can be
	 * notified about background (async) work executed after the delete operation
	 * @param securityContext the user auth data & context info
	 * @param oid the identifier of the entity to be deleted
	 * @param callbackSpec if the delete operation executes some background (async) work, the caller can be notified
	 * @return a {@link CRUDResult} that encapsulates the deleted entity
	 */
	public CRUDResult<M> delete(final SecurityContext securityContext,
								final O oid,
								final PersistenceOperationCallbackSpec callbackSpec);
}
