package r01f.html.parser;

import lombok.experimental.Accessors;
import r01f.html.parser.base.HtmlParserTokenBase;

@Accessors(prefix="_")
public class HtmlParserToken
     extends HtmlParserTokenBase<HtmlParserTokenType> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	HtmlParserToken(final HtmlParserTokenType type,final String text) {
		super(type, text);
	}
}
