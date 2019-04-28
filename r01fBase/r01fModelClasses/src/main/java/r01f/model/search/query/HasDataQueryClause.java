package r01f.model.search.query;

import com.google.common.annotations.GwtIncompatible;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

/**
 * Query clause that checks if there's any data on a field
 * Usage
 * <pre class='brush:java'>
 *		HasDataQueryClause hasData = HasDataQueryClause.forMetaData("myField");
 * </pre>
 * @param <T>
 */
@MarshallType(as="hasDataClause") 
@GwtIncompatible
@Accessors(prefix="_")
@NoArgsConstructor
public class HasDataQueryClause
     extends QueryClauseBase {

	private static final long serialVersionUID = 7795282433302951121L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	HasDataQueryClause(final FieldID fieldId) {
		super(fieldId);
	}
	public static HasDataQueryClause forField(final FieldID fieldId) {
		return new HasDataQueryClause(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.HAS_DATA;
	}	
	@Override
	public <V> V getValue() {
		throw new IllegalStateException(Strings.customized("{} clauses do not have values",HasDataQueryClause.class));
	}
	@Override
	public <V> Class<V> getValueType() {
		throw new IllegalStateException(Strings.customized("{} clauses do not have values",HasDataQueryClause.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof HasDataQueryClause)) return false;
		
		return super.equals(obj);	// checks fieldId
	}
	@Override
	public int hashCode() {
		return super.hashCode();	
	}	
}
