package r01f.html.parser.starttag;

import lombok.experimental.Accessors;
import r01f.html.parser.base.HtmlParserTokenBase;

@Accessors(prefix="_")
public class HtmlStartTagParserToken
     extends HtmlParserTokenBase<HtmlStartTagParserTokenType> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	HtmlStartTagParserToken(final HtmlStartTagParserTokenType type,final String text) {
		super(type,text);
	}
}
