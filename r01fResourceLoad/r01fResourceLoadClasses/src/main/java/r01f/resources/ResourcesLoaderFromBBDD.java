package r01f.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.patterns.Memoized;
import r01f.reflection.ReflectionUtils;
import r01f.resources.db.DBSQLExecutor;
import r01f.types.IsPath;
import r01f.util.types.collections.CollectionUtils;

/**
 * Clase que carga un fichero de propiedades desde la Base de Datos.
 * 
 * Propiedades con la informaci�n de conexi�n a la Base de Datos,
 * la query de consulta del fichero de propiedades y la columna que contiene el fichero.
 *
 * <p>Ejemplo de conexi�n v�a JDBC a la Base de datos:
 * <pre class="brush:xml">
 * <resourcesLoader type='BBDD'>
 * 	<props>
 * 		<class>oracle.jdbc.OracleDriver</class> <!-- Driver de acceso a la base de datos -->
 *		<uri>jdbc:oracle:thin:@ejhp67:1524:ede2</uri> <!-- Cadena de coneon a la BBDD  -->
 *		<user>r01</user> <!-- Usuario de acceso a la base de datos -->
 *		<password>r01</password> <!-- Clave de acceso a la base de datos -->
 *		<query>
 *			<!-- Query para obtener el fichero de propiedades de la base de datos, OBLIGATORIAMENTE se
 *				debe definir un alias llamado PROPERTIES_XML para obtener el campo que almacena las propiedades,
 *				la consulta se realizar� a trav�s de dicho alias. Solamente se consulta la versi�n activa, y adem�s esta
 *				debe ser �nica. -->
 *			<![CDATA[
 *				SELECT DATA_01 AS PROPERTIES_XML
 *				    FROM PROPERTIES_CFG
 *				  WHERE COD_APLIC_01 = 'r01f'
 *					AND COMPONENT_01 ='general'
 *					AND ENVIRONMENT_01 = 'local'
 *					AND VERSION_ON_01 = 1
 *			]]>
 *		</query>
 * 		<updatets>
 * 			<!-- Actualizaci�n de la fecha con la �ltima recarga. -->
 * 			<![CDATA[
 * 				UPDATE PROPERTIES_CFG
 *				  		SET LAST_UPDATE_TS_01 = SYSDATE
 *				 	WHERE COD_APLIC_01 = 'r01f'
 *					   AND COMPONENT_01 ='general'
 *					   AND ENVIRONMENT_01 = 'local'
 *					   AND VERSION_ON_01 = 1
 * 			]]>
 * 		</updatets>
 * 	</props>
 * 	<reloadControl><!-- Definici�n del control de la recarga. --></reloadControl>
 * </resourcesLoader>
 * </pre>
 *
 * <p>Ejemplo de conexi�n con Datasource:
 * <pre class="brush:xml">
 * <resourcesLoader type='BBDD'>
 * 	<props>
 *		<class>DataSource</class>
 *		<uri>r01n.r01nDataSource</uri>
 *		<query>
 *			<!-- Query para obtener el fichero de propiedades de la base de datos, OBLIGATORIAMENTE se
 *				debe definir un alias llamado PROPERTIES_XML para obtener el campo que almacena las propiedades,
 *				la consulta se realizar� a trav�s de dicho alias. -->
 *			<![CDATA[
 *				SELECT DATA_01 AS PROPERTIES_XML
 *				    FROM PROPERTIES_CFG
 *				  WHERE COD_APLIC_01 = 'r01f'
 *					AND COMPONENT_01 ='general'
 *					AND ENVIRONMENT_01 = 'local'
 *			]]>
 *		</query>
 * 		<updatets>
 * 			<!-- Actualizaci�n de la fecha con la �ltima recarga. -->
 * 			<![CDATA[
 * 				UPDATE LAST_UPDATE_TS_01
 * 					SET SYSDATE
 * 				 WHERE COD_APLIC_01 = 'r01f'
 * 				   AND COMPONENT_01 ='general'
 * 				   AND ENVIRONMENT_01 = 'local'
 * 			]]>
 * 		</updatets>
 * 	</props>
 * 	<reloadControl><!-- Definici�n del control de la recarga. --></reloadControl>
 * </resourcesLoader>
 * </pre>
 */
@Accessors(prefix="_")
public class ResourcesLoaderFromBBDD 
     extends ResourcesLoaderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static final String CLASS = "class";
	static final String URI = "uri";
	static final String USER = "user";
	static final String PASSWORD = "password";
	static final String LOAD_SQL = "query";
	static final String UPDATE_TS_SQL = "updatets";
	static final String DEFAULT_LOAD_SQL = "SELECT DATA_01 AS PROPERTIES_XML " +
										     "FROM PROPERTIES_CFG " + 
										    "WHERE COD_APLIC_01 = 'r01f' " +
										      "AND COMPONENT_01 ='general' " +
										      "AND ENVIRONMENT_01 = 'local' " +
										      "AND VERSION_ON_01 = 1";
	static final String DEFAULT_UPDATE_TS_SQL = "UPDATE PROPERTIES_CFG " +
												   "SET LAST_UPDATE_TS_01 = SYSDATE " + 
												 "WHERE COD_APLIC_01 = 'r01f' " + 
												   "AND COMPONENT_01 ='general' " + 
												   "AND ENVIRONMENT_01 = 'local' " + 
												   "AND VERSION_ON_01 = 1";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Properties
	 */
	@Getter @Setter private Properties _dbConnectionProps;
	/**
	 * {@link Memoized} {@link DBSQLExecutor} instance
	 * (uses reflection to create the {@link DBSQLExecutor} avoid de dependency between r01fbClasses & r01fbPersistenceClasses)
	 */
	Memoized<DBSQLExecutor> _sqlExec = new Memoized<DBSQLExecutor>() {
													@Override
													protected DBSQLExecutor supply() {
														DBSQLExecutor sqlExec = ReflectionUtils.createInstanceOf("r01f.persistence.db.sql.DBRawSQLExecutor",
																												 new Class<?>[] {Properties.class},new Object[] {_dbConnectionProps});
														return sqlExec;
													}
									   };

///////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	ResourcesLoaderFromBBDD(final ResourcesLoaderDef def) {
		super(def);
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		boolean outOK = true;
		if (CollectionUtils.isNullOrEmpty(props)) {
			outOK = false;
		} else if (props.get(ResourcesLoaderFromBBDD.CLASS) != null && props.get(ResourcesLoaderFromBBDD.CLASS).equals("DataSource")) {
			outOK = CollectionUtils.of(props)
					 			   .containsAllTheseKeys(ResourcesLoaderFromBBDD.URI,
					 					   				 LOAD_SQL,
													     UPDATE_TS_SQL);
		} else if (props.get(ResourcesLoaderFromBBDD.CLASS) != null && !props.get(ResourcesLoaderFromBBDD.CLASS).equals("DataSource")) {
			outOK = CollectionUtils.of(props)
					 			   .containsAllTheseKeys(ResourcesLoaderFromBBDD.URI,
					 					   				 ResourcesLoaderFromBBDD.USER,ResourcesLoaderFromBBDD.PASSWORD,
					 					   				 LOAD_SQL,
													     UPDATE_TS_SQL);				
		} else {
			outOK = false;
		}
		return outOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    protected InputStream _doGetInputStream(final IsPath resourceName,
    								  	    final boolean reload) throws IOException {
		InputStream fileIS = null;
		if (!_dbConnectionProps.containsKey("query")) {
			throw new IllegalStateException("Revisar la definici�n del componente cuyo 'resourcesLoader' debe contener una propiedad denominada 'query' con la query " +
					 						"para obtener el fichero de propiedades de la base de datos, OBLIGATORIAMENTE se " +
					 						"debe definir un alias llamado PROPERTIES_XML para obtener el campo que almacena las propiedades, " +
					 						"la consulta se realizar� a trav�s de dicho alias.");
		}
		String sql = _dbConnectionProps.get("query").toString();

		List<Map<String,String>> result = null;
		try {
			result = _sqlExec.get().query(sql.toString());
		} catch (SQLException sqlEx) {
			Throwables.throwUnchecked(sqlEx);
		}
		if (result != null) {
			fileIS = new ByteArrayInputStream(result.get(0).get("PROPERTIES_XML")
														   .toString().getBytes(this.getConfig().getCharset()));
			_updateReloadTS();	// Actualizar la fecha de la �ltima actualizaci�n
		}
        return fileIS;
    }
    /**
	 * Actualizar con la fecha actual la fecha de �ltima recarga del fichero de propiedades.
	 */
	private void _updateReloadTS() {
		if (!_dbConnectionProps.containsKey("updatets")) {
			throw new IllegalStateException("Revisar la definici�n del componente cuyo 'resourcesLoader' debe contener una propiedad denominada 'updatets' con " +
					 						"la update para la actualizaci�n de la fecha con la �ltima recarga del fichero de propiedades.");
		}

		String updateSQL = _dbConnectionProps.get("updatets").toString();
		try {
			_sqlExec.get().update(updateSQL);
		} catch (SQLException sqlEx) {
			Throwables.throwUnchecked(sqlEx);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////

}
