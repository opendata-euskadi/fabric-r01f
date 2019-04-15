package r01f.html.parser.starttag;

import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlStartTagTokenizerStateHandlerForEOF
extends HtmlTokenizerStateHandlerBase<HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlStartTagTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		throw new IllegalStateException();	// never call this
	}
}
