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
		String outText = null;
		try {
			outText = _hasLangTexts != null ? _hasLangTexts.get() != null
													? _hasLangTexts.get()
											  			   		   .get(lang)
											  		: null
										 : null;
		} catch (IllegalStateException illEx) {
			// no text for for lang			
		}
		return Optional.fromNullable(outText);
	}
	public String getInOrNull(final Language lang) {
		return this.getIn(lang)
				   .orNull();
	}
	public String getInOrDefault(final Language lang,
								 final String def) {
		return def != null
				? this.getIn(lang)
					  .or(def)
				: this.getIn(lang)
					  .orNull();
	}
	public Optional<String> getInAnyLanguage() {
		if (_hasLangTexts == null) return Optional.fromNullable(null);
		return Optional.fromNullable(_hasLangTexts != null ? _hasLangTexts.get() != null
																	? _hasLangTexts.get()
																				   .getAny()
																	: null
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
