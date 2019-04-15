package r01f.html.parser.starttag;


import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.html.parser.base.HtmlTokenizerStateHandlerBase;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlStartTagTokenizerStateHandlerForTagName
extends HtmlTokenizerStateHandlerBase<HtmlStartTagTokenizer> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	static final String ALLOWED_CHARS = "_-abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ0123456789";
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlStartTagTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		char c = charReader.read();
		
		if (tokenizer.getCurrentTokenText().length() == 0 && c == '<') {
			// ignore
		}
		else  if (_isAllowedChar(c)) {
        	// tagname
        	tokenizer.addTextToCurrentToken(c);				
		}
		else if (_isWhitespaceChar(c)) {
			// begins whitespace
        	charReader.unread(1);					// unread... it's another token	
    		tokenizer.nextState(HtmlStartTagParserTokenizerState.WhiteSpace);
    		tokenFinished = true;
        }
		else if (tokenizer.getCurrentTokenText().length() > 0 
			  && (c == '>' || (c == '/' && charReader.nextEquals(">")))) {		
			// <tagName> or <tagName/>
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
        	throw new HtmlParseError(charReader.currentPosition()-1,"Malformed start tag: illegal tag name character: " + c);
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
