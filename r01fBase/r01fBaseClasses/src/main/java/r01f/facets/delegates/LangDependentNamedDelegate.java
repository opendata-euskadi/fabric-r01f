package r01f.facets.delegates;

import java.util.Collection;

import r01f.facets.LangDependentNamed;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.locale.Language;

/**
 * Encapsulates the {@link LangDependentNamed} behavior
 * @param <SELF_TYPE>
 */
public class LangDependentNamedDelegate<SELF_TYPE extends HasLangDependentNamedFacet>
	 extends FacetDelegateBase<SELF_TYPE>
  implements LangDependentNamed {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LangDependentNamedDelegate(final SELF_TYPE hasLangDependentNameFacet) {
		super(hasLangDependentNameFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getNameIn(final Language lang) {
		return _modelObject.getName()
						   .getIn(lang)
						   .orNull();
	}
	@Override
	public void setNameIn(final Language lang,final String name) {
		_modelObject.getName()
				    .add(lang,name);
	}
	@Override
	public Collection<Language> getAvailableLanguages() {
		return  _modelObject.getNameByLanguage() != null ? _modelObject.getNameByLanguage()
																		.getDefinedLanguages()
														  : null;
	}
}
