package r01f.persistence.db.sql;

/**
 * Es una clase de ayuda para ejecutar diversas operaciones de base de datos.
 * Para poder utilizar los metodos generales de esta clase, es necesario establecer
 * las propiedades de la conexion de base de datos que se va a utilizar, para ello
 * se utiliza el constructor o el metodo setConnectionProperties. A estos métodos
 * hay que pasar un objeto Properties con datos para obtener la conexion.
 * Los datos de la conexion normalmente se obtienen de un fichero .properties.xml
 * que ha de tener una seccion con la forma:
 * <database>
 *		Ejemplo JDBC-ODBC bridge
 *		<connection name='prueba'>
 *			<class>sun.jdbc.odbc.JdbcOdbcDriver</class>
 *			<uri>jdbc:odbc:prueba</uri>
 *		</connection>
 *		Ejemplo Drivers ORACLE
 *		<connection name='pruebaOracle'>
 *			<class>weblogic.jdbc20.oci.Driver</class>
 *			<uri>jdbc20:weblogic:oracle</uri>
 *			<user>usuario</user>
 *			<password>password</password>
 *			<server>server</server>
 *		</connection>
 *		Ejemplo OCI
 *		<connection name='pruebaOCI'>
 *			<class>oracle.jdbc.driver.OracleDriver</class>
 *			<uri>jdbc:oracle:oci8:@mydb_dbmachine</uri>
 *			<user>scott</user>
 *			<password>tiger</password>
 *		</connection>
 *	 	Ejemplo acceso ThinDriver de Oracle
 *		<connection name='pruebaThin'>
 *			<class>oracle.jdbc.driver.OracleDriver</class>
 *			<uri>
 *				jdbc:oracle:thin:user/password@
 *					(description=(address_list=(address=(protocol=tcp)(host=dbmachine)(port=1521)))
 *								 (source_route=yes)
 *								 (connect_data=(sid=ejbdemo))
 *					)
 *			</uri>
 *		</connection>
 *		Ejemplo acceso ThinDriver de Oracle
 *		<connection name='pruebaThin2'>
 *			<class>oracle.jdbc.driver.OracleDriver</class>
 *			<uri>jdbc:oracle:thin:@dbmachine:1521:mydb</uri>
 *			<user>scott</user>
 *			<password>tiger</password>
 *		</connection>
 *		Ejemplo DataSource (pool weblogic)
 *		<connection name='pruebaPool'>
 *			<class>Datasource</class>
 *			<uri>poolOracleNonJtsDataSource</uri>
 *		</connection>
 * </database>
 * Utilizando el metodo DBSQLHelpper.getConnectionProperties(appCode,connectionName)
 * se obtiene el objeto Properties a partir del XML anterior.
 *
 * Adicionalmente se ofrecen una serie de métodos estáticos para obtener conexiones
 *
 * IMPORTANTE!!!!
 * Hay dos modos de uso:
 * MODO NORMAL:		Se abre una conexíón o una transaccion antes de ejecutar cualquier cosa
 * 					y se cierra al final
 * 					DBSQLHelpper sqlHelp = new DBSQLHelpper(properties);
 * 					sqlHelp.obtainConnection();  // o bien sqlHelp.beginTransaction();
 * 					sqlHelp.<cualquier operacion>
 * 					sqlHelp.releaseConnection(); // o bien sqlHelp.endTransaction();
 *
 * MODO STANDALONE:	Para cada operación se abre implicitamente una conexión de base de datos
 * 					y se cierra automáticamente
 * 					SQLHelppper sqlHelp = new DBSQLHelpper(properties);
 * 					sqlHelp.<cualquier operacion>
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@NoArgsConstructor
public class DBManager {
/////////////////////////////////////////////////////////////////////////////////////////
//  ESTADO
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter protected Properties _conxProps;  	// Propiedades para obtener la conexion a BD
    				protected Connection _conx = null;	// Conexión a base de datos

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR/DESTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor a partir de las propiedades de conexion a base de datos
     */
    public DBManager(Properties conxProps) {
        _conxProps = conxProps;
    }
	/**
	 * Liberar la conexion de base de datos
	 */
	public void finalice() {
	    try {
		    if (_conx != null) {
	            DBConnectionHelpper.closeConnection(_conx);
		        _conx = null;
		    }
	    } catch(SQLException sqlEx) {
	    	sqlEx.printStackTrace(System.out);
	        /* Ignorar ya que no se puede hacer nada */
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONTROL DE TRANSACCIONES
/////////////////////////////////////////////////////////////////////////////////////////
    	/**
    	 * Comienza una transaccion
    	 * @throws SQLException
    	 */
    	public void beginTransaction() throws SQLException {
    		if (_conxProps == null) throw new SQLException( "No se han establecido las propiedades para obtener la conexión (null)" );
    		if (_conx == null) _conx = DBConnectionHelpper.getConnection(_conxProps);
    	}
    	/**
    	 * Finaliza la transacción haciendo un commit implicitamente
    	 * @throws SQLException
    	 */
    	public void endTransaction() throws SQLException {
    		if (_conx != null) {
    		    _conx.commit();
    		    DBConnectionHelpper.closeConnection(_conx);
    		    _conx = null;
    		}

    	}
    	/**
    	 * Cancela los cambios en la transacción
    	 * @throws SQLException
    	 */
    	public void rollBackTransaction() throws SQLException {
    	    _conx.rollback();
    	}
     	/**
     	 * Hace commit de los cambios en BD
     	 * @throws SQLException
     	 */
     	public void commit() throws SQLException {
     		if (_conx != null) _conx.commit();
     	}

/////////////////////////////////////////////////////////////////////////////////////////
//  SELECT
/////////////////////////////////////////////////////////////////////////////////////////
        /**
         * Ejecuta una sentencia SELECT
         * @param columns Columnas que intervienen en la select
         * @param tables Tablas que intervienen en la select
         * @param params Lista con los parametros de la parte WHERE de la select
         * @param where WHERE de la select
         * @param orderColumns indice de las columnas por las que se ordena
         * @param desc indica si los resultados se ordenan de forma descendente
         * @param distinctRows Indica si hay que incluir el indicador DISTINCT en la select
    	 * @return las filas encontradas en la base de datos en forma de una
         *         lista en la que cada elemento es un mapa con la estructura: nombreColumna|valor
    	 * @throws SQLException si hay algun error al acceder a la base de datos
         */
        public List<Map<String, String>> executeSelect(final List<String> columns,
                                  					   final List<String> tables,final List<String> params,
                                  					   final String where,
                                  					   final int[] orderColumns,final boolean[] desc,final boolean distinctRows) throws SQLException {
            String sql = DBSQLHelpper.composeSelect(columns,tables,where,orderColumns,desc,distinctRows);
            return executeQuery(sql,params);
        }
        /**
         * Método que permite ejecutar una query con parametros en la base de datos
         * Primero se prepara la query sustituyendo los simbolos "?" del String (primer parametro)
         * por los parametros pasados en la lista (segudo parametro) y a continuacion se recuperan los datos
         * de la BD devolviendo una lista con las filas en la que cada elemento es otra lista con las columnas.
         * En el caso de que la lista de parametros (segundo parametro) sea nulo, NO será necesario
         * realizar el paso previo de montar la query final.
         * @param sql Query SQL que quiere ser ejecutada.
         * @param params Lista de parametros que completan la query final que va a ser ejecutada.
         * @return las filas encontradas en la base de datos en forma de una
         *         lista en la que cada elemento es un mapa con la estructura: nombreColumna|valor
         * @exception SQLException si hay errores en el acceso a la base de datos.
         */
		@SuppressWarnings("resource")
		public List<Map<String, String>> executeQuery(final String sql,final List<String> params) throws SQLException {
            Connection conx = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (sql == null) throw new SQLException("La sentencia SQL no puede ser nula");

            try {
                // Obtener una nueva conexion solo si no hay una ya creada (transacción)
                conx = (_conx == null ? DBConnectionHelpper.getConnection(_conxProps) : _conx);

                // Obtener un Statement y sustituir los parametros si los hay
                ps = conx.prepareStatement(sql);
                if (params != null) {
                  for (int i = 0; i < params.size(); i++)
                      ps.setObject(i + 1, params.get(i));
                }
                // Ejecutar la query y devolver las filas encontradas en la base de datos en forma de una
                // lista en la que cada elemento es un mapa con la estructura: nombreColumna|valor
                rs = ps.executeQuery();
                return _loadRows(rs);
            } finally {
                if (ps != null) ps.close();
                ps = null;
                if (rs != null) rs.close();
                rs = null;
                // Solo cerrar la conexión si esta se ha creado para la transacción actual
                if (_conx == null && conx != null) {
                    DBConnectionHelpper.closeConnection(conx);
                    conx = null;
                }
            }
        }
        /**
         * Devuelve las filas encontradas en la base de datos en forma de una
         * lista en la que cada elemento es un mapa con la estructura: nombreColumna|valor
         */
        private static List<Map<String, String>> _loadRows(final ResultSet rs) throws SQLException {
        	List<Map<String, String>> outList = null;

            if (rs != null && rs.next()) {
                outList = new ArrayList<Map<String,String>>();     // Lista para los objetos
                do {
                    Map<String, String> rowMap = new HashMap<String,String>(rs.getMetaData().getColumnCount());
                    for (int i=1; i <= rs.getMetaData().getColumnCount(); i++) {
                        rowMap.put( rs.getMetaData().getColumnName(i).toUpperCase(),rs.getString(i) );
                    }
                    // Meter el objeto en la lista
                    outList.add(rowMap);
                } while(rs.next());
            }
            return outList;
        }
/////////////////////////////////////////////////////////////////////////////////////////
//  UPDATE / INSERT / DELETE
/////////////////////////////////////////////////////////////////////////////////////////
        /**
         * Ejecuta una sentencia UPDATE
         * @param updateData Parejas de elementos NOMBRE_COLUMNA / VALOR
         * @param table Tabla sobre la que queremos que se ejecute el update
         * @param where WHERE de la select
         * @param params Lista con los parametros de la parte WHERE del update en el
         *                caso de que se utilice una PreparedStatement
         * @return El numero de columnas afectadas
         * @throws SQLException si hay error al acceder a la base de datos
         */
        public int executeUpdate(final List<DBSQLHelpper.DBData> updateData,
        						 final String table,final String where,final List<String> params) throws SQLException {
            String sql = DBSQLHelpper.composeUpdate(updateData,table,where);
            return executeStatement(sql,params);
        }
        /**
         * Ejecuta una sentencia UPDATE
         * @param updateSQL sql de update
         * @return El numero de columnas afectadas
         * @throws SQLException si hay error al acceder a la base de datos
         */
        public int executeUpdate(final String updateSQL) throws SQLException {
            return executeStatement(updateSQL,null);
        }
        /**
         * Ejecuta una sentencia INSERT
         * @param insertData Parejas de elementos NOMBRE_COLUMNA / VALOR
         * @param table Tabla sobre la que queremos que se ejecute el update
         * @param params Lista con los parametros de la parte WHERE del update en el
         *                caso de que se utilice una PreparedStatement
         * @return El numero de columnas afectadas
         * @throws SQLException si hay error al acceder a la base de datos
         */
        public int executeInsert(final List<DBSQLHelpper.DBData> insertData,
        						 final String table,final List<String> params) throws SQLException {
            String sql = DBSQLHelpper.composeInsert(insertData,table);
            return executeStatement(sql,params);
        }
        /**
         * Ejecuta una sentencia INSERT
         * @param insertSQL sql de insercion
         * @return El numero de columnas afectadas
         * @throws SQLException si hay error al acceder a la base de datos
         */
        public int executeInsert(final String insertSQL) throws SQLException {
            return executeStatement(insertSQL,null);
        }
        /**
         * Ejecuta una sentencia DELETE
         * @param table Tabla sobre la que se quiere que se ejecute el delete
         * @param where WHERE del delete
         * @return El string con la sentencia DELETE
         */
        public int executeDelete(final String table,final String where,final List<String> params) throws SQLException {
            String sql = DBSQLHelpper.composeDelete(table,where);
            return executeStatement(sql,params);
        }
        /**
         * Ejecuta una sentencia DELETE
         * @param deleteSQL sql de borrado
         * @return El string con la sentencia DELETE
         */
        public int executeDelete(final String deleteSQL) throws SQLException {
            return executeStatement(deleteSQL,null);
        }
        /**
         * Ejecuta un statement en la base de datos a partir de una sentencia SQL a ejecutar
         * y los parámetros a dicha sentencia.
         * @param sql La query a ejecutar
         * @param params Los parametros de la query
         * @return El numero de filas afectadas por la operacion
         * @throws SQLException si hay algún error en la base de datos
         */
		@SuppressWarnings("resource")
		public int executeStatement(final String sql,final List<String> params) throws SQLException {
            Connection conx = null;
            PreparedStatement ps = null;
            if (sql == null) throw new SQLException("La sentencia SQL no puede ser nula");
            try {
    	        // Obtener una nueva conexion solo si no hay una ya creada (transacción)
    	        conx = (_conx == null ? DBConnectionHelpper.getConnection(_conxProps) : _conx);

    	        // Obtener un Statement y sustituir los parametros si los hay
    	        ps = conx.prepareStatement(sql);
    	        if (params != null) {
    	          for (int i = 0; i < params.size(); i++)
    	              ps.setObject(i + 1, params.get(i));
    	        }
    	        // Ejecutar la query
                return ps.executeUpdate();
            } finally {
                if (ps != null) ps.close();
                ps = null;
                // Solo cerrar la conexión si esta se ha creado para la transacción actual
                if (_conx == null && conx != null) {
                    DBConnectionHelpper.closeConnection(conx);
                    conx = null;
                }
            }
        }

/////////////////////////////////////////////////////////////////////////////////////////
//  SECUENCIAS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Crea una nueva secuencia en la base de datos
     * @param seqName El nombre de la secuencia
     * @param startValue
     * @param increment
     * @return True o false dependiendo si se ha creado o no la secuencia
     */
	@SuppressWarnings("resource")
	public boolean createNewSequence(final String seqName,final long startValue,final int increment) throws SQLException {
    	Connection conx = null;
        PreparedStatement stmt = null;
        String strSql = null;

        // Crear una nueva sequencia
        try {
            strSql = "CREATE SEQUENCE " + seqName + " " +
                               "START WITH " + startValue;
            // Obtener una nueva conexion solo si no hay una ya creada (transacción)
			conx = (_conx == null ? DBConnectionHelpper.getConnection(_conxProps) : _conx);
            stmt = conx.prepareStatement(strSql);
            return stmt.execute();
        } finally {
            // Cerrar todo
        	if (stmt != null) stmt.close();
        	// Solo cerrar la conexión si esta se ha creado para la transacción actual
            if (_conx == null && conx != null) {
                DBConnectionHelpper.closeConnection(conx);
    	        conx = null;
            }
        }
    }
    /**
     * Obtiene el siguiente valor de la secuencia cuyo nombre se pasa como parametro
     * @param seqName El nombre de la secuencia
     */
	@SuppressWarnings("resource")
	public long getSequenceNextValue(final String seqName) throws SQLException {
    	Connection conx = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (seqName == null) throw new SQLException( "El nombre de la secuencia no puede ser nulo" );
        String sql = null;
        try {
            sql = "SELECT " + seqName + ".nextVal FROM DUAL";
            // Obtener una nueva conexion solo si no hay una ya creada (transacción)
			conx = (_conx == null ? DBConnectionHelpper.getConnection(_conxProps) : _conx);
            ps = conx.prepareStatement(sql);
            ps.executeQuery();      //OJO!! No hacer rs = ps.executeQuery ya que falla con los TXDatasources
            rs = ps.getResultSet();
            if (rs.next()) return rs.getLong(1);    // Devolver el valor de la secuencia
            throw new SQLException( "No se ha podido obtener un valor de la secuencia " + seqName );
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            // Solo cerrar la conexión si esta se ha creado para la transacción actual
            if (_conx == null && conx != null) {
                DBConnectionHelpper.closeConnection(conx);
    	        conx = null;
            }
        }
    }

}
