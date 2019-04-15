package r01f.html.elements;

import lombok.experimental.Accessors;

@Accessors(prefix="_") 
public class BodyHtmlEl 
     extends HtmlElementBase {

	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public BodyHtmlEl() {
		super("body");
	}
	public BodyHtmlEl( final String tagText) {
		super("body",
			  HtmlElements.parseAttributes(tagText));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public BodyHtmlEl newBodyTagMixingWith(final BodyHtmlEl otherBodyTag) {
		BodyHtmlEl finalBodyTag = null;
		if (otherBodyTag == null) {
			finalBodyTag = this;
		} else {
			finalBodyTag = new BodyHtmlEl();
			finalBodyTag.addAttributesFrom(this);
			finalBodyTag.addAttributesFrom(otherBodyTag);
		}
		return finalBodyTag;
	}
}