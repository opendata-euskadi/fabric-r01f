package r01f.persistence.db.eclipselink;

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.platform.database.MySQLPlatform;

/**
 * MySQL platform Extension that adds the MATCH operator in order to support full text searching
 * IMPORTANT!!
 * 		Tables MUST be MyISAM (InnoDB) type; to change the table type:
 * 			ALTER TABLE [table] engine=MyISAM;
 * 
 * 		also a FULLTEXT index must be added to the cols:
 *			ALTER TABLE [table] ADD FULLTEXT [NOMBRE INDICE](col1,col2,...) ;
 *		
 *		Once the above is done, a FULL-TEXT search can be executed like:
 *			select * 
 *			  from [table]
 *			 where MATCH(col1,col2) AGAINST ('[text]');
 *
 * In order to use this extension, it MUST be registered at the persistence.xml file:
 *		<property name="eclipselink.target-database" value="r01f.persistence.db.eclipselink.MySQLPlatformExtension"/>
 *	
 * see:
 * 		Docs at: http://wiki.eclipse.org/Introduction_to_EclipseLink_Expressions_%28ELUG%29#Parameterized_Expressions
 * 		sample at: 
 * 			- http://planetlotus.org/profiles/arne-menting_94841_spatial-queries-for-ms-sql-in-eclipselink-jpa-expressions
 * 			- http://blog.techdriveactive.de/2011/09/howto-spatial-queries-for-ms-sql-in.html
 * 			- https://dev.eclipse.org/svnroot/rt/org.eclipse.persistence/tags/2.1.0-M5/foundation/org.eclipse.persistence.core/src/org/eclipse/persistence/platform/database/FirebirdPlatform.java
 */
public class MySQLPlatformExtension 
	 extends MySQLPlatform {
	private static final long serialVersionUID = -1533756071794307722L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS   
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * see http://dev.mysql.com/doc/refman/5.0/en/fulltext-search.html 
	 * 	   http://devzone.zend.com/26/using-mysql-full-text-searching/
	 * 		
     * FullText search eclipselink extension operator
     * 			MATCH(?) AGAINST (?)
     * where arguments are:
     * 			?1: col1,col2,col3..
     * 			?2: text to match against
     */
	static final int EXPRESSION_OPERATOR_MATCH = 1420; 	// The MATCH expression is NOT within the standard JPQL expressions 
														// (see ExpressionOperator constants) 	
        												// EclipseLink uses codes from 0 a 500
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR   
/////////////////////////////////////////////////////////////////////////////////////////
	public MySQLPlatformExtension() {
		super();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//     
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initializePlatformOperators() {
		super.initializePlatformOperators();
        // Create user-defined function... Make it available to this platform only
        addOperator(_matchAgainstExpressionOperator());
	}
	@SuppressWarnings("unchecked")
	private static ExpressionOperator _matchAgainstExpressionOperator() {
        ExpressionOperator matchAgainst = new ExpressionOperator();
        matchAgainst.setSelector(EXPRESSION_OPERATOR_MATCH);
        
        // To create the expression MATCH(?) AGAINST (?) create a strings collection that will be rendered alongside the arguments
        // depending on if it's matchAgainst.bePrefix() or matchAgainst.bePostfix()
        // For example if matchAgainst.bePrefix() is set, the expression will be composed as:
        // 		exprStrings(0)   +  ?1  +    exprStrings(1) + ?2 + exprStrings(3)
        //           MATCH(  textCol1,textCol2  ) AGAINST (  'text'  )
        // 		(the exprStrings(0) is printed FIRST (prefix) BEFORE the first argument) 
        final Vector<String> exprStrings = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(3);
        exprStrings.add("MATCH(");
        exprStrings.add(") AGAINST('");
        exprStrings.add("')");
        
        matchAgainst.printsAs(exprStrings);
        matchAgainst.bePrefix();		// Tell the operator to be pretfix, i.e. the operator starts printing before the arguments
        matchAgainst.setNodeClass(FunctionExpression.class);
        
        return matchAgainst;
	}
}
