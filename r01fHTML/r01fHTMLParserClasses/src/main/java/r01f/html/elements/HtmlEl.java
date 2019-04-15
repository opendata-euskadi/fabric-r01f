package r01f.html.elements;

import lombok.experimental.Accessors;

@Accessors(prefix="_")
public class HtmlEl 
     extends HtmlElementBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlEl() {
		super("html");
	}
	public HtmlEl( final String tagText) {
		super("html",
			  HtmlElements.parseAttributes(tagText));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlEl newHtmlTagMixingWith(final HtmlEl otherHtmlTag) {
		HtmlEl finalHtmlTag = null;
		if (otherHtmlTag == null) {
			finalHtmlTag = this;
		} else {
			finalHtmlTag = new HtmlEl();
			finalHtmlTag.addAttributesFrom(this);
			finalHtmlTag.addAttributesFrom(otherHtmlTag);	// the other attributes have preference
		}
		return finalHtmlTag;
	}
}