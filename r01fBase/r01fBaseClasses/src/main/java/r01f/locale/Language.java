package r01f.locale;


import java.util.Iterator;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;

/**
 * Models languages supported by R01
 * see:
 * 		iso country codes: http://kirste.userpage.fu-berlin.de/diverse/doc/ISO_3166.html
 * 		iso languages: https://www.loc.gov/standards/iso639-2/php/code_list.php
 */
@Accessors(prefix="_")
public enum Language
 implements EnumWithCode<Integer,Language> {
	SPANISH		(10,"es","spa"),
	BASQUE		(11,"eu","eus"),

	ENGLISH		(20,"en","eng"),

	FRENCH		(30,"fr","fra"),

	DEUTCH		(40,"de","deu"),

	ITALIAN		(58,"it","ita"),
	PORTUGUESE	(59,"pt","por"),
	POLISH		(51,"pl","pol"),
	SWEDISH		(52,"sv","swe"),
	HUNGARIAN	(53,"hu","hun"),
	CZECH		(54,"cs","ces"),
	ROMANIAN	(55,"ro","ron"),
	
	RUSSIAN		(57,"ru","rus"),
	
	KOREAN		(50,"ko","kor"),
	JAPANESE	(56,"ja","jpn"),

	ANY			(0,null,null);

	public static Language DEFAULT = Language.SPANISH;		// TODO get it from properties

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Class<Integer> _codeType = Integer.class;
	@Getter private final Integer _code;
	
	// iso codes: see https://www.loc.gov/standards/iso639-2/php/code_list.php
	@Getter private final String _iso639_1;
	@Getter private final String _iso639_2;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private Language(final int code,
					 final String iso639_1,final String iso639_2) {
		_code = code;
		_iso639_1 = iso639_1;
		_iso639_2 = iso639_2;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumWithCodeWrapper<Integer,Language> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(Language.class);
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static Language fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	public static Language fromCode(final int code) {
		return WRAPPER.fromCode(code);
	}
	public static Language fromString(final String s) {
		return Language.fromName(s);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean is(final Language other) {
		return this == other;
	}
	public boolean isNOT(final Language other) {
		return !this.is(other);
	}
	@Override
	public boolean isIn(final Language... els) {
		return WRAPPER.isIn(this,els);
	}
	public boolean in(final Language... others) {
		boolean outIn = false;
		if (CollectionUtils.hasData(others)) {
			for (Language lang : others) {
				if (this == lang) {
					outIn = true;
					break;
				}
			}
		}
		return outIn;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public static String pattern() {
		return CollectionUtils.toStringSeparatedWith(FluentIterable.from(Language.values())
																   .filter(new Predicate<Language>() {
																					@Override
																					public boolean apply(final Language lang) {
																						return lang != ANY;
																					}
																   		   })
																   .toList(),
													 '|');
	}
	@GwtIncompatible()
	public static String patternOfCountryCodes() {
		return CollectionUtils.toStringSeparatedWith(FluentIterable.from(Language.values())
														   .filter(new Predicate<Language>() {
																			@Override
																			public boolean apply(final Language lang) {
																				return lang != ANY;
																			}
														   		   })
														   .transform(new Function<Language,String>() {
																				@Override
																				public String apply(final Language lang) {
																					return Languages.countryLowerCase(lang);
																				}
														   			  })
														   .toList(),
										            '|');
	}
	public static boolean canBe(final String lang) {
		return WRAPPER.canBe(lang);
	}
	public static boolean canBe(final int code) {
		return WRAPPER.canBeFromCode(code);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Set} of {@link Language}
	 * @return
	 */
	public static Set<Language> valueSet() {
		return Sets.newLinkedHashSet(new Iterable<Language>() {
											@Override
											public Iterator<Language> iterator() {
												return Iterators.forArray(Language.values());
											}
									 });
	}
}
