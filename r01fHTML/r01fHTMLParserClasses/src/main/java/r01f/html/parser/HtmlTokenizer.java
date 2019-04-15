package r01f.html.parser;

import r01f.html.parser.base.HtmlTokenizerBase;
import r01f.io.CharacterStreamSource;

  class HtmlTokenizer
extends HtmlTokenizerBase<HtmlParserTokenType,HtmlParserToken,
						  HtmlParserTokenizerState,
						  HtmlTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlTokenizer(final CharacterStreamSource charReader) {
		super(charReader,
			  HtmlParserTokenizerState.Text);
	}
}
