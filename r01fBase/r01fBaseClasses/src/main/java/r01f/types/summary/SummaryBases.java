package r01f.types.summary;

import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.objectstreamer.annotations.MarshallIgnoredField;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SummaryBases {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	static abstract class SummaryBase
	           implements Summary {

		private static final long serialVersionUID = -8822340374297098830L;
		/**
		 * Tells if this summary is going to be indexed 
		 */
		@MarshallIgnoredField	// do not serialize, do not track changes
		private final transient boolean _fullTextSummary;
		
		@Override
		public boolean isLangDependent() {
			return this instanceof LangDependentSummary;
		}
		@Override
		public boolean isLangIndependent() {
			return this instanceof LangIndependentSummary;
		}
		@Override
		public boolean isFullTextSummary() {
			return _fullTextSummary;
		}
		@Override
		public LangDependentSummary asLangDependent() {
			return (LangDependentSummary)this;
		}
		@Override
		public LangIndependentSummary asLangIndependent() {
			return (LangIndependentSummary)this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static abstract class LangDependentSummaryBase
			      extends SummaryBase 
               implements LangDependentSummary {

		private static final long serialVersionUID = 2427249816412571251L;
		
		public LangDependentSummaryBase(final boolean fullText) {
			super(fullText);
		}
		@Override
		public String asString(final Language lang) {
			return this.asLanguageTexts() != null ? this.asLanguageTexts().get(lang)
												  : null;
		}
		@Override @GwtIncompatible("GWT does not supports IO")
		public Reader asReader(final Language lang) {
			return new StringReader(this.asString(lang));
		}
		@Override
		public boolean isEmpty(final Language lang) {
			String str = this.asString(lang);
			return str == null || str.trim().length() == 0;
		}
		@Override
		public boolean hasData(final Language lang) {
			return !this.isEmpty(lang);
		}
		@Override
		public Set<Language> getAvailableLanguages() {
			LanguageTexts langTexts = this.asLanguageTexts();
			return langTexts != null ? langTexts.getDefinedLanguages()
									 : null;
		}
	}
	public static abstract class MutableLangDependentSummary 
						 extends LangDependentSummaryBase {
		private static final long serialVersionUID = 5061045724104787443L;
		
		protected MutableLangDependentSummary(final boolean fullText) {
			super(fullText);
		}
	}
	@Immutable
	public static abstract class ImmutableLangDependentSummary 
					     extends MutableLangDependentSummary {

		private static final long serialVersionUID = 2932174854238079765L;
		protected ImmutableLangDependentSummary(final boolean fullText) {
			super(fullText);
		}
		@Override
		public void setSummary(final Language lang,final String summary) {
			throw new UnsupportedOperationException("Summary cannot be set");
		}
		@Override
		public String asString() {
			return this.asString(Language.DEFAULT);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static abstract class LangIndependentSummaryBase 
			      extends SummaryBase 
               implements LangIndependentSummary {
		
		private static final long serialVersionUID = 968762068851924097L;

		public LangIndependentSummaryBase(final boolean fullText) {
			super(fullText);
		}
		@Override @GwtIncompatible("GWT does not supports IO")
		public Reader asReader() {
			return !this.isEmpty() ? new StringReader(this.asString())
								   : null;
		}
		@Override
		public boolean isEmpty() {
			String str = this.asString();
			return str == null || str.trim().length() == 0;
		}
		@Override
		public boolean hasData() {
			return !this.isEmpty();
		}
	}
	public static abstract class MutableLangIndependentSummary 
				         extends LangIndependentSummaryBase {
	
		private static final long serialVersionUID = 1341093279101233181L;

		protected MutableLangIndependentSummary(final boolean fullText) {
			super(fullText);
		}
	}
	@Immutable
	public static abstract class ImmutableLangIndependentSummary 
				         extends MutableLangIndependentSummary {

		private static final long serialVersionUID = 4750780736595116663L;
		
		protected ImmutableLangIndependentSummary(final boolean fullText) {
			super(fullText);
		}
		@Override
		public void setSummary(final String summary) {
			throw new UnsupportedOperationException("Summary cannot be set");
		}
	}
}
