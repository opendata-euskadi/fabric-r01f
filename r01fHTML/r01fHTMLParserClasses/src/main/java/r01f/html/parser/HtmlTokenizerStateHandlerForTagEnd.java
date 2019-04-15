package r01f.html.parser;

import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlTokenizerStateHandlerForTagEnd
extends HtmlTokenizerStateHandlerForTagBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		
		char c = charReader.read();
		if (tokenizer.getCurrentTokenText().length() == 0 && c == '<') {
			tokenizer.addTextToCurrentToken(c);			
		}
		else if (c == '>') {
        	tokenizer.addTextToCurrentToken(c);
    		tokenizer.nextState(HtmlParserTokenizerState.Text);
    		tokenFinished = true;
        }
        else if (!_isAllowedChar(c)) {
        	charReader.unread(1);					// unread... it's another token	
    		tokenizer.nextState(HtmlParserTokenizerState.Text);
    		tokenFinished = false;
        }
        else if (c == CharacterStreamSource.NULL_CHAR) {                
            throw new HtmlParseError(charReader.currentPosition()-1,"Null char detected");
        } 
        else if (c == CharacterStreamSource.EOF) {
        	tokenizer.nextState(HtmlParserTokenizerState.EOF);	// we've done!
        	tokenFinished = true;
        }
        else {
        	tokenizer.addTextToCurrentToken(c);	
        }
        return tokenFinished;
	}
}
