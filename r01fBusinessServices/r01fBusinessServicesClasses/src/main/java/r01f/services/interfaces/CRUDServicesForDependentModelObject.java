package r01f.services.interfaces;

import r01f.guids.OID;
import r01f.guids.PersistableObjectOID;
import r01f.model.ModelObjectRef;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceOperationExecResult;
import r01f.securitycontext.SecurityContext;

/**
 * CRUD (create, read, update, delete) interface for not {@link Versionable} model object
 * @param <O>
 * @param <M>
 */
public interface CRUDServicesForDependentModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>,
													 P extends PersistableModelObject<?>>
		 extends ServiceInterfaceForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a entity
	 * @param securityContext the user auth data & context info
	 * @param parentRef the parent object reference
	 * @param modelObj the entity to be created
	 * @return a {@link CRUDResult} that encapsulates the created entity
	 */
	public <PR extends ModelObjectRef<P>> CRUDResult<M> create(final SecurityContext securityContext,
				  										       final PR parentRef,
				  										       final M modelObj);
	/**
	 * Changes an entity's parent 
	 * @param securityContext the user auth data & context info
	 * @param oid the entity's oid to be changed
	 * @param newParentRef the net parent reference
	 * @return a {@link CRUDResult} that encapsulates the updated entity
	 */
	public <PR extends ModelObjectRef<P>> CRUDResult<M> changeParent(final SecurityContext securityContext,
				  					  								 final O oid,final PR newParentRef);
	/**
	 * Returns the parent object reference of a given entity
	 * @param securityContext
	 * @param oid
	 * @return
	 */
	public <PR extends ModelObjectRef<P>> PersistenceOperationExecResult<PR> parentReferenceOf(final SecurityContext securityContext,
							   			   								  					   final O oid);
	/**
	 * Deletes all child objects of the given parent object
	 * @param securityContext
	 * @param parentOid
	 * @return
	 */
	public <PO extends OID> CRUDOnMultipleResult<M> deleteChildsOf(final SecurityContext securityContext,
												  				   final PO parentOid);
}
