package r01f.services.interfaces;

import java.util.Date;

import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;

/**
 * Finding
 * @param <O>
 * @param <M>
 */
public interface FindServicesForModelObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
		 extends ServiceInterfaceForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FINDING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds all persisted model object oids
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param securityContext the user auth data & context info
	 * @return a {@link FindResult} that encapsulates the oids
	 */
	public FindOIDsResult<O> findAll(final SecurityContext securityContext);	
	/**
	 * Finds all persisted model object oids which create date is in the provided range
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param securityContext the user auth data & context info
	 * @param createDate
	 * @return a {@link FindResult} that encapsulates the oids
	 */
	public FindOIDsResult<O> findByCreateDate(final SecurityContext securityContext,
											  final Range<Date> createDate);
	/**
	 * Finds all persisted model object oids which last update date is in the provided range
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param securityContext the user auth data & context info
	 * @param lastUpdateDate
	 * @return a {@link FindResult} that encapsulates the oids
	 */
	public FindOIDsResult<O> findByLastUpdateDate(final SecurityContext securityContext,
											  	  final Range<Date> lastUpdateDate);
	/**
	 * Finds all persisted model object oids created by the provided user
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param securityContext the user auth data & context info
	 * @param creatorUserCode
	 * @return a {@link FindResult} that encapsulates the oids
	 */
	public FindOIDsResult<O> findByCreator(final SecurityContext securityContext,
									   	   final UserCode creatorUserCode);
	/**
	 * Finds all persisted model object oids last updated by the provided user
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param securityContext the user auth data & context info
	 * @param lastUpdtorUserCode
	 * @return a {@link FindResult} that encapsulates the oids
	 */
	public FindOIDsResult<O> findByLastUpdator(final SecurityContext securityContext,
										   	   final UserCode lastUpdtorUserCode);
}
