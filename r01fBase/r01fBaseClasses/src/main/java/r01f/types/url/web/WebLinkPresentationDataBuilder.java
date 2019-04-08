package r01f.types.url.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.mime.MimeType;
import r01f.patterns.IsBuilder;
import r01f.types.html.HtmlElementId;
import r01f.types.html.HtmlElementJSEvent;
import r01f.types.html.MediaQuery;
import r01f.types.url.web.WebLinkTargetResourceData.RelationBetweenTargetAndLinkContainerDocuments;
import r01f.types.url.web.WebLinkWindowOpeningMode.OpeningWindowAppearance;
import r01f.types.url.web.WebLinkWindowOpeningMode.OpeningWindowBars;
import r01f.types.url.web.WebLinkWindowOpeningMode.OpeningWindowMode;

/**
 * Builder for {@link WebLinkPresentationData} objects
 * Usage:
 * <pre class="brush:java'>
 *	WebLinkPresentationData data = WebLinkPresentationDataBuilder.createWithId(HtmlElementId.forId("myId"))
 *																	.targetResource(Language.SPANISH)
 *																	.withoutAccessKey()
 *																	.newWindowWith(WebLinkBuilder.htmlLinkOpenInWindowWithName("newWindow")
 *																								  .centeredWithDimensions(100,100)
 *																								  .resizable()
 *																								  .withDefaultBars()
 *																								  .showNewWindowIcon()
 *																								  .build())
 *																	.withoutStyleClassNames()
 *																	.withoutJSEvents()
 *																	.withoutMediaQueries()
 *																	.build())
 *										.build();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class WebLinkPresentationDataBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static WebLinkPresentationDataBuilderTargetStep createWithout() {
		return WebLinkPresentationDataBuilder.createWithId((HtmlElementId)null);
	}
	public static WebLinkPresentationDataBuilderTargetStep createWithId(final String id) {
		return WebLinkPresentationDataBuilder.createWithId(HtmlElementId.forId(id));
	}
	public static WebLinkPresentationDataBuilderTargetStep createWithId(final HtmlElementId id) {
		WebLinkPresentationData presentationData = new WebLinkPresentationData();
		if (id != null) presentationData.setId(id);
		return new WebLinkPresentationDataBuilder() { /* nothing */ }
						.new WebLinkPresentationDataBuilderTargetStep(presentationData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WebLinkPresentationDataBuilder
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkPresentationDataBuilderTargetStep {
		private final WebLinkPresentationData _presentation;

		public WebLinkPresentationDataBuilderAccessKeyStep targetResource(final RelationBetweenTargetAndLinkContainerDocuments relation,
																		  final Language lang,
																		  final MimeType mimeType) {
			_presentation.setTargetResourceData(new WebLinkTargetResourceData(relation,
																			   lang,
																			   mimeType));
			return new WebLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
		public WebLinkPresentationDataBuilderAccessKeyStep targetResource(final RelationBetweenTargetAndLinkContainerDocuments relation,
																		  final Language lang) {
			_presentation.setTargetResourceData(new WebLinkTargetResourceData(relation,
																			   lang,
																			   null));		// no mime-type
			return new WebLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
		public WebLinkPresentationDataBuilderAccessKeyStep targetResource(final Language lang,
																		  final MimeType mimeType) {
			_presentation.setTargetResourceData(new WebLinkTargetResourceData(null,		// no relationO
																			  lang,
																			  mimeType));
			return new WebLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
		public WebLinkPresentationDataBuilderAccessKeyStep targetResource(final Language lang) {
			_presentation.setTargetResourceData(new WebLinkTargetResourceData(null,	// no relation
																			  lang,
																			  null));	// no mime-type
			return new WebLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkPresentationDataBuilderAccessKeyStep {
		private final WebLinkPresentationData _presentation;

		public WebLinkPresentationDataBuilderWindowOpeningStep withAccessKey(final WebLinkAccessKey accessKey) {
			_presentation.setAccessKey(accessKey);
			return new WebLinkPresentationDataBuilderWindowOpeningStep(_presentation);
		}
		public WebLinkPresentationDataBuilderWindowOpeningStep withoutAccessKey() {
			return new WebLinkPresentationDataBuilderWindowOpeningStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkPresentationDataBuilderWindowOpeningStep {
		private final WebLinkPresentationData _presentation;

		public WebLinkPresentationDataBuilderCSSStylesStep newWindowWith(final WebLinkWindowOpeningMode newWindowOpeningMode) {
			_presentation.setNewWindowOpeningMode(newWindowOpeningMode);
			return new WebLinkPresentationDataBuilderCSSStylesStep(_presentation);
		}
		public WebLinkPresentationDataBuilderCSSStylesStep sameWindow() {
			return new WebLinkPresentationDataBuilderCSSStylesStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkPresentationDataBuilderCSSStylesStep {
		private final WebLinkPresentationData _presentation;

		public WebLinkPresentationDataBuilderJSEventsStep withStylesWithClassNames(final String...  names) {
			_presentation.withStyleClasses(names);
			return new WebLinkPresentationDataBuilderJSEventsStep(_presentation);
		}
		public WebLinkPresentationDataBuilderJSEventsStep withoutStyleClassNames() {
			return new WebLinkPresentationDataBuilderJSEventsStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkPresentationDataBuilderJSEventsStep {
		private final WebLinkPresentationData _presentation;

		public WebLinkPresentationDataBuilderMediaQueryStep withJSEvents(final HtmlElementJSEvent...  events) {
			_presentation.withJavaScriptEvents(events);
			return new WebLinkPresentationDataBuilderMediaQueryStep(_presentation);
		}
		public WebLinkPresentationDataBuilderMediaQueryStep withoutJSEvents() {
			return new WebLinkPresentationDataBuilderMediaQueryStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkPresentationDataBuilderMediaQueryStep {
		private final WebLinkPresentationData _presentation;

		public WebLinkPresentationDataBuilderBuildStep withOrCombinedMediaQueries(final MediaQuery... mediaQueries) {
			_presentation.withOrCombinedMediaQueries(mediaQueries);
			return new WebLinkPresentationDataBuilderBuildStep(_presentation);
		}
		public WebLinkPresentationDataBuilderBuildStep withoutMediaQueries() {
			return new WebLinkPresentationDataBuilderBuildStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkPresentationDataBuilderBuildStep {
		private final WebLinkPresentationData _presentation;

		public WebLinkPresentationData build() {
			return _presentation;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WebLinkWindowOpeningMode
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkWindowOpeningModeBuilder {
		private final WebLinkWindowOpeningMode _opening;

		public WebLinkWindowOpeningModeBuilderResizableStep centeredWithDimensions(final int width,final int height) {
	    	OpeningWindowAppearance appearance = new OpeningWindowAppearance();
	    	appearance.setOpeningMode(OpeningWindowMode.CENTERED);
	    	_opening.setAppearance(appearance);
	    	return new WebLinkWindowOpeningModeBuilderResizableStep(_opening);
		}
		public WebLinkWindowOpeningModeBuilderResizableStep maximized() {
	    	OpeningWindowAppearance appearance = new OpeningWindowAppearance();
	    	appearance.setOpeningMode(OpeningWindowMode.MAXIMIZED);
			_opening.setAppearance(appearance);
			return new WebLinkWindowOpeningModeBuilderResizableStep(_opening);
		}
		public WebLinkWindowOpeningModeBuilderPositionStep withDimensions(final int width,final int height) {
	    	OpeningWindowAppearance appearance = new OpeningWindowAppearance();
	    	appearance.setOpeningMode(OpeningWindowMode.SIMPLE);
			_opening.setAppearance(appearance);
			_opening.getAppearance().setWidth(width);
			_opening.getAppearance().setHeight(height);
			return new WebLinkWindowOpeningModeBuilderPositionStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkWindowOpeningModeBuilderPositionStep {
		private final WebLinkWindowOpeningMode _opening;

		public WebLinkWindowOpeningModeBuilderResizableStep locatedAt(final int pixelsToTheRightFromUpperLeftCorner,final int pixelsToTheBottomFromTheUpperLeftCorner) {
			_opening.getAppearance().setX(pixelsToTheRightFromUpperLeftCorner);
			_opening.getAppearance().setY(pixelsToTheBottomFromTheUpperLeftCorner);
			return new WebLinkWindowOpeningModeBuilderResizableStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkWindowOpeningModeBuilderResizableStep {
		private final WebLinkWindowOpeningMode _opening;

		public WebLinkWindowOpeningModeBuilderBarsStep resizable() {
			_opening.getAppearance().setResizable(true);
			return new WebLinkWindowOpeningModeBuilderBarsStep(_opening);
		}
		public WebLinkWindowOpeningModeBuilderBarsStep notResizable() {
			_opening.getAppearance().setResizable(false);
			return new WebLinkWindowOpeningModeBuilderBarsStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkWindowOpeningModeBuilderBarsStep {
		private final WebLinkWindowOpeningMode _opening;

		public WebLinkWindowOpeningModeBuilderShowNewWindowIconStep withBars(final OpeningWindowBars bars) {
			_opening.getAppearance().setBars(bars);
			return new WebLinkWindowOpeningModeBuilderShowNewWindowIconStep(_opening);
		}
		public WebLinkWindowOpeningModeBuilderShowNewWindowIconStep withDefaultBars() {
			return new WebLinkWindowOpeningModeBuilderShowNewWindowIconStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkWindowOpeningModeBuilderShowNewWindowIconStep {
		private final WebLinkWindowOpeningMode _opening;

		public WebLinkWindowOpeningModeBuilderBuildStep showNewWindowIcon() {
			_opening.setShowNewWindowIcon(true);
			return new WebLinkWindowOpeningModeBuilderBuildStep(_opening);
		}
		public WebLinkWindowOpeningModeBuilderBuildStep doNOTShowNewWindowIcon() {
			_opening.setShowNewWindowIcon(false);
			return new WebLinkWindowOpeningModeBuilderBuildStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class WebLinkWindowOpeningModeBuilderBuildStep {
		private final WebLinkWindowOpeningMode _opening;

		public WebLinkWindowOpeningMode build() {
			return _opening;
		}
	}
}
