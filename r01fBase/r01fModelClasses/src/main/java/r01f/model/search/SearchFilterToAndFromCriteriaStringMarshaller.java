package r01f.model.search;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.ModelObject;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.MetaDataDescribable;
import r01f.model.metadata.TypeMetaData;
import r01f.model.search.query.BooleanQueryClause;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible
@Slf4j
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
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
    public SearchFilterAsCriteriaString toCriteriaString(final F filter) {
		// The filter as as a criteria string has the following structure:
		//		uiLanguage:es#filterType:MySearchFilterType#modelObjTypes:{modelObjTypeCode,modelObjTypeCode,...}#{boolean query as criteriaString}
		//			[1]      #                  [2]        #               [3]                                   #             [4]
		
		// [1] - Compose the uiLanguage part
		String uiLangPart = filter.getUILanguage() != null ? Strings.customized("uiLanguage:{}",filter.getUILanguage()) 
											    		   : null;
		// [2] - Compose the filter's type part
		String filterTypePart = Strings.customized("filterType:{}",filter.getClass().getCanonicalName());
		
		// [3] - Compose the model object filter's type part 
		// 2.b: model objs type codes
		Collection<Long> modelObjTypesCodes = CollectionUtils.hasData(filter.getFilteredModelObjectTypes())
												? FluentIterable.from(filter.getFilteredModelObjectTypes())
												    .transform(new Function<Class<? extends MetaDataDescribable>,Long>() {
																		@Override
																		public Long apply(final Class<? extends MetaDataDescribable> modelObjType) {
																			return _hasTypesMetaData.getTypeMetaDataFor(modelObjType)
																										.getTypeMetaData()
																											.modelObjTypeCode();
																		}
												   			  })
												    .toSet()
												: null;
		String modelObjTypesCriteriaStrPart = CollectionUtils.hasData(modelObjTypesCodes)
													? Strings.customized("modelObjTypes:{}",
																 		 CollectionUtils.of(modelObjTypesCodes)
																			    		.toStringCommaSeparated())
													: "";
		
		// [3] - Compose the boolean query criteria string
		String boolClausesPart = filter.getBooleanQuery().encodeAsString();
		
		// --- Join them all
		String outCriteriaStr = null;
		if (uiLangPart != null && boolClausesPart != null) {
			outCriteriaStr = String.format("%s#%s#%s#%s",
										   uiLangPart,filterTypePart,
										   modelObjTypesCriteriaStrPart,
										   boolClausesPart);
		} else if (uiLangPart != null && boolClausesPart == null) {
			outCriteriaStr = String.format("%s#%s#%s",
										   uiLangPart,filterTypePart,
										   modelObjTypesCriteriaStrPart);
		} else if (uiLangPart == null && boolClausesPart != null) {
			outCriteriaStr = String.format("%s#%s#%s",
										   filterTypePart,
										   modelObjTypesCriteriaStrPart,
										   boolClausesPart);
		} else if (uiLangPart == null && boolClausesPart == null) {
			outCriteriaStr = modelObjTypesCriteriaStrPart;
		}
		return SearchFilterAsCriteriaString.of(outCriteriaStr);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern CRITERIA_PATTERN = Pattern.compile("(?:uiLanguage:(" + Language.pattern() + ")#)?filterType:([^#]+)#modelObjTypes:([^#]+)?#(.*)");
	
	@SuppressWarnings("unchecked")
	public F fromCriteriaString(final SearchFilterAsCriteriaString criteriaStr) {
    	F outFilter = null;
		if (!criteriaStr.hasData()) {
			log.error("NO filter info available");
		} else {			
			String uiLangStr = null;
			String filterTypeStr = null;
			String modelObjTypesStr = null;
			String clausesStr = null;
			Matcher m = CRITERIA_PATTERN.matcher(criteriaStr.asString());
			if (m.find()) {
				uiLangStr = m.group(1);
				filterTypeStr = m.group(2);
				modelObjTypesStr = m.group(3);
				clausesStr = m.group(4);
				
				// [1] - Create a filter instance from the info available at the criteria string				
				// 1.2 - Get the filtered model obj types
				Collection<Class<? extends ModelObject>> modelObjTypes = Sets.newHashSet();
				String[] modelObjTypeCodes = modelObjTypesStr.split(",");
				for (String typeCodeStr : modelObjTypeCodes) {
					long typeCode = Long.parseLong(typeCodeStr);
					TypeMetaData<? extends MetaDataDescribable> modelObjMetaData = _hasTypesMetaData.getTypeMetaDataFor(typeCode);
					modelObjTypes.add((Class<? extends ModelObject>)modelObjMetaData.getRawType());
				}
				// 1.2 - Create the filter
				Class<? extends SearchFilterForModelObject> filterType = ReflectionUtils.typeFromClassName(filterTypeStr);
				// outFilter = ReflectionUtils.<F>createInstanceOf(filterType,
				//												   new Class<?>[] {Collection.class},new Object[] {modelObjTypes});
				outFilter = ReflectionUtils.<F>createInstanceOf(filterType);
				if (CollectionUtils.hasData(modelObjTypes)) outFilter.setModelObjectTypesToBeFiltered(modelObjTypes);
				
				// [2] - Set filter data
				if (Strings.isNOTNullOrEmpty(uiLangStr)) outFilter.setUILanguage(Language.fromName(uiLangStr));							
				
				if (Strings.isNOTNullOrEmpty(clausesStr)) {
					Class<? extends ModelObject> anyModelObjType = CollectionUtils.pickOneElement(modelObjTypes);	// if multiple model obj types are set at the filter
																													// all MUST share the filtered fields, so any of the 
																													// types metadata can be used to decode the field clauses
					TypeMetaData<? extends MetaDataDescribable> modelObjMetaData = _hasTypesMetaData.getTypeMetaDataFor(anyModelObjType);
					BooleanQueryClause boolQry = BooleanQueryClause.fromString(clausesStr,
																			   modelObjMetaData);
					((SearchFilterForModelObjectBase<?>)outFilter).setBooleanQuery(boolQry);
				} 
			} else {
				log.error("The criteria string {} does NOT mathch the required pattern {}",criteriaStr,CRITERIA_PATTERN.toString());
			}
		}
		return outFilter;
    }
}
