package r01f.persistence.db.config;

import java.util.Properties;
import java.util.logging.Level;

import javax.persistence.EntityManager;

import r01f.config.ContainsConfigData;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;

/**
 * db module config
 */
public interface DBModuleConfig
		 extends ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the app code
	 */
	public CoreAppCode getAppCode();
	/**
	 * @return the app module
	 */
	public CoreModule getAppModule();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the {@link PersistenceUnitType}
	 */
	public PersistenceUnitType getUnitType();
	/**
	 * @return the {@link DBSpec}
	 */
	public DBSpec getDbSpec();
	/**
	 * @return how the DB schema will be created
	 */
	public DBSchemaCreation getDbSchemaCreation();
	/**
	 * @return the log leve
	 */
	public Level getLogLevel();
	/**
	 * @return other {@link Properties}
	 */
	public Properties getOtherProps();
	/**
	 * @return all the properties as a {@link Properties} object
	 */
	public Properties asProperties();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Override this method like:
	 * <pre class='brush:java'>
	 * 		@Override
	 *		public boolean isFullTextSearchSupported(final EntityManager entityManager) {
	 *			return _fullTextSearchSupported.get(new Factory<Boolean>() {
	 *														@Override
	 *														public Boolean create() {
	 *															// tries to run a full-text search... if it fails full text search is NOT enabled
	 *															return _testFullText(entityManager,
	 *																				 MyDBEntity.class,
	 *																				 "_fullText");	// any full-text indexed col
	 *														}
	 *												});
	 *		}
	 * </pre>
	 * @param entityManager
	 * @return
	 */
	public boolean isFullTextSearchSupported(final EntityManager entityManager);
}

