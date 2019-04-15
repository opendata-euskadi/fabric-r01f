package r01f.services.delegates.persistence;


import java.util.Date;

import com.google.common.eventbus.EventBus;

import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindOIDsResultBuilder;
import r01f.persistence.db.DBFindForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.FindServicesForModelObject;
import r01f.types.Range;

/**
 * Service layer delegated type for CRUD find operations
 */
public abstract class FindServicesForModelObjectDelegateBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
			  extends PersistenceServicesForModelObjectDelegateBase<O,M>
		   implements FindServicesForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR  
/////////////////////////////////////////////////////////////////////////////////////////
	public FindServicesForModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												  final Class<M> modelObjectType,
												  final DBFindForModelObject<O,M> findServices) {
		this(coreCfg,
			 modelObjectType,
			 findServices,
			 null);
	}
	public FindServicesForModelObjectDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												  final Class<M> modelObjectType,
												  final DBFindForModelObject<O,M> findServices,
												  final EventBus eventBus) {
		super(coreCfg,
			  modelObjectType,
			  findServices,
			  eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<O> findAll(final SecurityContext securityContext) {
		FindOIDsResult<O> outResults = this.getServiceImplAs(FindServicesForModelObject.class)
												.findAll(securityContext);
		return outResults;
	}
	@Override
	public FindOIDsResult<O> findByCreateDate(final SecurityContext securityContext,
										  	  final Range<Date> createDate) {
		// [0] - check the date
		if (createDate == null) {
			return FindOIDsResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.errorFindingOids()
											.causedByClientBadRequest("The date range MUST NOT be null in order to find entities by create date");
		}
		// [1] - do the find
		FindOIDsResult<O> outResults = this.getServiceImplAs(FindServicesForModelObject.class)
												.findByCreateDate(securityContext,
																  createDate);
		return outResults;
	}
	@Override
	public FindOIDsResult<O> findByLastUpdateDate(final SecurityContext securityContext,
											  	  final Range<Date> lastUpdateDate) {
		// [0] - check the date
		if (lastUpdateDate == null) {
			return FindOIDsResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.errorFindingOids()
											.causedByClientBadRequest("The date range MUST NOT be null in order to find entities by last update date");
		}
		// [1] - do the find
		FindOIDsResult<O> outResults = this.getServiceImplAs(FindServicesForModelObject.class)
												.findByLastUpdateDate(securityContext,
																   	   	   lastUpdateDate);
		return outResults;
	}
	@Override
	public FindOIDsResult<O> findByCreator(final SecurityContext securityContext,
									   	   final UserCode creatorUserCode) {
		// [0] - check the date
		if (creatorUserCode == null) {
			return FindOIDsResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.errorFindingOids()
												.causedByClientBadRequest("The user code MUST NOT be null in order to find entities creator");
		}
		// [1] - do the find
		FindOIDsResult<O> outResults = this.getServiceImplAs(FindServicesForModelObject.class)
												.findByCreator(securityContext,
															   creatorUserCode);
		return outResults;
	}
	@Override 
	public FindOIDsResult<O> findByLastUpdator(final SecurityContext securityContext,
										       final UserCode lastUpdatorUserCode) {
		// [0] - check the date
		if (lastUpdatorUserCode == null) {
			return FindOIDsResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.errorFindingOids()
											.causedByClientBadRequest("The user code MUST NOT be null in order to find entities by last updator user code");
		}
		// [1] - do the find
		FindOIDsResult<O> outResults = this.getServiceImplAs(FindServicesForModelObject.class)
												.findByLastUpdator(securityContext,
																   lastUpdatorUserCode);
		return outResults;
	}
}
