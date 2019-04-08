package r01f.types.url.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.IsBuilder;
import r01f.types.url.Url;

/**
 * Builds {@link WebLink}s
 * <pre class='brush:java'>
 * 		WebLink link = WebLinkBuilder.of(Url.from("www.google.com")
 * 									 .withText("Google")
 * 									 .in(Language.ENGLISH)
 * 									 .withPresentationData(... use presentation data builder ...)
 * 									 .build();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class WebLinkBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final WebLinkBuilderTextsStep of(final String url) {
		return WebLinkBuilder.of(Url.from(url));
	}
	public static final WebLinkBuilderTextsStep of(final Url url) {
		return new WebLinkBuilder() { /* nothing */ }
					.new WebLinkBuilderTextsStep(url);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkBuilderTextsStep {
		private final Url _url;

		public WebLinkBuilderPresentationDataStep with(final WebLinkText text) {
			return new WebLinkBuilderPresentationDataStep(_url,
														  text);
		}
		public WebLinkBuilderTextsLangStep withTextAndTitle(final String text,final String title) {
			WebLinkText linkText = new WebLinkText();
			linkText.setText(text);
			linkText.setTitle(title);
			return new WebLinkBuilderTextsLangStep(_url,
												   linkText);
		}
		public WebLinkBuilderTextsLangStep withText(final String text) {
			WebLinkText linkText = new WebLinkText();
			linkText.setText(text);
			return new WebLinkBuilderTextsLangStep(_url,
												   linkText);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkBuilderTextsLangStep {
		private final Url _url;
		private final WebLinkText _texts;

		public WebLinkBuilderPresentationDataStep undefinedLanguage() {
			return new WebLinkBuilderPresentationDataStep(_url,
														  _texts);
		}
		public WebLinkBuilderPresentationDataStep in(final Language lang) {
			_texts.setLang(lang);
			return new WebLinkBuilderPresentationDataStep(_url,
														  _texts);
		}
		public WebLink build() {
			return new WebLinkBuilderBuildStep(_url,
											   _texts,
											   null)
							.build();
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkBuilderPresentationDataStep {
		private final Url _url;
		private final WebLinkText _text;

		public WebLinkBuilderBuildStep withoutPresentationData() {
			return new WebLinkBuilderBuildStep(_url,
											   _text,
											   null);
		}
		public WebLinkBuilderBuildStep usingPresentationData(final WebLinkPresentationData presentation) {
			return new WebLinkBuilderBuildStep(_url,
											   _text,
											   presentation);
		}
		public WebLink build() {
			return new WebLinkBuilderBuildStep(_url,
											   _text,
											   null)
							.build();
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkBuilderBuildStep {
		private final Url _url;
		private final WebLinkText _text;
		private final WebLinkPresentationData _presentation;

		public WebLink build() {
			return new WebLink(_url,
							   _text,
							   _presentation);
		}
	}
}
