package r01f.persistence.db.config;

import java.util.Properties;
import java.util.logging.Level;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;

/**
 * Wraps a DBModuleConfig to extends the properties
 * ... for example when the full text support is guessed at run-time
 * <pre class='brush:java'>
 * 
 *		public class MyDBModuleConfig 
 *			 extends DBModuleConfigWrapper {
 *
 *			private final MemoizedUponFactory<Boolean> _fullTextSearchSupported = new MemoizedUponFactory<Boolean>();
 *			
 *			public MyDBModuleConfig(final DBModuleConfig _wrappedConfig) {
 *				super(_wrappedConfig);
 *			}
 *			public static final MyDBModuleConfig dbConfigFor(final XMLPropertiesForAppComponent xmlProps) {
 *				DBModuleConfig dbModuleConfig = DBModuleConfigBuilder.dbConfigFor(xmlProps);
 *				return new MyDBModuleConfig(dbModuleConfig);		// wrap
 *			}
 *			public static MyDBModuleConfig dbModuleConfigFrom(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg) {
 *				DBModuleConfig dbModuleConfig = coreCfg.getSubModuleConfigFor(CoreModule.DBPERSISTENCE);
 *				return new MyDBModuleConfig(dbModuleConfig);		// wrap
 *			}
 *			@Override
 *			public boolean isFullTextSearchSupported(final EntityManager entityManager) {
 *				return _fullTextSearchSupported.get(new Factory<Boolean>() {
 *															@Override
 *															public Boolean create() {
 *																// tries to run a full-text search... if it fails full text search is NOT enabled
 *																return DBModuleConfigBase.<MyDBEntityForContent>testFullText(entityManager,
 *																					 										   MyDBEntityForContent.class,
 *																					 										   "_fullTextProp");	// any full-text indexed col
 *															}
 *													});
 *			}
 *
 * </pre>
 */
@RequiredArgsConstructor
public class DBModuleConfigWrapper 
  implements DBModuleConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final DBModuleConfig _wrappedConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CoreAppCode getAppCode() {
		return _wrappedConfig.getAppCode();
	}
	@Override
	public CoreModule getAppModule() {
		return _wrappedConfig.getAppModule();
	}
	@Override
	public PersistenceUnitType getUnitType() {
		return _wrappedConfig.getUnitType();
	}
	@Override
	public DBSpec getDbSpec() {
		return _wrappedConfig.getDbSpec();
	}
	@Override
	public DBSchemaCreation getDbSchemaCreation() {
		return _wrappedConfig.getDbSchemaCreation();
	}
	@Override
	public Level getLogLevel() {
		return _wrappedConfig.getLogLevel();
	}
	@Override
	public Properties getOtherProps() {
		return _wrappedConfig.getOtherProps();
	}
	@Override
	public Properties asProperties() {
		return _wrappedConfig.asProperties();
	}
	@Override
	public boolean isFullTextSearchSupported(final EntityManager entityManager) {
		return _wrappedConfig.isFullTextSearchSupported(entityManager);
	}
}
