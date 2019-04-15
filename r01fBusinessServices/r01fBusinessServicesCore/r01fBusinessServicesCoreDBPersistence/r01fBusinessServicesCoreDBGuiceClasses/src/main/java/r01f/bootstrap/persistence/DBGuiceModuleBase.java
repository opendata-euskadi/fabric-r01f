package r01f.bootstrap.persistence;

import java.util.Properties;

import javax.inject.Inject;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.bootstrap.services.core.DBPersistenceGuiceModule;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.config.PersistenceUnitType;
import r01f.service.ServiceHandler;
import r01f.util.types.Strings;

/**
 * Base type for DB guice modules
 */
@Slf4j
public abstract class DBGuiceModuleBase
     	   implements DBPersistenceGuiceModule {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encapsulates the db config
	 */
	private final DBModuleConfig _dbConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBGuiceModuleBase(final DBModuleConfig config) {
		_dbConfig = config;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GUICE MODULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		Binder theBinder = binder;
		
		// bind the config
		binder.bind(DBModuleConfig.class)
			  .toInstance(_dbConfig);

		// Sometimes it's an app component (ie: the urlAlias component for the r01t app)
		// On these cases:
		//	- the persistence properties are going to be looked after as  {appCode}.{appComponent}.persistence.properties.xml
		//	- the persistence unit is going to be looked after as persistenceUnit.{appCode}.{appComponent}
		//	  otherwise (no appComponent is set):
		String jpaServiceName = _dbConfig.getAppModule() == null ? _dbConfig.getAppCode().asString()
													  		     : Strings.customized("{}.{}",
													  				   				  _dbConfig.getAppCode(),_dbConfig.getAppModule());

		// Load properties
		Properties props = _dbConfig.asProperties();

		// Create the module
		String jpaModuleName = _persistenceUnitName(_dbConfig.getUnitType());
		JpaPersistModule jpaModule = new JpaPersistModule(jpaModuleName);	// for an alternative way see http://stackoverflow.com/questions/18101488/does-guice-persist-provide-transaction-scoped-or-application-managed-entitymanag
		jpaModule.properties(props);
		theBinder.install(jpaModule);


		// Service handler used to control (start/stop) the Persistence Service (see ServletContextListenerBase)
		// do NO forget!!
		ServicesBootstrapUtil.bindServiceHandler(theBinder,
												 JPAPersistenceServiceControl.class,jpaServiceName);
		// this is IMPORTANT (and cannot be moved to ServicesBootstrapUtil.bindServiceHandler)
		if (binder instanceof PrivateBinder) {
			PrivateBinder privateBinder = (PrivateBinder)binder;
			privateBinder.expose(Key.get(ServiceHandler.class,
										 Names.named(jpaServiceName)));	// expose the binding
		}

		log.warn("... binded jpa persistence unit {} whose entity manager is handled by ServiceHandler with name {}",
				 jpaModuleName,jpaServiceName);
	}
	protected String _persistenceUnitName(final PersistenceUnitType persistenceUnitType) {
		// Sometimes it's an app component (ie: the urlAlias component for the r01t app)
		// On these cases:
		//	- the persistence properties are going to be looked after as  {appCode}.{appComponent}.persistence.properties.xml
		//	- the persistence unit is going to be looked after as persistenceUnit.{appCode}.{appComponent}
		//	  otherwise (no appComponent is set):
		//	- the persistence properties are going to be looked after as  {appCode}.persistence.properties.xml
		//	- the persistence unit is going to be looked after as persistenceUnit.{appCode}
		String jpaServiceName = _dbConfig.getAppModule() == null ? _dbConfig.getAppCode().asString()
													  		     : Strings.customized("{}.{}",
													  				   				  _dbConfig.getAppCode(),_dbConfig.getAppModule());
//		final String persistenceUnitName = Strings.customized("persistenceUnit.{}.{}",
//													 	 	  jpaServiceName,			// ie: "r01t.urlAlias"
//													 	 	  persistenceUnitType);		// ie: "persistenceUnit.r01n.myComponent.dataSource"
		final String persistenceUnitName = Strings.customized("persistenceUnit.{}",
												 	 	      jpaServiceName);			// ie: "r01t.urlAlias"
		return persistenceUnitName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PersistenceService control
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree
	 * The {@link ServiceHandler} interface is used to start & stop the JPA's PersistenceService
	 * at ServletContextListenerBase type
	 */
	static class JPAPersistenceServiceControl
	  implements ServiceHandler {

		private final PersistService _service;

		@Inject
		public JPAPersistenceServiceControl(final PersistService service) {
			_service = service;
		}
		@Override
		public void start() {
			if (_service == null) throw new IllegalStateException("NO persistence service available!");
			log.warn("######################################################################################");
			log.warn("Starting PersistService");
			log.warn("######################################################################################");
			_service.start();
		}
		@Override
		public void stop() {
			if (_service == null) throw new IllegalStateException("NO persistence service available!");
		log.warn("######################################################################################");
		log.warn("Stopping PersistService");
		log.warn("######################################################################################");
			try {
				_service.stop();
			} catch (Throwable th) {/* just in the case where PersistenceService were NOT started */ }
		}
	}
}
