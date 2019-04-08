package r01f.locale;

public interface LanguageTextsI18NBundleBacked 
		 extends LanguageTexts {
	/**
	 * Sets the key for the texts
	 * @param messageKey
	 */
	public LanguageTexts forKey(final String messageKey);
}
