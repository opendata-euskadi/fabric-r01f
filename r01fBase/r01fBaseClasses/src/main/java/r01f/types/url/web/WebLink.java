package r01f.types.url.web;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasID;
import r01f.facets.HasLanguage;
import r01f.facets.Tagged;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.html.HtmlElementId;
import r01f.types.tag.TagList;
import r01f.types.url.HasUrl;
import r01f.types.url.Url;
import r01f.util.types.Strings;

/**
 * Models an HTML link
 * @see HtmlLinkPresentationDataBuilder
 * Usage:
 * <pre class="brush:java'>
 *	WebLink link = WebLinkBuilder.of(Url.from("www.google.com"))
 *							.withText("Google",Language.SPANISH)
 *							.presentationData(WebLinkPresentationDataBuilder.createWithId(HtmlElementId.forId("myId"))
 *														.targetResource(Language.SPANISH)
 *														.withoutAccessKey()
 *														.newWindowWith(WebLinkBuilder.htmlLinkOpenInWindowWithName("newWindow")
 *																					  .centeredWithDimensions(100,100)
 *																					  .resizable()
 *																					  .withDefaultBars()
 *																					  .showNewWindowIcon()
 *																					  .build())
 *														.withoutStyleClassNames()
 *														.withoutJSEvents()
 *														.withoutMediaQueries()
 *														.build())
 *							.build();
 * </pre>
 */
@MarshallType(as="webLink")
@Accessors(prefix="_")
public class WebLink
  implements HasUrl,
  			 HasID<HtmlElementId>,
  			 HasLanguage,
  			 HasTaggeableFacet<String> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The url
	 */
	@MarshallField(as="url")
	@Getter @Setter private Url _url;
	/**
	 * The link text
	 * Use {@link WebLinkTextBuilder}
	 */
	@MarshallField(as="texts")
	@Getter @Setter private WebLinkText _textData;
    /**
     * link presentation
     */
	@MarshallField(as="presentation")
    @Getter @Setter private WebLinkPresentationData _presentation;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLink() {
		// default no-args constructor
	}
	public WebLink(final WebLink other) {
		_url = new Url(other.getUrl() != null ? other.getUrl() : new Url(""));	// clone! (but ensure the url is NOT null)
		_textData = other.getTextData();
		_presentation = other.getPresentation() != null ? new WebLinkPresentationData(other.getPresentation()) : null;
	}
	public WebLink(final Url url) {
		this(url,
			 null,	// no text
			 null);	// no presentation data
	}
	public WebLink(final Url url,
				   final WebLinkText text) {
		this(url,
			 text,
			 null);
	}
	public WebLink(final Url url,
				   final WebLinkText text,
				   final WebLinkPresentationData presentation) {
		_url = url;
		_textData = text;
		_presentation = presentation;
	}
	public static WebLink of(final Url url) {
		return new WebLink(url);
	}
	public static WebLink of(final Url url,
							 final WebLinkText text) {
		return new WebLink(url,
						   text);
	}
	public static WebLink of(final Url url,
				   			 final WebLinkText text,
				   			 final WebLinkPresentationData presentation) {
		return new WebLink(url,
						   text,
						   presentation);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACETS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public HtmlElementId getId() {
		return _presentation != null ? _presentation.getId() : null;
	}
	@Override
	public void setId(final HtmlElementId id) {
		if (_presentation == null) _presentation = new WebLinkPresentationData();
		_presentation.setId(id);
	}
	@Override
	public void unsafeSetId(final OID id) {
		this.setId((HtmlElementId)id);
	}
	@Override
	public Language getLanguage() {
		return _textData != null ? _textData.getLang() : null;
	}
	@Override
	public void setLanguage(final Language lang) {
		if (_textData == null) _textData = new WebLinkText();
		_textData.setLang(lang);
	}
	public String getText() {
		return _textData != null ? _textData.getText() : null;
	}
	public void setText(final String text) {
		if (_textData == null) _textData = new WebLinkText();
		_textData.setText(text);
	}
	public String getTitle() {
		return _textData != null ? _textData.getTitle() : null;
	}
	public void setTitle(final String title) {
		if (_textData == null) _textData = new WebLinkText();
		_textData.setTitle(title);
	}
	public String getDescription() {
		return _textData != null ? _textData.getDescription() : null;
	}
	public void setDescription(final String description) {
		if (_textData == null) _textData = new WebLinkText();
		_textData.setDescription(description);
	}
	@Override
	public Tagged<String> asTaggeable() {
		if (_textData == null) _textData = new WebLinkText();
		return _textData.asTaggeable();
	}
	@Override
	public TagList<String> getTags() {
		return _textData != null ? _textData.getTags() : null;
	}
	@Override
	public void setTags(final TagList<String> tags) {
		if (_textData == null) _textData = new WebLinkText();
		_textData.setTags(tags);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the most suitable text to be displayed: if the text is availaible it's returned,
	 * otherwise the alt text is returned and if neither the text nor the alt text are available,
	 * the url is returned
	 * @return
	 */
	public String getDisplayText() {
		return Strings.isNOTNullOrEmpty(this.getText()) 
						? this.getText()
						: Strings.isNOTNullOrEmpty(this.getDescription()) 
								? this.getDescription()
								: this.getUrl() != null
										? this.getUrl().asString()
										: "NO TEXT DEFINED";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof WebLink)) return false;

		WebLink other = (WebLink)obj;
		boolean urlEq = this.getUrl() != null && other.getUrl() != null
								? this.getUrl().equals(other.getUrl())
								: this.getUrl() != null && other.getUrl() == null ? false
																				  : this.getUrl() == null && other.getUrl() != null ? false
																						  											: true;		// both urls null
//		boolean textEq = this.getTextData() != null && other.getTextData() != null
//								? this.getTextData().equals(other.getTextData())
//								: this.getTextData() != null && other.getTextData() == null ? false
//																						    : this.getTextData() == null && other.getTextData() != null ? false
//																				   		   											    				: true;		// both text null
//		boolean presEq = this.getPresentation() != null && other.getPresentation() != null
//								? this.getPresentation().equals(other.getPresentation())
//								: this.getPresentation() != null && other.getPresentation() == null ? false
//																									: this.getPresentation() == null && other.getPresentation() != null ? false
//																																										: true;		// both presentation null
		return urlEq;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_url);
	}
}
