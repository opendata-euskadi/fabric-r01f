package r01f.model.builders.facets;

import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;


public class LanguageInDependentNamedBuilder<NEXT_BUILD_STEP,
											 NAMED extends HasLangInDependentNamedFacet> 
  	 extends FacetBuilderBase<NEXT_BUILD_STEP,NAMED> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageInDependentNamedBuilder(final NEXT_BUILD_STEP parentBuilder,
										   final NAMED hasLanguageInDependentNamedFacet) {
		super(parentBuilder,
			  hasLanguageInDependentNamedFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public NEXT_BUILD_STEP withName(final String name) {
		_modelObject.setName(name);
    	return _nextBuilder;
    }
}
