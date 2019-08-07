package r01f.resources;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.patterns.Memoized;
import r01f.reflection.ReflectionUtils;
import r01f.resources.db.DBSQLExecutor;
import r01f.util.types.collections.CollectionUtils;


/**
 * Implementaci�n de la pol�tica de recarga de propiedades en base a un token en BBDD.<br>
 * <p>En los par�metros de configuraci�n de la pol�tica de recarga se ha de identificar:<br>
 * <ul>
 * 		<li>El dataSource para obtener la conexi�n con la BBDD.</li>
 * 		<li>El nombre de la tabla y columna que contienen el TimeStamp de �ltima modificaci�n de las propiedades.</li>
 * 		<li>El periodo de comprobaci�n de la recarga
 * 	  		(el sistema comprueba la del TimeStamp y si es POSTERIOR a la �ltima vez que se revis� este TimeStamp, recarga las propiedades).</li>
 * </ul>
 * La definici�n ser�a de esta forma:
 * <pre class="brush:xml">
 * <reloadControl impl='BBDD' enabled='true' checkInterval='2s'>
 * 		<props>
 * 			<class>oracle.jdbc.OracleDriver</class> <!-- Driver de acceso a la base de datos -->
 *			<uri>jdbc:oracle:thin:@host:port:instance</uri> <!-- Cadena de coneon a la BBDD  -->
 *			<user>user</user> <!-- Usuario de acceso a la base de datos -->
 *			<password>pwd</password> <!-- Clave de acceso a la base de datos -->
 * 			<reloadFlagQuerySql>
 * 				<!-- Query para obtener el flag de recarga del fichero de configuraciones, a 0 no recarga, a 1 debe recargar el fichero. -->
 * 				<![CDATA[
 * 				SELECT RELOAD_01 AS RELOAD_FLAG
 * 				  FROM PROPERTIES_CFG
 * 				 WHERE COD_APLIC_01 = 'r01f'
 * 				   AND COMPONENT_01 ='general'
 * 				   AND ENVIRONMENT_01 = 'local'
 * 				]]>
 * 			</reloadFlagQuerySql>
 * 			<reloadFlagUpdateSql>
 * 				<!-- Actualizaci�n del flag, para desactivarlo. -->
 * 				<![CDATA[
 * 				UPDATE PROPERTIES_CFG
 * 					SET RELOAD_01 = 0
 * 		 		WHERE COD_APLIC_01 = 'r01f'
 * 		   		AND COMPONENT_01 ='general'
 * 		   		AND ENVIRONMENT_01 = 'local'
 * 				]]>
 * 			</reloadFlagUpdateSql>
 * 		</props>
 * </reloadControl>
 * </pre>
 *
 * <p>Ejemplo de conexi�n con Datasource:
 * <pre class="brush:xml">
 * <reloadControl impl='BBDD' enabled='true' checkInterval='2s'>
 * 		<props>
 *			<class>DataSource</class>
 *			<uri>r01n.r01nDataSource</uri>
 * 			<reloadFlagQuerySql>
 * 				<!-- Query para obtener el flag de recarga del fichero de configuraciones, a 0 no recarga, a 1 debe recargar el fichero. -->
 * 				<![CDATA[
 * 				SELECT RELOAD_01 AS RELOAD_FLAG
 * 				  FROM PROPERTIES_CFG
 * 				 WHERE COD_APLIC_01 = 'r01f'
 * 				   AND COMPONENT_01 ='general'
 * 				   AND ENVIRONMENT_01 = 'local'
 * 				]]>
 * 			</reloadFlagQuerySql>
 * 			<reloadFlagUpdateSql>
 * 				<!-- Actualizaci�n del flag, para desactivarlo. -->
 * 				<![CDATA[
 * 				UPDATE PROPERTIES_CFG
 * 					SET RELOAD_01 = 0
 * 		 		WHERE COD_APLIC_01 = 'r01f'
 * 		   		AND COMPONENT_01 ='general'
 * 		   		AND ENVIRONMENT_01 = 'local'
 * 				]]>
 * 			</reloadFlagUpdateSql>
 * 		</props>
 * </reloadControl>
 * </pre>
 * 
 * Si es necesario crear a mano esta clase:
 * <pre class='brush:java'>
 * 		ReloadControl reloadControl = ReloadControlBBDDFlagBased.create()
 * 															    .checkingIfReloadIsNeededEvery("5s")
 * 																.conectingToDBWith(props)
 * 																.checkingReloadFlagWith(querySql)
 * 																.updatingReloadFlagWith(updateSql)
 * </pre>
 */
@Slf4j
public class ResourcesReloadControlBBDDFlagBased 
     extends ResourcesReloadControlBase<ResourcesReloadControlBBDDFlagBased> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static final String RELOADFLAGQUERYSQL_PROP = "reloadFlagQuerySql";
	static final String RELOADFLAGUPDATESQL_PROP = "reloadFlagUpdateSql";
	static final String DEFAULT_LOAD_FLAG_SQL = "SELECT RELOAD_01 AS RELOAD_FLAG " +
  				  								  "FROM PROPERTIES_CFG " + 
  				  								 "WHERE COD_APLIC_01 = 'r01f' " + 
  				  								   "AND COMPONENT_01 ='general' " +
  				  								   "AND ENVIRONMENT_01 = 'local'";
	static final String DEFAULT_UPDATE_FLAG_SQL = "UPDATE PROPERTIES_CFG " + 
													 "SET RELOAD_01 = 0 " +
												   "WHERE COD_APLIC_01 = 'r01f' " +
												     "AND COMPONENT_01 ='general' " +
												     "AND ENVIRONMENT_01 = 'local'";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * {@link Memoized} {@link DBSQLExecutor} instance
	 * (uses reflection to create the {@link DBSQLExecutor} avoid de dependency between r01fbClasses & r01fbPersistenceClasses)
	 */
	Memoized<DBSQLExecutor> _sqlExec = new Memoized<DBSQLExecutor>() {
													@Override
													public DBSQLExecutor supply() {
														Properties dbConnectionProps = CollectionUtils.toProperties(ResourcesReloadControlBBDDFlagBased.this.getReloadControlDef().getControlProps());
														DBSQLExecutor sqlExec = ReflectionUtils.createInstanceOf("r01f.persistence.db.sql.DBRawSQLExecutor",
																												 new Class<?>[] {Properties.class},new Object[] {dbConnectionProps});
														return sqlExec;
													}
									   };
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ResourcesReloadControlBBDDFlagBased() {
		
	}
	public ResourcesReloadControlBBDDFlagBased(final ResourcesReloadControlDef ctrlDef) {
		super(ctrlDef);
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		boolean outOK = true;
		if (CollectionUtils.isNullOrEmpty(props)) {
			outOK = false;
		} else if (props.get(ResourcesLoaderFromBBDD.CLASS) != null && props.get(ResourcesLoaderFromBBDD.CLASS).equals("DataSource")) {
			outOK = CollectionUtils.of(props)
					 			   .containsAllTheseKeys(ResourcesLoaderFromBBDD.URI,
					 					   				 RELOADFLAGQUERYSQL_PROP,
													     RELOADFLAGUPDATESQL_PROP);
		} else if (props.get(ResourcesLoaderFromBBDD.CLASS) != null && !props.get(ResourcesLoaderFromBBDD.CLASS).equals("DataSource")) {
			outOK = CollectionUtils.of(props)
					 			   .containsAllTheseKeys(ResourcesLoaderFromBBDD.URI,
					 					   				 ResourcesLoaderFromBBDD.USER,ResourcesLoaderFromBBDD.PASSWORD,
					 					   				 RELOADFLAGQUERYSQL_PROP,
													     RELOADFLAGUPDATESQL_PROP);				
		} else {
			outOK = false;
		}
		return outOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Interfaz r01f.resources.ResourcesReloadControl
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean needsReload(final String component) {
		boolean outReload = false;
		
		if (_hasToCheckIfReloadIsNeeded()) {
			outReload = _checkReloadFlag();
			if (outReload) _disableReloadFlag();
		}
		return outReload;
	}
	/**
	 * Consulta de flag de recarga de las configuraciones.
	 * @return <code>true</code> si el valor del flag es 1 (activado),<br>
	 * 		   <code>false</code> si el valor del flag es 0 (desactivado).
	 */
	@SuppressWarnings("null")
	private boolean _checkReloadFlag() {
		log.debug(">> Query reload flag using {}....",this.getReloadControlDef().getControlProps()
																				.get(RELOADFLAGQUERYSQL_PROP));
		
		List<Map<String,String>> resultRows = null;
		try {
			resultRows = _sqlExec.get().query(this.getReloadControlDef().getControlProps()
																		.get(RELOADFLAGQUERYSQL_PROP));
		} catch (SQLException sqlEx) {
			Throwables.throwUnchecked(sqlEx);
		}
		int flag = 0;
		if (CollectionUtils.hasData(resultRows)) {
			Map<String,String> resultCols = resultRows.get(0);
			Map.Entry<String,String> reloadFlagColValue = CollectionUtils.of(resultCols).pickOneAndOnlyEntry();
			flag = Integer.parseInt(reloadFlagColValue.getValue());
		} else {
			log.error("The query for the reload flag {} didn't return any result; review the reload config",this.getReloadControlDef().getControlProps()
																																	  .get(RELOADFLAGQUERYSQL_PROP));
		}
		return flag == 1 ? true : false;
	}
	/**
	 * Cambia el estado del flag al valor 0 (desactivado).
	 */
	private void _disableReloadFlag() {
		log.debug(">> Updating reload flag using {}....",this.getReloadControlDef().getControlProps()
														 						   .get(RELOADFLAGUPDATESQL_PROP));
		try {
			_sqlExec.get().update(this.getReloadControlDef().getControlProps()
														    .get(RELOADFLAGUPDATESQL_PROP));
		} catch (SQLException sqlEx) {
			Throwables.throwUnchecked(sqlEx);
		}
	}
}
