package r01f.model.search.query;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Usage:
 * <pre class='brush:java'>
 * 		NullQueryClause<AppCode> eq = NullQueryClause.forField("myField")
 * 													 .of(AppCode.forId("r01"));
 * </pre>
 *
 * @param <T>
 */
@MarshallType(as="nullClause")
@GwtIncompatible
@Accessors(prefix="_")
@NoArgsConstructor
public class NullQueryClause
	 extends QueryClauseBase {

	private static final long serialVersionUID = 5313898684928391593L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="null")
	@Getter @Setter private boolean _null;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	NullQueryClause(final FieldID fieldId,
					final boolean isNull) {
		super(fieldId);
		_null = isNull;
	}
	public static NullQueryClauseBuilder forField(final FieldID fieldId) {
		return new NullQueryClauseBuilder(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.NULL;
	}	

/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof NullQueryClause)) return false;
		
		if (!super.equals(obj)) return false;	// checks fieldId
		
		NullQueryClause otherNull = (NullQueryClause)obj;
		return _null == otherNull.isNull();		// both null
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getFieldId(),_null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class NullQueryClauseBuilder {
		private final FieldID _fieldId;
		
		public NullQueryClause ofNull(final boolean isNull) {
			return new NullQueryClause(_fieldId,
									   isNull);
		}

	}
	@Override
	public <V> V getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <V> Class<V> getValueType() {
		// TODO Auto-generated method stub
		return null;
	}
}
