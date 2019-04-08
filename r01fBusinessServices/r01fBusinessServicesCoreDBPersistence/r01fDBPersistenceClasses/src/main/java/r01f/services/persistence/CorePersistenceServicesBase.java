package r01f.services.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import com.google.common.eventbus.EventBus;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.facets.HasTenantID;
import r01f.guids.CommonOIDs.TenantID;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.HasEntityManagerProvider;
import r01f.securitycontext.SecurityContext;

@Slf4j
@Accessors(prefix="_")
public abstract class CorePersistenceServicesBase
 	   		  extends CoreServicesBase 
 	   	   implements HasEntityManagerProvider,
 	   	   			  HasMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * {@link EntityManager} provider
	 */
	@Getter protected final Provider<EntityManager> _entityManagerProvider;	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param cfg core config
	 * @param modelObjectsMarshaller annotated with @ModelObjectsMarshaller
	 * @param eventBus
	 * @param entityManagerProvider
	 */
	public CorePersistenceServicesBase(final ServicesCoreBootstrapConfigWhenBeanExposed cfg,
									   final Marshaller modelObjectsMarshaller,
									   final EventBus eventBus,
									   final Provider<EntityManager> entityManagerProvider) {
		super(cfg,
			  modelObjectsMarshaller,
			  eventBus);
		_entityManagerProvider = entityManagerProvider;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public EntityManager getFreshNewEntityManager() {
		return _getFreshNewEntityManager(null,
										 false);	// tenant IS null	
	}
	@Override
	public EntityManager getFreshNewEntityManager(final TenantID tenantId) {
		return _getFreshNewEntityManager(tenantId,
										 true);		// tenant id should NOT be null		
	}
	@Override
	public EntityManager getFreshNewEntityManager(final SecurityContext securityContext) {
		if (securityContext instanceof HasTenantID) {
			return _getFreshNewEntityManager(((HasTenantID)securityContext).getTenantId(),
										 	 true);		// tenant id should NOT be null	
		} 
		return _getFreshNewEntityManager(null,
										 false);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private EntityManager _getFreshNewEntityManager(final TenantID tenantId,
													final boolean tenantIdShouldNOTBeNull) {
		return CorePersistenceServicesBase.freshNewEntityManager(_entityManagerProvider,
																 tenantId,
																 tenantIdShouldNOTBeNull,
																 this.getClass());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static EntityManager freshNewEntityManager(final Provider<EntityManager> entityManagerProvider,
													  final Class<?> callerType) {
		return CorePersistenceServicesBase.freshNewEntityManager(entityManagerProvider,
																 null,false,	// null tenantId
																 callerType);	
	}
	public static EntityManager freshNewEntityManager(final Provider<EntityManager> entityManagerProvider) {
		return CorePersistenceServicesBase.freshNewEntityManager(entityManagerProvider,
																 null,false,	// null tenantId
																 CorePersistenceServicesBase.class);	
	}
	public static EntityManager freshNewEntityManager(final Provider<EntityManager> entityManagerProvider,
													  final TenantID tenantId,
													  final boolean tenantIdShouldNOTBeNull) {
		return CorePersistenceServicesBase.freshNewEntityManager(entityManagerProvider,
																 tenantId,tenantIdShouldNOTBeNull,	
																 CorePersistenceServicesBase.class);
	}
	public static EntityManager freshNewEntityManager(final Provider<EntityManager> entityManagerProvider,
													  final TenantID tenantId,
													  final boolean tenantIdShouldNOTBeNull,
													  final Class<?> callerType) {
		EntityManager outEntityManager = null;
		
		// tenant
		if (tenantId != null) {	
			Map<String,Object> emfProperties = new HashMap<String,Object>();
			emfProperties.put("eclipselink.tenant-id",
							  tenantId.asString());
						
			// There are 3 options for the [Persistence Unit] when using multi-tenants (see https://wiki.eclipse.org/EclipseLink/Examples/JPA/Multitenant)
			//		NOTE:	[Persistence Unit]: 	The [EntityManagerFactory] + the cache							--> Injected at @PersistenceUnit
			//				[Persistence Context]: 	The [EntityManager] created from the [EntityManagerFactory]		--> Injected at @PersistenceContext
			//
			// [1] - Dedicated [Persistence Unit] / Tenant --> 	[Persistence Unit] per tenant (one persistence.xml file per tenant)
			//													Each persistence.xml file MUST set the tenant-id using a property
			//												  		<properties>
			//															<property name="eclipselink.tenant-id" value="{tenantId}"/>
			//															...
			//														</properties>
			//										  			The application MUST request/use the correct [Persistence Unit]
			//
			// [2] - [Persistence Context] / Tenant 		--> There's a single [Persistence Unit] (a single persistence.xml file) for all tenants
			//													... There is a SINGLE [EntityManagerFactory] / cache so when creating the
			//														[Persistence Context] (the [EntityManager]) the tenantId MUST be set
			//															Map<String,Object> emfProps = new HashMap<String,Object>();
			//															emfProps.put("eclipselink.tenant-id",{tenantId});
			//															entityManagerFactory.createEntityManager(emfProps);
			// 
			// [3] - [Persistence Unit] / Tenant			--> There's a single [Persistence Unit] (a single persistence.xml file) for all tenants
			//													... Every application creates it's OWN [EntityManagerFactory / cache by using:
			//															EntityManagerFactory emf = Persistence.createEntityManagerFactory();
			
			// The problem here is that there's NO way to use GUICE-Persist and the option [2] ([Persistence Context] / Tenant:
			//		... although the EntityManagerFactory can be injected (a Provider<EntityManagerFactory> 
			//			and a [Persistence Context] (an [EntityManager]) can be obtained for the correct tenant
			//			the returned [EntityManager] is NOT binded to the transaction
			//			... so the only option is to manually manage the transactions
			
//			outEntityManager = _entityManagerFactoryProvider.get().createEntityManager(emfProperties);
			
			// ... so fallback to the option [1]
			outEntityManager = entityManagerProvider.get();
		} else {
			if (tenantIdShouldNOTBeNull) log.warn("A call to get an entity manager for a tenant BUT no tenant id was provided; if no tenancy is used, do NOT call {}.getFreshNewEntityManager(tenantId) method, call {}.getFreshNewEntityManager() instead!",
					 							  callerType.getName(),callerType.getName());
			outEntityManager = entityManagerProvider.get();
		}
		 
		
		// TODO needs some research... really must have to call clear??
		//		(see http://stackoverflow.com/questions/9146239/auto-cleared-sessions-with-guice-persist)
		//
		// If em.clear() is called, the following DOES NOT works
		//		public class MyTransactionalService {
		//			private final Provider<EntityManager> _emProvider;
		//			private final MyOTHERTransactionalService _otherTransactionalService;
		//			
		//			@Inject 
		//			public MyTransactionalService(final Provider<EntityManager> emProvider
		//										  final MyOTHERTransactionalService otherTransactionalService) {
		//				_emProvider = emProvider;
		//				_otherTransactionalService = otherTransactionalService;
		//			}
		//
		//			@Transactional
		//			public void anyTransactionalMethod() {
		//				MyDAO dao = new MyDAO(_emProvider.get());
		//				dao.doSomeDBStuff();
		//
		//				_otherTransactionalService.doSomeDBStuff();		<!-- if this does NOT exists, everything works fine 
		//			}														 ... BUT it just fails because that:   
		//		}																 1:  _otherTransactionalService calls _emProvider.get() that returns the SAME EntityMangager
		//		public class MyOTHERTransactionalService {						     since everything is run in the SAME THREAD (it'll work if _otherTransactionalService.doSomeDBStuff() was run in other thread)
		//			private final Provider<EntityManager> _emProvider;			 2: em.clear() is called
		//			
		//			@Inject 
		//			public MyOTHERTransactionalService(final Provider<EntityManager> emProvider) {
		//				_emProvider = emProvider;
		//			}
		//
		//			@Transactional
		//			public void anyTransactionalMethod() {
		//				MyOTHERDAO dao = new MyOTHERDAO(_emProvider.get());		<-- as the guice's JpaPersistService EntityManager provider returns a ThreadLocal-stored EntityManager
		//				dao.doSomeDBStuff();										and the service is called in the SAME thread, the SAME EntityManager as for MyTransactionalService 
		//			}																is returned
		//		}																	... so if em.clear() is called, ALL PREVIOUS DB work is DISCARDED!!
		//
		//																			NOTE that everything would have work if run in ANOTHER Thread since guice's JpaPersistService EntityManager provider 
		//																			returns a ThreadLocal-stored EntityManager 
		//
		if (!outEntityManager.isJoinedToTransaction()) {
			outEntityManager.clear();	// BEWARE that in the SAME thread the EntityManagerProvider reuses EntityManager storing it at a ThreadLocal store
										// since the EntityManager could have cached entity instances... discard them all
			outEntityManager.setFlushMode(FlushModeType.COMMIT);
		}		
		return outEntityManager;
	}
}
