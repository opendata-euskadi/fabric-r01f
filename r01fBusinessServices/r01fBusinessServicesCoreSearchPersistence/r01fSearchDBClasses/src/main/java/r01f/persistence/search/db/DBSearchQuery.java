package r01f.persistence.search.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import r01f.locale.Language;
import r01f.model.search.SearchFilter;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.search.QueryBase;

/**
 * Can be used to run DB search queries 
 * <pre class='brush:java'>
 * 		BooleanQuery filter = BooleanQueryClause.create()
 *	    							.field("oid").must().beWithin(MyEntityOID.forId("xxxlc814593391b1d721a3067bdde926665c5e952"),
 *	    							 							  MyEntityOID.forId("xxxlc9145932d760a241b21e51fc9b298602689bd"))
 *	    							.field("createDate").must().beInsideDateRange(Range.atMost(new Date()))
 *	    							.build();
 * 
 * 		// Put eclipselink.jar and the mysql connector driver in the classpath
 * 		// ... also copy the persistence.xml in the META-INF dir
 *	    Properties props = new Properties();
 *	    props.put("javax.persistence.jdbc.user","r01e");
 *	    props.put("javax.persistence.jdbc.password","r01e");
 *	    props.put("javax.persistence.jdbc.driver","com.mysql.jdbc.Driver");		// IMPORTANT!! When in Tomcat & MySQL, set jconector MySQL's driver at $CATALINA_HOME/lib  
 *	    props.put("javax.persistence.jdbc.url","jdbc:mysql://localhost:3306/r01e");
 *	    props.put("eclipselink.target-database","org.eclipse.persistence.platform.database.MySQLPlatform");		// org.eclipse.persistence.platform.database.oracle.OraclePlatform
 *	    
 *	    
 *	    EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistenceUnit.xxx",props);
 *	    EntityManager em = emf.createEntityManager();
 *
 *		Query q = DBSearchQueryForModelObjectBuilder.forDBEntityType(MyDBEntity.class)
 *														  .usingDBModuleConfig(dbModuleConfig)
 *														  .with(_entityManager)
 *														  .noUILanguage()
 *														  .withFilterType(filterType)
 *												    .getCountQuery(filter);
 *
 *	    Query jpaQry = qry.getCountQuery();
 *	    System.out.println(">>>" + jpaQry.getSingleResult());
 * </pre>
 * @param <F>
 * @param <DB>
 */
public class DBSearchQuery<F extends SearchFilter,
					       DB extends DBEntity> 
	 extends QueryBase<DBSearchQuery<F,DB>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Class<DB> _dbEntityType;
	protected final DBModuleConfig _dbModuleConfig;
	protected final EntityManager _entityManager;		
	protected final DBSearchQueryToJPQLTranslator<F,DB> _searchQueryToJpq;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBSearchQuery(final Class<DB> dbEntityType,
						 final DBModuleConfig dbModuleConfig,
						 final EntityManager entityManager) {
		this(dbEntityType,
			 dbModuleConfig,
			 entityManager,
			 (Language)null);
	}
	public DBSearchQuery(final Class<DB> dbEntityType,
						 final DBModuleConfig dbModuleConfig,
						 final EntityManager entityManager,
						 final Language uiLanguage) {
		this(dbEntityType,
			 dbModuleConfig,
			 entityManager,
			 uiLanguage,
			 new DBSearchQueryToJPQLTranslator<F,DB>(dbEntityType,
											    	 dbModuleConfig,
													 entityManager));
	}
	public DBSearchQuery(final Class<DB> dbEntityType,
						 final DBModuleConfig dbModuleConfig,
						 final EntityManager entityManager,						 
						 final DBSearchQueryToJPQLTranslator<F,DB> searchQueryToJpql) {
		this(dbEntityType,
			 dbModuleConfig,
			 entityManager,
			 (Language)null,
			 searchQueryToJpql);
	}
	public DBSearchQuery(final Class<DB> dbEntityType,
						 final DBModuleConfig dbModuleConfig,
						 final EntityManager entityManager,						 
						 final Language uiLanguage,
						 final DBSearchQueryToJPQLTranslator<F,DB> searchQueryToJpql) {
		super(uiLanguage);
		_dbEntityType = dbEntityType;
		_dbModuleConfig = dbModuleConfig;
		_entityManager = entityManager;
		_searchQueryToJpq = searchQueryToJpql;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public TypedQuery<Long> getCountQuery(final F filter) {
		// [1] - Compose the JPQL
		String countJPQL = _searchQueryToJpq.composeCountJPQL(filter);
		
		// [2] - Create the JPA query & set the params
		TypedQuery<Long> countQry = _entityManager.createQuery(countJPQL.toString(),
												   			   Long.class);
		_searchQueryToJpq.setJPAQueryParameters(filter,countQry);
		return countQry;
	}
	public TypedQuery<DB> getResultsQuery(final F filter,
										  final Collection<SearchResultsOrdering> ordering) {
		// [1] - Compose the JPQL
		String jpql = _searchQueryToJpq.composeRetrieveJPQL(filter,
								   						    ordering);		
		// [2] - Create the JPA query & set the params
		TypedQuery<DB> qry = _entityManager.createQuery(jpql.toString(),
														_dbEntityType);
		_searchQueryToJpq.setJPAQueryParameters(filter,qry);
		return qry;
	}
	public Query getOidsQuery(final F filter) {
		// [1] - Compose the JPQL
		String jpql = _searchQueryToJpq.composeRetrieveOidsJPQL(filter);
		
		// [2] - Create the JPA query & set the params
		Query outQry = _entityManager.createQuery(jpql);
		_searchQueryToJpq.setJPAQueryParameters(filter,outQry); 
		return outQry;
	}
}
 