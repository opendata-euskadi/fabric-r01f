package r01f.html.parser.starttag;


import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlStartTagTokenizerStateHandlerForTagAttrName
extends HtmlTokenizerStateHandlerBase<HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static final String ALLOWED_CHARS = ":_-abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ0123456789";
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlStartTagTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		char c = charReader.read();

		if (_isAllowedChar(c)) {
			// still reading the attr name
			tokenizer.addTextToCurrentToken(c);
		}
		else if (_isWhitespaceChar(c)) {
			// ends the attr name
			charReader.unread(1);
			tokenFinished = true;
			tokenizer.nextState(HtmlStartTagParserTokenizerState.WhiteSpace);
		}
		else if (c == '=') {
			// ends teh attr name and begins the attr value (or whitespace)
			charReader.unread(1);
			tokenizer.nextState(HtmlStartTagParserTokenizerState.EqualsSign);
			tokenFinished = true;
		}
		else if (c == '>' || (c == '/' && charReader.nextEquals(">"))) {
			// end tag: > or />
			tokenizer.nextState(HtmlStartTagParserTokenizerState.EOF);	// we've done!
			tokenFinished = true;
		}
        else if (c == CharacterStreamSource.NULL_CHAR) {
            throw new HtmlParseError(charReader.currentPosition()-1,"Null char detected");
        }
        else if (c == CharacterStreamSource.EOF) {
        	tokenizer.nextState(HtmlStartTagParserTokenizerState.EOF);	// we've done!
        	tokenFinished = true;
        }
        else {
        	throw new HtmlParseError(charReader.currentPosition()-1,"Malformed start tag attribute name: illegal character: " + c);
        }
        return tokenFinished;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected static boolean _isAllowedChar(final char c) {
		return _isAllowedChar(ALLOWED_CHARS,c);
	}
}
