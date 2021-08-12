package r01f.facets.builders;

import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.patterns.FactoryFrom;


public class LanguageInDependentNamedBuilder<NEXT_BUILD_STEP,
											 M extends HasLangInDependentNamedFacet> 
  	 extends FacetBuilderBase<NEXT_BUILD_STEP,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageInDependentNamedBuilder(final NEXT_BUILD_STEP parentBuilder,
										   final M hasLanguageInDependentNamedFacet) {
		super(parentBuilder,
			  hasLanguageInDependentNamedFacet);
	}
	public LanguageInDependentNamedBuilder(final FactoryFrom<M,NEXT_BUILD_STEP> parentBuilderFactory,
										   final M hasLanguageInDependentNamedFacet) {
		super(parentBuilderFactory,
			  hasLanguageInDependentNamedFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public NEXT_BUILD_STEP withName(final String name) {
		_modelObject.setName(name);
    	return this.next();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public NEXT_BUILD_STEP build() {
		return this.next();
	}
}
