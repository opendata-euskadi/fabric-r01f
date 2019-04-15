package r01f.persistence.db.config;

import java.util.Properties;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import r01f.file.FileName;
import r01f.guids.CommonOIDs.AppCode;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public class DBSchemaCreation {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final DBSchemaTablesDDLAction _ddlAction;
	private final Path _ddlScriptPath;
	private final FileName _ddlCreateFileName;
	private final FileName _ddlDropFileName;
	private final Path _dataLoadScriptPath;
	private final Properties _otherProps;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBSchemaCreation(final DBSchemaTablesDDLAction ddlAction,
							final Path ddlScriptPath,final FileName ddlCreateFileName,final FileName ddlDropFileName,
							final Path dataLoadScriptPath,
							final Properties otherProps) {
		_ddlAction = ddlAction;
		_ddlScriptPath = ddlScriptPath;
		_ddlCreateFileName = ddlCreateFileName;
		_ddlDropFileName = ddlDropFileName;
		_dataLoadScriptPath = dataLoadScriptPath;
		_otherProps = otherProps;
	}
	public DBSchemaCreation(final DBSchemaTablesDDLAction ddlAction,
							final Path ddlScriptPath,final FileName ddlCreateFileName,final FileName ddlDropFileName,
							final Path dataLoadScriptPath) {
		this(ddlAction,
			 ddlScriptPath,ddlCreateFileName,ddlDropFileName,
			 dataLoadScriptPath,
			 null);
	}
	public DBSchemaCreation(final DBSchemaTablesDDLAction ddlAction,
							final Path ddlScriptPath,final FileName ddlCreateFileName,final FileName ddlDropFileName,
							final Properties otherProps) {
		this(ddlAction,
			 ddlScriptPath,ddlCreateFileName,ddlDropFileName,
			 null,
			 otherProps);
	}
	public DBSchemaCreation(final DBSchemaTablesDDLAction ddlAction,
							final Path ddlScriptPath,final FileName ddlCreateFileName,final FileName ddlDropFileName) {
		this(ddlAction,
			 ddlScriptPath,ddlCreateFileName,ddlDropFileName,
			 null,
			 null);
	}
	public DBSchemaCreation(final DBSchemaTablesDDLAction ddlAction,
							final Path dataLoadScriptPath,
							final Properties otherProps) {
		this(ddlAction,
			 null,null,null,
			 dataLoadScriptPath,
			 otherProps);
	}
	public DBSchemaCreation(final DBSchemaTablesDDLAction ddlAction,
							final Path dataLoadScriptPath) {
		this(ddlAction,
			 null,null,null,
			 dataLoadScriptPath,
			 null);
	}
	public DBSchemaCreation(final AppCode appCode,
							final DBSpec spec,
							final XMLPropertiesForAppComponent xmlProps) {
		// [1] - Generate schema
		// see
		//		https://wiki.eclipse.org/EclipseLink/Release/2.5/JPA21
		//		http://www.eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_ddl_generation_output_mode.htm
		//		http://wiki.eclipse.org/EclipseLink/DesignDocs/368365)
		_ddlAction = xmlProps.propertyAt("persistence/schema/generationMode")
			   					  .asEnumElement(DBSchemaTablesDDLAction.class,
			   							  		 DBSchemaTablesDDLAction.NONE);

		// [2] == Schema generation from DDL scripts
		// specify the file system directory in which EclipseLink writes (outputs) DDL files. - See more at: http://www.eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_application_location.htm#CACHGDEJ
		String ddlScriptPath = xmlProps.propertyAt("persistence/schema/writeDDLScriptTo")
								 			.asString();	// Path.from(Strings.customized("d:/temp_dev/{}",appCode))
		if (Strings.isNOTNullOrEmpty(ddlScriptPath)) {
			String theDDLScriptPath = ddlScriptPath		// the properties specified path can have placeholders for the appCode
											 .replaceAll("\\{APPCODE\\}",appCode.asString());
			_ddlScriptPath = Path.from(theDDLScriptPath);
		} else {
			_ddlScriptPath = null;
		}
		_ddlCreateFileName = FileName.of(appCode.asString() + "Create.sql");
		_ddlDropFileName = FileName.of(appCode.asString() + "Drop.sql");

		// [3] == Initial load script
		// If the database has to be loaded with initial data this property specifies the
		// SQL load script for database initialization
		String loadScriptPath = xmlProps.propertyAt("persistence/schema/loadScriptPath")
								    		.asString();
		if (Strings.isNOTNullOrEmpty(loadScriptPath)) {
			String theLoadScriptPath = loadScriptPath		// the properties specified path can have placeholders for the appCode
											 .replaceAll("\\{APPCODE\\}",appCode.asString());
			_dataLoadScriptPath = Path.from(theLoadScriptPath);
		} else {
			_dataLoadScriptPath = null;
		}

		// [4] == Other properties
		_otherProps = xmlProps.propertyAt("persistence/schema/properties[@vendor='" + spec.getVendor().getCode() + "']")
									.asProperties();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public Properties asProperties() {
		Properties props = new Properties();

		if (_ddlAction == DBSchemaTablesDDLAction.NONE) return props;

		// [1] == Schema generation from the object/relational mapping metadata
		// specifies the action to be taken by the persistence provider with regard to the database artifacts

		// props.put(PersistenceUnitProperties.SCHEMA_GENERATION_CONNECTION,"");
		props.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS,"false");						// do NOT generate the schema
		props.put(PersistenceUnitProperties.DDL_GENERATION_MODE,PersistenceUnitProperties.DDL_BOTH_GENERATION);		// database / sql-script / both

		// specifies whether the creation of database artifacts is to occur on the basis of
		//		(1) the object/relational mapping metadata,
		//		(2) DDL script,
		//		(3) a combination (1) and (2)
       	props.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SOURCE,"metadata");			// metadata / script / metadata-then-script / script-then-metadata
       	props.put(PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SOURCE,"metadata");				// metadata / script / metadata-then-script / script-then-metadata

       	if (_ddlAction != null) {
	       	props.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION,_ddlAction.getJpaAction());	// none / create / create-or-extend-tables / drop-and-create / drop
	       	props.put(PersistenceUnitProperties.DDL_GENERATION,_ddlAction.getEclipseLinkAction());				// none / create-tables / create-or-extend-tables / drop-and-create-tables

			// Generate indexes for foreign keys: http://java-persistence-performance.blogspot.com.es/2013/06/cool-performance-features-of.html
			props.put(PersistenceUnitProperties.DDL_GENERATION_INDEX_FOREIGN_KEYS,"true");
       	}

		// [2] == Schema generation from DDL scripts
		// specify the file system directory in which EclipseLink writes (outputs) DDL files. - See more at: http://www.eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_application_location.htm#CACHGDEJ
		if (_ddlScriptPath != null) {
			props.put(PersistenceUnitProperties.APP_LOCATION,_ddlScriptPath.asAbsoluteString());

			// specifies which scripts are to be generated by the persistence provider
	       	props.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION,"drop-and-create");		// none / create / drop-and-create / drop

	       	// props.put("PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE","META-INF/create.sql");
			// props.put("PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SCRIPT_SOURCE,"META-INF/drop.jdbc");


			// If JPA GENERATES the schema generation scripts to be executed later
			props.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET,_ddlCreateFileName.asString());
			props.put(PersistenceUnitProperties.CREATE_JDBC_DDL_FILE,_ddlCreateFileName.asString());

			props.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET,_ddlDropFileName.asString());
			props.put(PersistenceUnitProperties.DROP_JDBC_DDL_FILE,_ddlDropFileName.asString());
		}

		// [3] == Initial load script
		// If the database has to be loaded with initial data this property specifies the
		// SQL load script for database initialization
		if (_dataLoadScriptPath != null) {
			props.put(PersistenceUnitProperties.SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE,_dataLoadScriptPath.asAbsoluteString());
		}

		// [4] == Other properties
		if (_otherProps != null) props.putAll(_otherProps);

		return props;
	}
}

