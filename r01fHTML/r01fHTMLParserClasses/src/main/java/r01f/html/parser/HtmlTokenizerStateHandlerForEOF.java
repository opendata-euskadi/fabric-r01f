package r01f.html.parser;

import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlTokenizerStateHandlerForEOF
extends HtmlTokenizerStateHandlerBase<HtmlTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		throw new IllegalStateException();	// never call this
	}
}
