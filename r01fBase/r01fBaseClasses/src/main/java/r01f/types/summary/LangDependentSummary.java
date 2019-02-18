package r01f.types.summary;

import java.io.Reader;
import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;

import r01f.locale.Language;
import r01f.locale.LanguageTexts;

/**
 * Interface for summarizable objects, that is, objects for which a language-dependant text summary can be obtained and used
 * to be indexed and full-text searched against
 */
public interface LangDependentSummary 
	     extends Summary {
	/**
	 * Sets a {@link Summary} in a {@link Language}
	 * @param lang
	 * @param summary
	 */
	public void setSummary(final Language lang,
						   final String summary);
	/**
	 * Gets the summary as a {@link LanguageTexts} object	
	 * @return
	 */
	public LanguageTexts asLanguageTexts();
	/**
	 * Gets the summary in a {@link Language} as a {@link String}
	 * @param lang
	 * @return the summary as a {@link String}
	 */
	public String asString(final Language lang);
	/**
	 * Gets the summary in a {@link Language} as a {@link Reader}
	 * @return the summary as a {@link Reader}
	 */
	@GwtIncompatible("GWT does not supports IO")
	public Reader asReader(final Language lang);
	/**
	 * @return a {@link Collection} of the {@link Language}s for which a {@link Summary} is available
	 */
	public Set<Language> getAvailableLanguages();
	/**
	 * @param lang
	 * @return true if the summary has no data
	 */
	public boolean isEmpty(final Language lang);
	/**
	 * @param lang
	 * @return true if the summary has data
	 */
	public boolean hasData(final Language lang);
}
