package r01f.persistence.db.sql;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.resources.db.DBSQLExecutor;

/**
 * Runs sql statements 
 * Properties sample:
 * <pre class="brush:xml">
 * <props>
 * 		<class>oracle.jdbc.OracleDriver</class> 			<!-- DBDrivet -->
 *		<uri>jdbc:oracle:thin:@ejhp67:1524:ede2</uri> 		<!-- DB connection string  -->
 *		<user>r01</user> 									<!-- db user -->
 *		<password>r01</password> 							<!-- db password -->
 * </props>
 * </pre>
 *
 * DataSource sample:
 * <pre class="brush:xml">
 * <props>
 *		<class>DataSource</class>
 *		<uri>r01n.r01nDataSource</uri>
 * </props>
 * </pre>
 */
@Slf4j
@NoArgsConstructor
@Accessors(prefix="_")
public class DBRawSQLExecutor 
  implements DBSQLExecutor {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter
    private DBManager _dbManager;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public DBRawSQLExecutor(final Properties conxProps) {
        this.init(conxProps);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Builds a query executor using the given db connection properties 
     * @param conxProps
     * @return
     */
    public static DBSQLExecutor forConnectionCreatedWith(final Properties conxProps) {
    	DBSQLExecutor outExec = new DBRawSQLExecutor(conxProps);
    	return outExec;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  INIT
/////////////////////////////////////////////////////////////////////////////////////////
    protected void init(final Properties conxProps) {
        _dbManager = new DBManager(conxProps);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  QUERY
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<Map<String,String>> query(final String querySql) throws SQLException {
        return query(querySql,null);
    }
    @Override
    public List<Map<String,String>> query(final String querySql,
    						 			  final List<String> params) throws SQLException {
    	List<Map<String,String>> rdo = null;
        if (querySql != null) {
            rdo = _dbManager.executeQuery(querySql,params);
            if (rdo == null) {
                log.debug("Query {} did NOT return any result",querySql);
            } else {
            	log.debug("Query {} returned {} result items",
            			  querySql,rdo.size());
            }
        } else {
        	log.warn("Se ha intentado ejecutar una consulta NULA contra la base de datos");
        }
        return rdo;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  INSERT
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void insert(final String insertSQL) throws SQLException {
        insert(insertSQL,null);
    }
    @Override
    public void insert(final String insertSQL,
    				   final List<String> params) throws SQLException {
        if (insertSQL != null) {
            _dbManager.executeStatement(insertSQL, params);
        } else {
            log.warn("insert sql is null!!");
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void update(final String updateSQL) throws SQLException {
        this.update(updateSQL,null);
    }
    @Override
    public void update(final String updateSQL,
    				   final List<String> params) throws SQLException {
        if (updateSQL != null) {
            _dbManager.executeStatement(updateSQL, params);
        } else {
            log.warn("Update sql is null!!!");
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void delete(final String deleteSQL) throws SQLException {
        this.delete(deleteSQL,null);
    }
    @Override
    public void delete(final String deleteSQL,
    		           final List<String> params) throws SQLException {
        if (deleteSQL != null) {
            _dbManager.executeStatement(deleteSQL,params);
        } else {
            log.warn("Delete sql is null!!");
        }
    }
}
