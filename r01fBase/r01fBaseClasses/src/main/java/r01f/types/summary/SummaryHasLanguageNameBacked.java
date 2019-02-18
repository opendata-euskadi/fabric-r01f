package r01f.types.summary;

import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.types.summary.SummaryBases.MutableLangIndependentSummary;

/**
 * Summary in multiple languages for an object that a name in a certain language
 */
public class SummaryHasLanguageNameBacked 
     extends MutableLangIndependentSummary {

	private static final long serialVersionUID = 9205216966859228529L;
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
	private SummaryHasLanguageNameBacked(final HasLangInDependentNamedFacet hasName) {
		super(false);
		_hasName = hasName;
	}
	private SummaryHasLanguageNameBacked(final boolean fullText,
										 final HasLangInDependentNamedFacet hasName) {
		super(fullText);
		_hasName = hasName;
	}
	public static SummaryHasLanguageNameBacked of(final HasLangInDependentNamedFacet hasName) {
		SummaryHasLanguageNameBacked outSummary = new SummaryHasLanguageNameBacked(false,
																				   hasName);
		return outSummary;
	}
	public static SummaryHasLanguageNameBacked fullTextOf(final HasLangInDependentNamedFacet hasName) {
		SummaryHasLanguageNameBacked outSummary = new SummaryHasLanguageNameBacked(true,
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
