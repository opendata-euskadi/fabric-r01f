package r01f.html.parser.starttag;

import r01f.html.parser.base.HtmlTokenizerBase;
import r01f.io.CharacterStreamSource;

  class HtmlStartTagTokenizer 
extends HtmlTokenizerBase<HtmlStartTagParserTokenType,HtmlStartTagParserToken,
						  HtmlStartTagParserTokenizerState,
						  HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlStartTagTokenizer(final CharacterStreamSource charReader) {
		super(charReader,
			  HtmlStartTagParserTokenizerState.TagName);
	}
}
