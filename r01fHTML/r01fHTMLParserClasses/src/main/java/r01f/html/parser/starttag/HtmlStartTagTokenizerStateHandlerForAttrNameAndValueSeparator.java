package r01f.html.parser.starttag;


import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlStartTagTokenizerStateHandlerForAttrNameAndValueSeparator
extends HtmlTokenizerStateHandlerBase<HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlStartTagTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		char c = charReader.read();
		
		if (c == '=') {
			if (tokenizer.getPreviousState() != HtmlStartTagParserTokenizerState.AttributeName) throw new IllegalStateException("= not preceeded by an attribute name!");
			tokenizer.addTextToCurrentToken("=");
			tokenizer.nextState(HtmlStartTagParserTokenizerState.WhiteSpace);
			tokenFinished = true;
		}
        else {
        	throw new HtmlParseError(charReader.currentPosition()-1,"Malformed start tag white space: illegal character: " + c);
        }
        return tokenFinished;
	}
}
