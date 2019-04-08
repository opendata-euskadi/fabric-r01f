package r01f.services.client.api.delegates;

import java.util.Collection;
import java.util.Date;

import javax.inject.Provider;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.FindServicesForModelObject;
import r01f.types.Range;

/**
 * Adapts Persistence API method invocations to the service proxy that performs the core method invocations
 * @param <O>
 * @param <M>
 */
@Slf4j
public abstract class ClientAPIDelegateForModelObjectFindServices<O extends PersistableObjectOID,M extends PersistableModelObject<O>> 
	          extends ClientAPIServiceDelegateBase<FindServicesForModelObject<O,M>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForModelObjectFindServices(final Provider<SecurityContext> securityContextProvider,
													   final Marshaller modelObjectsMarshaller,
												   	   final FindServicesForModelObject<O,M> services) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  services);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds all persisted model object 
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @return a {@link PersistenceOperationResult} that encapsulates the entities
	 */
	public Collection<O> findAll() {
		FindOIDsResult<O> findResult = this.getServiceProxy()
												.findAll(this.getSecurityContext());
		
		log.debug(findResult.debugInfo().toString());
		
		Collection<O> outOids = findResult.getOrThrow();
		return outOids;
	}	
	/**
	 * Finds all persisted model object which create date is in the provided range
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param createDate
	 * @return a {@link PersistenceOperationResult} that encapsulates the entities
	 */
	public Collection<O> findByCreateDate(final Range<Date> createDate) {
		FindOIDsResult<O> findResult = this.getServiceProxy()
												.findByCreateDate(this.getSecurityContext(),
															  	  createDate);
		
		log.debug(findResult.debugInfo().toString());
		
		Collection<O> outOids = findResult.getOrThrow();		
		return outOids;
	}
	/**
	 * Finds all persisted model object entities which last update date is in the provided range
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param lastUpdateDate
	 * @return a {@link PersistenceOperationResult} that encapsulates the entities
	 */
	public Collection<O> findByLastUpdateDate(final Range<Date> lastUpdateDate) {
		FindOIDsResult<O> findResult = this.getServiceProxy()
												.findByLastUpdateDate(this.getSecurityContext(),
															  	  	  lastUpdateDate);
		
		log.debug(findResult.debugInfo().toString());
		
		Collection<O> outOids = findResult.getOrThrow();		
		return outOids;
	}
	/**
	 * Finds all persisted model object entities created by the provided user
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param creatorUserCode
	 * @return a {@link PersistenceOperationResult} that encapsulates the entities
	 */
	public Collection<O> findByCreator(final UserCode creatorUserCode) {
		FindOIDsResult<O> findResult = this.getServiceProxy()
												.findByCreator(this.getSecurityContext(),
															   creatorUserCode);
		
		log.debug(findResult.debugInfo().toString());
		
		Collection<O> outOids = findResult.getOrThrow();		
		return outOids;
	}
	/**
	 * Finds all persisted model object entities last updated by the provided user
	 * If the entity is a {@link Versionable}  {@link PersistableModelObject}, it returns the 
	 * currently active versions
	 * @param lastUpdatorUserCode
	 * @return a {@link PersistenceOperationResult} that encapsulates the entities
	 */
	public Collection<O> findByLastUpdator(final UserCode lastUpdatorUserCode) {
		FindOIDsResult<O> findResult = this.getServiceProxy()
													.findByLastUpdator(this.getSecurityContext(),
																   	   lastUpdatorUserCode);
		
		log.debug(findResult.debugInfo().toString());
		
		Collection<O> outOids = findResult.getOrThrow();	
		return outOids;
	}	
}
