package r01f.model.builders.facets;

import java.util.Map;

import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;


public class LanguageDependentNamedBuilder<CONTAINER_TYPE,
										   T extends HasLangDependentNamedFacet> 
  	 extends FacetBuilderBase<CONTAINER_TYPE,T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageDependentNamedBuilder(final CONTAINER_TYPE parentType,
										 final T hasLanguageDependentNamedFacet) {
		super(parentType,
			  hasLanguageDependentNamedFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageDependentNamedBuilder<CONTAINER_TYPE,T> withNameIn(final Language lang,final String name) {
		_modelObject.getName().add(lang,name);
    	return this;
    }
    public CONTAINER_TYPE withNames(final LanguageTexts names) {
    	for (Language lang : names.getDefinedLanguages()) {
    		_modelObject.getName().add(lang,names.get(lang));
    	}
    	return _nextBuilder;
    }
    public CONTAINER_TYPE withNames(final Map<Language,String> names) {
    	for (Language lang : names.keySet()) {
    		_modelObject.getName()
    					.add(lang,names.get(lang));
    	}
    	return _nextBuilder;
    }
}
