package r01f.model.search.query;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum QueryCondition 
 implements EnumExtended<QueryCondition> {
	beEqualTo,			// text, date or number
	beInsideRange,		// a range for example of dates or numbers
	beWithin,			// within an spectrum of values (ie an enum)
	last,				// for dates: ie last x days from now
	next,				// for dates: ie next x days from now
	contain,			// text contain
	haveData;			// have data (not null)
	
	private static EnumExtendedWrapper<QueryCondition> _enums = EnumExtendedWrapper.wrapEnumExtended(QueryCondition.class);
	
	public static String pattern() {
		return CollectionUtils.of(QueryCondition.values()).toStringSeparatedWith('|');
	}
	public static QueryCondition fromName(final String name) {
		return _enums.fromName(name);
	}
	public static QueryCondition fromQuery(final QueryClause clause) {
		QueryCondition outPredicate = null;
		if (clause instanceof EqualsQueryClause) {
			outPredicate = beEqualTo;
			
		} else if (clause instanceof ContainsTextQueryClause) {
			outPredicate = contain;
			
		} else if (clause instanceof ContainedInQueryClause) {
			outPredicate = beWithin;
			
		} else if (clause instanceof RangeQueryClause) {
			outPredicate = beInsideRange;
			
		} else if (clause instanceof HasDataQueryClause) {
			outPredicate = haveData;
			
		} else if (clause instanceof BooleanQueryClause) {
			throw new IllegalStateException("NOT currently supported!!");
		}
		return outPredicate;
	}
	@Override
	public boolean isIn(final QueryCondition... els) {
		return _enums.isIn(this,els);
	}
	@Override
	public boolean is(final QueryCondition el) {
		return _enums.is(this,el);
	}
}