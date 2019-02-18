package r01f.util.types.locale;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import r01f.exceptions.Throwables;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.util.types.collections.CollectionUtils;

/**
 * Language locale
 * (this methods are NOT in the {@link Languages} type because GWT does NOT support
 *  {@link Locale})
 */

public class Languages {
/////////////////////////////////////////////////////////////////////////////////////////
//  http://www.w3.org/International/articles/language-tags/
//	http://download1.parallels.com/SiteBuilder/Windows/docs/3.2/en_US/sitebulder-3.2-win-sdk-localization-pack-creation-guide/30801.htm
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final Locale SPANISH = new Locale("es","ES");
	public static final Locale BASQUE = new Locale("eu","ES");
	public static final Locale ENGLISH = new Locale("en","EN");
	public static final Locale FRENCH = new Locale("fr","FR");
	public static final Locale DEUTCH = new Locale("de","DE");
	public static final Locale KOREAN = new Locale("ko","KR");
	public static final Locale POLISH = new Locale("pl","PL");
	public static final Locale SWEDISH = new Locale("sv","SE");
	public static final Locale HUNGARIAN = new Locale("hu","HU");
	public static final Locale CZECH = new Locale("cs","CZ");
	public static final Locale ROMANIAN = new Locale("ro","RO");
	public static final Locale JAPANESE = new Locale("ja","JP");
	public static final Locale RUSSIAN = new Locale("ru","RU");
	public static final Locale ITALIAN = new Locale("it","IT"); 
	public static final Locale PORTUGUESE = new Locale("pt","PT");
	
/////////////////////////////////////////////////////////////////////////////////////////
//  LANGUAGE NAME TRANSLATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Map<Language,LanguageTexts> LANGUAGE_NAMES = Maps.newHashMap();
	static {
		LANGUAGE_NAMES.put(Language.SPANISH,
							new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"EspaÃ±ol")
									.add(Language.BASQUE,"Gaztelania")
									.add(Language.ENGLISH,"Spanish"));
		LANGUAGE_NAMES.put(Language.BASQUE,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"Euskera")
									.add(Language.BASQUE,"Euskara")
									.add(Language.ENGLISH,"Basque"));
		LANGUAGE_NAMES.put(Language.ENGLISH,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"InglÃ©s")
									.add(Language.BASQUE,"Ingelesa")
									.add(Language.ENGLISH,"English"));
		LANGUAGE_NAMES.put(Language.DEUTCH,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"AlemÃ¡n")
									.add(Language.BASQUE,"[eu] AlemÃ¡n")
									.add(Language.ENGLISH,"Deutch"));
		LANGUAGE_NAMES.put(Language.FRENCH,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.THROW_EXCEPTION)
									.add(Language.SPANISH,"FrancÃ©s")
									.add(Language.BASQUE,"[eu] Frances")
									.add(Language.ENGLISH,"French"));
		// TODO completar
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds a {@link Language} from the {@link Locale}
	 * @param loc
	 * @return
	 */
	public static Language of(final Locale loc) {
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (Languages.getLocale(lang).equals(loc)) {
				outLang = lang;
				break;
			}
		}
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the language code (es, eu, en...)
	 * @param language
	 * @return
	 */
	public static Language of(final String language) {
		String theLang = language.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (theLang.equals("cz")) theLang = "cs";	// bug: czech republic was incorrectly represented as cz BUT really it's cs-CZ  
			if (Languages.getLocale(lang).getLanguage().equals(theLang)) {
				outLang = lang;
				break;
			}
		}
		if (outLang == null 
		 && (theLang.equalsIgnoreCase("--") 
				|| theLang.equalsIgnoreCase("any") 
				|| theLang.equalsIgnoreCase("all"))) outLang = Language.ANY;
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the language code (es, eu, en...) and the country code (ES, FR, EN...)
	 * @param language
	 * @param country
	 * @return
	 */
	public static Language of(final String language,
							  final String country) {
		String theLang = language.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			
			Locale loc = Languages.getLocale(lang);
			if (loc.getLanguage().equals(theLang)
			 && loc.getCountry().equals(country)) {
				outLang = lang;
				break;
			}
		}
		if (outLang == null && (theLang.equals("--") || theLang.equals("any"))) outLang = Language.ANY;
		return outLang;
	}
	public static Language fromName(final String name) {
		return Language.fromName(name);
	}
	public static Language fromNameOrThrow(final String name) {
		Language outLang = Languages.fromName(name);
		if (outLang == null) throw new IllegalArgumentException(Throwables.message("{} is NOT a valid {}",name,Language.class));
		return outLang;
	}
	/**
	 * This method does ...
	 * @deprecated replaced by fromCountryCodeLowercase(String langCode)
	 */
	@Deprecated
	public static Language fromContentLangVersionFolder(final String folder) {
		return Languages.fromLanguageCode(folder);
	}
	public static Language fromCountryCodeLowercase(final String langCode) {
		return Languages.fromLanguageCode(langCode); 
	}
	public static Language fromLanguage(final String lang) {
		return Languages.fromLanguageCode(lang);
	}
	public static Language fromLanguageLowercase(final String lang) {
		return Languages.fromLanguageCode(lang);
	}
	public static Language fromLanguageCode(final String langCode) {
		if (langCode == null) throw new IllegalArgumentException("Not a valid lang code (null)");
		Language outLang = Languages.of(langCode.substring(0,2));
		if (outLang == null 
		 && (langCode.equalsIgnoreCase("--") 
				|| langCode.equalsIgnoreCase("any") 
				|| langCode.equalsIgnoreCase("all"))) outLang = Language.ANY;
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the iso 639_1 code 
	 * @param language
	 * @return
	 */
	public static Language fromISO639_1(final String iso) {
		String theIso = iso.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (lang.getIso639_1().equals(theIso)) {
				outLang = lang;
				break;
			}
		}
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the iso 639_2 code 
	 * @param language
	 * @return
	 */
	public static Language fromISO639_2(final String iso) {
		String theIso = iso.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (lang.getIso639_2().equals(theIso)) {
				outLang = lang;
				break;
			}
		}
		return outLang;
	}
	public static boolean canBe(final String name) {
		return Language.canBe(name);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a locale 
	 * @param lang
	 * @return
	 */
	public static Locale getLocale(final Language lang) {
		Locale outLocale = null;
		switch(lang) {
		case ANY:
			throw new IllegalStateException("unknown language");
		case BASQUE:
			outLocale = Languages.BASQUE;
			break;
		case DEUTCH:
			outLocale = Languages.DEUTCH;
			break;
		case ENGLISH:
			outLocale = Languages.ENGLISH;
			break;
		case FRENCH:
			outLocale = Languages.FRENCH;
			break;
		case SPANISH:
			outLocale = Languages.SPANISH;
			break;
		case KOREAN:
			outLocale = Languages.KOREAN;
			break;
		case POLISH:
			outLocale = Languages.POLISH;
			break;	
		case SWEDISH:
			outLocale = Languages.SWEDISH;
			break;	
		case HUNGARIAN:
			outLocale = Languages.HUNGARIAN;
			break;
		case CZECH:
			outLocale = Languages.CZECH;
			break;
		case ROMANIAN:
			outLocale = Languages.ROMANIAN;
			break;	
		case JAPANESE:
			outLocale = Languages.JAPANESE;
			break;	
		case RUSSIAN:
			outLocale = Languages.RUSSIAN;
			break;
		case ITALIAN:
			outLocale = Languages.ITALIAN;
			break;
		case PORTUGUESE:
			outLocale = Languages.PORTUGUESE;
			break;
		default:
			outLocale = Languages.getLocale(Language.DEFAULT);
			break;
		}
		return outLocale;
		
	}
	/**
	 * A var that represents a language
	 * @return
	 */
	public static String genericCountry() {
		return "%R01_LANG%";
	}
	/**
	 * @return the country
	 */
	public static String country(final Language lang) {
		String outCountry = null;
		if (lang == Language.ANY) {
			outCountry = Languages.genericCountry();
		} else {
			outCountry = Languages.getLocale(lang)
								  .getCountry();
		} 
		return outCountry;
	}
	/**
	 * @return the country
	 */
	public static String countryLowerCase(final Language lang) {
		return Languages.country(lang)
						.toLowerCase();
	}
	/**
	 * @return the country
	 */
	public static String countryUppperCase(final Language lang) {
		return Languages.country(lang)
						.toUpperCase();
	}
	/**
	 * @return the language
	 */
	public static String language(final Language lang) {
		if (lang == Language.ANY) return "any";
		return Languages.getLocale(lang)
						.getLanguage();
	}
	/**
	 * @return the language
	 */
	public static String languageLowerCase(final Language lang) {
		return Languages.language(lang)
						.toLowerCase();
	}
	/**
	 * @return the language
	 */
	public static String languageUpperCase(final Language lang) {
		return Languages.language(lang)
						.toUpperCase();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a list of the countries
	 * @return ie: es,eu,en,fr,...
	 */
	public static String[] countries() {
		return _collectionBuilder(new Function<Language,String>() {
										@Override
										public String apply(final Language lang) {
											return Languages.getLocale(lang).getCountry();
										}
								  });
	}
	/**THROW_EXCEPTION
	 * Returns a regular expression to match the countries
	 * @param captureGroup if the regular expression conforms a capture group 
	 * @return (es|eu|en|fr...) if captureGroup=true and (?:es|eu|en|fr...) if captureGroup=false
	 */
	public static String countryMatchRegEx(final boolean captureGroup) {
		String[] countries = Languages.countries();
		return _matchRegEx(countries,
						   captureGroup);
	}
	/**
	 * Returns a list of the languages
	 * @return ie: es,eu,en,fr,...
	 */
	public static String[] languages() {
		return _collectionBuilder(new Function<Language,String>() {
										@Override
										public String apply(final Language lang) {
											return Languages.getLocale(lang).getLanguage();
										}
								  });
	}
	/**
	 * Returns the language name in a given language
	 * (ie: spanish in Language.SPANISH is "EspaÃ±ol"
	 * @param lang
	 * @return
	 */
	public static String languageNameIn(final Language lang) {
		LanguageTexts names = LANGUAGE_NAMES.get(lang);
		if (names == null) return Languages.language(lang);
		String nameIn = names.get(lang);
		if (nameIn == null) return Languages.language(lang);
		return nameIn;
	}
	/**
	 * Returns a regular expression to match the language
	 * @param captureGroup if the regular expression conforms a capture group 
	 * @return (es|eu|en|fr...) if captureGroup=true and (?:es|eu|en|fr...) if captureGroup=false
	 */
	public static String languageMatchRegEx(final boolean captureGroup) {
		String[] languages = Languages.languages();
		return _matchRegEx(languages,
						   captureGroup);
	}
	private static String[] _collectionBuilder(final Function<Language,String> valFunc) {
		String[] out = new String[Language.values().length-1];	// Language.UNKNOWN do not have locale
		int i=0;
		for (Language l : Language.values()) {
			if (l == Language.ANY) continue;
			if (Languages.getLocale(l) != null) out[i++] = valFunc.apply(l);
		}
		return out;
	}
	private static String _matchRegEx(final String[] col,
									  final boolean captureGroup) {
		StringBuilder sb = new StringBuilder(2 + col.length*3);	// ( + 2 chars, the country and the separator)
		sb.append(captureGroup ? "(" : "(?:");
		for (int i=0; i<col.length; i++) {
			sb.append(col[i]);
			if (i < col.length-1) sb.append("|");
		}
		sb.append(")");
		return sb.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Fakes a translation
	 * If the provided text is for example 'my text' and the language is ENGLISH, 
	 * the returned fake translation is '(en) my text'
	 * @param text
	 * @param lang
	 * @return
	 */
	public static String fakeTranslate(final String text,
									   final Language lang) {
		return new StringBuilder(text.length()+5)
					  .append("(").append(Languages.country(lang)).append(") ")
					  .append(text)
					  .toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARATOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link String} comparator that takes the language into account
	 * @param lang
	 * @return
	 */
	public static Comparator<String> stringComparatorFor(final Language lang) {
		// use a collator to compare the names taking the locale into account
		final Collator collator = Collator.getInstance(Languages.getLocale(lang));
		collator.setStrength(Collator.PRIMARY);
		
		return new Comparator<String>() {
						@Override
						public int compare(final String arg0,final String arg1) {
							return collator.compare(arg0,arg1);
						}
			   };
	}
	public static Comparator<Language> comparator() {
		return new Comparator<Language>() {
						@Override
						public int compare(final Language o1,final Language o2) {
							return o1.ordinal() == o2.ordinal() 
										? 0
										: o1.ordinal() > o2.ordinal() ? 1
																	  : -1;
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Filter a collection of objects extending HasLanguage so it return only 
	 * the objects with the given language
	 * @param links
	 * @return
	 */
	public static <L extends HasLanguage> Collection<L> filter(final Collection<L> hasLangCol,
															   final Language lang) {
		return CollectionUtils.hasData(hasLangCol)
					? FluentIterable.from(hasLangCol)
									.filter(new Predicate<L>() {
													@Override
													public boolean apply(final L link) {
														return link.getLanguage().is(lang);
													}
											})
									.toList()
					: null;
	}
	
}
