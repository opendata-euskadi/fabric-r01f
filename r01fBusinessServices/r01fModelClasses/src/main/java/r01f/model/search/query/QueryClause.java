package r01f.model.search.query;

import java.io.Serializable;

import com.google.common.annotations.GwtIncompatible;

import r01f.generics.TypeRef;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;


/**
 * Interface for query clauses
 */
@MarshallPolymorphicTypeInfo
@GwtIncompatible
public interface QueryClause 
		 extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The field which the query refers to
	 * @return
	 */
	public FieldID getFieldId();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the query clause type
	 */
	public QueryClauseType getClauseType();
	/**
	 * @return The clause value
	 */
	public <V> V getValue();
	/**
	 * @return the type of the value
	 */
	public <V> Class<V> getValueType();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @pram typeRef
	 * @return the query type casted
	 */
	public <Q extends QueryClause> Q as(final Class<Q> type);
	/**
	 * @param typeRef
	 * @return
	 */
	public <Q extends QueryClause> Q as(final TypeRef<Q> typeRef);
}
