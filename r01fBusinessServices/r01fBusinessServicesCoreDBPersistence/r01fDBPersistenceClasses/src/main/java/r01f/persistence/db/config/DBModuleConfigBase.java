package r01f.persistence.db.config;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.patterns.Memoized;
import r01f.patterns.MemoizedUponFactory;
import r01f.persistence.db.DBEntity;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public abstract class DBModuleConfigBase
    	   implements DBModuleConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final CoreAppCode _appCode;
	@Getter protected final CoreModule _appModule;
	@Getter protected final PersistenceUnitType _unitType;
	@Getter protected final DBSpec _dbSpec;
	@Getter protected final DBSchemaCreation _dbSchemaCreation;
	@Getter protected final Level _logLevel;
	@Getter protected final Properties _otherProps;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public DBModuleConfigBase(final CoreAppCode appCode,final CoreModule appModule,
							  final PersistenceUnitType unitType,
							  final DBSpec dbSpec,
							  final DBSchemaCreation dbSchemaCreation,
							  final Level logLevel,
							  final Properties otherProps) {
		_appCode = appCode;
		_appModule = appModule;
		_unitType = unitType;
		_dbSpec = dbSpec;
		_dbSchemaCreation = dbSchemaCreation;
		_logLevel = logLevel;
		_otherProps = otherProps;
	}
	public DBModuleConfigBase(final CoreAppCode appCode,final CoreModule appModule,
							  final PersistenceUnitType unitType,final DBSpec dbSpec,
							  final DBSchemaCreation dbSchemaCreation,
							  final Level logLevel) {
		this(appCode,appModule,
			 unitType,dbSpec,
			 dbSchemaCreation,
			 logLevel,
			 null);
	}
	public DBModuleConfigBase(final CoreAppCode appCode,final CoreModule appModule,
							  final PersistenceUnitType unitType,final DBSpec dbSpec,
							  final DBSchemaCreation dbSchemaCreation) {
		this(appCode,appModule,
			 unitType,dbSpec,
			 dbSchemaCreation,
			 Level.INFO,
			 null);
	}
	public DBModuleConfigBase(final XMLPropertiesForAppComponent xmlProps,
							  final PersistenceUnitType unitType) {
		// [0] - Get the appcode and module
		//		 (beware that the module can be [module].dbpersistence so the 'dbpersistence' suffix must be stripped out)
		_appCode = CoreAppCode.of(xmlProps.getAppCode());
		String modStr = xmlProps.getAppComponent().asString();
		_appModule = modStr.contains(".dbpersistence") 
							? CoreModule.forId(modStr.substring(0,modStr.indexOf(".dbpersistence")))
							: CoreModule.of(xmlProps.getAppComponent());															

		// [1] - Unit Type
		_unitType = unitType;

		// [2] - DBSpec
		String targetDB = xmlProps.propertyAt("persistence/unit[@type='" + _unitType + "']/@targetDB")
										.asString();
		if (Strings.isNullOrEmpty(targetDB)) {
			targetDB = xmlProps.propertyAt("persistence/unit[@type='" + _unitType + "']/@targetBBDD")
									.asString();
			if (Strings.isNOTNullOrEmpty(targetDB)) log.warn("using an old db persistence properties XML file spec: xpath 'persistence/unit[@type='{}']/@targetBBDD' should be 'persistence/unit[@type='{}']/@targetBBDD' (the word targetBBDD has changed to targetDB",
															 _unitType,_unitType);
		}
		if (Strings.isNullOrEmpty(targetDB)) {
			log.error("Could NO load the DB vendor and version from persistence/unit[@type='{}']/@targetDB at {} properties file... {} is used by default",
					  _unitType,_appCode,DBSpec.DEFAULT);
			targetDB = DBSpec.DEFAULT;	// MySql is the default
		}
		_dbSpec = DBSpec.valueOf(targetDB);

		// [3] - schema creation
		_dbSchemaCreation = new DBSchemaCreation(_appCode,
												 _dbSpec,
												 xmlProps);
		// [4] - Log level
		String logXPath = "persistence/debugSQL";
		Level logLevel = Level.INFO;

		// the debugSQL property might contain true/false or a log Level name (off, warn, error, etc)
		String debugSQLValue = xmlProps.propertyAt(logXPath)
											.asString("false");
		if (debugSQLValue.equalsIgnoreCase("false")) {
			logLevel = Level.INFO;
		} else if (debugSQLValue.equalsIgnoreCase("true")) {
			logLevel = Level.FINEST;
		} else {
			// the debugSQL property might contain a Level name
			try {
				logLevel = Level.parse(debugSQLValue);
			} catch(Throwable th) {
				th.printStackTrace(System.out);
				logLevel = Level.FINEST;
			}
		}
		_logLevel = logLevel;

		// [5] - Caching
		// CACHING http://wiki.eclipse.org/EclipseLink/Examples/JPA/Caching -->
		// Es importante DESHABILITAR el cache en AWS ya que hay multiples instancias del servidor de apps -->
		// En caso de HABILITAR el cache en AWS hay que coordinar las caches: http://wiki.eclipse.org/EclipseLink/UserGuide/JPA/sandbox/caching/Cache_Coordination -->
		// <property name="eclipselink.cache.shared.default" value="false"/>

		// [6] - Other properties
		String otherPropsXPath = "persistence/unit[@type='" + _unitType + "']/properties";
		_otherProps = xmlProps.propertyAt(otherPropsXPath)
							   	.asProperties();
		if (CollectionUtils.isNullOrEmpty(_otherProps)) log.info("There're NO aditional JPA properties at {} at properties file {}",
					   											 otherPropsXPath,_appCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Properties asProperties() {
		Properties appServerVendorProps = this.appServerVendorProperties();
		Properties dbVendorProps = this.dbVendorProperties();
		Properties schemaCreateProps = this.getDbSchemaCreation().asProperties();
		Properties logProps = this.logProperties();
		Properties otherProps = this.getOtherProps();

		// a bit of log
		if (appServerVendorProps != null) 	_logProps("APPSERVER VENDOR",appServerVendorProps);
		if (dbVendorProps != null) 			_logProps("DB VENDOR",dbVendorProps);
		if (schemaCreateProps != null) 		_logProps("DB SCHEMA CREATE",schemaCreateProps);
		if (logProps != null) 				_logProps("LOG",logProps);
		if (otherProps != null) 			_logProps("OTHER",otherProps);

		// put all props in a single properties
		final Properties outProps = new Properties();
		if (appServerVendorProps != null) 	outProps.putAll(appServerVendorProps);
		if (dbVendorProps != null) 			outProps.putAll(dbVendorProps);
		if (schemaCreateProps != null) 		outProps.putAll(schemaCreateProps);
		if (logProps != null) 				outProps.putAll(logProps);
		if (otherProps != null) 			outProps.putAll(otherProps);


		return outProps;
	}
	public Properties appServerVendorProperties() {
		Properties props = new Properties();
		//props.put(PersistenceUnitProperties.TARGET_SERVER,TargetServer.WebLogic_10);
		return props;
	}
	public Properties dbVendorProperties() {
		Properties props = new Properties();

		props.put(PersistenceUnitProperties.TARGET_DATABASE,_dbSpec.getVendor().getCode());	// MySQLPlatformExtension.class.getCanonicalName()

		// used when generating schema
		props.put(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME,_dbSpec.getVendor().getCode());
		props.put(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION,_dbSpec.getMajorVersion());
		props.put(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION,_dbSpec.getMinorVersion());

		// enable innoDB tables in MySql (needed for full-text searching)
		if (_dbSpec.getVendor().is(DBVendor.MySQL)) {
			props.put("eclipselink.ddl.default-table-suffix","engine=InnoDB");
		}
		return props;
	}
	public Properties logProperties() {
		// Set the log level: see Logging: http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging
		Properties props = new Properties();
		props.put(PersistenceUnitProperties.LOGGING_LOGGER,"ServerLogger");
		props.put(PersistenceUnitProperties.LOGGING_LEVEL,_logLevel != null ? _logLevel.getName() : Level.INFO.getName());
		props.put(PersistenceUnitProperties.LOGGING_TIMESTAMP,"false");
		props.put(PersistenceUnitProperties.LOGGING_THREAD,"true");
		props.put(PersistenceUnitProperties.LOGGING_SESSION,"true");
		props.put(PersistenceUnitProperties.LOGGING_CONNECTION,"true");
		props.put(PersistenceUnitProperties.LOGGING_EXCEPTIONS,"true");
		props.put(PersistenceUnitProperties.LOGGING_PARAMETERS,"true");
		return props;
	}
	protected static void _logProps(final String propsId,final Properties props) {
		log.warn("\tDDBB {} PROPERTIES:::::::::::::::::::::::::",propsId);
		Set<Entry<Object,Object>> propEntries = props.entrySet();
		for (Entry<Object,Object> propEntry : propEntries) {
			log.warn("\t\t{} = {}",propEntry.getKey(),propEntry.getValue());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TEST FULLT-TEXT DB CAPABILITIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isFullTextSearchSupported(final EntityManager entityManager) {
		return false;	// not supported by default
	}
	/**
	 * tries to run a full-text search... if it fails full text search is NOT enabled
	 * <ul>
	 * 	<li>If full text support is FALSE, the full-text queries are translated to LIKE queries</li>
	 *  <li>If full text support is TRUE, the full-text queries are translated to vendor-specific operators
	 *	  							  ...so the underlying database MUST support full-text searching
	 *	  		<ul>
	 *				<li>MySQL: Tables MUST be MyISAM and an index MUST be created on the cols to be searched</li>
	 *				<li>ORACLE: Oracle-Text MUST be enabled and an index MUST be created on the cols to be searched</li>
	 *			</ul>
	 *  </li>
	 * </ul>
	 * In order to avoid calls to {@link #_isFullTextSupported()} usually a {@link Memoized} instance
	 * is used like:
	 * <pre class='brush:java'>
	 *		private final Memoized<Boolean> _fullTextSearchSupported = new Memoized<Boolean>() {
	 *																			@Override
	 *																			protected Boolean supply() {
	 *																				// tries to run a full-text search... if it fails full text search is NOT enabled
	 *																				return _testFullText(_entityManager,
	 *																									 MyDBEntity.class,
	 *																									 "_fullText");	// any full-text indexed col
	 *																			}
	 *																   };
	 *		protected boolean _isFullTextSupported() {
	 *			return _fullTextSearchSupported.get();
	 *		}
	 * </pre> 
	 * ... but the EntityManager is NOT available when creating the config so a {@link MemoizedUponFactory} can be used like:
	 * <pre class='brush:java'>
	 * 		private final MemoizedUponFactory<Boolean> _fullTextSearchSupported = new MemoizedUponFactory<Boolean>();
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
	 * @param em
	 * @parm dbEntity the type of a db entity that has a full-text indexed field
	 * @param fieldName the db entity's field name
	 * @return
	 */
	public static <DB extends DBEntity> boolean testFullText(final EntityManager em,
										 					 final Class<DB> dbEntity,
										 					 final String fieldName) {
		DBSpec theDBSpec = DBSpec.usedAt(em);
		
		boolean outSupportsFullText = false;
		// SELECT entity
		//	 FROM AA14DBEntityForOrganizationalEntityBase entity 
		//  WHERE ... predicates ...
		StringBuilder jpql = new StringBuilder("SELECT entity " +
										  		 "FROM ").append(dbEntity.getSimpleName()).append(" entity ")
										.append("WHERE ");
		if (theDBSpec.getVendor().is(DBVendor.MySQL)) {
			jpql.append(Strings.customized("SQL('MATCH(?) AGAINST(?)',entity.{},:text)",
										   fieldName));
		} else if (theDBSpec.getVendor().is(DBVendor.ORACLE)) {
			// SELECT t.*
			//   FROM {table} t 
			//  WHERE CONTAINS({column},'{text}',1) > 0
			jpql.append(Strings.customized("SQL('CONTAINS(?,?,1) > 0',entity.{},:text) ",
										   fieldName));
		}
		// Execute the query
		try {
			TypedQuery<DB> qry = em.createQuery(jpql.toString(),
												dbEntity);
			qry.setParameter("text","anything...");
			qry.getResultList();			// this should fail if full text is NOT properly enabled
			outSupportsFullText = true;		// it it reach this point... full text is enabled	
		} catch(Throwable dbEx) {
			log.error("It seems the {} DB engine does NOT supports FULL-TEXT queries: testing {} leads to an error: {}",
					  theDBSpec.getVendor(),jpql,
					  dbEx.getMessage(),
					  dbEx);
		}
		// log
		log.warn("**************************************************************************************************************************************************************************");
		if (theDBSpec.getVendor().is(DBVendor.MySQL)) {
			log.warn("The {} db store does{} supports full-text searching: ensure that the tables are MyISAM type and that a FULLTEXT index exists on name cols of entity table",
					 DBVendor.MySQL,
					 outSupportsFullText ? "" : " NOT");
		} else if (theDBSpec.getVendor().is(DBVendor.ORACLE)) {
			log.warn("The {} db store does{} supports full-text searching: ensure that ORACLE-TEXT is enabled and that a FULLTEXT index exists on name cols of entity table",
					 DBVendor.ORACLE,
					 outSupportsFullText ? "" : " NOT");
		}
		log.warn("**************************************************************************************************************************************************************************");
		return outSupportsFullText;
	}

}
