package r01f.types.url.web;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasLang;
import r01f.facets.HasLanguage;
import r01f.facets.Tagged;
import r01f.facets.Tagged.HasTaggeableFacet;
import r01f.facets.delegates.TaggeableDelegate;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.tag.StringTagList;
import r01f.types.tag.TagList;
import r01f.util.types.collections.CollectionUtils;

/**
 * Models an link
 * @see WebLinkTextBuilder
 * Usage:
 * <pre class="brush:java'>
 *	WebLinkText linkText = WebLinkTextBuilder.in(Language.ENGLISH)
 *								  .text("Google")
 * 								  .taggedAs("myLink","this_is_a_link")
 *								  .build();
 * </pre>
 */
@MarshallType(as="webLinkText")
@Accessors(prefix="_")
public class WebLinkText
  implements HasLanguage,HasLang,
  			 HasTaggeableFacet<String> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Lang
	 */
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _lang;
	/**
	 * The text
	 */
	@MarshallField(as="text",escape=true)
	@Getter @Setter private String _text;
    /**
     * Link title
     */
	@MarshallField(as="title",escape=true)
    @Getter @Setter private String _title;
	/**
	 * Link description
	 */
	@MarshallField(as="description",escape=true)
	@Getter @Setter private String _description;
	/**
	 * Tags
	 */
	@MarshallField(as="tags",
				   whenXml=@MarshallFieldAsXml(collectionElementName="tag"))
	@Getter 		private StringTagList _tags;

	@Override
	public void setTags(final TagList<String> tags) {
		_tags = new StringTagList(tags);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkText() {
		// default no-args constructor
	}
	public WebLinkText(final WebLinkText other) {
		_lang = other.getLang();
		_text = other.getText();
		_title = other.getTitle();
		_description = other.getDescription();
		_tags = other.getTags();
	}
	public WebLinkText(final String... tags) {
		this(CollectionUtils.hasData(tags) ? new StringTagList(tags) : null);
	}
	public WebLinkText(final StringTagList tags) {
		this(null,
			 tags);
	}
	public WebLinkText(final Language lang,
				   	   final String... tags) {
		this(lang,
			 CollectionUtils.hasData(tags) ? new StringTagList(tags) : null);
	}
	public WebLinkText(final Language lang,
				   	   final StringTagList tags) {
		this(lang,
			 null,null,
			 tags);
	}
	/*public WebLinkText(final String text,final String title,
				   	   final String... tags) {
		this(text,title,
			 CollectionUtils.hasData(tags) ? new StringTagList(tags) : null);
	}*/
	public WebLinkText(final String text,final String title,
				   	   final StringTagList tags) {
		this(null,
			 text,title,
			 tags);
	}
	/*public WebLinkText(final Language lang,
				   	   final String text,final String title,
				   	   final String... tags) {
		this(lang,
			 text,title,
			 CollectionUtils.hasData(tags) ? new StringTagList(tags) : null);
	}*/
	public WebLinkText(final Language lang,
				   	   final String text,final String title,
				   	   final StringTagList tags) {
		_lang = lang;
		_text = text;
		_title = title;
		_tags = tags;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACETS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Tagged<String> asTaggeable() {
		return new TaggeableDelegate<String,WebLinkText>(this);
	}
	@Override
	public Language getLanguage() {
		return _lang;
	}
	@Override
	public void setLanguage(final Language lang) {
		_lang = lang;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof WebLinkText)) return false;

		WebLinkText other = (WebLinkText)obj;
		boolean langEq = this.getLang() != null && other.getLang() != null
								? this.getLang().equals(other.getLang())
								: this.getLang() != null && other.getLang() == null ? false
																					: this.getLang() == null && other.getLang() != null ? false
																																		: true;		// both lang null
		boolean textEq = this.getText() != null && other.getText() != null
								? this.getText().equals(other.getText())
								: this.getText() != null && other.getText() == null ? false
																					: this.getText() == null && other.getText() != null ? false
																																		: true;		// both text null
		boolean titleEq = this.getTitle() != null && other.getTitle() != null
								? this.getTitle().equals(other.getTitle())
								: this.getTitle() != null && other.getTitle() == null ? false
																					  : this.getTitle() == null && other.getTitle() != null ? false
																																		    : true;		// both title null
		boolean descEq = this.getDescription() != null && other.getDescription() != null
								? this.getDescription().equals(other.getDescription())
								: this.getDescription() != null && other.getDescription() == null ? false
																								  : this.getDescription() == null && other.getDescription() != null ? false
																																		    						: true;		// both title null
		boolean tagsEq = this.getTags() != null && other.getTags() != null
								? this.getTags().equals(other.getTags())
								: this.getTags() != null && other.getTags() == null ? false
									   											    : this.getTags() == null && other.getTags() != null ? false
																				   													    : true;		// both tags null
		return langEq && textEq && titleEq && descEq && tagsEq;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(_title,_text,_description,_lang);
	}
}
