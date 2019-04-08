package r01f.persistence.db.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * BDConnectionHelpper
 * Clase que se encarga de manejar conexiones a bases de datos.
 * Hay que pasar un objeto Properties con datos para obtener la conexion.
 * Los datos de la conexion normalmente se obtienen de un fichero .properties.xml
 * que ha de tener una seccion con la forma:
 * <database>
 *		<connection name='prueba'>
 *			<class>weblogic.jdbc20.oci.Driver</class>
 *			<uri>jdbc20:weblogic:oracle</uri>
 *			<user>usuario</user>
 *			<password>password</password>
 *			<server>server</server>
 *		</connection>
 *		<connection name='pruebaPool'>
 *			<class>Datasource</class>
 *			<uri>poolPrueba</uri>
 *		</connection>
 * </database>
 * Utilizando el metodo SQLHelpper.getConnectionProperties(appCode,connectionName)
 * se obtiene el objeto Properties a partir del XML anterior.
 *
 * Se ofrecen una serie de métodos estáticos para obtener conexiones
 */
@Slf4j
@NoArgsConstructor
public class DBConnectionHelpper {
/////////////////////////////////////////////////////////////////////////////////////////
// 	ESTADO
/////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Devuelve una conexión con la base de datos
     * Hay que pasar como parametro un objeto Properties con la definicion de
     * la conexion.
     * Esta definicion normalmente se hace en un fichero .properties.xml
     * en una seccion como la siguiente.
     * <database>
     *		<connection name='prueba'>
     *			<class>weblogic.jdbc20.oci.Driver</class>
     *			<uri>jdbc20:weblogic:oracle</uri>
     *			<user>usuario</user>
     *			<password>password</password>
     *			<server>server</server>
     *		</connection>
     *		<connection name='pruebaPool'>
     *			<class>DataSource</class>
     *			<uri>poolPrueba</uri>
     *		</connection>
     * </database>
     * Si class es Datasource, se obtiene una conexion de un datasource (pool) cuyo nombre
     * se especifica en uri
     * Si class es un Driver jdbc se obtiene una conexion con dicho driver y
     * utilizando el uri, servidor, usuario y clave especificados
     *
     * @param props Propiedades para obtener la conexion
     * @return Conexión con la base de datos
     * @throws SQLException si hay algun error
     */
    public static Connection getConnection(final Properties props) throws SQLException {
        log.trace("Obteniendo conexion");
        if (props == null) {
            log.warn(DBConnectionHelpper.composeNoConfigErrorMessage() );
            throw new IllegalArgumentException( "Las propiedades para obtener la conexión no son validas (null)" );
        }

        // Obtener la clase y el uri para la conexion
        String driverClass = props.getProperty("class");
        if (driverClass == null || driverClass.length() == 0) {
        	log.warn(DBConnectionHelpper.composeNoConfigErrorMessage() );
            throw new IllegalArgumentException( "La clase (class) de conexion especificada en las propiedades no es valida (null)" );
        }
        String uri = props.getProperty("uri");
        if (uri == null || uri.length() == 0) {
        	log.warn(DBConnectionHelpper.composeNoConfigErrorMessage() );
            throw new IllegalArgumentException( "El uri especificado en las propiedades no es valida (null)" );
        }

        // Si driverClass="DataSource" se obtiene una conexion de un datasource
        // si por el contrario driverClass="Pool" se obtiene una conexion de un pool
        // en otro caso, se obtiene una conexion jdbc normal
        if ( driverClass.equalsIgnoreCase("DataSource") ) {
            String dataSourceName = props.getProperty("uri");
            if (dataSourceName == null || dataSourceName.length() == 0) throw new IllegalArgumentException( "El nombre del dataSource especificado en las propiedades no es valida (null)" );
            return _obtainDataSourceConnection(dataSourceName,false);
        } else if ( driverClass.equalsIgnoreCase("TXDataSource") ) {
            String dataSourceName = props.getProperty("uri");
            if (dataSourceName == null || dataSourceName.length() == 0) throw new IllegalArgumentException( "El nombre del dataSource especificado en las propiedades no es valida (null)" );
            return _obtainDataSourceConnection(dataSourceName,true);
        } else if ( driverClass.equalsIgnoreCase("Pool") ) {
            String poolName = props.getProperty("uri");
            if (poolName == null || poolName.length() == 0) throw new IllegalArgumentException( "El nombre del pool especificado en las propiedades no es valida (null)" );
            // return SQLHelpper._obtainPoolConnection(poolName,true);
            return null;    // De momento... no esta implementado
        } else {
            String user = props.getProperty("user");
            String password = props.getProperty("password");
            String server = props.getProperty("server");
            return _obtainJDBCConnection(driverClass,uri,user,password,server);
        }
    }
    /**
     * Libera una conexión a base de datos
     * @param conx la conexión que hay que cerrar
     * @throws SQLException si hay algun error al cerrar
     */
    public static void closeConnection(final Connection conx) throws SQLException {
    	log.trace("Liberando conexion");
        if (conx != null) {
            conx.close();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS DE UTILIDAD
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Compone un error indicando que no se ha definido la seccion de base de datos
     * en las properties.xml
     * @return el mensaje de error
     */
    public static String composeNoConfigErrorMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("ERROR EN LA DEFINICIÓN DE LA CONEXION A BASE DE DATOS\r\n");
        sb.append("-------------------------------------------------------------\r\n");
        sb.append("Este error puede estar provocado porque no se ha definido\r\n");
        sb.append("la conexion en el fichero .properties.xml o bien porque la\r\n");
        sb.append("definición no es correcta\r\n");
        sb.append("Una conexión en este fichero tiene las formas:\r\n");
        sb.append("<connection name='prueba'>\r\n");
        sb.append(      "\t<class>weblogic.jdbc20.oci.Driver</class>\r\n");
        sb.append(      "\t<uri>jdbc20:weblogic:oracle</uri>\r\n");
        sb.append(      "\t<user>usuario</user>\r\n");
        sb.append(      "\t<password>password</password>\r\n");
        sb.append(      "\t<server>server</server>\r\n");
        sb.append("</connection>\r\n");
        sb.append("<connection name='pruebaPool'>\r\n");
        sb.append(      "\t<class>Datasource</class>\r\n");
        sb.append(      "\t<uri>poolPrueba</uri>\r\n");
        sb.append("</connection>\r\n");
        sb.append("Solo hay que identificar la conexión con un nombre y pedirla\r\n");
        sb.append("con este mismo nombre en los programas java\r\n");
        return sb.toString();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Devuelve una conexión con la base de datos a partir del pool que se pasa
     * @param props Propiedades para obtener la conexion
     * @param isTX Indica si el datasource es TX o no
     * @return Conexión con la base de datos
     * @throws SQLException si hay algun error al obtener el dataSource
     */
    private static Connection _obtainDataSourceConnection(final String dataSourceName,final boolean isTX) throws SQLException {
        try {
            Context ctx = new InitialContext();
            DataSource ds = null;
            if (isTX) {
                ds = (DataSource)ctx.lookup(dataSourceName);
            } else {
                ds = (DataSource)ctx.lookup(dataSourceName);
            }
            Connection conx = ds.getConnection();
            if (conx == null) throw new SQLException( "No se ha podido obtener una conexion del dataSource " + dataSourceName );
            return conx;
        } catch (javax.naming.NamingException nEx) {
            throw new SQLException( "Error al obtener el DataSource: "  + nEx.toString() );
        }
    }
    /**
     * Devuelve una conexión con la base de datos a partir del pool que se pasa
     * @param driverClass La clase java del driver jdbc
     * @param uri El uri para obtener la conexion
     * @param user El usuario de base de datos
     * @param password El password de la base de datos
     * @param server El servidor de BD
     * @return Conexión con la base de datos
     * @throws SQLException si hay algun error
     */
    private static Connection _obtainJDBCConnection(final String driverClass,final String uri,
                                             		final String user,final String password,
                                             		final String server) throws SQLException {
        try {
            // Establecer un objeto properties para obtener la conexion
            Properties props = new Properties();
            if (user != null)       props.setProperty("user",user);
            if (password != null)   props.setProperty("password",password);
            if (server != null)     props.setProperty("server",server);
            // Inicializar el driver
            Class.forName(driverClass).newInstance();	// newInstance() asegura que el inicializador estático
            											// del driver que lo registra en el sistema se llama
            											// en todas las máquinas virtuales.
            //Connection conx = DriverManager.getConnection(uri,props);
            Connection conx = DriverManager.getConnection(uri,user,password);
            if (conx == null) throw new SQLException( "No se ha podido obtener una conexion utilizando el driver " + driverClass + " a la uri " + uri );
            return conx;
        } catch (ClassNotFoundException cnfEx) {
            throw new SQLException( "Error al instanciar el driver JDBC [" + driverClass + "]: " + cnfEx.toString() );
        } catch (IllegalAccessException illAccEx) {
            throw new SQLException( "Acceso ilegar al instanciar el driver JDBC [" + driverClass + "]: " + illAccEx.toString() );
        } catch (InstantiationException instEx) {
            throw new SQLException( "Error al instanciar el driver JDBC [" + driverClass + "]: " + instEx.toString() );
        } catch (Exception e) {
        	e.printStackTrace(System.out);
        	throw new SQLException( "Error al instanciar el driver JDBC [" + driverClass + "]: " + e.toString() );
        }
    }
}
