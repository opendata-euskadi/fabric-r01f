package r01f.services.persistence;

import java.util.Date;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.common.eventbus.EventBus;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.PersistableObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindOIDsResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.FindServicesForModelObject;
import r01f.types.Range;


/**
 * Core service base for persistence services
 * 
 * INJECTED STATUS
 * ===============
 * 	The TRANSACTIONAL methods are located at the Services layer (this type); 
 * 	Some operations might span multiple @Transactional-annotated methods so it's very important
 *  	for the {@link EntityManager} to have Extended-scope
 * 		see http://piotrnowicki.com/2012/11/types-of-entitymanagers-application-managed-entitymanager/
 * 		 or http://www.javacodegeeks.com/2013/06/jpa-2-entitymanagers-transactions-and-everything-around-it.html 
 *    	
 * 	The {@link EntityManager} should be created at the services layer (this type) and handled to every delegated
 * 	type (crud, search, etc)
 * 
 *  	Beware that:
 *  	<ul>
 *  		<li>This type is (at the end) injected in a service-layer that usually is a {@link Singleton} instance</li>
 *  		<li>{@link EntityManager} is NOT usually thread safe and an {@link EntityManagerFactory} should be used if the type is NOT thread safe to create an {@link EntityManager}</li>
 *  		<li>Because this type is (at the end) injected in a {@link Singleton} and it's NOT thread-safe, the {@link EntityManagerFactory} should be used</li>
 *  		<li>When creating an {@link EntityManager} from a {@link EntityManagerFactory} the application is responsible for creation and
 *  			removal of the {@link EntityManager}... so it's an [Application-Managed {@link EntityManager}] and these types of
 *  			managers ALLWAYS have EXTENDED SCOPE (see http://piotrnowicki.com/2012/11/types-of-entitymanagers-application-managed-entitymanager/)</li>
 *  	</ul>
 *  	See
 *  	<ul> 
 *  		<li>http://www.javacodegeeks.com/2013/06/jpa-2-entitymanagers-transactions-and-everything-around-it.html</li>
 *  		<li>http://piotrnowicki.com/2012/11/types-of-entitymanagers-application-managed-entitymanager/</li>
 *  	</ul>
 *
 * @param <O>
 * @param <M>
 * @param <FD>
 */
@Accessors(prefix="_")
public abstract class CoreFindServicesForModelObjectBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
			  extends CorePersistenceServicesBase 
		   implements FindServicesForModelObject<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 * @param modelObjectsMarshaller 
	 * @param eventBus
	 * @param entityManagerProvider
	 */
	public CoreFindServicesForModelObjectBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											  final Marshaller modelObjectsMarshaller,
									   		  final EventBus eventBus,
									   		  final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public FindOIDsResult<O> findAll(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(FindServicesForModelObject.class)
							.findAll(securityContext);
	}
	@Override
	public FindOIDsResult<O> findByCreateDate(final SecurityContext securityContext,
											  final Range<Date> createDate) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(FindServicesForModelObject.class)
							.findByCreateDate(securityContext,
											  createDate);
	}
	@Override
	public FindOIDsResult<O> findByLastUpdateDate(final SecurityContext securityContext,
												  final Range<Date> lastUpdateDate) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(FindServicesForModelObject.class)
							.findByLastUpdateDate(securityContext,
												  lastUpdateDate);
	}
	@Override 
	public FindOIDsResult<O> findByCreator(final SecurityContext securityContext,
										   final UserCode creatorUserCode) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(FindServicesForModelObject.class)
							.findByCreator(securityContext,
										   creatorUserCode);
	}
	@Override 
	public FindOIDsResult<O> findByLastUpdator(final SecurityContext securityContext,
											   final UserCode lastUpdtorUserCode) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(FindServicesForModelObject.class)
							.findByLastUpdator(securityContext,
											   lastUpdtorUserCode);
	}
}
