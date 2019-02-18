package r01f.locale;

import com.google.common.base.Optional;

import lombok.RequiredArgsConstructor;

/**
 * A wrapper to access a {@link LanguageTexts} data
 */
@RequiredArgsConstructor
public final class LanguageTextsAccessWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final transient HasLanguageTexts _hasLangTexts;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static LanguageTextsAccessWrapper wrap(final HasLanguageTexts hasLangTexts) {
		return new LanguageTextsAccessWrapper(hasLangTexts);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Optional<String> getIn(final Language lang) {
		if (_hasLangTexts == null) return Optional.fromNullable(null);
		return Optional.fromNullable(_hasLangTexts != null ? _hasLangTexts.get()
																		  .get(lang)
														   : null);
	}
	public String getInOrNull(final Language lang) {
		return this.getIn(lang)
				   .orNull();
	}
	public String getInOrDefault(final Language lang,
								 final String def) {
		return this.getIn(lang)
				   .or(def);
	}
	public Optional<String> getInAnyLanguage() {
		if (_hasLangTexts == null) return Optional.fromNullable(null);
		return Optional.fromNullable(_hasLangTexts != null ? _hasLangTexts.get()
																		  .getAny()
														   : null);
	}
	public String getInAnyLanguageOrDefault(final String def) {
		return this.getInAnyLanguage()
				  .or(def);
	}
	public String getInAnyLanguageOrNull() {
		return this.getInAnyLanguage()
				  .orNull();
	}
}
