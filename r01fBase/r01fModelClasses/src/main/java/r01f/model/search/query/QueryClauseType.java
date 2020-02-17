package r01f.model.search.query;

import com.google.common.annotations.GwtIncompatible;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

@GwtIncompatible
public enum QueryClauseType 
 implements EnumExtended<QueryClauseType> {
	BOOLEAN,		// join of two or more clauses
	EQUALS,			// a = b
	CONTAINED_IN,	// a,b,c...
	CONTAINS_TEXT,	// text*
	RANGE,			// (..10) / (1..10) (10..)
	NULL,			// a IS NULL / b IS NOT NULL
	HAS_DATA;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumExtendedWrapper<QueryClauseType> WRAPPER = EnumExtendedWrapper.wrapEnumExtended(QueryClauseType.class);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static QueryClauseType fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	@Override
	public boolean isIn(final QueryClauseType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final QueryClauseType el) {
		return WRAPPER.is(this,el);
	}
	
}
