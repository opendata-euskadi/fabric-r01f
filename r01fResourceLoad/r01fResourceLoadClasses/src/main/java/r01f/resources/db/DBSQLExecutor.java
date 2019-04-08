package r01f.resources.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DBSQLExecutor {
	/**
     * Runs a query without params 
     * @param querySql 
     * @return resutlt list where every row is a Map indexed by the db column name
     * @throws SQLException 
     */
    public List<Map<String,String>> query(final String querySql) throws SQLException;
    /**
     * Runs a query with params
     * @param querySql 
     * @param params 
     * @return resutlt list where every row is a Map indexed by the db column name
     * @throws SQLException
     */
    public List<Map<String,String>> query(final String querySql,
    						 			  final List<String> params) throws SQLException;
    /**
     * Runs an insert
     * @param insertSQL 
     * @throws SQLException
     */
    public void insert(final String insertSQL) throws SQLException;
    /**
     * Runs an insert with params
     * @param insertSQL 
     * @param params 
     * @throws SQLException 
     */
    public void insert(final String insertSQL,
    				   final List<String> params) throws SQLException;
    /**
     * Runs an update without params
     * @param updateSQL 
     * @throws SQLException 
     */
    public void update(final String updateSQL) throws SQLException;
    /**
     * Runs an Update with params
     * @param updateSQL 
     * @param params 
     * @throws SQLException 
     */
    public void update(final String updateSQL,
    				   final List<String> params) throws SQLException;
    /**
     * Runs a delete
     * @param deleteSQL 
     * @throws SQLException 
     */
    public void delete(final String deleteSQL) throws SQLException;
    /**
     * Runs an insert 
     * @param deleteSQL 
     * @param params 
     * @throws SQLException 
     */
    public void delete(final String deleteSQL,
    				   final List<String> params) throws SQLException;
}