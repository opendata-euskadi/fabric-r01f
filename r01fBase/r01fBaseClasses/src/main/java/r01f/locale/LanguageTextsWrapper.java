package r01f.locale;

import com.google.common.base.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;

/**
 * Wraps the access to a {@link LanguageTexts} field:
 * Example:
 * <pre class='brush:java'>
 * 		@Accessors(prefix="_")
 * 		public class MyType {
 * 			@Getter @Setter private LanguageTexts _nameByLang;
 * 
 * 			@Getter private final transient LanguageTextsWrapper<MyType> _name = LanguageTextsWrapper.at(this)
 * 																					.wrap(new HasLanguageTexts() {
 * 																					     		public LanguageTexts get() {
 * 																					     			return _nameByLang;
 * 																					     		}
 * 																					     		public void set(LanguageTexts langTexts) {
 * 																					     			_nameByLang = langTexts;
 * 																					     		}
 * 																					       });
 * 		}
 * </pre>
 * If <pre>MyType</pre> implements {@link HasLangDependentNamedFacet} then just:
 * <pre class='brush:java'>
 * 		@Accessors(prefix="_")
 * 		public class MyType 
 * 		  implements HasLangDependentNamedFacet {
 * 			@Getter @Setter private LanguageTexts _nameByLang;
 * 
 * 			@Getter private final transient LanguageTextsWrapper<MyType> _name = LanguageTextsWrapper.at(this);
 * 					private final transient LangDependentNamed _langDepNamedDelegate = new LangDependentNamedDelegate<MyType>(this);
 * 
 * 	        @Override
 * 	        public LangDependentNamed asLangDependentNamed() {
 * 	        	return _langDepNamedDelegate;
 * 	        }
 *	        @Override
 *	        public Summarizable asSummarizable() {
 *	        	return SummarizableBuilder.summarizableFrom(SummaryBuilder.languageDependent()
 *	        															  .create(this));
 *	        }
 * 	   }
 * </pre>
 * 
 * 
 * Now '_nameByLang' field can be easily accessed:
 * <pre class='brush:java'>
 * 		MyType t = new MyType();
 *		t.getName().add(Language.SPANISH,"Nombre")
 *	 	 .getTitle().add(Language.BASQUE,"Izena");
 *		System.out.println("Name: " + info.getTitle().getIn(Language.SPANISH).orNull());
 * </pre>
 * @param <T>
 */
public class LanguageTextsWrapper<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final T _langTextsContainer;
	private final HasLanguageTexts _hasLangTexts;
	private transient LanguageTextsAccessWrapper _accessWrapper;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LanguageTextsWrapper(final T langTextsContainer,
								final HasLanguageTexts hasLangTexts) {
		_langTextsContainer = langTextsContainer;
		_hasLangTexts = hasLangTexts;
		
		// ensure that there exists a LangTexts object
		if (_hasLangTexts.get() == null) _hasLangTexts.set(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL));
		
		// create the wrapper
		_accessWrapper = LanguageTextsAccessWrapper.wrap(_hasLangTexts);
	}
	public static <T> LanguageTextsWrapperBuildStep<T> at(final T langTextsContainer) {
		return new LanguageTextsWrapperBuildStep<T>(langTextsContainer);
	}
	public static <T extends HasLangDependentNamedFacet> LanguageTextsWrapper<T> atHasLang(final T langTextsContainer) {
		return new LanguageTextsWrapper<T>(langTextsContainer,
										   new HasLanguageTexts() {
													@Override
													public LanguageTexts get() {
														return langTextsContainer.getNameByLanguage();
													}
													@Override
													public void set(LanguageTexts langText) {
														langTextsContainer.setNameByLanguage(langText);
													}
										   });
	}
	public static <T extends HasLanguageTexts> LanguageTextsWrapper<T> wrap(final T langTextsContainer) {
		return new LanguageTextsWrapperBuildStep<T>(langTextsContainer)
						.wrap(langTextsContainer);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class LanguageTextsWrapperBuildStep<T> {
		private final T _langTextsContainer;
		public LanguageTextsWrapper<T> wrap(final HasLanguageTexts hasLangTexts) {
			return new LanguageTextsWrapper<T>(_langTextsContainer,
											   hasLangTexts);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PUT
/////////////////////////////////////////////////////////////////////////////////////////
	public T add(final Language lang,
			  	 final String title) {
		// get the lang texts
		LanguageTexts hasLangTexts = _hasLangTexts.get();
		// ensure there's a lang texts containers
		if (hasLangTexts == null) {
			hasLangTexts = new LanguageTextsMapBacked();
			this.set(hasLangTexts);
		}
		// add
		hasLangTexts.add(lang,title);
		
		return _langTextsContainer;
	}
	public T set(final LanguageTexts langTexts) {
		_hasLangTexts.set(langTexts);
		return _langTextsContainer;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET
/////////////////////////////////////////////////////////////////////////////////////////	
	public Optional<String> getIn(final Language lang) {
		return _accessWrapper.getIn(lang);
	}
	public String getInOrNull(final Language lang) {
		return _accessWrapper.getInOrNull(lang);
	}
	public String getInOrDefault(final Language lang,
								 final String def) {
		return _accessWrapper.getInOrDefault(lang,def);
	}
	public Optional<String> getInAnyLanguage() {
		return _accessWrapper.getInAnyLanguage();
	}
	public String getInAnyLanguageOrDefault(final String def) {
		return _accessWrapper.getInAnyLanguageOrDefault(def);
	}
	public String getInAnyLanguageOrNull() {
		return _accessWrapper.getInAnyLanguageOrNull();
	}
}
