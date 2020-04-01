package r01f.types.url.web;

import java.io.Serializable;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.html.CSSStyleClassName;
import r01f.types.html.HtmlElementId;
import r01f.types.html.HtmlElementJSEvent;
import r01f.types.html.MediaQuery;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * Models an html link presentation data
 * @see WebLinkPresentationDataBuilder
 * Usage:
 * <pre class='brush:java'>
 * 		HtmlLinkPresentationData presentation = HtmlLinkPresentationData.create()
 * 													.withId("myLink")
 * 													.forTargetResource(HtmlLinkTargetResourceData.create()
 * 																			.relatedAs(LICENSE)
 * 																			.withLang(Language.ENGLISH)
 * 																			.withMimeType(MimeTypeForDocument.PDF))
 * 													.openingInNewWindowAs(HtmlLinkWindowOpeningMode.create()
 * 																			.withName("My new window")
 * 																			.withAppearance(OpeningWindowAppearance.create(CENTERED,false)
 * 																								.withDimensions(800,600)
 * 																								.notResizable()
 * 																								.withBars(OpeningWindowBars.create()
 * 																												.showingLocationBar()
 * 																												.showingMenuBar()
 * 																												.hidingStatusBar()
 * 																												.hidingScrollBars())))
 * 													.withStyleClasses(CSSStyleClassName.forId("myStyleClass1"),CSSStyleClassName.forId("myStyleClass2"))
 * 													.addJavaScriptEvent(new HtmlElementJSEvent(LinkJSEvent.ON_CLICK,"doSomething()")
 * 													.withOrCombinedMediaQueries(MediaQuery.createForDevice(MediaQueryDevice.SCREEN)
 *											 												.pixelRatioMinForWebKit(1.5F));
 * </pre>
 * Or using the builder:
 * <pre class="brush:java'>
 *	HtmlLinkPresentationData presentation HtmlLinkBuilder.htmlPresentationDataBuilder()
 *																	.withId(HtmlElementId.forId("myId"))
 *																	.targetResource(Language.SPANISH)
 *																	.withoutAccessKey()
 *																	.newWindowWith(HtmlLinkBuilder.htmlLinkOpenInWindowWithName("newWindow")
 *																								  .centeredWithDimensions(100,100)
 *																								  .resizable()
 *																								  .withDefaultBars()
 *																								  .showNewWindowIcon()
 *																								  .build())
 *																	.withoutStyleClassNames()
 *																	.withoutJSEvents()
 *																	.withoutMediaQueries()
 *																	.build();
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="linkPresentationData")
@Accessors(prefix="_")
public class WebLinkPresentationData
  implements Serializable {

	private static final long serialVersionUID = 6720826281308071548L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Element id
	 */
	@MarshallField(as="id",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private HtmlElementId _id;
	/**
	 * Link destination resource description
	 */
	@MarshallField(as="targetResourceData")
	@Getter @Setter private WebLinkTargetResourceData _targetResourceData;
	/**
	 * Access key
	 */
	@MarshallField(as="accessKey")
	@Getter @Setter private WebLinkAccessKey _accessKey;
	/**
	 * Open target: self / parent / blank / frame name
	 */
	@MarshallField(as="openTarget")
	@Getter @Setter private WebLinkOpenTarget _openTarget;
    /**
     * If the link is opened in a new window this object contains this new window properties
     */
	@MarshallField(as="windowOpeningMode")
    @Getter @Setter private WebLinkWindowOpeningMode _newWindowOpeningMode;
    /**
     * Style classes to apply to the html link
     */
	@MarshallField(as="styleClasses",
				   whenXml=@MarshallFieldAsXml(collectionElementName="styleClass"))
    @Getter @Setter private Collection<CSSStyleClassName> _styleClasses;
	/**
	 * Inline style
	 */
	@MarshallField(as="style")
	@Getter @Setter private String _inlineStyle;
    /**
     * JavaScript events to be handled: eventName - event code
     */
	@MarshallField(as="jsEvents",
				   whenXml=@MarshallFieldAsXml(collectionElementName="event"))
    @Getter @Setter private Collection<HtmlElementJSEvent> _javaScriptEvents;
	/**
	 * Media queries
	 */
	@MarshallField(as="mediaQuery",escape=true)
	@Getter @Setter private Collection<MediaQuery> _mediaQueries;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkPresentationData() {
		// defautl no-args constructor
	}
	public WebLinkPresentationData(final WebLinkPresentationData other) {
		_id = other.getId() != null ? HtmlElementId.forId(other.getId().asString()) : null;
		_openTarget = other.getOpenTarget() != null ? WebLinkOpenTarget.forId(other.getOpenTarget().getId()) : null;
		_targetResourceData = other.getTargetResourceData() != null ? new WebLinkTargetResourceData(other.getTargetResourceData()) : null;
		_accessKey = other.getAccessKey() != null ? new WebLinkAccessKey(other.getAccessKey()) : null;
		_newWindowOpeningMode = other.getNewWindowOpeningMode() != null ? new WebLinkWindowOpeningMode(other.getNewWindowOpeningMode()) : null;
		_styleClasses = CollectionUtils.hasData(other.getStyleClasses()) ? FluentIterable.from(other.getStyleClasses())
																						 .transform(new Function<CSSStyleClassName,CSSStyleClassName>() {
																											@Override
																											public CSSStyleClassName apply(final CSSStyleClassName style) {
																												return new CSSStyleClassName(style);
																											}

																						 			})
																						 .toList()
																		 : null;
		_inlineStyle = Strings.isNOTNullOrEmpty(other.getInlineStyle()) ? new String(other.getInlineStyle()) : null;
		_javaScriptEvents = CollectionUtils.hasData(other.getJavaScriptEvents()) ? FluentIterable.from(other.getJavaScriptEvents())
																								 .transform(new Function<HtmlElementJSEvent,HtmlElementJSEvent>() {
																													@Override
																													public HtmlElementJSEvent apply(final HtmlElementJSEvent jsEvt) {
																														return new HtmlElementJSEvent(jsEvt);
																													}
																								 			})
																								 .toList()
																				 : null;
		_mediaQueries = CollectionUtils.hasData(other.getMediaQueries()) ? FluentIterable.from(other.getMediaQueries())
																						 .transform(new Function<MediaQuery,MediaQuery>() {
																											@Override
																											public MediaQuery apply(final MediaQuery mq) {
																												return new MediaQuery(mq);
																											}
																						 			})
																						 .toList()
																		 : null;

	}
	public static WebLinkPresentationData create() {
		WebLinkPresentationData outData = new WebLinkPresentationData();
		return outData;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkPresentationData withId(final String id) {
		_id = HtmlElementId.forId(id);
		return this;
	}
	public WebLinkPresentationData withId(final HtmlElementId id) {
		_id = id;
		return this;
	}
	public WebLinkPresentationData withOpenTarget(final WebLinkOpenTarget target) {
		_openTarget = target;
		return this;
	}
	public WebLinkPresentationData forTargetResource(final WebLinkTargetResourceData targetResource) {
		_targetResourceData = targetResource;
		return this;
	}
	public WebLinkPresentationData openingInNewWindowAs(final WebLinkWindowOpeningMode newWindowOpeningMode) {
		_newWindowOpeningMode = newWindowOpeningMode;
		return this;
	}
	public WebLinkPresentationData withStyleClasses(final CSSStyleClassName... classes) {
		if (CollectionUtils.hasData(classes)) {
			_styleClasses = Lists.newArrayList(classes);
		}
		return this;
	}
	public WebLinkPresentationData addStyleClass(final CSSStyleClassName cssClassName) {
		if (_styleClasses == null) _styleClasses = Lists.newArrayList();
		_styleClasses.add(cssClassName);
		return this;
	}
	public WebLinkPresentationData withStyleClasses(final String... classes) {
		Collection<CSSStyleClassName> styles = null;
		if (CollectionUtils.hasData(classes)) {
			if (classes.length > 1) {
				styles = FluentIterable.from(classes)
									   .transform(new Function<String,CSSStyleClassName>() {
															@Override
															public CSSStyleClassName apply(final String cssClass) {
																return new CSSStyleClassName(cssClass);
															}
									   			   })
									   .toList();
			} else {
				styles = FluentIterable.from(Splitter.on(",")
													 .trimResults()
													 .split(classes[0]))
									   .transform(new Function<String,CSSStyleClassName>() {
															@Override
															public CSSStyleClassName apply(final String cssClass) {
																return new CSSStyleClassName(cssClass);
															}
									   			   })
										.toList();
			}
			_styleClasses = Lists.newArrayList(styles);
		}
		return this;
	}
	public WebLinkPresentationData withJavaScriptEvents(final HtmlElementJSEvent... events) {
		_javaScriptEvents = Lists.newArrayList(events);
		return this;
	}
	public WebLinkPresentationData addJavaScriptEvent(final HtmlElementJSEvent event) {
		if (_javaScriptEvents == null) _javaScriptEvents = Lists.newArrayList();
		_javaScriptEvents.add(event);
		return this;
	}
	public WebLinkPresentationData withOrCombinedMediaQueries(final MediaQuery... orCombinedMediaQueries) {
		if (CollectionUtils.hasData(orCombinedMediaQueries)) {
			Collection<MediaQuery> mq = Lists.newArrayList(orCombinedMediaQueries);
			_mediaQueries = mq;
		}
		return this;
	}
	public WebLinkPresentationData addOrCombinedMediaQuery(final MediaQuery qry) {
		if (_mediaQueries == null) _mediaQueries = Lists.newArrayList();
		_mediaQueries.add(qry);
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isOpeningInNewWindow() {
		return (_openTarget != null && _openTarget.isNOT(WebLinkOpenTarget.SELF))
				||
			   _newWindowOpeningMode != null;
				
	}
}
