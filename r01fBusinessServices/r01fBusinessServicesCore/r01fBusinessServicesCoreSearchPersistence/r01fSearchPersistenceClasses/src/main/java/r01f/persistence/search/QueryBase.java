package r01f.persistence.search;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.HasMetaDataForHasLanguageModelObject;
import r01f.model.metadata.FieldID;
import r01f.model.search.SearchFilter;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.EqualsQueryClause;


/**
 * Helps creating a query for a searchable repository (whether it's a lucene, db or whatever searchable repository)
 * The query is composed of individual query predicates
 * Classes that extends this type (ie: {@link DBSearchQuery} or {@link LuceneSearchQuery} offer methods to get the
 * product-specific query (ie: a JPA query or a lucene query)
 */
@Accessors(prefix="_")
public abstract class QueryBase<SELF_TYPE extends QueryBase<SELF_TYPE>> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * {@link BooleanQueryClause} that contains the predicates of the filter clauses
	 */
//	@Getter(AccessLevel.PROTECTED) private final BooleanQueryClause _containerBoolQry;
	/**
	 * The language in which the user is doing the query at the user interface
	 */
	@Getter(AccessLevel.PROTECTED) private final Language _UILanguage;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected QueryBase(final Language uiLanguage) {
		_UILanguage = uiLanguage;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link Language} for the query (if necessary by any subtype)
	 * First checks if there's any {@link EqualsQueryClause} for the language
	 * Second checks if the user language has been set
	 * If any of the above identified a {@link Language}, it returns the default {@link Language}
	 * @param searchFilter
	 * @return
	 */
	protected Language getLanguage(final SearchFilter searchFilter) {
		Language outLang = null;
		// [1] - try to see if there's any lang clause
		FieldID languageFieldId = FieldID.from(HasMetaDataForHasLanguageModelObject.SEARCHABLE_METADATA.LANGUAGE);
		BooleanQueryClause containerBoolQry = searchFilter != null ? searchFilter.getBooleanQuery()
																   : null;
		EqualsQueryClause<Language> langEqualsClause = containerBoolQry != null ? containerBoolQry.findLanguageQueryClause(languageFieldId)
																				: null;
		if (langEqualsClause != null) {
			outLang = langEqualsClause.getValue();
		}
		// [2] - use the user interface language
		else if (_UILanguage != null) {
			outLang = _UILanguage;
		}
		// [3] - use the default language
		else {
			outLang = Language.DEFAULT;
		}
		return outLang;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Range helper methods
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	protected static <N extends Number & Comparable<N>> Range<N> _createRange(final N lowerBound,final BoundType lowerBoundType,
																			  final N upperBound,final BoundType upperBoundType,
																			  final Class<N> numberType) {
		Object createdRange = null;
		if (numberType.equals(Integer.class)) {
			int _lowerBound = lowerBound.intValue();
			int	 _upperBound = upperBound.intValue();
			createdRange = _createRange(_lowerBound,lowerBoundType,
					                   _upperBound,upperBoundType);
		} else if (numberType.equals(Long.class)) {
			long _lowerBound = lowerBound.longValue();
			long	 _upperBound = upperBound.longValue();
			createdRange = _createRange(_lowerBound,lowerBoundType,
										_upperBound,upperBoundType);
		} else if (numberType.equals(Double.class)) {
			double _lowerBound = lowerBound.doubleValue();
			double	 _upperBound = upperBound.doubleValue();
			createdRange = _createRange(_lowerBound ,lowerBoundType,
										_upperBound,upperBoundType);
		} else if (numberType.equals(Float.class)) {
			float _lowerBound = lowerBound.floatValue();
			float	 _upperBound = upperBound.floatValue();
			createdRange = _createRange(_lowerBound,lowerBoundType,
					                    _upperBound,upperBoundType);
		} else {
			throw new IllegalArgumentException();
		}

		return (Range<N>)createdRange;
	}


	protected static Range<Integer> _createRange(final int lowerBound,final BoundType lowerBoundType,
											     final int upperBound,final BoundType upperBoundType) {
		return Range.range(lowerBound,lowerBoundType,
			   	   		   upperBound,upperBoundType);
	}
	protected static Range<Long> _createRange(final long lowerBound,final BoundType lowerBoundType,
											  final long upperBound,final BoundType upperBoundType) {
		return Range.range(lowerBound,lowerBoundType,
					   	   upperBound,upperBoundType);
	}
	protected static Range<Double> _createRange(final double lowerBound,final BoundType lowerBoundType,
											    final double upperBound,final BoundType upperBoundType) {
		return Range.range(lowerBound,lowerBoundType,
					   	   upperBound,upperBoundType);
	}
	protected static Range<Float> _createRange(final float lowerBound,final BoundType lowerBoundType,
											   final float upperBound,final BoundType upperBoundType) {
		return Range.range(lowerBound,lowerBoundType,
					   	   upperBound,upperBoundType);
	}
}
