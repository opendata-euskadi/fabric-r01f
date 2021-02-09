package r01f.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.types.html.CSSStyleClassName;
import r01f.types.html.HtmlElementId;
import r01f.types.html.HtmlElementJSEvent;
import r01f.types.url.Url;
import r01f.types.url.web.WebLink;
import r01f.types.url.web.WebLinkOpenTarget;
import r01f.types.url.web.WebLinkPresentationData;
import r01f.types.url.web.WebLinkTargetResourceData;
import r01f.types.url.web.WebLinkText;
import r01f.types.url.web.WebLinkWindowOpeningMode;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;


/**
 * Renders html link presentation data
 * <pre class='brush:java'>
 * 		String html = HtmlLinkRenderer.of(url,
 * 										  "The link text","The link title",
 * 										  presentation)
 * 									  .render();
 * </pre>
 * If you want to include the link XML as an XML data island (see https://developer.mozilla.org/en/docs/Using_XML_Data_Islands_in_Mozilla)
 * <pre class='brush:java'>
 * 		String html = HtmlLinkRenderer.of(url,
 * 										  "The link text","The link title",
 * 										  presentation)
 * 									  .renderWithXmlData(xmlDataIsland);
 * </pre>
 */
@GwtIncompatible
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HtmlLinkRenderer {
///////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////
	public static HtmlLinkRendererRenderStep of(final WebLink link) {
		return new HtmlLinkRenderer() { /* nothing */ }
						.new HtmlLinkRendererRenderStep(link);
	}

	public static HtmlLinkRendererRenderStep of(final Url url,
												final String linkText,final String linkTitle) {
		return HtmlLinkRenderer.of(url,null,
								   linkText,linkTitle);
	}
	public static HtmlLinkRendererRenderStep of(final Url url,final Language lang,
												final String linkText,final String linkTitle) {
		return HtmlLinkRenderer.of(url,lang,
								   linkText,linkTitle,
								   null);
	}
	public static HtmlLinkRendererRenderStep of(final Url url,
												final String linkText,final String linkTitle,
												final WebLinkPresentationData presentation) {
		return HtmlLinkRenderer.of(url,null,
								   linkText,linkTitle,
								   presentation);
	}
	public static HtmlLinkRendererRenderStep of(final Url url,final Language lang,
												final String linkText,final String linkTitle,
												final WebLinkPresentationData presentation) {
		return HtmlLinkRenderer.of(url,
								  new WebLinkText(lang,
												  linkText,linkTitle, null),// Fixes an issue with method overload
								  presentation);
	}
	public static HtmlLinkRendererRenderStep of(final Url url,
												final WebLinkText text,
												final WebLinkPresentationData presentation) {
		return new HtmlLinkRenderer() { /* nothing */ }
						.new HtmlLinkRendererRenderStep(new WebLink(url,
																	text,
																	presentation));
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkRendererRenderStep {
		private final WebLink _link;

		/**
		 * Renders a link
		 * @return
		 */
		public String render() {
			String outLink = "<a href=''></a>";
			if (_link != null) {
				outLink = Strings.customized("<a href='{}' {}>{}</a>",
											   		_link.getUrl().asStringNotUrlEncodingQueryStringParamsValues(),
											   		_renderPresentationData(_link.getText() != null ? _link.getTitle() : null,
											   								_link.getPresentation()),
											   		_link.getText() != null ? _link.getText() 
											   								: _link.getUrl() != null ? _link.getUrl() : "");
			}
			return outLink;
		}
		public String renderForceProtocol() {
			String outLink = "<a href=''></a>";
			if (_link != null) {
				String urlStr = _link.getUrl().asStringNotUrlEncodingQueryStringParamsValues();
				if (_link.getUrl().getProtocol() == null) urlStr = "http://" + urlStr;
				outLink = Strings.customized("<a href='{}' {}>{}</a>",
											   		urlStr,
											   		_renderPresentationData(_link.getText() != null ? _link.getTitle() : null,
											   								_link.getPresentation()),
											   		_link.getText() != null ? _link.getText() 
											   								: _link.getUrl() != null ? _link.getUrl() : "");
			}
			return outLink;
		}
		public String renderForceNewWindow() {
			String outLink = "<a href=''></a>";
			if (_link != null) {
				outLink = Strings.customized("<a href='{}' {}>{}</a>",
											   		_link.getUrl().asStringNotUrlEncodingQueryStringParamsValues(),
											   		_renderPresentationData(_link.getText() != null ? _link.getTitle() : null,
											   								WebLinkPresentationData.create()
											   													   .withOpenTarget(WebLinkOpenTarget.BLANK)),		// force new win
											   		_link.getText() != null ? _link.getText() 
											   								: _link.getUrl() != null ? _link.getUrl() : "");
			}
			return outLink;
		}
		public String renderForceProtocolAndNewWindow() {
			String outLink = "<a href=''></a>";
			if (_link != null) {
				String urlStr = _link.getUrl().asStringNotUrlEncodingQueryStringParamsValues();
				if (_link.getUrl().getHost() != null && _link.getUrl().getProtocol() == null) urlStr = "http://" + urlStr;
				outLink = Strings.customized("<a href='{}' {}>{}</a>",
											   		urlStr,
											   		_renderPresentationData(_link.getText() != null ? _link.getTitle() : null,
											   								WebLinkPresentationData.create()
											   													   .withOpenTarget(WebLinkOpenTarget.BLANK)),		// force new win
											   		_link.getText() != null ? _link.getText() 
											   								: _link.getUrl() != null ? _link.getUrl() : "");
			}
			return outLink;
		}
		/**
		 * Renders a link alongside with an XML representation of the link in a data island
		 * (see https://developer.mozilla.org/en/docs/Using_XML_Data_Islands_in_Mozilla)
		 * @return
		 */
		public String renderWithXmlData(final String xmlData) {
			// An id for the link is mandatory so if none is provided one is generated
			HtmlElementId id = _link.getPresentation() != null ? _link.getPresentation().getId() : null;
			if (id != null) id = HtmlElementId.supply();
			String xmlDataIsland = Strings.isNOTNullOrEmpty(xmlData) ? Strings.customized("<script id='{}_data' type='application/xml'>\n{}\n</script>",
													  									  id,xmlData)
																	 : null;
			String outLink = Strings.customized("{}\n" +
												"{}",
												this.render(),
												xmlDataIsland);		// data island
			return outLink;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _renderPresentationData(final String title,
												  final WebLinkPresentationData data) {
		if (data == null) return "";

		// >>> [1] - Presentation data
		Collection<String> presentationDataParams = _presentationDataParams(title,data);

		// >>> [2] - Target resource data
		Collection<String> targetDataParams = _targetResourceDataParams(data.getTargetResourceData());

		// >>> [3] - Open target: _blank|_self|_parent|_top|frameName
		String openTarget = data.getOpenTarget() != null ? Strings.customized("target='{}'",
																			  data.getOpenTarget())
														 : null;
		// >>> [4] - window opening features
		Collection<String> windowOpeningParam = _windowOpeningModeParams(data.getNewWindowOpeningMode());

		Collection<String> allParams = Sets.newHashSet();
		if (Strings.isNOTNullOrEmpty(openTarget)) allParams.add(openTarget);
		if (CollectionUtils.hasData(presentationDataParams)) allParams.addAll(presentationDataParams);
		if (CollectionUtils.hasData(targetDataParams)) allParams.addAll(targetDataParams);
		if (CollectionUtils.hasData(windowOpeningParam)) allParams.addAll(windowOpeningParam);

		// ---- Return
		return CollectionUtils.toStringSeparatedWith(allParams," ");
	}
	private static Collection<String> _presentationDataParams(final String title,
															  final WebLinkPresentationData data) {
		if (data == null) return null;

		Collection<String> params = new ArrayList<String>();

		// Id
		if (data.getId() != null) {
			params.add(Strings.customized("id='{}'",
							 		 	  data.getId()));
		}
		// Title
		if (Strings.isNOTNullOrEmpty(title)) {
			params.add(Strings.customized("title='{}'",
							  		 	  title.trim()));
		}
		// style classes
		if (CollectionUtils.hasData(data.getStyleClasses())) {
			StringBuilder sb = new StringBuilder(data.getStyleClasses().size() * 10);
			for (Iterator<CSSStyleClassName> styleIt = data.getStyleClasses().iterator(); styleIt.hasNext(); ) {
				CSSStyleClassName style = styleIt.next();
				sb.append(style);
				if (styleIt.hasNext()) sb.append(" ");
			}
			params.add(Strings.customized("class='{}'",
							  		      sb));
		}
		// inline style
		if (Strings.isNOTNullOrEmpty(data.getInlineStyle())) {
			params.add(Strings.customized("style='{}'",
							  		 	  data.getInlineStyle().trim()));
		}
		// JavaScript events
		if (CollectionUtils.hasData(data.getJavaScriptEvents())) {
			StringBuilder sb = new StringBuilder(data.getJavaScriptEvents().size() * 30);
			for (Iterator<HtmlElementJSEvent> eventIt = data.getJavaScriptEvents().iterator(); eventIt.hasNext(); ) {
				HtmlElementJSEvent event = eventIt.next();
				sb.append(Strings.customized("{}='{}'",
								 			 event.getEvent().getCode(),
								 			 event.getJsCode()));
				if (eventIt.hasNext()) sb.append(" ");
			}
			params.add(sb.toString());
		}
		return params;
	}
	private static Collection<String> _targetResourceDataParams(final WebLinkTargetResourceData resData) {
		if (resData == null) return null;

		Collection<String> parts = new ArrayList<String>();

		if (resData.getLanguage() != null) {
			parts.add(Strings.customized("lang='{}'",
							 			 Languages.languageLowerCase(resData.getLanguage())));
		}
		if (resData.getRelationWithSource() != null) {
			parts.add(Strings.customized("rel='{}'",
							 			 resData.getRelationWithSource().name().toLowerCase()));
		}
		if (resData.getMimeType() != null) {
			parts.add(Strings.customized("type='{}'",
							 			 resData.getMimeType()));
		}

		// ---- Return
		return parts;
	}
	private static Collection<String> _windowOpeningModeParams(final WebLinkWindowOpeningMode openMode) {
		Collection<String> parts = new ArrayList<String>();

		// TODO terminar el renderizado del modo de apertura del enlace
		if (openMode != null) {
			/* todo terminar */
		}

		// ---- Return
		return parts;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

}
