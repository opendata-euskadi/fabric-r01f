package r01f.model.search.query;

import com.google.common.annotations.GwtIncompatible;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.generics.TypeRef;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Base type for query clauses
 */
@GwtIncompatible
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public abstract class QueryClauseBase 
           implements QueryClause {

	private static final long serialVersionUID = 4619225766025953309L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="forMetaData",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private FieldID _fieldId;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <Q extends QueryClause> Q as(final Class<Q> type) {
		return (Q)this;
	}
	@Override @SuppressWarnings("unchecked")
	public <Q extends QueryClause> Q as(final TypeRef<Q> type) {
		return (Q)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof QueryClauseBase)) return false;
		
		QueryClauseBase otherBase = (QueryClauseBase)obj;
		return _fieldId != null ? otherBase.getFieldId() != null ? _fieldId.equals(otherBase.getFieldId())
							    								 : false
							    : true;		// both null
	}
	@Override
	public int hashCode() {
		return _fieldId.hashCode();
	}
}
