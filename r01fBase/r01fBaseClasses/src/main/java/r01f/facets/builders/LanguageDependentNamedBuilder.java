package r01f.facets.builders;

import java.util.Map;

import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.patterns.FactoryFrom;


public class LanguageDependentNamedBuilder<NEXT_BUILDER,
										   T extends HasLangDependentNamedFacet> 
  	 extends FacetBuilderBase<NEXT_BUILDER,T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageDependentNamedBuilder(final NEXT_BUILDER nextBuilder,
										 final T hasLanguageDependentNamedFacet) {
		super(nextBuilder,
			  hasLanguageDependentNamedFacet);
	}
	public LanguageDependentNamedBuilder(final FactoryFrom<T,NEXT_BUILDER> nextBuilderFactory,
										 final T hasLanguageDependentNamedFacet) {
		super(nextBuilderFactory,
			  hasLanguageDependentNamedFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageDependentNamedBuilder<NEXT_BUILDER,T> withNameIn(final Language lang,final String name) {
		_modelObject.getName().add(lang,name);
		return this;
	}
	public NEXT_BUILDER withNames(final LanguageTexts names) {
		for (Language lang : names.getDefinedLanguages()) {
			_modelObject.getName().add(lang,names.get(lang));
		}
		return this.next();
	}
	public NEXT_BUILDER withNames(final Map<Language,String> names) {
		for (Language lang : names.keySet()) {
			_modelObject.getName()
						.add(lang,names.get(lang));
		}
		return this.next();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public NEXT_BUILDER build() {
		return this.next();
	}
}
