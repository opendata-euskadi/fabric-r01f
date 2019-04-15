package r01f.html.parser;


import lombok.NoArgsConstructor;
import r01f.html.parser.base.HtmlParseError;
import r01f.io.CharacterStreamSource;

@NoArgsConstructor
  class HtmlTokenizerStateHandlerForTagStart
extends HtmlTokenizerStateHandlerForTagBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean read(final HtmlTokenizer tokenizer,final CharacterStreamSource charReader) throws HtmlParseError {
		boolean tokenFinished = false;
		
		char c = charReader.read();
		if (tokenizer.getCurrentTokenText().length() == 0 && c == '<') {
			// start tag
			tokenizer.addTextToCurrentToken(c);		
		}
		else if (c == '>') {
			// end tag
        	tokenizer.addTextToCurrentToken(c);		// read and finish
    		tokenizer.nextState(HtmlParserTokenizerState.Text);
    		tokenFinished = true;
        }
		else if (c == '<') {
			// inside tag
			if (charReader.nextEquals("!--#")) {
				// maybe an ssi directive inside an attribute
				// 	ie: <html lang="!--#echo var='LANG'-->">
				tokenizer.addTextToCurrentToken(c);	
			} else {
				// any < char inside a tag is not allowed: ie: <bo<dy>
	        	charReader.unread(1);					// unread... it's another token	
	    		tokenizer.nextState(HtmlParserTokenizerState.Text);
	    		tokenFinished = false;
			}
		}
		else if (c == '-'
			  && charReader.nextEquals("->")
			  && tokenizer.currentTokenTextLastIndexOf("<!--#") > 0) {
			// might be reading an ssi directive end
        	tokenizer.addTextToCurrentToken(c);		
    		tokenizer.addTextToCurrentToken("->");
    		_skip(charReader,"->".length());
		}
        else if (!_isAllowedChar(c)) {
        	charReader.unread(1);					// unread... it's another token	
    		tokenizer.nextState(HtmlParserTokenizerState.Text);
    		tokenFinished = false;
        }
		// TODO maybe limit the tag length
        else if (c == CharacterStreamSource.NULL_CHAR) {                
            throw new HtmlParseError(charReader.currentPosition()-1,"Null char detected");
        } 
        else if (c == CharacterStreamSource.EOF) {
        	tokenizer.nextState(HtmlParserTokenizerState.EOF);	// we've done!
        	tokenFinished = true;
        }
        else {
        	// inside tag start
        	tokenizer.addTextToCurrentToken(c);	
        }
        return tokenFinished;
	}
}
