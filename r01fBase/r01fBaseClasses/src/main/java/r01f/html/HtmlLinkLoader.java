package r01f.html;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import r01f.exceptions.Throwables;
import r01f.mime.MimeType;
import r01f.types.html.HtmlElementId;
import r01f.types.html.HtmlElementJSEvent;
import r01f.types.html.HtmlElementJSEvent.LinkJSEvent;
import r01f.types.url.Url;
import r01f.types.url.web.WebLink;
import r01f.types.url.web.WebLinkPresentationData;
import r01f.types.url.web.WebLinkTargetResourceData;
import r01f.types.url.web.WebLinkTargetResourceData.RelationBetweenTargetAndLinkContainerDocuments;
import r01f.types.url.web.WebLinkText;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;

/**
 * Loads a link from it's html representation
 * <pre class='brush:html'>
 * 		<a href='http://www.euskadi.net/ayudas/migrupo/ikt2013/r33-2220/es'
 * 		   id='myLink'
 * 		   title='a link'
 * 		   lang='en'
 * 		   rel='search'
 * 		   type='application/pdf'
 * 	       style='myStyleClass1 myStyleClass2'
 * 		   onClick='doSomething()'>
 * 				My linked text
 * 		</a>
 * </pre>
 */
@GwtIncompatible
public class HtmlLinkLoader {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final transient String LINK_REGEX = "<a\\s+href='([^']+)'\\s*([^>]+)?>(.*)</a>";
	private static final transient Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX);

	private static final transient String LINK_ATTRIBUTES_REGEX = "\\s*([^=]+)\\s*=\\s*'([^']+)'";
	private static final transient Pattern LINK_ATTRIBUTES_PATTERN = Pattern.compile(LINK_ATTRIBUTES_REGEX);
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Parses a link from it's html representation
	 * @param linkAsString
	 * @return
	 */
	@SuppressWarnings("null")
	public static WebLink parse(final String linkAsString) {
		Preconditions.checkArgument(Strings.isNOTNullOrEmpty(linkAsString),"The link cannot be null");

		WebLink outLinkData = null;
		Matcher m = LINK_PATTERN.matcher(linkAsString);
		if (m.find()) {
			String hrefStr = m.group(1);				// href is mandatory
			String linkAttributes = m.group(2);			// attributes
			String linkText = m.group(3);				// text is mandatory
			// other link properties
			Map<String,String> props = null;
			if (Strings.isNOTNullOrEmpty(linkAttributes)) {
				props = Maps.newHashMap();
				Matcher attrMatcher = LINK_ATTRIBUTES_PATTERN.matcher(linkAttributes);
				while (attrMatcher.find()) {
					props.put(attrMatcher.group(1),attrMatcher.group(2));
				}
			}
			// Create the link data
			String linkTitle = null;
			WebLinkPresentationData presentation = null;
			if (CollectionUtils.hasData(props)) {
				presentation = new WebLinkPresentationData();
				if (props.containsKey("id")) 	presentation.setId(HtmlElementId.forId(props.get("id")));
				if (props.containsKey("title"))	linkTitle = props.get("title");
				if (props.containsKey("lang"))	{
					if (presentation.getTargetResourceData() == null) presentation.setTargetResourceData(new WebLinkTargetResourceData());
					presentation.getTargetResourceData()
								.setLanguage(Languages.fromLanguageCode(props.get("lang")));
				}
				if (props.containsKey("rel") && RelationBetweenTargetAndLinkContainerDocuments.canBe(props.get("rel"))) {
					if (presentation.getTargetResourceData() == null) presentation.setTargetResourceData(new WebLinkTargetResourceData());
					presentation.getTargetResourceData()
								.setRelationWithSource(RelationBetweenTargetAndLinkContainerDocuments.fromName(props.get("rel")));
				}
				if (props.containsKey("type")) {
					if (presentation.getTargetResourceData() == null) presentation.setTargetResourceData(new WebLinkTargetResourceData());
					String mimeTypeStr = props.get("type");
					presentation.getTargetResourceData()
								.setMimeType(new MimeType(mimeTypeStr));
				}
				if (props.containsKey("style")) presentation.setInlineStyle(props.get("style"));
				if (props.containsKey("class")) presentation.withStyleClasses(props.get("class"));
				if (props.containsKey("onClick")) {
					presentation.addJavaScriptEvent(new HtmlElementJSEvent(LinkJSEvent.ON_CLICK,
																		   props.get("onClick")));
				}
			}
			outLinkData = new WebLink(Url.from(hrefStr),
									  new WebLinkText(linkText,linkTitle),
									   presentation);
		} else {
			throw new IllegalArgumentException(Throwables.message("The link {} does NOT match the pattern {}",linkAsString,LINK_REGEX));
		}
		return outLinkData;
	}
//	public static void main(String[] args) {
//		String testLink = "<a href='http://www.euskadi.net/ayudas/migrupo/ikt2013/r33-2220/es' " +
//							 "id='myLink' " +
//							 "title='a link' " +
//							 "lang='en' " +
//							 "rel='search' " +
//							 "type='application/pdf' " +
//							 "style='h' " +
//							 "class='myStyleClass1 myStyleClass2' " +
//							 "onClick='doSomething()'>" +
//							 	"My linked text" +
//						   "</a>";
//		System.out.println(testLink);
//		HtmlLink linkData = HtmlLinkLoader.parse(testLink);
//		System.out.println(HtmlLinkRenderer.render(linkData));
//	}
}





