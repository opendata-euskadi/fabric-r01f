package r01f.types.summary;

import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.facets.FullTextSummarizable;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.facets.LangNamed.HasLangNamedFacet;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.locale.LanguageTexts;
import r01f.patterns.IsBuilder;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.summary.SummaryBases.ImmutableLangDependentSummary;
import r01f.types.summary.SummaryBases.ImmutableLangIndependentSummary;

/**
 * {@link Summary} builder for {@link Summarizable} and {@link FullTextSummarizable} objects
 * Usage
 * <pre class='brush:java'>
 *	// Language dependent summary backed by a LanguageTexts object
 *	LangDependentSummary summary1 = SummaryBuilder.languageDependent()
 *												  .create(LanguageTextsBuilder.createMapBacked()
 *														 					  .returningWhenLangTextMissing("--not configured--")
 *														 					  .addForLang(Language.SPANISH,"Spanish summary")
 *														 					  .addForLang(Language.ENGLISH,"English summary")
 *														 					  .build());
 *	// Language dependent full text summary backed by a HasLangDependentNamedFacet
 *	HasLangDependentNamedFacet hasLangDependentNamed = ...;
 *	LangDependentSummary summary2 = SummaryBuilder.languageDependent()
 *												  .createFullText(hasLangDependentNamed);
 *	// Language independent summary
 *	LangIndependentSummary summary3 = SummaryBuilder.languageInDependent()
 *													.create("A summary");
 *	HasLangInDependentNamedFacet hasLangInDependentNamed = ...;
 *	LangIndependentSummary summary4 = SummaryBuilder.languageInDependent()
 *													.createFullText(hasLangInDependentNamed);
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SummaryBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static SummaryBuilderForLangDependent languageDependent() {
		return new SummaryBuilder() { /* nothing */ }
						.new SummaryBuilderForLangDependent();
	}
	public static SummaryBuilderForLangInDependent languageInDependent() {
		return new SummaryBuilder() { /* nothing */ }
						.new SummaryBuilderForLangInDependent();
	}
	public static SummaryBuilderForLangNamed languageNamed() {
		return new SummaryBuilder() { /* nothing */ }
						.new SummaryBuilderForLangNamed();
	}
	@SuppressWarnings("serial")
	public static Summary wrapAsImmutable(final boolean isFullText,
										  final Summary summary) {
		Preconditions.checkArgument(summary != null,"summary cannot be null!");
		if (summary instanceof LangDependentSummary) {
			return new ImmutableLangDependentSummary(isFullText) {
							@Override
							public LanguageTexts asLanguageTexts() {
								return summary.asLangDependent()
											  .asLanguageTexts();
							}	
			};				
		}
		else if (summary instanceof LangIndependentSummary) {
			return new ImmutableLangIndependentSummary(isFullText) {	
							@Override
							public String asString() {
								return summary.asString();
							}
			};			
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	public static Summary wrapAsImmutable(final Summary summary) {
		return SummaryBuilder.wrapAsImmutable(false,
											  summary);
	}
	public static Summary wrapAsImmutableForFullText(final Summary summary) {
		return SummaryBuilder.wrapAsImmutable(true,
											  summary);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class SummaryBuilderForLangDependent {
		@SuppressWarnings("static-method")
		public LangDependentSummary create(final LanguageTexts langTexts) {
			return SummaryLanguageTextsBacked.of(langTexts);	
		}
		@SuppressWarnings("static-method")
		public LangDependentSummary createFullText(final LanguageTexts langTexts) {
			return SummaryLanguageTextsBacked.fullTextOf(langTexts);
		}
		@SuppressWarnings("static-method")
		public LangDependentSummary create(final HasLangDependentNamedFacet hasName) {
			return SummaryHasLanguageDependentNameBacked.of(hasName);
		}
		@SuppressWarnings("static-method")
		public LangDependentSummary createFullText(final HasLangDependentNamedFacet hasName) {
			return SummaryHasLanguageDependentNameBacked.fullTextOf(hasName);
		}
		public LangDependentSummary createFullText(final HasSummaryFacet hasSummary) {
			return this.createFullText(hasSummary.asSummarizable()
												 .getSummary()
												 .asLangDependent()
												 .asLanguageTexts());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class SummaryBuilderForLangInDependent {
		@SuppressWarnings("static-method")
		public LangIndependentSummary create(final CanBeRepresentedAsString summary) {
			return SummaryStringBacked.of(summary);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary create(final String summary) {
			return SummaryStringBacked.of(summary);
		}
		public LangIndependentSummary create(final String summary,final Object... vars) {
			return SummaryStringBacked.of(summary,vars);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary createFullText(final CanBeRepresentedAsString summary) {
			return SummaryStringBacked.fullTextOf(summary);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary createFullText(final String summary) {
			return SummaryStringBacked.fullTextOf(summary);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary createFullText(final String summary,final Object... vars) {
			return SummaryStringBacked.fullTextOf(summary,vars);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary create(final HasLangInDependentNamedFacet hasName) {
			return SummaryHasLanguageIndependentNameBacked.of(hasName);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary createFullText(final HasLangInDependentNamedFacet hasName) {
			return SummaryHasLanguageIndependentNameBacked.fullTextOf(hasName);
		}
		public LangIndependentSummary createFullText(final HasSummaryFacet hasSummary) {
			return this.createFullText(hasSummary.asSummarizable()
												 .getSummary()
												 .asLangIndependent()
												 .asString());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public final class SummaryBuilderForLangNamed {
		@SuppressWarnings("static-method")
		public LangIndependentSummary create(final String summary) {
			return SummaryStringBacked.of(summary);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary createFullText(final String summary) {
			return SummaryStringBacked.fullTextOf(summary);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary create(final HasLangNamedFacet hasName) {
			return SummaryHasLanguageNameBacked.of(hasName);
		}
		@SuppressWarnings("static-method")
		public LangIndependentSummary createFullText(final HasLangNamedFacet hasName) {
			return SummaryHasLanguageNameBacked.fullTextOf(hasName);
		}
		public LangIndependentSummary createFullText(final HasSummaryFacet hasSummary) {
			return this.createFullText(hasSummary.asSummarizable()
												 .getSummary()
												 .asLangIndependent()
												 .asString());
		}
	}
}
