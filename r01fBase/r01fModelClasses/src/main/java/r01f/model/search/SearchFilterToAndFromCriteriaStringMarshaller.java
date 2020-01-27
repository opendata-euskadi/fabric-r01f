package r01f.model.search;

import com.google.common.annotations.GwtIncompatible;

import lombok.RequiredArgsConstructor;
import r01f.model.metadata.HasTypesMetaData;

@GwtIncompatible
@RequiredArgsConstructor
class SearchFilterToAndFromCriteriaStringMarshaller<F extends SearchFilterForModelObject> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Holds metadata info about model object types
	 */
	private final HasTypesMetaData _hasTypesMetaData;
/////////////////////////////////////////////////////////////////////////////////////////
//  TO - FROM CRITERIA
/////////////////////////////////////////////////////////////////////////////////////////	
    public SearchFilterAsCriteriaString toCriteriaString(final F filter) {
		return SearchFilters.searchFilterForModelObjectToCriteriaString(filter, 
																		_hasTypesMetaData);
    }
	public F fromCriteriaString(final SearchFilterAsCriteriaString criteriaStr) {
		return SearchFilters.searchFilterForModelObjectFromCriteriaString(criteriaStr,
																		  _hasTypesMetaData);
    }
}
