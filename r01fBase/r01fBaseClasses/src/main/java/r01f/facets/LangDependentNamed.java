package r01f.facets;

import java.util.Collection;

import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsWrapper;

/**
 * Interface for objects that can have names in multiple languages
 * Usage:
 * <pre class='brush:java'>
 * 		@Accessors(prefix="_")
 * 		public class MyLangDepNamedType
 * 		  implements HasLangDependentNamedFacet {
 *
 * 			@MarshallField(as="nameByLang",escape=true)
 *			@Getter @Setter private LanguageTexts _nameByLanguage;
 *
 *			@Getter private final LanguageTextsWrapper<MT01WeatherForecastTown> _name = LanguageTextsWrapper.atHasLang(this);
 *
 *			@Override
 *			public LangDependentNamed asLangDependentNamed() {
 *				return new LangDependentNamedDelegate<MyLangDepNamedType>(this);
 *			}
 *		}
 * </pre>
 */
public interface LangDependentNamed {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasLangDependentFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasLangDependentNamedFacet
					extends HasName {	// If an object has a name... it has a summary
		public LangDependentNamed asLangDependentNamed();

		public LanguageTexts getNameByLanguage();
		public void setNameByLanguage(LanguageTexts langTexts);

		public <T> LanguageTextsWrapper<T> getName();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a name in a {@link Language}
	 * @param lang
	 * @param name
	 */
	public void setNameIn(final Language lang,final String name);
	/**
	 * Gets the name in a provided language
	 * @param lang
	 * @return
	 */
	public String getNameIn(Language lang);
	/**
	 * @return the list of languages in which the name is available
	 */
	public Collection<Language> getAvailableLanguages();
}
