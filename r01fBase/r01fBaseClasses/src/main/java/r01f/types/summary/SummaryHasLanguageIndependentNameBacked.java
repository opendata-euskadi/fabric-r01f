package r01f.types.summary;

import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.types.summary.SummaryBases.MutableLangIndependentSummary;

/**
 * Summary in multiple languages for an object that has multiple-languages names
 */
public class SummaryHasLanguageIndependentNameBacked 
     extends MutableLangIndependentSummary {

	private static final long serialVersionUID = 5733649286153305427L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Summary in each language
	 */
	private final HasLangInDependentNamedFacet _hasName;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private SummaryHasLanguageIndependentNameBacked(final HasLangInDependentNamedFacet hasName) {
		super(false);
		_hasName = hasName;
	}
	private SummaryHasLanguageIndependentNameBacked(final boolean fullText,
													final HasLangInDependentNamedFacet hasName) {
		super(fullText);
		_hasName = hasName;
	}
	public static SummaryHasLanguageIndependentNameBacked of(final HasLangInDependentNamedFacet hasName) {
		SummaryHasLanguageIndependentNameBacked outSummary = new SummaryHasLanguageIndependentNameBacked(false,
																										 hasName);
		return outSummary;
	}
	public static SummaryHasLanguageIndependentNameBacked fullTextOf(final HasLangInDependentNamedFacet hasName) {
		SummaryHasLanguageIndependentNameBacked outSummary = new SummaryHasLanguageIndependentNameBacked(true,
																										 hasName);
		return outSummary;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _hasName != null ? _hasName.getName()
								: null;
	}
	@Override
	public void setSummary(final String summary) {
		_hasName.setName(summary);
	}
}
